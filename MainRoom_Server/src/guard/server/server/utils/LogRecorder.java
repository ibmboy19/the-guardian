package guard.server.server.utils;

import guard.server.server.ClientProcess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogRecorder {
	/* BufferedWriter */
	private static BufferedWriter out = null;

	/** base */
	public static void writeLog(String messenge) {
		try {
			out = new BufferedWriter(new FileWriter("log\\Log.log", true));
			out.write(messenge + "\r\n");
			out.close();
		} catch (IOException e) {
			System.out.println("以下是錯誤訊息: " + e.getMessage());
		}
	}
	
	/** base */
	public static void writePlayerLog(ClientProcess client) {
		try {
			out = new BufferedWriter(new FileWriter("log\\PlayerLog.log", true));
			out.write("Player: " + client.getAccountName() + " - IP: " + client.getIp() +" @"+ new Date().toString() + "\r\n");
			out.close();
		} catch (IOException e) {
			System.out.println("以下是錯誤訊息: " + e.getMessage());
		}
	}

}
