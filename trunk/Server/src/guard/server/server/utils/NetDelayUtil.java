package guard.server.server.utils;

import guard.server.server.ClientProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetDelayUtil extends Thread{
	private ClientProcess pc = null;
	private int result = -1;
	
	public NetDelayUtil(ClientProcess pc){
		this.pc = pc;
		this.start();
	}
	
	
	@Override
	public void run() {
		try {
			result = netStatus(pc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(!this.isInterrupted()){
			if(result != -1){
				this.interrupt();
			}
		}
	}
	
	public int getResult(){
		return result;
	}
	
	/**
	 * 網路狀態偵測
	 * 
	 * @throws IOException
	 */
	private int netStatus(ClientProcess pc) throws IOException {
		@SuppressWarnings("unused")
		String lost = "", delay = "", str = "";
		Process p = null;
		int type = 0;
		try {
			p = Runtime.getRuntime().exec("ping -4 " + pc.getIp());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream(),"big5"));
		buf.readLine();
		if ((str = buf.readLine()) != null) {
			if (str.contains("使用")) {
				type = 1;
			} else if (str.contains("with")) {
				type = 2;
			}
		}

		while ((str = buf.readLine()) != null) {
			switch (type) {
			case 1:
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
					delay = str.substring(i + 2, j);
				}
				break;
			case 2:
				// detect package lost
				if (str.contains("Lost")) {
					int i = str.indexOf("Lost");
					int j = str.indexOf("%");
					lost = str.substring(i + 9, j);
				}
				// detect package delay
				if (str.contains("Average")) {
					str = str.substring(str.indexOf("Average"), str.length());
					int i = str.indexOf("=");
					int j = str.indexOf("ms");
					delay = str.substring(i + 2, j);
				}
				break;
			}

		}
		buf.close();
		p.destroy();
		return Integer.parseInt(delay);

	}
}