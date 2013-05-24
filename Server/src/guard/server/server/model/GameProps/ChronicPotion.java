package guard.server.server.model.GameProps;

import guard.server.server.model.instance.HunterInstance;

public class ChronicPotion extends HunterItem {
	/** 持續性的藥水 */

	protected PotionType _potionType;
	protected float _effectDuration;

	public ChronicPotion() {
		super();
	}

	public ChronicPotion(int _itemID, String _name, int _iconID, int _price,
			int _potionType, float _effectDuration) {
		super(_itemID, _name, _iconID, _price);
		this._potionType = PotionType.values()[_potionType];
		this._effectDuration = _effectDuration;
	}

	public void UseItem(HunterInstance _hunter) {
		/**
		 * TODO Send Packet : C_HunterState
		 * 
		 * 
		 * */
	}

	public enum PotionType {
		Stamina, // 耐力藥水
		Stealth// 隱形藥水
	}
}
