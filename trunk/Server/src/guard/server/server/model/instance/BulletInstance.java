package guard.server.server.model.instance;

public class BulletInstance {

	// 開槍的玩者
	private final String _bulletBlonger;
	// 子彈被產生的時間
	private final float _bulletCreateTime;
	// 子彈的識別碼 - 用System.CurrentMills?
	private final long _bulletInstanceID;

	public long getBulletInstanceID() {
		return _bulletInstanceID;
	}

	public BulletInstance(String _bulletBlonger, long _bulletInstanceID,
			float _bulletCreateTime) {
		this._bulletBlonger = _bulletBlonger;
		this._bulletInstanceID = _bulletInstanceID;
		this._bulletCreateTime = _bulletCreateTime;
	}

	// 擊中後 ， 子彈銷毀
	public void Hit() {
		//
	}

	// 子彈過時 銷毀
	public boolean CheckTimeExpire(float _gameTime) {
		if (_gameTime - _bulletCreateTime >= 3) {
			return true;
		}
		return false;
	}

	public enum BulletState {
		Fire, Hit,
	}
}
