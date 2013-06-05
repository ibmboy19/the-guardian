package guard.server.server.model.instance;

import guard.server.server.model.instance.TrapInstance.TrapState;

public class DetonatedTrapInstance extends TrapInstance {

	private int _dmgHP;

	public DetonatedTrapInstance(int _slotID, int _key, float _gameTime,
			float _buildTime, int _dmgHP) {
		super(_slotID, _key, _gameTime, _buildTime);
		this._dmgHP = _dmgHP;
	}

	// 陷阱被觸發
	public boolean TrapTrigged(float gameTime) {
		if (this._trapState != TrapState.BuildUp)
			return false;
		this._trapState = TrapState.Trigged;

		SetupAutoDestroy(gameTime);

		return true;
		// TODO 回傳陷阱狀態
	}

	public int getDamageHP() {
		return _dmgHP;
	}
}
