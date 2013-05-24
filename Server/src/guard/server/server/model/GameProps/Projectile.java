package guard.server.server.model.GameProps;

import guard.server.server.model.instance.HunterInstance;

public class Projectile extends HunterItem {
	/** 投擲類道具 */
	protected int _modelID;

	public Projectile(int _itemID, String _name, int _iconID, int _price) {
		super(_itemID, _name, _iconID, _price);
	}

	public void UseItem(HunterInstance _hunter) {
		/**
		 * TODO Send Packet : C_NewGameObject
		 * 
		 * 
		 * */
	}
}
