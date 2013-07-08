package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.PlayerInstance;

public class C_SwitchPlayerType {
	public C_SwitchPlayerType(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		GameRoom room = pc.getRoom();
		if (room == null) {
			return;
		}
		if (room.IsLocked())
			return;
		pc.SwitchPlayerType();
		_packet += pc.getAccountName() + C_PacketSymbol + pc.getPlayerType();
		room.broadcastPacketToRoom(_packet);

	}
}
