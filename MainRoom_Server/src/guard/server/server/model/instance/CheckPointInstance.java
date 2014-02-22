package guard.server.server.model.instance;

public class CheckPointInstance {

	private final int _checkPointID;

	public int getCheckPointID() {
		return _checkPointID;
	}

	private final String _owner;

	private final boolean _isStartPoint;

	public CheckPointInstance(int _checkPointID, String _owner,
			boolean _isStartPoint) {
		this._checkPointID = _checkPointID;
		this._owner = _owner;
		this._isStartPoint = _isStartPoint;
	}

	public boolean IsOwner(String _accountName) {
		return _owner == _accountName;
	}

	public boolean IsStartPoint() {
		return _isStartPoint;
	}
}
