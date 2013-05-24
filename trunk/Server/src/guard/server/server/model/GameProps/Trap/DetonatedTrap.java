package guard.server.server.model.GameProps.Trap;

public class DetonatedTrap extends Trap {

	/**
	 * 引爆型陷阱
	 * */

	protected int _dmgHp;
	protected int _dmgStamina;
	protected int _dmgGold;
	protected int _rubGold;
	protected int _slowDownMoveSpd;

	public DetonatedTrap() {
		super();
	}

	public DetonatedTrap(int _itemID, String _name, int _iconID, int _price,
			float _buildingTime, int _trapSetupType, int _trapTriggerModelID,
			int _trapBoxModelID, int _dmgHp, int _dmgStamina, int _dmgGold,
			int _rubGold, int _slowDownMoveSpd) {
		super(_itemID, _name, _iconID, _price, _buildingTime, _trapSetupType,
				_trapTriggerModelID, _trapBoxModelID);
		this._dmgHp = _dmgHp;
		this._dmgStamina = _dmgStamina;
		this._dmgGold = _dmgGold;
		this._rubGold = _rubGold;
		this._slowDownMoveSpd = _slowDownMoveSpd;
	}
}
