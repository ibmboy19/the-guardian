package guard.server.server.model.instance;

import static guard.server.server.clientpacket.C_Gold.C_Gold_Effect;
import static guard.server.server.clientpacket.C_Gold.C_Gold_Normal;
import static guard.server.server.clientpacket.ClientOpcodes.C_Gold;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Guardian;
import guard.server.server.model.GameRoom;

public class GuardianInstance extends WickedRoadPlayerInstance {

	/** 玩者 */
	private final PlayerInstance _pc;

	public PlayerInstance getActiveChar() {
		return _pc;
	}

	/** 使用的房間 */
	private final GameRoom _room;

	public GuardianInstance() {
		super();
		_room = null;
		_pc = null;
	}

	public GuardianInstance(GameRoom _room, PlayerInstance _pc) {
		super(_room.getMap().getGuardianInitGold());
		this._room = _room;
		this._pc = _pc;
	}

	/** 傷害獲得黃金 - guardian only */
	public void AcquireGold(int _dmg, int _rewardPerDmg) {
		int _acquireGoldAmount = _dmg * _rewardPerDmg;

		_gold += _acquireGoldAmount;
		// Send Packet : Acquire Gold
		_pc.SendClientPacket(C_Gold + C_PacketSymbol
				+ String.valueOf(C_Gold_Effect) + C_PacketSymbol
				+ String.valueOf(_pc.getWRPlayerInstance().getGold()));

	}

	/** 黃金獎勵 - 定期 - guardian only */
	public void RewardGold() {
		_gold += _room.getMap().getGuardianReward(_room.getGame());
		_pc.SendClientPacket(C_Gold + C_PacketSymbol
				+ String.valueOf(C_Gold_Normal) + C_PacketSymbol
				+ String.valueOf(getGold()));
	}
	
	public boolean CostGold(int _gold) {
		if (this._gold >= _gold) {
			this._gold -= _gold;
			_pc.SendClientPacket(C_Gold + C_PacketSymbol
					+ String.valueOf(C_Gold_Normal) + C_PacketSymbol
					+ String.valueOf(getGold()));
			return true;
		}
		return false;
	}

	/** 取得玩者資料 */
	public String getPlayerModelData() {
		String _data = String.valueOf(PlayerType_Guardian) + ","
				+ String.valueOf(_gold);
		return _data;
	}
}
