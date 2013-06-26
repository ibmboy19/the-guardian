package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.GuardianInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_UpgradeObject {
	public C_UpgradeObject(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}
		GameRoom room = pc.getRoom();
		if (room == null) {
			return;
		}
		GameInstance game = room.getGame();
		if (game == null) {
			return;
		}
		if (pc.IsHunter())
			return;
		
		GuardianInstance guardian = (GuardianInstance)pc.getWRPlayerInstance();
		
		guardian.CostGold(Integer.valueOf(_packet.split(C_PacketSymbol)[3]));
		
		room.broadcastPacketToRoom(_packet);
		
	}
}
