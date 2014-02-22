package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.instance.PlayerInstance;

public class C_Chat {

	/**
	 * Chat Code -> 0 : Chat In Room 一般文字 , 1 : Chat In Room 系統文字 , 2 : Chat In
	 * Game 無線電系統
	 */
	public static final int C_Chat_ChatInRoomNormal = 0;
	public static final int C_Chat_ChatInRoomSystem = 1;
	public static final int C_Chat_ChatInGame = 2;
	public static final int C_Chat_ChatInLobby = 3;

	public C_Chat(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();

		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_Chat_ChatInRoomNormal:
		case C_Chat_ChatInRoomSystem:
		case C_Chat_ChatInGame:
			/** 檢查狀態 */
			if (!pc.isInRoom()) {
				return;
			}
			System.out.println(_packet);
			pc.getRoom().broadcastPacketToRoom(_packet);
			break;
		case C_Chat_ChatInLobby:
			for (PlayerInstance _pc : GuardWorld.getInstance().getAllPlayers()) {
				if (!_pc.isInRoom()) {
					_pc.SendClientPacket(_packet);
				}
			}
			break;
		}

	}
}
