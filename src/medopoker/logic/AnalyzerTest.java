/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.logic;

import javax.microedition.midlet.*;

/**
 * @author Martin
 */
public class AnalyzerTest extends MIDlet {
    public void startApp() {
		for (int j=0; j<50; j++) {
		Card[] deck = Card.getDeck();
		Card[] on_table = new Card[5];
		Card[] hole = new Card[2];
		int deck_pos = 0;

		for (int i=0; i<5; i++) {
			on_table[i] = deck[deck_pos++];
		}

		for (int i=0; i<2; i++) {
			hole[i] = deck[deck_pos++];
		}

		System.out.println("On table:");
		for (int i=0; i<on_table.length; i++)
			System.out.println("\t"+on_table[i]);

		System.out.println("\nIn hands:");
		for (int i=0; i<hole.length; i++)
			System.out.println("\t"+hole[i]);



		HandAnalyzer ha = new HandAnalyzer(on_table, hole);
		ha.analyze();
		ha.report();
		
		}

		/*Card[] on_table = {new Card(1,1), new Card(1, 5), new Card(1,3), new Card(1, 4), new Card(3, 8)};
		Card[] hole = {new Card(1, 7), new Card(3,9)};
		System.out.println("::::::::::::::::::::::::");
				System.out.println("On table:");
		for (int i=0; i<on_table.length; i++)
			System.out.println("\t"+on_table[i]);

		System.out.println("\nIn hands:");
		for (int i=0; i<hole.length; i++)
			System.out.println("\t"+hole[i]);


		HandAnalyzer ha = new HandAnalyzer(on_table, hole);
		ha.analyze();
		ha.report();

		destroyApp(true);*/
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
