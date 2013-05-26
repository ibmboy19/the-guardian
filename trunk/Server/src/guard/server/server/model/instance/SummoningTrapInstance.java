package guard.server.server.model.instance;

public class SummoningTrapInstance extends TrapInstance {

	private int _currentHp, _maxHp;

	public SummoningTrapInstance(int _trapInstanceID, float _gameTime,
			float _buildTime, int _maxHp) {
		super(_trapInstanceID, _gameTime, _buildTime);
		this._maxHp = _maxHp;
		this._currentHp = _maxHp;
	}
}
