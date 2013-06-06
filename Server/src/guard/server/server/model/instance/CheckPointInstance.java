package guard.server.server.model.instance;

public class CheckPointInstance {

	private final int _checkPointID;

	public int getCheckPointID() {
		return _checkPointID;
	}
	
	private final String _belonger;
	
	private final boolean _isStartPoint;

	public CheckPointInstance(int _checkPointID,String _belonger,boolean _isStartPoint) {
		this._checkPointID = _checkPointID;
		this._belonger = _belonger;
		this._isStartPoint = _isStartPoint;
	}
	public boolean IsBelonger(String _accountName){
		return _belonger == _accountName;
	}
	public boolean IsStartPoint(){
		return _isStartPoint;
	}
}
