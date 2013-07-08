package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_Treasure;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_Treasure {

	public static final int C_Treasure_RubbedTreasure = 0;
	public static final int C_Treasure_TreasureReturn = 1;

	public C_Treasure(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;

		GameRoom room = pc.getRoom();
		if (room == null)
			return;

		GameInstance game = room.getGame();
		if (game == null)
			return;

		if (pc.IsGuardian())
			return;

		HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();

		// 死人不會搶寶藏
		if (hunter.IsDead())
			return;

		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_Treasure_RubbedTreasure:

			game.getTreasure().Rubbed(hunter);

			// TODO Send Packet 搶奪寶藏

			room.broadcastPacketToRoom(String.valueOf(C_Treasure)
					+ C_PacketSymbol
					+ String.valueOf(C_Treasure_RubbedTreasure)
					+ C_PacketSymbol + pc.getAccountName());

			break;
		}

	}
}
