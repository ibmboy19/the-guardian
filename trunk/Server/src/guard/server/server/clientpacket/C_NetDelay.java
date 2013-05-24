package guard.server.server.clientpacket;

import guard.server.server.ClientProcess;
import guard.server.server.utils.NetDelayUtil;

import java.io.IOException;

public class C_NetDelay {
	public C_NetDelay(ClientProcess _client) {
		int delay = 0;
		try {
			delay = NetDelayUtil.netStatus(_client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// TODO 傳送延遲給所有遊戲室的玩家
}
