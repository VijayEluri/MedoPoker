/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.log;


/**
 *
 * @author Nejc Saje
 */
public class Log {

	public static final boolean DEBUG = true;
	
	public static void err(String msg) {
		if (DEBUG) System.out.println(msg);
	}

	public static void notify(String msg) {
		if (DEBUG) System.out.println(msg);
	}

}
