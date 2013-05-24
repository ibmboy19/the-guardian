package guard.server.server.model.GameProps.Trap;

public class SummoningTrap extends Trap {
	/**
	 * 召喚型陷阱
	 * */

	private int _hp;// 陷阱的生命值
	

	public SummoningTrap() {
		super();
	}

	public SummoningTrap(int _itemID, String _name, int _iconID, int _price,
			float _buildingTime, int _trapSetupType, int _trapTriggerModelID,
			int _trapBoxModelID, int _hp) {
		super(_itemID, _name, _iconID, _price, _buildingTime, _trapSetupType,
				_trapTriggerModelID, _trapBoxModelID);
		this._hp = _hp;
	}
}
