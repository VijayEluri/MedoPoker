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

	private Vector deviceList;
	private Player[] players;
	private Thread t;

	private Card[] deck;
	private Card[] on_table;

	public ServerManager(Vector dl) {
		deviceList = dl;
	}

	public void startGame() {
		players = new Player[deviceList.size()];
		for (int i=0; i<players.length; i++) {
			players[i] = new Player(((Device)deviceList.elementAt(i)).getFriendlyName(), STARTING_MONEY);
		}

		t = new Thread(this);
		t.start();
	}


	public void run() {
		broadcast("START");
		playerInit();
		playerUpdate();

		dealCards();
		updateHand();
		setTable();

		broadcast("EXIT");
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

	private void logUpdate(String s) {
		broadcast("LU");
		broadcast(s);
	}

	public void addLocalDevice(Device d) {
		deviceList.addElement(d);
	}

	public void broadcast(String msg) {
		for (int i=0; i<deviceList.size(); i++) {
			Log.notify("Sending message '"+msg+"' to device at index "+i);
			((Device)deviceList.elementAt(i)).send(msg);
		}
	}

	public void recieveFrom(int i) {
		((Device)deviceList.elementAt(i)).recieve();
	}
	
	public void sendTo(int i, String msg) {
		((Device)deviceList.elementAt(i)).send(msg);
	}
}
