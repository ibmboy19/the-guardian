package guard.server.server.model.instance;

public class CheckPointInstance {

	private final int _checkPointID;

	public int getCheckPointID() {
		return _checkPointID;
	}
	
	private final String _belonger;

	public CheckPointInstance(int _checkPointID,String _belonger) {
		this._checkPointID = _checkPointID;
		this._belonger = _belonger;
	}
	public boolean IsBelonger(String _accountName){
		return _belonger == _accountName;
	}
}
