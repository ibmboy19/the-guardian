package guard.server.server.model.GameProps;

public class Item {
	/** 描述Client端使用的物品資訊 */
	protected String _name;
	public String getName(){return _name;}
	protected int _itemID;
	protected int _iconID;
	protected int _price;
	public int getPrice(){return _price;}
	public Item(){
		
	}

	public Item(int _itemID,String _name,int _iconID,int _price){
		this._name = _name;
		this._itemID = _itemID;
		this._iconID = _iconID;
		this._price = _price;
	}
	

}
