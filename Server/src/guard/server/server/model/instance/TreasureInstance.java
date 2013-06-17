package guard.server.server.model.instance;

public class TreasureInstance {

	private WickedRoadPlayerInstance _wrPcOwner;

	public boolean IsOwner(WickedRoadPlayerInstance _wrPcOwner) {
		return this._wrPcOwner == _wrPcOwner;
		/*if (this._wrPcOwner instanceof GuardianInstance) {
			if (_wrPcOwner instanceof GuardianInstance) {
				return true;
			}
		}

		if (this._wrPcOwner instanceof HunterInstance) {
			if (_wrPcOwner instanceof HunterInstance) {
				return ((HunterInstance) (this._wrPcOwner)).getAccountName() == ((HunterInstance) (_wrPcOwner))
						.getAccountName();
			}
		}

		return false;*/
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
