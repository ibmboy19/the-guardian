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
	private TrapState _trapState = TrapState.Building;

	// 陷阱被產生的時間
	private final float _trapCreateTime;

	public float getCreateTime() {
		return _trapCreateTime;
	}

	private final float _buildTime;

	public float getBuildTime() {
		return _buildTime;
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

	// 陷阱被觸發
	public boolean TrapTrigged() {
		if (this._trapState != TrapState.BuildUp)
			return false;
		this._trapState = TrapState.Trigged;
		return true;
		// TODO 回傳陷阱狀態
	}

	/**
	 * 條件須同時成立 1.陷阱被啟用 2.陷阱被破壞/陷阱超過時間(如沼澤)/陷阱超出飛射範圍
	 * */
	public void DestroyTrapInstance() {
		if (this._trapState != TrapState.Trigged)
			return;
		// TODO 回傳陷阱狀態

	}

	public enum TrapState {
		Building, BuildUp, Trigged, Destroy
	}

}
