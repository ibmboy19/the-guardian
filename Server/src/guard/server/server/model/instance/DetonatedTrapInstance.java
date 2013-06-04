package guard.server.server.model.instance;

public class DetonatedTrapInstance extends TrapInstance {

	private int _dmgHP;

	public DetonatedTrapInstance(int _slotID, int _key, float _gameTime,
			float _buildTime, int _dmgHP) {
		super(_slotID, _key, _gameTime, _buildTime);
		this._dmgHP = _dmgHP;
	}

	public int getDamageHP() {
		return _dmgHP;
	}
}
