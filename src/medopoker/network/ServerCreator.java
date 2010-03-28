/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.network;

import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import javax.microedition.midlet.MIDlet;
import medopoker.flow.MedoPoker;
import medopoker.log.Log;

/**
 *
 * @author Nejc
 */
public class ServerCreator implements Runnable, CommandListener {
	private boolean accepting;
	private Vector deviceList;
	private Display disp;
	private List list;
	private Command start;
	private Command kick;
    private Command back;
	private ServerParent sp;
    private Thread t;

	public ServerCreator(MIDlet m) {
		accepting = true;
		deviceList = new Vector();
		disp = Display.getDisplay(m);
		sp = (ServerParent) m;
		startAccepting();
	}

	public void removeDevice(Device d) {
		deviceList.removeElement(d);
	}

	public void startAccepting() {
		t = new Thread(this);
		t.start();
		list = new List("Waiting for devices", List.IMPLICIT);
		start = new Command("Start", Command.OK, 1);
        back = new Command("Back", Command.CANCEL, 1);
		kick = new Command("Kick selected", Command.STOP, 1);
		list.addCommand(start);
		list.addCommand(kick);
        list.addCommand(back);
		list.setCommandListener(this);
		disp.setCurrent(list);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == start) {
			accepting = false;
			sp.startServer(deviceList);
		} else if (c == kick) {
			int index = list.getSelectedIndex();
			deviceList.removeElementAt(index);
			list.delete(index);
		} else if (c == back) {
            t.interrupt();
            disp.setCurrent(((MedoPoker)sp).getList());
        }
	}

	public void run() {
		try {
			Log.notify("Starting server...");
			
			UUID uuid = new UUID("01101101", true);
			String serviceURL = "btspp://localhost:" + uuid + ";name=MedoPokerServer";

			LocalDevice ld = LocalDevice.getLocalDevice();
			ld.setDiscoverable(DiscoveryAgent.GIAC);

			StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open(serviceURL);

			while (accepting && deviceList.size() < 5) {
				StreamConnection conn = streamConnNotifier.acceptAndOpen();
				Device d = new Device(conn);
				deviceList.addElement(d);
				list.append(d.getFriendlyName(), null);
				Log.notify("New device connected!");
			}
		} catch (IOException e) {
			Log.err(e.getMessage());
		}
	}
}
