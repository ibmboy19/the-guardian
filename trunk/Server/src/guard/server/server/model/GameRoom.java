package guard.server.server.model;

import static guard.server.server.clientpacket.C_JoinRoom.C_JoinRoom_OtherJoin;
import static guard.server.server.clientpacket.C_JoinRoom.C_JoinRoom_PCJoin;
import static guard.server.server.clientpacket.C_JoinRoom.getJoinFailedPacket;
import static guard.server.server.clientpacket.C_LeaveRoom.C_LeaveRoom_BreakUp;
import static guard.server.server.clientpacket.C_LeaveRoom.C_LeaveRoom_OtherLeave;
import static guard.server.server.clientpacket.C_LeaveRoom.C_LeaveRoom_PCLeave;
import static guard.server.server.clientpacket.C_Logout.C_Logout_BackToLobby;
import static guard.server.server.clientpacket.C_RoomReady.C_RoomReady_Ready;
import static guard.server.server.clientpacket.ClientOpcodes.C_GameOver;
import static guard.server.server.clientpacket.ClientOpcodes.C_JoinRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_LeaveRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_Logout;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_RoomReady;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Guardian;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Hunter;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;
import guard.server.server.utils.collections.Lists;

import java.util.List;
import java.util.Random;

/**
 * 遊戲室控管類別
 */
public class GameRoom {
	/** 房間成員清單 */
	private List<PlayerInstance> _membersList = Lists.newList();

	/** 室長 */
	private PlayerInstance _leader = null;
	/** 房名 */
	private String _roomName;
	/** 遊戲室人數上限 */
	private int _maxPcCount = 4;

	/** 遊戲實例 */
	private GameInstance _game;

	public GameMap getMap() {
		return _game.getMap();
	}

	/** 房間鎖 - 若準備開始，會鎖房間 */
	private boolean _isLock = false;

	public boolean IsLocked() {
		return _isLock;
	}

	public void LockRoom() {
		_isLock = true;
	}

	public void CheckReadyState() {
		/**
		 * @purpose :檢查準備狀態
		 * @Condition :，若所有成員都為Ready且房間人數已滿。 TODO 鎖房間，準備開始
		 * */
		if (isVacancy()) {// 人數不滿，return
			return;
		}
		boolean _isRoomReady = true;
		for (PlayerInstance _member : getMembers()) {
			_isRoomReady &= _member.IsReady();
		}
		if (_isRoomReady) {
			LockRoom();
			// System.out.println("房間準備開始...");
			/**
			 * TODO 分配Guardian與Hunter Step1 : 計算Guardian數量 Step2 :
			 * 若Guardian數量不為1，計算。否則直接回傳 *
			 * */

			// TODO Step 1 計算Guardian數量
			int _gCount = 0;
			for (PlayerInstance _member : getMembers()) {
				if (_member.getPlayerType() == 1) {
					_gCount++;
				}
			}
			// TODO Step2 分配Guardian
			if (_gCount != 1) {
				// 隨機挑選
				if (_gCount == 0) {
					Random _rand = new Random();
					int _index = _rand.nextInt(_maxPcCount);
					_membersList.get(_index).SwitchPlayerType(
							PlayerType_Guardian);

				}
				// 從候選中挑選
				else {
					List<Integer> _candidate = Lists.newList();
					for (int cnt = 0; cnt < getMembers().length; cnt++) {
						if (getMembers()[cnt].getPlayerType() == PlayerType_Guardian) {
							_candidate.add(cnt);
						}
					}
					Random _rand = new Random();
					int _index = _rand.nextInt(_candidate.size());
					for (int cnt = 0; cnt < _candidate.size(); cnt++) {
						if (_index != cnt) {
							_membersList.get(_candidate.get(cnt))
									.SwitchPlayerType(PlayerType_Hunter);
						} else {
							_membersList.get(_candidate.get(cnt))
									.SwitchPlayerType(PlayerType_Guardian);
						}
					}

				}

			}

			// TODO 回傳分配結果封包
			String _packet = C_RoomReady + C_PacketSymbol
					+ String.valueOf(C_RoomReady_Ready);
			for (PlayerInstance _member : getMembers()) {
				_packet += C_PacketSymbol + _member.getAccountName()
						+ C_PacketSymbol
						+ String.valueOf(_member.getPlayerType());
			}
			broadcastPacketToRoom(_packet);
		}
	}

	/**
	 * Format: Map Info :
	 * (roomName,hostID,mapName,checkCode,gameMode,maxPCCount,
	 * playTime,hunterLives) ItemList : (,,,,,);(,,,,,);(,,,,,)...... TrapList :
	 * (,,,,,);(,,,,,);(,,,,,)......
	 **/
	public GameRoom(String _roomName, PlayerInstance _leader, int _maxPcCount,
			GameMap _map, GameInstance _game) {
		this._roomName = _roomName;
		this._maxPcCount = _maxPcCount;
		/* 遊戲開始程序 */
		// TODO 遊戲開始後，產生遊戲物件

		this._game = _game;

		setLeader(_leader);
		_membersList.add(_leader);
		_leader.setRoom(this);
		connectionDetectThread.start();
		// System.out.println("創造一間" + _maxPcCount + "人的房間");
	}

	/**
	 * 加入遊戲房
	 */
	public void joinRoom(PlayerInstance pc) {
		String _packet = "";
		if (pc == null) {
			throw new NullPointerException();
		}
		if (!this.isVacancy() || _membersList.contains(pc)) {
			// 房間已滿或已在房間內，回傳加入失敗訊息
			pc.SendClientPacket(getJoinFailedPacket());
			return;
		} else if (_membersList.isEmpty()) {
			// 遊戲室是空的
			setLeader(pc);
		}
		_membersList.add(pc);
		pc.setRoom(this);
		// TODO Send Join Room Packet
		for (PlayerInstance member : getMembers()) {
			if (pc != member) {
				// TODO 通知其他人有玩家加入
				_packet = String.valueOf(C_JoinRoom) + C_PacketSymbol
						+ String.valueOf(C_JoinRoom_OtherJoin) + C_PacketSymbol
						+ pc.getAccountName() + C_PacketSymbol
						+ pc.getPlayerType();
			} else {
				// TODO 傳送加入房間訊息，房間資訊，其他玩者
				_packet = String.valueOf(C_JoinRoom) + C_PacketSymbol
						+ String.valueOf(C_JoinRoom_PCJoin) + C_PacketSymbol
						+ getRoomInfoPacket();
			}
			member.SendClientPacket(_packet);
		}
	}

	/**
	 * 離開房間(處理函式) 不要使用
	 */
	private void leaveMenber(PlayerInstance pc) {
		if (!_membersList.contains(pc)) {
			return;
		}

		_membersList.remove(pc);
		pc.ResetState();
		// TODO Send Leave Room Packet
		// 告知PC離開房間
		pc.SendClientPacket(C_LeaveRoom + C_PacketSymbol
				+ String.valueOf(C_LeaveRoom_OtherLeave));
		// 告知剩餘成員，有一個PC離開
		for (PlayerInstance member : getMembers()) {
			member.SendClientPacket(C_LeaveRoom + C_PacketSymbol
					+ String.valueOf(C_LeaveRoom_PCLeave) + C_PacketSymbol
					+ pc.getAccountName());
		}
	}
	/**
	 * 遊戲結束
	 * */
	public void GameOver(){
		
		this.breakup();
		this._game.cancel();
	}

	/**
	 * 是否還有空間
	 */
	public boolean isVacancy() {
		return _membersList.size() < _maxPcCount;
	}

	public int getVacancy() {
		return _maxPcCount - _membersList.size();
	}

	/** 取得該房間的簡易資訊 */
	public String getGameRoomInfoPacket() {
		String _packet = _roomName + "," + _leader.getAccountName() + ","
				+ _game.getMap().getMapName() + "," + _membersList.size() + ","
				+ _maxPcCount + ","
				+ String.valueOf(_game.getMap().getGameMode()) + ","
				+ String.valueOf(_game.getMap().getGamePlayTime()) + ","
				+ String.valueOf(_game.getMap().getHunterLives());
		return _packet;
	}

	public boolean isMember(PlayerInstance pc) {
		return _membersList.contains(pc);
	}

	private void setLeader(PlayerInstance pc) {
		_leader = pc;
	}

	public PlayerInstance getLeader() {
		return _leader;
	}

	public boolean isLeader(PlayerInstance pc) {
		return pc.getAccountName() == _leader.getAccountName();
	}

	public String getMembersNameList() {
		String _result = new String("");
		for (PlayerInstance pc : _membersList) {
			_result = _result + pc.getAccountName() + " ";
		}
		return _result;
	}

	public List<PlayerInstance> get_membersList() {
		return _membersList;
	}

	/**
	 * 解散遊戲房
	 */
	private void breakup() {
		// 房間即將被銷毀，不必通知他人玩者離開，只回傳一個房間解散訊息。
		// PlayerInstance[] members = getMembers();
		for (PlayerInstance member : _membersList) {
			member.ResetState();
			member.SendClientPacket(String.valueOf(C_LeaveRoom + C_PacketSymbol
					+ String.valueOf(C_LeaveRoom_BreakUp)));
		}

		GuardWorld.getInstance().RemoveRoom(this);
	}

	/**
	 * 傳遞室長 - 不支援，請勿使用
	 * 
	 * @param pc
	 */
	public void passLeader(PlayerInstance pc) {
		pc.getRoom().setLeader(pc);
		// TODO 傳遞室長的封包
		// broadcastPacketToRoom();
	}

	/**
	 * 離開房間主要函式
	 * 
	 * @param pc
	 */
	public void leaveRoom(PlayerInstance pc) {
		if (isLeader(pc)) {
			// 如果是房主離開，就解散
			breakup();
		} else if (pc.IsGuardian()) {
			breakup();
		} else {
			leaveMenber(pc);
			// TODO 離房的封包 - 通知剩下的玩家
			// broadcastPacketToRoom();
		}
	}

	/** 暫時不支援 */
	public void kickMember(PlayerInstance pc) {
		if (getNumOfMembers() == 2) {
			breakup();
		} else {
			leaveRoom(pc);
			// TODO 剔除的封包
			// broadcastPacketToRoom();
		}
	}

	public PlayerInstance[] getMembers() {
		return _membersList.toArray(new PlayerInstance[_membersList.size()]);
	}

	public int getNumOfMembers() {
		return _membersList.size();
	}

	public String getRoomName() {
		return _roomName;
	}

	public void setRoomName(String roomName) {
		_roomName = roomName;
	}

	public GameInstance getGame() {
		return _game;
	}

	/**
	 * 遊戲房廣播
	 */
	public void broadcastPacketToRoom(String packet) {
		for (PlayerInstance member : getMembers()) {
			member.SendClientPacket(packet);
		}
	}

	private String getRoomInfoPacket() {
		String _ret = "";
		_ret = _roomName + C_PacketSymbol + _game.getMap().getMapName()
				+ C_PacketSymbol + _game.getMap().getGameMode()
				+ C_PacketSymbol + _game.getMap().getGamePlayTime()
				+ C_PacketSymbol + _game.getMap().getHunterLives()
				+ C_PacketSymbol + _maxPcCount + C_PacketSymbol
				+ _leader.getAccountName() + C_PacketSymbol
				+ _leader.getPlayerType();

		for (PlayerInstance member : getMembers()) {

			if (member != _leader)
				_ret += C_PacketSymbol + member.getAccountName()
						+ C_PacketSymbol + member.getPlayerType();

		}
		return _ret;
	}

	/**
	 * 連線偵測執行緒
	 */
	private Thread connectionDetectThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (!gameIsOver()) {
				for (PlayerInstance pc : _membersList) {
					if (pc.getNetConnection().get_csocket().isClosed()) {
						if (pc.isInRoom()) {
							// TODO 斷線處理
							if (pc.getRoom().getGame().IsGaming()) {

								if (pc == _leader || pc.IsGuardian()) {
									for (PlayerInstance _member : _membersList) {
										_member.SendClientPacket(String
												.valueOf(C_Logout)
												+ C_PacketSymbol
												+ String.valueOf(C_Logout_BackToLobby)
												+ C_PacketSymbol
												+ pc.getAccountName());// _member.getRoom().getGame().Logout(_member,C_Logout_BackToLobby);
									}
									breakup();
								}

							} else {
								pc.getRoom().leaveRoom(pc);
							}
						}
						GuardWorld.getInstance().RemovePlayer(pc);
					}
				}
			}
			connectionDetectThread.interrupt();
		}

		private boolean gameIsOver() {
			return _leader.getRoom().getGame().IsGameOver();
		}
	});

}
