package guard.server.server.model.GameProps;

import guard.server.server.model.instance.HunterInstance;

public class InstantPotion extends HunterItem {
	/** 回復血量的藥水 */
	protected int _recoveryValue;

	public int getRecoveryValue(int _hunterMaxHP) {
		switch (_recoveryAmountType) {
		case Value:
			return _recoveryValue;
		case Rate:
			return (int)(_hunterMaxHP*((float)_recoveryValue/100));
		default:
			return _recoveryValue;
		}
	}

	protected RecoveryAmountType _recoveryAmountType;

	public InstantPotion() {
		super();
	}

	public InstantPotion(int _itemID, String _name, int _iconID, int _price,
			int _recoveryValue, int _recoveryAmountType) {
		super(_itemID, _name, _iconID, _price);
		this._recoveryValue = _recoveryValue;
		this._recoveryAmountType = RecoveryAmountType.values()[_recoveryAmountType];
	}

	public void UseItem(HunterInstance _hunter) {
		/**
		 * TODO Send Packet : C_HunterState
		 * 
		 * 
		 * */
	}

	// 回復量的依據。根據%回復，或是以值回復
	public enum RecoveryAmountType {
		Value, Rate
	}

}
