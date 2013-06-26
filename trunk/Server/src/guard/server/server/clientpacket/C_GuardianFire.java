package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_GuardianFire;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.BulletInstance;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_GuardianFire {

	public static final int C_GuardianFire_Init = 0;
	public static final int C_GuardianFire_Fire = 1;
	public static final int C_GuardianFire_Hit = 2;
	public static final int C_GuardianFire_Destroy = 3;

	public C_GuardianFire(ClientProcess _client, String _packet) {
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

		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_GuardianFire_Fire:
			String _bulletID = String.valueOf(game.getTime());
			game.GuardianFire(_bulletID, _packet.split(C_PacketSymbol)[2],
					_packet.split(C_PacketSymbol)[3]);
			break;
		case C_GuardianFire_Hit:
			BulletInstance bullet = null;
			String bulletID;
			bulletID = _packet.split(C_PacketSymbol)[2];
			bullet = game.getGuardainBullets(bulletID);
			if (bullet == null)
				return;
			if (bullet.IsHit())
				return;
			if (pc.IsGuardian())
				return;
			HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();
			bullet.Hit(hunter,
					Integer.valueOf(_packet.split(C_PacketSymbol)[3]));
			room.broadcastPacketToRoom(String.valueOf(C_GuardianFire)
					+ C_PacketSymbol + String.valueOf(C_GuardianFire_Destroy)
					+ C_PacketSymbol + bulletID);
			break;
		}

	}
}
