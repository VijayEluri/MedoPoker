/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.flow;

import java.util.Vector;
import medopoker.log.Log;
import medopoker.logic.*;
import medopoker.network.Device;

/**
 *
 * @author Martin
 */
public class ServerManager implements Runnable {

	private final float STARTING_MONEY = 200.0f;
	private final float SB = 5.0f;

	private float pot = 0.0f;

	private Vector deviceList;
	private ServerPlayer[] players;
	private Thread t;

	private Card[] deck;
	private Card[] on_table;

	private String[] actions = {"FOLD", "CHECK", "CALL", "RAISE", "SB", "BB"};

	public ServerManager(Vector dl) {
		deviceList = dl;
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
		/* TEST
		broadcast("START");
		playerInit();
		playerUpdate();

		dealCards();
		updateHand();
		setTable();
		startPlay();
		updatePot();

		int a = requestAction(0);
		Log.notify("Action: "+a+" from player 0");
		announceAction(0, a, 0.0f);

		a = requestAction(1);
		announceAction(1, a, 0.0f);

		broadcast("EXIT");
		*/

		broadcast("START");
		playerInit();

		boolean running = true;
		int dealer = 0;
		while (running) {
			pot = 0;
			
			playerUpdate();
			dealCards();
			updateHand();
			setTable();
			updatePot();

			startRound();

			int i = dealer;
			float highest_bet = 2*SB;
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
			i = increment(i);

			playerUpdate();
			updatePot();
			
			for(int r=0; r<3; r++) {

				if (r != 0) i = increment(dealer);

				do {
					int a = requestAction(i);
					switch(a) {
						case 0:
							players[i].setIngame(false);
							break;
						case 1:
							if (players[i].getMoneyIn() != highest_bet) {
								players[i].setIngame(false);
							}
							break;
						case 2:
							float amount = highest_bet-players[i].getMoneyIn();
							System.out.println("amount: "+amount+" moneyIn: "+players[i].getMoneyIn());
							pot+=amount;
							players[i].put(amount);
							break;
						case 3:
							players[i].put(SB);
							highest_bet += SB;
							highest_better = i;
							pot+=SB;
							break;
					}
					announceAction(i, a, 0.0f);
					playerUpdate();
					updatePot();
					i = increment(i);

				} while (i!=highest_better||(r==0&&i==highest_better)?true:false);

				if (r != 2) updateTable();
			}

			//showdown

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
	}

	private void logUpdate(String s) {
		broadcast("LU");
		broadcast(s);
	}
	
	private int requestAction(int i) {
		sendTo(i, "RA");
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
