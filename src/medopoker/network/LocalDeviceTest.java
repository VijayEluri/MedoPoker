/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package medopoker.network;

import java.util.Vector;
import javax.microedition.midlet.*;
import medopoker.network.*;

/**
 * @author Martin
 */
public class LocalDeviceTest extends MIDlet {
    public void startApp() {
		final Device d1 = new Device("local1");
		final Device d2 = new Device("local1");
		Vector msgQ = new Vector();

		new Thread(new Runnable() {

			public void run() {
				d1.connectLocal(d2);
				System.out.println("d1: sprejemam");
				System.out.println("d1: Prejeto: " + d1.recieve());
			}

		}).run();

		new Thread(new Runnable() {

			public void run() {
				d2.connectLocal(d1);
				System.out.println("d1: pošiljam");
				d2.send("TEST FROM D2");
				System.out.println("d2: poslamo");
			}

		}).run();

    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
