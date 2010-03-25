/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.flow;

import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import medopoker.network.*;
import org.netbeans.microedition.lcdui.SplashScreen;
import javax.bluetooth.LocalDevice;
import medopoker.log.Log;

/**
 * @author Martin
 */
public class MedoPoker extends MIDlet implements CommandListener, ServerParent, ClientParent {

    private boolean midletPaused = false;

    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private List list;
    private SplashScreen splashScreen;
    private Command exitCommand;
    private Image image1;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The MedoPoker constructor.
     */
    public MedoPoker() {
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    //</editor-fold>//GEN-END:|methods|0|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
        switchDisplayable(null, getSplashScreen());//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
    }//GEN-BEGIN:|3-startMIDlet|2|
    //</editor-fold>//GEN-END:|3-startMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
    //</editor-fold>//GEN-END:|4-resumeMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
		// write pre-action user code here
        if (displayable == list) {//GEN-BEGIN:|7-commandAction|1|21-preAction
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|1|21-preAction
				// write pre-action user code here
                listAction();//GEN-LINE:|7-commandAction|2|21-postAction
				// write post-action user code here
            }//GEN-BEGIN:|7-commandAction|3|29-preAction
        } else if (displayable == splashScreen) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|3|29-preAction
				// write pre-action user code here
                switchDisplayable(null, getList());//GEN-LINE:|7-commandAction|4|29-postAction
				// write post-action user code here
            }//GEN-BEGIN:|7-commandAction|5|7-postCommandAction
        }//GEN-END:|7-commandAction|5|7-postCommandAction
		// write post-action user code here
    }//GEN-BEGIN:|7-commandAction|6|
    //</editor-fold>//GEN-END:|7-commandAction|6|






    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image1 ">//GEN-BEGIN:|18-getter|0|18-preInit
    /**
     * Returns an initiliazed instance of image1 component.
     * @return the initialized component instance
     */
    public Image getImage1() {
        if (image1 == null) {//GEN-END:|18-getter|0|18-preInit
			// write pre-init user code here
            try {//GEN-BEGIN:|18-getter|1|18-@java.io.IOException
                image1 = Image.createImage("/resources/splash.png");
            } catch (java.io.IOException e) {//GEN-END:|18-getter|1|18-@java.io.IOException
				e.printStackTrace();
            }//GEN-LINE:|18-getter|2|18-postInit
			// write post-init user code here
        }//GEN-BEGIN:|18-getter|3|
        return image1;
    }
    //</editor-fold>//GEN-END:|18-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: list ">//GEN-BEGIN:|19-getter|0|19-preInit
    /**
     * Returns an initiliazed instance of list component.
     * @return the initialized component instance
     */
    public List getList() {
        if (list == null) {//GEN-END:|19-getter|0|19-preInit
			// write pre-init user code here
            list = new List("MedoPoker", Choice.IMPLICIT);//GEN-BEGIN:|19-getter|1|19-postInit
            list.append("Join game", null);
            list.append("Host game", null);
            list.append("About", null);
            list.append("Exit", null);
            list.setCommandListener(this);
            list.setSelectedFlags(new boolean[] { true, false, false, false });//GEN-END:|19-getter|1|19-postInit
			// write post-init user code here
        }//GEN-BEGIN:|19-getter|2|
        return list;
    }
    //</editor-fold>//GEN-END:|19-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: listAction ">//GEN-BEGIN:|19-action|0|19-preAction
    /**
     * Performs an action assigned to the selected list element in the list component.
     */
    public void listAction() {//GEN-END:|19-action|0|19-preAction
		// enter pre-action user code here
        String __selectedString = getList().getString(getList().getSelectedIndex());//GEN-BEGIN:|19-action|1|25-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("Join game")) {//GEN-END:|19-action|1|25-preAction
				Log.notify("Starting CC...");
				ClientCreator cr = new ClientCreator(this);
				Log.notify("CC started");
//GEN-LINE:|19-action|2|25-postAction
				// write post-action user code here
            } else if (__selectedString.equals("Host game")) {//GEN-LINE:|19-action|3|26-preAction
				ServerCreator sr = new ServerCreator(this);
//GEN-LINE:|19-action|4|26-postAction
				// write post-action user code here
            } else if (__selectedString.equals("About")) {//GEN-LINE:|19-action|5|27-preAction
				// write pre-action user code here
//GEN-LINE:|19-action|6|27-postAction
				// write post-action user code here
            } else if (__selectedString.equals("Exit")) {//GEN-LINE:|19-action|7|33-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|19-action|8|33-postAction
                // write post-action user code here
            }//GEN-BEGIN:|19-action|9|19-postAction
        }//GEN-END:|19-action|9|19-postAction
		// enter post-action user code here
    }//GEN-BEGIN:|19-action|10|
    //</editor-fold>//GEN-END:|19-action|10|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashScreen ">//GEN-BEGIN:|28-getter|0|28-preInit
    /**
     * Returns an initiliazed instance of splashScreen component.
     * @return the initialized component instance
     */
    public SplashScreen getSplashScreen() {
        if (splashScreen == null) {//GEN-END:|28-getter|0|28-preInit
			// write pre-init user code here
            splashScreen = new SplashScreen(getDisplay());//GEN-BEGIN:|28-getter|1|28-postInit
            splashScreen.setTitle("MedoSplash");
            splashScreen.setCommandListener(this);
            splashScreen.setFullScreenMode(true);
            splashScreen.setImage(getImage1());//GEN-END:|28-getter|1|28-postInit
			// write post-init user code here
        }//GEN-BEGIN:|28-getter|2|
        return splashScreen;
    }
    //</editor-fold>//GEN-END:|28-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|31-getter|0|31-preInit
    /**
     * Returns an initiliazed instance of exitCommand component.
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|31-getter|0|31-preInit
            // write pre-init user code here
            exitCommand = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|31-getter|1|31-postInit
            // write post-init user code here
        }//GEN-BEGIN:|31-getter|2|
        return exitCommand;
    }
    //</editor-fold>//GEN-END:|31-getter|2|



    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay () {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        switchDisplayable (null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet ();
        } else {
            initialize ();
            startMIDlet ();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }

	//////////////////////////////////////////////////////

	public void startClient(Device d) {
		Log.notify("Starting CM");
		ClientManager cm = new ClientManager(d, this);
	}

	public void startServer(Vector dl) {
		ServerManager sm = new ServerManager(dl);
		String localName = "Host";
		try {
			localName = LocalDevice.getLocalDevice().getFriendlyName();
		} catch (BluetoothStateException ex) {
			ex.printStackTrace();
		}
		Device localDevice = new Device(localName);
		Device localRemoteDevice = new Device(localName);
		localDevice.connectLocal(localRemoteDevice);
		localRemoteDevice.connectLocal(localDevice);

		sm.addLocalDevice(localDevice);

		startClient(localRemoteDevice);
		sm.startGame();
	}

}
