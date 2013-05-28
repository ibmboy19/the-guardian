package guard.server.server.model.GameProps.Trap;

public class TimingTrap extends DetonatedTrap {

	/**
	 * 持續型陷阱
	 * */
	protected float _lifeTime;// 陷阱存活時間

	public float getLifeTime() {
		return _lifeTime;
	}

	protected float _effectInterval;// 陷阱作用的間隔

	public float getEffectInterval() {
		return _effectInterval;
	}

	public TimingTrap() {
		super();
	}

	public TimingTrap(int _itemID, String _name, int _iconID, int _price,
			float _buildingTime, int _trapSetupType, int _trapTriggerModelID,
			int _trapBoxModelID, float _effectInterval, float _lifeTime,
			int _dmgHp, int _dmgStamina, int _dmgGold, int _rubGold,
			int _slowDownMoveSpd) {
		super(_itemID, _name, _iconID, _price, _buildingTime, _trapSetupType,
				_trapTriggerModelID, _trapBoxModelID, _dmgHp, _dmgStamina,
				_dmgGold, _rubGold, _slowDownMoveSpd);
		this._effectInterval = _effectInterval;
		this._lifeTime = _lifeTime;
	}
}
