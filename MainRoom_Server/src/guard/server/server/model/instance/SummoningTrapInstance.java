package guard.server.server.model.instance;

public class SummoningTrapInstance extends TrapInstance {

	private int _currentHp, _maxHp;
	public int getCurrentHp(){return _currentHp;}
	
	
	public void ApplyDamage(int _dmg){
		_currentHp -= _dmg;
		_currentHp = Math.max(_currentHp, 0);
	}
	
	public boolean IsDead(){
		return _currentHp == 0;
	}
	
	//Attack CD
	private float _lastAttackTime;
	public boolean CanAttack(float gameTime){
		if(gameTime - _lastAttackTime > .3f){
			_lastAttackTime = gameTime;
			return true;
		}
		return false;
	}

	public SummoningTrapInstance(int _slotID, int _key, float _gameTime,
			float _buildTime, int _maxHp) {
		super(_slotID, _key, _gameTime, _buildTime);
		this._maxHp = _maxHp;
		this._currentHp = _maxHp;
	}
}
