/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.network;

import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStoreException;
//import medopoker.testui.MIDPLogger;

/**
 * @author Nejc Saje
 */
public class ClientTest extends MIDlet implements ClientParent {
    //MIDPLogger logger;

    public ClientTest() {
        /*try {
            logger = new MIDPLogger(0, true, false);
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }*/
    }

    public void startApp() {
		ClientCreator c = new ClientCreator(this);
    }

	public void startClient(Device d) {
        //logger.write("Client started", 0);
        d.send("HI from Client");
       // logger.write("sent some stuff", 0);
        String msg = d.recieve();
       // logger.write("Recieved: " + msg, 0);
	}

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
