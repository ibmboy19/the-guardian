package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_Logout {

	public static final int C_Logout_BackToLobby = 0;
	public static final int C_Logout_PlayerExit = 1;
	public static final int C_Logout_Logout = 2;

	public C_Logout(ClientProcess _client, String _packet) {

		PlayerInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}

		GameRoom room = pc.getRoom();
		GameInstance game = null;
		if (room != null) {
			game = room.getGame();
			if (game != null) {
				if (game.IsGaming()) {
					room.broadcastPacketToRoom(_packet + C_PacketSymbol
							+ pc.getAccountName());
					room.leaveRoom(pc);
				} else {
					room.broadcastPacketToRoom(_packet + C_PacketSymbol
							+ pc.getAccountName());
				}
			} else {
				room.broadcastPacketToRoom(_packet + C_PacketSymbol
						+ pc.getAccountName());
			}
		} else {
			pc.SendClientPacket(_packet + C_PacketSymbol + pc.getAccountName());
		}

	}
}
