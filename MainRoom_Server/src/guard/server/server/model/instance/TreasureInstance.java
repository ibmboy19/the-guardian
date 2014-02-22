package guard.server.server.model.instance;

public class TreasureInstance {

	private WickedRoadPlayerInstance _wrPcOwner;

	public boolean IsOwner(WickedRoadPlayerInstance _wrPcOwner) {
		return this._wrPcOwner == _wrPcOwner;
	}
	
	public String getTreasurePacket(){
		String _packet = "";
		
		if(_wrPcOwner instanceof HunterInstance){
			_packet = "1," + ((HunterInstance)_wrPcOwner).getAccountName();
		}else {
			_packet = "0,0";
		}
		
		return _packet;
	}
	
	// Init Belonger must be Guardian
	public TreasureInstance(GuardianInstance _guardian) {
		_wrPcOwner = _guardian;
	}

	// Treasure Rubbed - Belonger -> some one Hunter
	public void Rubbed(HunterInstance _hunter) {
		_wrPcOwner = _hunter;
	}

	// Treasure Lost - Belonger -> Guardian
	public void Lost(GuardianInstance _guardian) {
		_wrPcOwner = _guardian;
	}
}
