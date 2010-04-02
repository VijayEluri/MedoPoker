/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.network;

import java.util.Vector;
import javax.microedition.midlet.*;
//import medopoker.testui.MIDPLogger;

/**
 * @author Nejc Saje
 */
public class ServerTest extends MIDlet implements ServerParent {
   // MIDPLogger logger;

    public ServerTest() {
       /* try {
            logger = new MIDPLogger(0, true, false);
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }*/
    }
    public void startApp() {
		ServerCreator s = new ServerCreator(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

	public void startServer(Vector dl) {
        //logger.write("Server started", 0);
        Device d = (Device)dl.elementAt(0);
        d.send("HI from Server");
        //logger.write("sent some stuff", 0);
        String msg = d.recieve();
        //logger.write("Recieved: " + msg, 0);
	}
}
