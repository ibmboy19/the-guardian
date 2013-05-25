package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_HunterFire {
	//玩家開火時
	public static final int C_HunterFire_Fire = 0;
	//子彈命中其他玩家 或 可攻擊的物件.其他碰撞物
	public static final int C_HunterFire_Hit = 1;
	//過時 銷毀
	public static final int C_HunterFire_Destroy = 2;

	public C_HunterFire(ClientProcess _client, String _packet) {
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
		case C_HunterFire_Fire:
			// 玩家開火 產生子彈
			game.HunterFire(pc, _packet.split(C_PacketSymbol)[2],
					_packet.split(C_PacketSymbol)[3]);
			// room.broadcastPacketToRoom(_packet);
			break;
		case C_HunterFire_Hit:
			// 發射的子彈打到對象，算傷害 刪除
			break;
		}

	}
}
