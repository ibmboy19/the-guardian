package guard.server.server.model.instance;

import static guard.server.server.PacketType.C_Packet;
import static guard.server.server.PacketType.S_Packet;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;

public class PlayerInstance {
	private ClientProcess _client;
	private GameRoom _room;
	private String _accountName;
	
	public static final int PlayerType_Hunter = 0;
	public static final int PlayerType_Guardian = 1;
	private int _playerType;// 0: Hunter; 1: Guardian

	public int getPlayerType() {
		return _playerType;
	}
	public boolean IsHunter(){
		return _wrPlayerInstance instanceof HunterInstance;
	}
	public boolean IsGuardian(){
		return _wrPlayerInstance instanceof GuardianInstance;
	}

	public void SwitchPlayerType() {
		switch(_playerType){
		case PlayerType_Hunter:
			_playerType = PlayerType_Guardian;
			break;
		case PlayerType_Guardian:
			_playerType = PlayerType_Hunter;
			break;
		}
	}

	public void SwitchPlayerType(int _index) {
		_playerType = _index;
	}

	/**等待室 - 準備開始*/
	private boolean _isReady;

	public void Ready() {
		_isReady = true;
	}

	public boolean IsReady() {
		return _isReady;
	}
	
	/**遊戲地圖載入 - 準備開始進行遊戲*/
	private boolean _isLoadMapDone;
	
	public void LoadMapDone(){
		_isLoadMapDone = true;
	}
	
	public boolean IsLoadMapDone(){
		return _isLoadMapDone;
	}

	/**玩家在遊戲中的角色模組*/
	private WickedRoadPlayerInstance _wrPlayerInstance;

	public WickedRoadPlayerInstance getWRPlayerInstance() {
		return _wrPlayerInstance;
	}

	public void setwrPlayerInstance(WickedRoadPlayerInstance _wrPlayerInstance) {
		this._wrPlayerInstance = _wrPlayerInstance;
	}

	/**帳號相關*/
	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(String s) {
		_accountName = s;
	}

	/**封包*/
	public void SendServerPacket(String packet) {
		_client.getWr().println(S_Packet + C_PacketSymbol + packet);
	}

	public void SendClientPacket(String packet) {
		_client.getWr().println(C_Packet + C_PacketSymbol + packet);
	}

	public PlayerInstance(ClientProcess _client) {
		this._client = _client;
	}

	public ClientProcess getNetConnection() {
		return _client;
	}

	public void setNetConnection(ClientProcess client) {
		_client = client;
	}

	public boolean isInRoom() {
		return getRoom() != null;
	}

	public GameRoom getRoom() {
		return _room;
	}

	public void setRoom(GameRoom r) {
		_room = r;
	}

	/** 玩家離開房間時，重新設置狀態 */
	public void ResetState() {
		setRoom(null);
		setwrPlayerInstance(null);
		_isReady = false;
		_isLoadMapDone = false;
	}

	/** 取得玩家列表用
	 * @return accountName + ping
	 *  */
	public String getPlayerInfoPacket() {
		String _packet;
		_packet = _accountName;
		return _packet;
	}
	/**取得玩家在遊戲中的角色資訊
	 * @return guardian 
	 * @return hunter 
	 * */
	public String getPlayerModelDataPacket(){
		if(_wrPlayerInstance == null)
			return "";		
		return _wrPlayerInstance.getPlayerModelData();
	}
}
