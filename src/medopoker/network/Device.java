/*
 *
 * Copyright 2010 Nejc Saje
 * nejc.saje@gmail.com
 *
 */

package medopoker.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.bluetooth.*;
import javax.microedition.io.*;

import medopoker.log.Log;

/**
 *
 * @author Nejc Saje
 */
public class Device {
	private boolean local;
	private Device localDevice;
	private Vector localMsgQueue;
	private StreamConnection conn;
	private InputStream is;
	private OutputStream os;
	private String name;
    
	public Device(String localName) {
		conn = null;
		local = true;
		localMsgQueue = new Vector();
		name = localName;
	}
	public void connectLocal(Device locDev) {
		localDevice = locDev;
	}
	
	public Device(StreamConnection connection) {
        
		local = false;
		localDevice = null;
		conn = connection;
		try {
			is = conn.openInputStream();
			os = conn.openOutputStream();
			RemoteDevice rd = RemoteDevice.getRemoteDevice(conn);
			name = rd.getFriendlyName(true);
		} catch (IOException e) {
			terminate();
		}
	}

	public String recieve() {

		if (local) {
			while(localMsgQueue.isEmpty()) {}
			String s = (String)localMsgQueue.firstElement();
			localMsgQueue.removeElementAt(0);
			Log.notify("recieved: "+s);
			return s;
		} else {
			byte[] data = null;
			try {
				int length = is.read();
				data = new byte[length];
				length = 0;

				while (length != data.length) {
					int num_bytes = is.read(data, length, data.length-length);
					if (num_bytes == -1)
						throw new IOException("Problem reading data");
					length += num_bytes;
				}
			} catch (IOException e) {
				Log.err(e.getMessage());
			}
			String s = new String(data);
			Log.notify("recieved: "+s);
			return s;
		}
	}

	public void send(String msg) {
		if (local) {
			localDevice.pushLocalMsg(msg);
		} else {
			try {
				os.write(msg.length());
				os.write(msg.getBytes());
                os.flush();
			} catch (IOException e) {
				Log.err(e.getMessage());
			}
		}
        Log.notify("sent: "+msg);
	}

	public String getFriendlyName() {
		return name;
	}

	public void pushLocalMsg(String s) {
		localMsgQueue.addElement(s);
	}

	private void terminate() {
		Log.err("A problem with device '"+name);
		try {conn.close();} catch (IOException e) {}
	}
}
