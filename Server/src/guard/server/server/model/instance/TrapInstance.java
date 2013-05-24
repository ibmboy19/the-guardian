package guard.server.server.model.instance;

public class TrapInstance {
	/**遊戲中，Guardian放置的陷阱模組*/
	
	public static final int TrapState_Building = 0;
	public static final int TrapState_Done = 1;
	
	private int _trapState = TrapState_Building;
	
	//陷阱箱是否被觸發
	private boolean _isEnable = false;
	//在場景中的陷阱的識別碼
	private final int _trapInstanceID;
	public int getTrapInstanceID(){
		return _trapInstanceID;
	}
	
	public TrapInstance(int _trapInstanceID){
		this._trapState = TrapState_Building;
		this._isEnable = false;
		this._trapInstanceID = _trapInstanceID;
	}
	
	/**踩到陷阱*/
	public void IsTrigged(){
		if(_trapState == TrapState_Building)return;
		if(_isEnable)return;
		this._isEnable = true;
		//TODO 回傳給該對象陷阱已被啟用訊息
		
	}
	
	/** 條件須同時成立
	 * 	1.陷阱被啟用
	 * 	2.陷阱被破壞/陷阱超過時間(如沼澤)/陷阱超出飛射範圍
	 * */
	public void DestroyTrapInstance(){
		//TODO 從列表中刪除陷阱
		
	}
	
	

}
