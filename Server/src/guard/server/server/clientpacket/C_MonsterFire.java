package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_MonsterFire;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.BulletInstance;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.GuardianInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_MonsterFire {

	public static final int C_MonsterFire_Fire = 0;
	public static final int C_MonsterFire_Destroy = 1;

	/** 處理TD子彈銷毀碰撞銷毀 */
	public C_MonsterFire(ClientProcess _client, String _packet) {
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
		if (pc.getWRPlayerInstance() instanceof GuardianInstance)
			return;
		HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();

		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_MonsterFire_Fire:

			BulletInstance bullet = null;
			String bulletID = _packet.split(C_PacketSymbol)[2];

			bullet = game.getMonsterBullets(bulletID);
			if (bullet == null)
				return;

			if (bullet.IsHit())
				return;
			
			int dmg = Integer.valueOf(_packet.split(C_PacketSymbol)[3]);
			
			// Damage			
			bullet.Hit(hunter, dmg);
			//Calc Guardian Gold
			if (dmg < 0) {
				game.getGuardian().AcquireGold(Math.abs(dmg),
						game.getMap().getGuardianDmgReward());
			}
			// Destroy
			room.broadcastPacketToRoom(String.valueOf(C_MonsterFire)
					+ C_PacketSymbol + String.valueOf(C_MonsterFire_Destroy)
					+ C_PacketSymbol + bulletID);
			break;
		case C_MonsterFire_Destroy:
			room.broadcastPacketToRoom(_packet);
			break;
		}

	}
}
