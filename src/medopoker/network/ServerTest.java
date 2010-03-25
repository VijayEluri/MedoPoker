/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.network;

import java.util.Vector;
import javax.microedition.midlet.*;

/**
 * @author Martin
 */
public class ServerTest extends MIDlet implements ServerParent {
    public void startApp() {
		ServerCreator s = new ServerCreator(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

	public void startServer(Vector dl) {
	}
}
