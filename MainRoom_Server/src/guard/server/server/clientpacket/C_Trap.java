package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_Trap {
	public static final int C_Trap_Build = 0;// 建造陷阱(G專用)
	public static final int C_Trap_BuildUp = 1;// 建造完成(伺服器偵測)
	public static final int C_Trap_Trigged = 2;// 陷阱觸發(依照陷阱屬性或獵人踩到)
	public static final int C_Trap_Destroy = 3;// 陷阱銷毀
	public static final int C_Trap_ApplyDamage = 4;// 陷阱傷害獵人
	public static final int C_Trap_Disable = 5;// 陷阱銷毀前置作業
	public static final int C_Trap_TurnAndAttack = 6;// 用於召喚型陷阱，攻擊目標時先確認資料
	public static final int C_Trap_BeAttacked = 7;

	public C_Trap(ClientProcess _client, String _packet) {

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
		case C_Trap_Build:
			// 獵人不能建造陷阱
			if (pc.IsHunter())
				return;
			game.CheckSlot(_packet);
			break;
		case C_Trap_Trigged:
			// 守護神不能觸發陷阱
			if (pc.IsGuardian())
				return;
			game.TrigTrap(_packet);
			break;
		case C_Trap_ApplyDamage:
			if (pc.IsGuardian())
				return;
			game.ApplyTrapDamage(_packet,
					(HunterInstance) pc.getWRPlayerInstance());
			break;
		case C_Trap_TurnAndAttack:
			game.SummoningTrapApplyAttack(_packet,
					(HunterInstance) pc.getWRPlayerInstance());
			break;
		default:
			return;
		}
	}
}
