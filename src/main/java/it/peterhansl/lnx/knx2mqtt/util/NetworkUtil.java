package it.peterhansl.lnx.knx2mqtt.util;

import java.net.InetAddress;

public class NetworkUtil {

	/**
	 * 
	 * Method taken from http://stackoverflow.com/questions/2448666/how-to-do-a-true-java-ping-from-windows
	 * 
	 * @param host
	 * @return
	 */
	public static boolean ping(InetAddress host) {
		try {
			String cmd = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
				// For Windows
				cmd = "ping -n 1 " + host.getHostAddress();
			} else {
				// For Linux and OSX
				cmd = "ping -c 1 " + host.getHostAddress();
			}

			Process myProcess = Runtime.getRuntime().exec(cmd);
			myProcess.waitFor();

			if (myProcess.exitValue() == 0) {

				return true;
			} else {

				return false;
			}

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
	}

}
