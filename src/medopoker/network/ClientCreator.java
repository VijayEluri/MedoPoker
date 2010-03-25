/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package medopoker.network;

import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.*;
import javax.microedition.io.*;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import medopoker.flow.MedoPoker;
import medopoker.log.Log;

/**
 *
 * @author Nejc
 */
public class ClientCreator implements CommandListener, Runnable {
	private final Object inqLock = new Object();
	private final Object servLock = new Object();
	private final Object devLock = new Object();
	private InquiryListener inqListener;
    private DiscoveryAgent da;
	private int devIndex = -1;
	private Device d;
	private Display disp;
	private ClientParent cp;
    private Command cancel;
    private Thread t;

	public ClientCreator(MIDlet m) {
		disp = Display.getDisplay(m);
		cp = (ClientParent)m;
		connect();
	}

	private void connect() {
		Log.notify("Starting client...");

		Form form = new Form("Join game");
		disp.setCurrent(form);

		String msg = "Searching for devices...";
		form.append(msg);
        cancel = new Command("Cancel", Command.CANCEL, 1);
        form.addCommand(cancel);
        form.setCommandListener(this);

		t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
			LocalDevice ld = LocalDevice.getLocalDevice();
			da = ld.getDiscoveryAgent();
			ld.setDiscoverable(DiscoveryAgent.GIAC);

			Log.notify("Starting device inquiry...");
			inqListener = new InquiryListener();
			synchronized(inqLock) {
				da.startInquiry(DiscoveryAgent.GIAC, inqListener);
				try {inqLock.wait();} catch(InterruptedException e){}
			}

			if (inqListener.getDevicesFound().isEmpty()) {
				Log.err("No devices found!");
                Alert a = new Alert("No devices found!");
                a.setString("No devices found!");
                a.setTimeout(Alert.FOREVER);
                disp.setCurrent(a, ((MedoPoker)cp).getList());
				return;
			}

			displayDeviceList(inqListener.getDevicesFound());
			synchronized (devLock) { // give user time to select the device
				try {devLock.wait();} catch(InterruptedException e){}
			}

			String url = (String)inqListener.getDevURLsFound().elementAt(devIndex);
            Log.notify("SERVICE URL: " + url);
			d = new Device((StreamConnection)Connector.open(url));
			
			cp.startClient(d);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void displayDeviceList(Vector devices) throws IOException {
		List list = new List("Devices found", List.IMPLICIT);
		for (int i=0; i<devices.size(); i++) {
			list.append(((RemoteDevice)devices.elementAt(i)).getFriendlyName(true), null);
		}
		list.setCommandListener(this);
		disp.setCurrent(list);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == List.SELECT_COMMAND) {
			devIndex = ((List)d).getSelectedIndex();
			synchronized (devLock) {
				devLock.notify();
			}
		} else if (c == cancel) {
            t.interrupt();
            disp.setCurrent(((MedoPoker)cp).getList());
        }
	}

	private class InquiryListener implements DiscoveryListener {

		ServiceRecord service;
        
		private Vector devsFound = new Vector();
        private Vector devURLs = new Vector();

		public Vector getDevicesFound() {
			return devsFound;
		}

        public Vector getDevURLsFound() {
            return devURLs;
        }
		
		public void deviceDiscovered(RemoteDevice dev, DeviceClass ds){
			Log.notify("Device discovered!");

			UUID[] uuids = {new UUID("01101101", true)};
            service = null;
			synchronized(servLock) {
                try {
                    da.searchServices(null, uuids, dev, this);
                    servLock.wait();
                } catch (BluetoothStateException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {}
			}

            if (service != null && !devsFound.contains(dev)) {
				devsFound.addElement(dev);
                devURLs.addElement(service.getConnectionURL(0, false));
            }
		}

		public void servicesDiscovered(int id, ServiceRecord[] sr) {
			service = sr[0];
		}

		public void serviceSearchCompleted(int arg0, int arg1) {
			synchronized(servLock) {servLock.notify();}
		}

		public void inquiryCompleted(int arg0) {
			Log.notify("Inquiry completed.");
			synchronized(inqLock) {
				inqLock.notify();
			}
		}

    }
}
