package guard.server.server.clientpacket;

import guard.server.server.ClientProcess;
import guard.server.server.model.instance.PlayerInstance;
import guard.server.server.utils.NetDelayUtil;

import java.io.IOException;

public class C_NetDelay {
	public C_NetDelay(ClientProcess _client) {
		PlayerInstance pc = _client.getActiveChar();
		if(pc == null){
			return;
		}
		int delay = 0;
		try {
			delay = NetDelayUtil.netStatus(_client);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(pc.isInRoom()){
			// TODO 傳送自己的Ping值給所有人
			pc.getRoom().broadcastPacketToRoom("");
		}
		
	}
	
}
