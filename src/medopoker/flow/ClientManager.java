/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.flow;

import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import medopoker.log.Log;
import medopoker.logic.Card;
import medopoker.network.Device;

/**
 *
 * @author Nejc
 */
public class ClientManager implements Runnable {
	private Device d;
	private Display disp;
	private PokerCanvas PCanvas;
	private String msg;

	private Player[] players;
	private Card[] hole;
	private Card[] on_table;

	public ClientManager(Device dev, MIDlet m) {
		d = dev;
		disp = Display.getDisplay(m);
		PCanvas = new PokerCanvas(this);
		Thread t = new Thread(this);
		t.start();
		System.out.println("CM running");
	}

	public void run() {
		Form f = new Form("Wait");
		f.append("Waiting for the game to start...");
		disp.setCurrent(f);
		do {
			msg = d.recieve();
		} while (!msg.equals("START"));
		disp.setCurrent(PCanvas);

		do {
			msg = d.recieve();
		} while (!msg.equals("PI"));
		recievePI();

		boolean running = true;
		while(running) {
			msg = d.recieve();
			if (msg.equals("PU")) recievePU(); // Player Update
			if (msg.equals("UH")) recieveUH(); // Update Hand
			if (msg.equals("ST")) recieveST(); // Set Table
			if (msg.equals("UT")) recieveUT(); // Update Table
			if (msg.equals("UPOT")) recieveUPOT(); // Update Pot
			if (msg.equals("LU")) recieveLU(); // Log Update
			if (msg.equals("RA")) recieveRA(); // Recieve Action
			if (msg.equals("AA")) recieveAA(); // Announce Action
			if (msg.equals("PLAY")) recievePLAY(); // Start the game
			if (msg.equals("EXIT")) running=false;
		}

		Log.notify("Game ended");

	}

	private void recievePI() {
		int length = Integer.parseInt(d.recieve());
		players = new Player[length];
		for (int i=0; i<length; i++) {
			msg = d.recieve();
			players[Integer.parseInt(split(msg)[0])] = new Player(split(msg)[1], 0);
			Log.notify("creating player");
		}
		PCanvas.updatePlayers(players);
	}

	private void recievePU() {
		for (int i=0; i<players.length; i++) {
			msg = d.recieve();
			players[Integer.parseInt(split(msg)[0])].setMoney(Float.parseFloat(split(msg)[1]));
		}
		PCanvas.updatePlayers(players);
	}

	private void recieveUH() {
		hole = new Card[2];
		for (int i=0; i<2; i++) {
			msg = d.recieve();
			hole[i] = new Card(Integer.parseInt(split(msg)[0]), Integer.parseInt(split(msg)[1]));
		}
		PCanvas.newHole(hole);
	}

	private void recieveST() {
		on_table = new Card[5];
		for (int i=0; i<5; i++) {
			msg = d.recieve();
			on_table[i] = new Card(Integer.parseInt(split(msg)[0]), Integer.parseInt(split(msg)[1]));
		}
		PCanvas.newOnTable(on_table);
	}

	private void recieveUT() {
		PCanvas.showMoreCards();
	}

	private void recieveUPOT() {
		msg = d.recieve();
		PCanvas.updatePot(msg);
	}

	private void recieveLU() {
		msg = d.recieve();
		PCanvas.appendToLog(msg);
	}

	private void recieveRA() {
		PCanvas.getAction();
	}

	private void recieveAA() {
		msg = d.recieve();
		int p = Integer.parseInt(split(msg)[0]);
		int a = Integer.parseInt(split(msg)[1]);
		float amount = Float.parseFloat(split(msg)[2]);
		String s = players[p].getName() + " " + PokerCanvas.actions[a];
		if (amount != 0.0) s = s + " " + amount;
		PCanvas.appendToLog(s);
		PCanvas.currentAction(a, p);
	}
	
	private void recievePLAY() {
		PCanvas.initialized = true;
		PCanvas.redraw();
	}


	/////////////////////////////

	public void sendAction(String a) {
		d.send(a);
	}

	private String[] split(String original) {
		Vector nodes = new Vector();
		String separator = ":";
		int index = original.indexOf(separator);
		
		while(index>=0) {
			nodes.addElement( original.substring(0, index) );
			original = original.substring(index+separator.length());
			index = original.indexOf(separator);
		}
		nodes.addElement( original );
		String[] result = new String[ nodes.size() ];

		if( nodes.size()>0 ) {
			for(int loop=0; loop<nodes.size(); loop++) {
				result[loop] = (String)nodes.elementAt(loop);
			}
		}

		return result;
	}
}
