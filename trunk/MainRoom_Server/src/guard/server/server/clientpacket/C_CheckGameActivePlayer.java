package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_CheckGameActivePlayer;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_CheckGameActivePlayer {
	public C_CheckGameActivePlayer(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;
		GameRoom room = pc.getRoom();
		if (room == null) {
			pc.SendClientPacket(String.valueOf(C_CheckGameActivePlayer)
					+ C_PacketSymbol + "0");
			return;
		}
		GameInstance game = room.getGame();
		if (game == null) {
			pc.SendClientPacket(String.valueOf(C_CheckGameActivePlayer)
					+ C_PacketSymbol + "0");
			return;
		}

		room.broadcastPacketToRoom(String.valueOf(C_CheckGameActivePlayer)
				+ C_PacketSymbol
				+ String.valueOf(room.get_membersList().size()));
	}
}
