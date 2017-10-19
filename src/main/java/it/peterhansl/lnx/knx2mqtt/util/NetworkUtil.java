package it.peterhansl.lnx.knx2mqtt.util;

import java.net.InetAddress;

public class NetworkUtil {


	private static final String SYSTEM_PROPERTY_OS_NAME = "os.name";
	private static final String OS_NAME_WINDOWS = "windows";
	
	private static final String WINDOWS_PING_CMD = "ping -n 1 ";
	private static final String UNIX_PING_CMD = "ping -c 1 ";

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
			if (System.getProperty(SYSTEM_PROPERTY_OS_NAME).toLowerCase().startsWith(OS_NAME_WINDOWS)) {
				// For Windows
				cmd = WINDOWS_PING_CMD + host.getHostAddress();
			} else {
				// For Linux and OSX
				cmd = UNIX_PING_CMD + host.getHostAddress();
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
