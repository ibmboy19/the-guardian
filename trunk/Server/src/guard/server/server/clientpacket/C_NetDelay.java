package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_NetDelay;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.PlayerInstance;
import guard.server.server.utils.NetDelayUtil;

import java.io.IOException;

public class C_NetDelay {
	public C_NetDelay(ClientProcess _client) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}

		GameRoom room = pc.getRoom();
		if (room == null)
			return;

		int delay = 0;
		try {
			delay = NetDelayUtil.netStatus(_client);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO 傳送自己的Ping值給所有人
		pc.getRoom().broadcastPacketToRoom(
				String.valueOf(C_NetDelay) + C_PacketSymbol
						+ pc.getAccountName() + C_PacketSymbol
						+ String.valueOf(delay));

	}

}
