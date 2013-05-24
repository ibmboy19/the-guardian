package guard.server.server.utils;

import guard.server.server.ClientProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetDelayUtil {
	/**
	 * 網路狀態偵測
	 * @throws IOException
	 */
	public static int netStatus(ClientProcess pc) throws IOException {
		String lost = "" , delay = "", str = "";
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("ping -4 " + pc.getIp());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((str = buf.readLine()) != null) {
			// 偵測封包遺失 (目前暫無使用)
			if (str.contains("已遺失")) {
				int i = str.indexOf("已遺失");
				int j = str.indexOf("%");
				lost = str.substring(i + 9, j);
			}
			// 偵測網路延遲 
			if (str.contains("平均")) {
				str = str.substring(str.indexOf("平均"), str.length());
				int i = str.indexOf("=");
				int j = str.indexOf("ms");
				delay = str.substring(i+2 , j);
			}
		}
		return Integer.parseInt(delay);
	}
}