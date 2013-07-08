package guard.server.server.model.instance;

public class WickedRoadPlayerInstance {

	protected boolean _isReady;

	public boolean IsReady() {
		return _isReady;
	}

	public void Ready() {
		_isReady = true;
	}

	protected int _gold;

	public int getGold() {
		return _gold;
	}

	public WickedRoadPlayerInstance() {
		_isReady = false;
	}

	public WickedRoadPlayerInstance(int _gold) {
		_isReady = false;
		this._gold = _gold;
	}

	public String getPlayerModelData() {
		return "";
	}
}
