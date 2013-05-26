package guard.server.server.model.instance;

public class TrapInstance {
	/** 遊戲中，Guardian放置的陷阱模組 */

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

	// 在場景中的陷阱的識別碼
	private final int _trapInstanceID;

	public int getTrapInstanceID() {
		return _trapInstanceID;
	}

	public TrapInstance(int _trapInstanceID, float _gameTime, float _buildTime) {
		this._trapInstanceID = _trapInstanceID;
		this._trapCreateTime = _gameTime;
		this._buildTime = _buildTime;
		this._trapState = TrapState.Building;
	}

	// 建造完成
	public void BuildUp(float _gameTime) {
		if (_trapState != TrapState.Building)
			return;
		if (_gameTime - _trapCreateTime < _buildTime)
			return;
		this._trapState = TrapState.BuildUp;
		// TODO 回傳陷阱狀態
	}

	// 被踩到/被打爆
	public void TrapTrigged() {
		if (this._trapState != TrapState.BuildUp)
			return;
		this._trapState = TrapState.Trigged;
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
