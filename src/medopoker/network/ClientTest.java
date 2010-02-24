/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.network;

import javax.microedition.midlet.*;

/**
 * @author Martin
 */
public class ClientTest extends MIDlet implements ClientParent {
    public void startApp() {
		ClientCreator c = new ClientCreator(this);
    }

	public void startClient(Device d) {
		System.out.println("client started");
	}

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
