package guard.server.server.model.instance;

public class TrapInstance {
	/** 遊戲中，Guardian放置的陷阱模組 */

	// 在game中的編號
	private final int _slotID, _slotKey;

	public int getSlotID() {
		return _slotID;
	}

	public int getSlotKey() {
		return _slotKey;
	}

	// 陷阱狀態
	protected TrapState _trapState = TrapState.Building;

	// 陷阱被產生的時間
	private final float _trapCreateTime;

	public float getCreateTime() {
		return _trapCreateTime;
	}

	private final float _buildTime;

	public float getBuildTime() {
		return _buildTime;
	}
	//陷阱銷毀
	private float _trapDestroyTime;
	
	public float getTrapDestroyTime(){
		return _trapDestroyTime;
	}

	public TrapInstance(int _slotID, int _slotKey, float _gameTime,
			float _buildTime) {
		this._slotID = _slotID;
		this._slotKey = _slotKey;
		this._trapCreateTime = _gameTime;
		this._buildTime = _buildTime;
		this._trapState = TrapState.Building;
	}

	// 建造完成
	public boolean IsBuildUp(float _gameTime) {
		if (_trapState != TrapState.Building)
			return false;
		if (_gameTime - _trapCreateTime < _buildTime)
			return false;
		this._trapState = TrapState.BuildUp;
		return true;
		// TODO 回傳陷阱狀態

	}

	public boolean IsAutoDestroy(){
		return this._trapState == TrapState.Destroy;
	}
	
	// 陷阱被觸發
	public boolean TrapTrigged() {
		if (this._trapState != TrapState.BuildUp)
			return false;
		this._trapState = TrapState.Trigged;
		
		return true;
		// TODO 回傳陷阱狀態
	}
	
	//陷阱若觸發或毀壞，會進入自動銷毀狀態
	public boolean SetupAutoDestroy(float gameTime) {
		// TODO 回傳陷阱狀態
		if(this._trapState == TrapState.Destroy)
			return false;
		this._trapState = TrapState.Destroy;
		_trapDestroyTime = gameTime+5;
		return true;
	}
	//陷阱過期會自動移除
	public boolean CanAutoDestroyTrap(float gameTime){
		if(_trapState == TrapState.Destroy && gameTime > _trapDestroyTime){
			return true;
		}
		return false;
	}

	public enum TrapState {
		Building, BuildUp, Trigged, Destroy
	}

}
