package guard.server.server.model.instance;

public class TimingTrapInstance extends TrapInstance {

	// 時間性陷阱存活時間
	private final float _lifeTime,_effectInterval;
	
	public boolean CheckExpire(float _gameTime){
		if(_gameTime - this.getCreateTime() -this.getBuildTime() > _lifeTime){
			return true;
		}
		return false;
	}

	public TimingTrapInstance(int _trapInstanceID, float _gameTime,
			float _buildTime, float _lifeTime,float _effectInterval) {
		super(_trapInstanceID, _gameTime, _buildTime);
		this._lifeTime = _lifeTime;
		this._effectInterval = _effectInterval;
	}
}
