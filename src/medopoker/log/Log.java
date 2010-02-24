/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.log;



/**
 *
 * @author Nejc
 */
public class Log {
	public static final boolean DEBUG = true;
	
	public static void err(String msg) {
		System.out.println(msg);
	}

	public static void notify(String msg) {
		System.out.println(msg);
	}

}
