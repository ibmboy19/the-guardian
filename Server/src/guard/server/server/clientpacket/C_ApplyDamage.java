package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_ApplyDamage {

	public static final int C_ApplyDamage_HP = 0;
	public static final int C_ApplyDamage_Stamina = 1;

	public C_ApplyDamage(ClientProcess _client, String _packet) {
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

		String _applyData = _packet.split(C_PacketSymbol)[1];
		for (String _data : _applyData.split(";")) {
			switch (Integer.valueOf(_data.split(",")[0])) {
			case C_ApplyDamage_HP:
				// positive
				if (Integer.valueOf(_data.split(",")[1]) == 1) {
					hunter.ApplyHP(Math.abs(Integer.valueOf(Integer
							.valueOf(_data.split(",")[2]))));
				}
				// negative
				else {
					int _damageValue = hunter.ApplyHP(-Math.abs(Integer
							.valueOf(Integer.valueOf(_data.split(",")[2]))));

					game.getGuardian().AcquireGold(Math.abs(_damageValue),
							game.getMap().getGuardianDmgReward());
				}
				break;
			case C_ApplyDamage_Stamina:
				if (Integer.valueOf(_data.split(",")[1]) == 1) {
					hunter.ApplyCostStamina(Math.abs(Float.valueOf(_data
							.split(",")[2])));
				} else {
					hunter.ApplyCostStamina(-Math.abs(Float.valueOf(_data
							.split(",")[2])));
				}
				break;
			}
		}

	}
}
