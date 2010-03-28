/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.flow;

import java.util.Vector;
import medopoker.log.Log;
import medopoker.logic.*;
import medopoker.logic.Util.Comparator;
import medopoker.logic.Util.HandComparator;
import medopoker.network.Device;

/**
 *
 * @author Nejc Saje
 */
public class ServerManager implements Runnable {

	private float STARTING_MONEY;
	private float SB;

	private float pot = 0.0f;
    private float highest_bet = 0.0f;

	private Vector deviceList;
	private ServerPlayer[] players;
	private Thread t;

	private Card[] deck;
	private Card[] on_table;

	private String[] actions = {"FOLD", "CHECK", "CALL", "RAISE", "SB", "BB"};

	public ServerManager(Vector dl, float sm, float sb) {
		deviceList = dl;
        STARTING_MONEY = sm;
        SB = sb;
	}

	public void startGame() {
		players = new ServerPlayer[deviceList.size()];
		for (int i=0; i<players.length; i++) {
			players[i] = new ServerPlayer(((Device)deviceList.elementAt(i)).getFriendlyName(), STARTING_MONEY);
		}

		t = new Thread(this);
		t.start();
	}


	public void run() {
		broadcast("START");
		playerInit();

		boolean running = true;
		int dealer = 0;
		while (running) {
			pot = 0;
            for (int i=0; i<players.length; i++) {
                players[i].reset_round();
            }
			
			playerUpdate();
			dealCards();
			updateHand();
			setTable();
			updatePot();

			startRound();

			int i = dealer;
			highest_bet = 2*SB;
            float per_player_in = 0;
			// SB & BB
			i = increment(i);
			players[i].put(SB);
			pot+=SB;
			announceAction(i, 4, SB);
			i = increment(i);
			players[i].put(2*SB);
			pot+=2*SB;
			announceAction(i, 5, 2*SB);
            
			int highest_better = i;
            int big_blind = i;
			i = increment(i);

            int players_ingame = players.length;

			playerUpdate();
			updatePot();
			
			for(int r=0; r<4; r++) {

				if (r != 0) {
                    i = increment(dealer);
                    highest_better = i;
                    highest_bet = 0;
                }

                updatePot();

                boolean round_finished = false;

				do {
                    if (!players[i].isIngame()) continue;
					int a = requestAction(i);
                    float amount;
					switch(a) {
						case 0: // FOLD
							players[i].setIngame(false);
                            players_ingame--;
                            announceAction(i, a, 0.0f);
							break;
						case 1: // CHECK
							if (players[i].getMoneyIn() != per_player_in+highest_bet) {
								players[i].setIngame(false);
                                players_ingame--;
                                a = 0;
							}
                            announceAction(i, a, 0.0f);
							break;
						case 2: // CALL
							amount = per_player_in+highest_bet-players[i].getMoneyIn();
							Log.notify("amount: "+amount+" moneyIn: "+players[i].getMoneyIn());
							pot+=amount;
							players[i].put(amount);
                            announceAction(i, a, amount);
							break;
						case 3: // RAISE
                            float raise = (requestRaise(i, highest_bet, SB)+1)*SB + highest_bet;
                            Log.notify("Raise: " + raise);
							pot+=(per_player_in+raise-players[i].getMoneyIn());
							players[i].put(per_player_in+raise-players[i].getMoneyIn());
							highest_bet = raise;
							highest_better = i;
                            announceAction(i, a, raise);
							break;
					}
					playerUpdate();
					updatePot();

                    if (increment(i) == highest_better) round_finished = true;
                    if (r==0) {  // if the BB guy raises
                        if (increment(i)==big_blind) {
                            round_finished = false;
                        }
                        if (i==big_blind && a<3) {
                            round_finished = true;
                            big_blind = -1;
                        }
                    }

					i = increment(i);
                    if (players_ingame == 1) break;

                } while (!round_finished);
                Log.notify("XYZ");

                per_player_in += highest_bet;
                if (players_ingame == 1) break;
				if (r != 3) updateTable();
                if (r == 0) {updateTable();updateTable();} // flop
			}

			//showdown
            Comparator cmp = new HandComparator();
            Hand highest = null;
            int winner = -1;
            for (int j=0; j<players.length; j++) {
                if (players[i].isIngame()) {
                    Card[] p_hole = players[i].getHole();
                    HandAnalyzer ha = new HandAnalyzer(on_table, p_hole);
                    Hand hand = ha.analyze();
                    if (highest == null || cmp.compare(hand, highest)==1) {
                        highest = hand;
                        winner = i;
                    }
                }
            }

            Log.notify("winner: "+winner);
            announceAction(winner, 6, pot);
            players[winner].win(pot);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            dealer = increment(dealer);
			endRound();
		}
		broadcast("EXIT");
	}

	private int increment(int i) {
		return (i+1)%players.length;
	}

	private void playerInit() {
		broadcast("PI"); // player initialization
		broadcast(players.length+"");
		for (int i=0; i<players.length; i++) {
			broadcast(i+":"+players[i].getName());
		}
	}

	private void playerUpdate() {
		broadcast("PU"); // player money update
		for (int i=0; i<players.length; i++) {
			broadcast(i+":"+players[i].getMoney());
		}
	}

	private void dealCards() {
		deck = Card.getDeck();
		//for (int i=0; i<deck.length; i++) Log.notify(deck[i].toString());
		on_table = new Card[5];
		int deck_pos = 0;

		for (int i=0; i<players.length; i++) {
			players[i].dealHole(new Card[]{deck[deck_pos++], deck[deck_pos++]});
		}

		for (int i=0; i<5; i++) {
			on_table[i] = deck[deck_pos++];
		}
	}

	private void updateHand() {
		for (int i=0; i<deviceList.size(); i++) {
			Card[] hole = players[i].getHole();
			sendTo(i, "UH"); // update hand
			sendTo(i, hole[0].getSuit()+":"+hole[0].getRank());
			sendTo(i, hole[1].getSuit()+":"+hole[1].getRank());
		}
	}

	private void setTable() {
		broadcast("ST"); // set table
		for (int i=0; i<on_table.length; i++) {
			broadcast(on_table[i].getSuit()+":"+on_table[i].getRank());
		}
	}

	private void updateTable() {
		broadcast("UT"); // update table
	}

	private void updatePot() {
		broadcast("UPOT");
		broadcast(String.valueOf(pot));
        broadcast(String.valueOf(highest_bet));
	}

	private void logUpdate(String s) {
		broadcast("LU");
		broadcast(s);
	}
	
	private int requestAction(int i) {
		sendTo(i, "RA");
		return Integer.parseInt(recieveFrom(i));
	}

	private int requestRaise(int i, float hb, float bb) {
		sendTo(i, "RR");
        sendTo(i, Float.toString(hb)); // highest bet
        sendTo(i, Float.toString(bb)); // big blind
		return Integer.parseInt(recieveFrom(i));
	}

	private void startRound() {
		broadcast("SR");
	}

	private void endRound() {
		broadcast("ER");
	}

	private void announceAction(int player, int action, float amount) {
		broadcast("AA");
		broadcast(player+":"+action+":"+amount);
	}

//////////////////////////////////////////////////////

	public void addLocalDevice(Device d) {
		deviceList.addElement(d);
	}

	public void broadcast(String msg) {
		for (int i=0; i<deviceList.size(); i++) {
			Log.notify("Sending message '"+msg+"' to device at index "+i);
			((Device)deviceList.elementAt(i)).send(msg);
		}
	}

	public String recieveFrom(int i) {
		return ((Device)deviceList.elementAt(i)).recieve();
	}
	
	public void sendTo(int i, String msg) {
		((Device)deviceList.elementAt(i)).send(msg);
	}
}
