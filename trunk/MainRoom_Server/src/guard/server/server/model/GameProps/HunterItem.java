package guard.server.server.model.GameProps;

import guard.server.server.model.instance.HunterInstance;

public class HunterItem extends Item implements Cloneable {
	public HunterItem() {
	}

	public HunterItem(int _itemID, String _name, int _iconID, int _price) {
		super(_itemID, _name, _iconID, _price);
	}

	

	/*public void UseItem(HunterInstance _hunter) {
		
	}*/
	
	public String toString(){
		return _itemID+","+_name+","+_iconID+","+_price;
	}
	
	@Override
	 public HunterItem clone() throws CloneNotSupportedException {
		HunterItem _cloneItem = (HunterItem) super.clone();
		return _cloneItem;
	 }

}
