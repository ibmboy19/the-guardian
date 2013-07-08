package guard.server.server.model.instance;

public class BulletInstance {

	// 開槍的玩者
	private final String _bulletOwner;
	public String getOwner(){return _bulletOwner;}
	// 子彈被產生的時間
	private final float _bulletCreateTime;
	// 子彈的識別碼 - 用System.CurrentMills?
	private final String _bulletInstanceID;
	private boolean _isHit;

	public boolean IsHit() {
		return _isHit;
	}

	public String getBulletInstanceID() {
		return _bulletInstanceID;
	}

	public BulletInstance(String _bulletOwner, String _bulletInstanceID,
			float _bulletCreateTime) {
		this._bulletOwner = _bulletOwner;
		this._bulletInstanceID = _bulletInstanceID;
		this._bulletCreateTime = _bulletCreateTime;
	}

	// 擊中後 ， 子彈銷毀
	public synchronized void Hit(HunterInstance _hunter,int _dmg) {
		//
		if(_hunter.getAccountName() == _bulletOwner)
			return;
		_isHit = true;
		_hunter.ApplyHP(_dmg);
		
	}
	//擊中其他
	public void Hit(){
		_isHit = true;
	}

	// 子彈過時 銷毀
	public boolean CheckExpire(float _gameTime) {
		if (_gameTime - _bulletCreateTime >= 3) {
			return true;
		}
		return false;
	}

	public enum BulletState {
		Fire, Hit,
	}
}
