/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.log;

import medopoker.testui.MIDPLogger;



/**
 *
 * @author Nejc
 */
public class Log {
    MIDPLogger ml;

    public Log() {
        //try {
        //    ml = new MIDPLogger(0, true, false);
        //} catch (Exception e) {}
    }

	public static final boolean DEBUG = true;
	
	public static void err(String msg) {
		if (DEBUG) System.out.println(msg);
        //ml.write(msg, 3);
	}

	public static void notify(String msg) {
		if (DEBUG) System.out.println(msg);
        //ml.write(msg, 0);
	}

}
