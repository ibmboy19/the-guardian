package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_Projectile {
	public static final int C_Projectile_Request = 0;
	public static final int C_Projectile_Spawn = 1;

	public C_Projectile(ClientProcess _client, String _packet) {
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
		if (pc.IsGuardian())
			return;
		
		HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();
		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_Projectile_Request:
			break;
		case C_Projectile_Spawn:
			room.broadcastPacketToRoom(_packet);
			System.out.println(_packet);
			break;
		}
	}

}
