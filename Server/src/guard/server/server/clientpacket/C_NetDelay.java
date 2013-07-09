package guard.server.server.clientpacket;

import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.PlayerInstance;
import guard.server.server.utils.NetDelayUtil;

public class C_NetDelay {
	public C_NetDelay(ClientProcess _client) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}

		GameRoom room = pc.getRoom();
		if (room == null)
			return;

		new NetDelayUtil(_client);
		
	}

}
