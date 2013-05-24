package guard.server.server.model.GameProps.Trap;

import guard.server.server.model.GameProps.Item;

public class Trap extends Item {
	/** 描述Client端使用的Trap資訊 */
	// 陷阱觸發器 模組編號
	protected int _trapTriggerModelID;
	// 陷阱箱 模組編號
	protected int _trapBoxModelID;

	// 陷阱設置的方式
	protected TrapSetupType _trapSetupType;

	// 陷阱建造時間
	protected float _buildingTime;

	public Trap() {
		super();
	}

	public Trap(int _itemID, String _name, int _iconID, int _price,
			float _buildingTime, int _trapSetupType, int _trapTriggerModelID,
			int _trapBoxModelID) {
		super(_itemID, _name, _iconID, _price);
		this._buildingTime = _buildingTime;
		this._trapSetupType = TrapSetupType.values()[_trapSetupType];
		this._trapTriggerModelID = _trapTriggerModelID;
		this._trapBoxModelID = _trapBoxModelID;
	}

	public enum TrapSetupType {
		Around, Top, Bottom, Center
	}
}
