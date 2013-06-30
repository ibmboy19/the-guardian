package guard.server.server.model.instance;

import static guard.server.server.clientpacket.C_Chat.C_Chat_ChatInRoomSystem;
import static guard.server.server.clientpacket.C_GameOver.C_GameOver_Result;
import static guard.server.server.clientpacket.C_GameOver.C_GameOver_ShowHero;
import static guard.server.server.clientpacket.C_GameTimeAlert.C_GameTimeAlert_Over;
import static guard.server.server.clientpacket.C_GameTimeAlert.C_GameTimeAlert_Start;
import static guard.server.server.clientpacket.C_GuardianFire.C_GuardianFire_Destroy;
import static guard.server.server.clientpacket.C_GuardianFire.C_GuardianFire_Fire;
import static guard.server.server.clientpacket.C_HunterFire.C_HunterFire_Destroy;
import static guard.server.server.clientpacket.C_HunterFire.C_HunterFire_Fire;
import static guard.server.server.clientpacket.C_HunterFire.C_HunterFire_Hit;
import static guard.server.server.clientpacket.C_HunterFire.Hit_Jail;
import static guard.server.server.clientpacket.C_LoadMapDone.C_LoadMapDone_Done;
import static guard.server.server.clientpacket.C_MonsterFire.C_MonsterFire_Destroy;
import static guard.server.server.clientpacket.C_RoomReady.C_RoomReady_Start;
import static guard.server.server.clientpacket.C_SelectPlayerSpawnPoint.C_SelectPlayerSpawnPoint_Request;
import static guard.server.server.clientpacket.C_SelectPlayerSpawnPoint.C_SelectPlayerSpawnPoint_UpdateCheckPoint;
import static guard.server.server.clientpacket.C_Trap.C_Trap_BeAttacked;
import static guard.server.server.clientpacket.C_Trap.C_Trap_BuildUp;
import static guard.server.server.clientpacket.C_Trap.C_Trap_Destroy;
import static guard.server.server.clientpacket.C_Trap.C_Trap_Disable;
import static guard.server.server.clientpacket.ClientOpcodes.C_ArriveCheckPoint;
import static guard.server.server.clientpacket.ClientOpcodes.C_Chat;
import static guard.server.server.clientpacket.ClientOpcodes.C_GameOver;
import static guard.server.server.clientpacket.ClientOpcodes.C_GameStart;
import static guard.server.server.clientpacket.ClientOpcodes.C_GameTimeAlert;
import static guard.server.server.clientpacket.ClientOpcodes.C_GuardianFire;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterFire;
import static guard.server.server.clientpacket.ClientOpcodes.C_LoadMapDone;
import static guard.server.server.clientpacket.ClientOpcodes.C_MonsterFire;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_RequestRemaingTime;
import static guard.server.server.clientpacket.ClientOpcodes.C_RoomReady;
import static guard.server.server.clientpacket.ClientOpcodes.C_SelectPlayerSpawnPoint;
import static guard.server.server.clientpacket.ClientOpcodes.C_Trap;
import guard.server.server.model.GameMap;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.TrapSlot;
import guard.server.server.model.GameProps.Trap.DetonatedTrap;
import guard.server.server.model.GameProps.Trap.SummoningTrap;
import guard.server.server.model.GameProps.Trap.TimingTrap;
import guard.server.server.utils.collections.Lists;
import guard.server.server.utils.collections.Maps;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 遊戲實例<br>
 * 開始遊戲所產生的物件
 */
public class GameInstance extends TimerTask {

	/** 計時器 */
	private Timer timer = new Timer();
	/** 遊戲秒數 */
	private float gameTime = 0.0f;
	/** 房間識別碼 */
	private final String _hostName;
	/** 使用的地圖 */
	private final GameMap _map;
	/**  */
	private TreasureInstance _treasure;

	public TreasureInstance getTreasure() {
		return _treasure;
	}

	public void TreasureReturn() {
		_treasure.Lost(_guardian);
	}

	/**
	 * 取得遊戲時間
	 * 
	 * @return float
	 */
	public float getTime() {
		return gameTime;
	}

	public float getRemainingGameTime() {
		switch (gameState) {
		case GameReady:
			return gameCountDown;
		case GameStart:
			return gameCountDown;
		default:
			return 0;
		}
	}

	/** 取得遊戲地圖 */
	public GameMap getMap() {
		return _map;
	}

	/** 玩家部分 */
	private List<HunterInstance> _hunterList = Lists.newList();

	public void DetecteGameIsOver() {
		boolean _isAllDead = true;
		for (HunterInstance hunter : _hunterList) {
			_isAllDead &= (hunter.getLives() == 0);
		}
		if (_isAllDead) {
			GuardWorld.getInstance().GameOver(
					GuardWorld.getInstance().getRoom(_hostName)
							.get_membersList().get(0).getAccountName());
		}
	}

	private GuardianInstance _guardian = null;

	public GuardianInstance getGuardian() {
		return _guardian;
	}

	// 獵人逃脫flag
	private boolean _escapeFlag = false;

	public void SetEscapeFlag() {
		_escapeFlag = true;
	}

	/** 檢查點 */
	private Map<Integer, CheckPointInstance> _allCheckPoints = Maps
			.newConcurrentMap();

	private boolean CanSpawnAtCheckPoint(int _checkPointID,
			String _accountName, boolean _isStartPoint) {
		if (!_allCheckPoints.containsKey(_checkPointID)) {
			_allCheckPoints.put(_checkPointID, new CheckPointInstance(
					_checkPointID, _accountName, _isStartPoint));
			return true;
		}
		// Check Game Mode
		// Cooperation Mode
		if (getMap().IsCooperationMode()) {
			return true;
		}
		// Greedy Mode
		else if (_allCheckPoints.get(_checkPointID).IsOwner(_accountName)) {
			return true;
		}

		return false;
	}

	private boolean CanArriveCheckPoint(int _checkPointID, String _accountName,
			boolean _isStartPoint) {
		if (!_allCheckPoints.containsKey(_checkPointID)) {
			_allCheckPoints.put(_checkPointID, new CheckPointInstance(
					_checkPointID, _accountName, _isStartPoint));
			return true;
		}
		return false;
	}

	public void SpawnHunter(String _packet, HunterInstance _hunter) {
		if (!_hunter.CanRevive())
			return;
		int _checkPointID = Integer.valueOf(_packet.split(C_PacketSymbol)[2]);
		if (CanSpawnAtCheckPoint(_checkPointID, _hunter.getAccountName(),
				Integer.valueOf(_packet.split(C_PacketSymbol)[4]) == 1 ? true
						: false)) {
			BroadcastPacketToRoom(String.valueOf(C_SelectPlayerSpawnPoint)
					+ C_PacketSymbol
					+ String.valueOf(C_SelectPlayerSpawnPoint_UpdateCheckPoint)
					+ C_PacketSymbol + _checkPointID + C_PacketSymbol
					+ _hunter.getAccountName() + C_PacketSymbol
					+ String.valueOf(_map.getGameMode()));
			_hunter.Revive();
			BroadcastPacketToRoom(_packet + C_PacketSymbol
					+ _hunter.getPlayerModelData());
		}
	}

	public void ArriveCheckPoint(String _packet, HunterInstance _hunter) {
		// 死人不會到達檢查點
		if (_hunter.IsDead())
			return;
		int _checkPointID = Integer.valueOf(_packet.split(C_PacketSymbol)[1]);
		int _checkPointIndex = Integer
				.valueOf(_packet.split(C_PacketSymbol)[4]);
		if (CanArriveCheckPoint(_checkPointID, _hunter.getAccountName(),
				Integer.valueOf(_packet.split(C_PacketSymbol)[3]) == 1 ? true
						: false)) {

			// 增加金錢 - 依據遊戲模式
			if (getMap().IsCooperationMode()) {
				for (HunterInstance _hunterInst : _hunterList) {
					_hunterInst.ArriveCheckPoint(_checkPointID,
							_checkPointIndex);
					BroadcastPacketToRoom(String.valueOf(C_ArriveCheckPoint)
							+ C_PacketSymbol + _hunterInst.getAccountName()
							+ C_PacketSymbol + String.valueOf(_checkPointID)
							+ C_PacketSymbol + String.valueOf(_checkPointIndex));
				}
			} else {
				_hunter.ArriveCheckPoint(_checkPointID, _checkPointIndex);
				BroadcastPacketToRoom(String.valueOf(C_ArriveCheckPoint)
						+ C_PacketSymbol + _hunter.getAccountName()
						+ C_PacketSymbol + String.valueOf(_checkPointID)
						+ C_PacketSymbol + String.valueOf(_checkPointIndex));
			}
		}
	}

	public void EnableFinalGuardian() {

		// TODO Setup Monster Data

		// TODO Send Packet

	}

	/** 醫藥箱 */
	private List<Integer> _allMedicalBoxID = Lists.newList();

	public void InitMedicalBox(String _idList) {
		for (String _id : _idList.split(";")) {
			_allMedicalBoxID.add(Integer.valueOf(_id.split("@")[0]));
			System.out.println("init box id = " + _id.split("@")[0]);
		}
		PrintMedicalCount();
	}

	public void PrintMedicalCount() {
		System.out.println("init medical box" + _allMedicalBoxID.size());
	}

	public boolean CheckMedicalBox(int _id) {
		System.out.println(_id);
		if (_allMedicalBoxID.contains((Object) _id)) {
			_allMedicalBoxID.remove((Object) _id);// cast to object, because of
													// overloading
			return true;
		}
		return false;
	}

	/** 陷阱們 */
	private Map<Integer, TrapSlot> _allTrapList = Maps.newConcurrentMap();

	private TrapInstance getTrapInstance(int _slot, int _key) {
		if (_allTrapList.containsKey(_slot)
				&& _allTrapList.get(_slot).getTrapList().containsKey(_key)) {
			return _allTrapList.get(_slot).getTrapList().get(_key);
		}
		return null;
	}

	// 建造陷阱,確認此格已佔有?
	public void CheckSlot(String _packet) {
		int _slot = Integer.valueOf(_packet.split(C_PacketSymbol)[2]);
		int _key = Integer.valueOf(_packet.split(C_PacketSymbol)[3]);
		int _trapID = Integer.valueOf(_packet.split(C_PacketSymbol)[4]);
		// System.out.println("Spawn Trap "+_trapID);
		if (!_allTrapList.containsKey(_slot)) {
			_allTrapList.put(_slot, new TrapSlot());
			// System.out.println("no slot");
		}
		if (_allTrapList.get(_slot).CheckSlot(_key)) {
			// System.out.println("no key");
			return;
		}
		if (_map.getTrap(_trapID) == null) {
			// System.out.println("trap not found");
			return;
		}
		boolean _isBuild = false;

		if (_guardian.CostGold(_map.getTrap(_trapID).getPrice())) {
			if (_map.getTrap(_trapID) instanceof TimingTrap) {
				_allTrapList
						.get(_slot)
						.PutTrap(
								_key,
								new TimingTrapInstance(
										_slot,
										_key,
										gameTime,
										_map.getTrap(_trapID).getBuildingTime(),
										((TimingTrap) _map.getTrap(_trapID))
												.getLifeTime(),
										((TimingTrap) _map.getTrap(_trapID))
												.getEffectInterval()));
				_isBuild = true;

			} else if (_map.getTrap(_trapID) instanceof DetonatedTrap) {
				_allTrapList.get(_slot).PutTrap(
						_key,
						new DetonatedTrapInstance(_slot, _key, gameTime, _map
								.getTrap(_trapID).getBuildingTime(),
								((DetonatedTrap) _map.getTrap(_trapID))
										.getDmgHP()));
				_isBuild = true;
			} else if (_map.getTrap(_trapID) instanceof SummoningTrap) {
				_allTrapList.get(_slot)
						.PutTrap(
								_key,
								new SummoningTrapInstance(_slot, _key,
										gameTime, _map.getTrap(_trapID)
												.getBuildingTime(),
										((SummoningTrap) _map.getTrap(_trapID))
												.getHp()));
				_isBuild = true;

			}
		}

		if (_isBuild) {
			// Send Packet
			BroadcastPacketToRoom(_packet);
		}
	}

	public void TrigTrap(String _packet) {
		int _slot = Integer.valueOf(_packet.split(C_PacketSymbol)[2]);
		int _key = Integer.valueOf(_packet.split(C_PacketSymbol)[3]);

		TrapInstance _trap = getTrapInstance(_slot, _key);
		if (_trap == null)
			return;

		if (_trap instanceof DetonatedTrapInstance) {
			if (((DetonatedTrapInstance) _trap).TrapTrigged(gameTime)) {
				// Send Packet
				BroadcastPacketToRoom(_packet);
			}
		} else if (_trap.TrapTrigged()) {

			// Send Packet
			BroadcastPacketToRoom(_packet);
		}

	}

	public void ApplyTrapDamage(String _packet, HunterInstance _hunter) {
		int _slot = Integer.valueOf(_packet.split(C_PacketSymbol)[2]);
		int _key = Integer.valueOf(_packet.split(C_PacketSymbol)[3]);

		TrapInstance _trap = getTrapInstance(_slot, _key);
		if (_trap == null)
			return;

		if (_trap.IsAutoDestroy()) {
			if (_trap instanceof DetonatedTrapInstance) {
				// System.out.println("Apply Hunter HP");

				// Calc Guardian Gold
				int _damageValue = _hunter
						.ApplyHP(((DetonatedTrapInstance) _trap).getDamageHP() > 0 ? -((DetonatedTrapInstance) _trap)
								.getDamageHP()
								: ((DetonatedTrapInstance) _trap).getDamageHP());

				_guardian.AcquireGold(Math.abs(_damageValue), getMap()
						.getGuardianDmgReward());

			}
		}

	}

	public void SummoningTrapApplyAttack(String _packet, HunterInstance _hunter) {
		int _slot = Integer.valueOf(_packet.split(C_PacketSymbol)[2]);
		int _key = Integer.valueOf(_packet.split(C_PacketSymbol)[3]);

		TrapInstance _trap = getTrapInstance(_slot, _key);
		if (_trap == null)
			return;
		if (_trap instanceof SummoningTrapInstance) {
			// Check Attack
			if (((SummoningTrapInstance) _trap).CanAttack(gameTime)) {
				String _mbulletID = String.valueOf(_slot)
						+ String.valueOf(_key) + String.valueOf(gameTime);
				MonsterFire(_mbulletID);
				BroadcastPacketToRoom(_packet + C_PacketSymbol + _mbulletID);
			}

		}
	}

	public void MeleeAttackApplyToHunter(WickedRoadPlayerInstance _wrPlayer) {

		if (_wrPlayer instanceof GuardianInstance)
			return;
		HunterInstance _hunter = (HunterInstance) _wrPlayer;

		_hunter.ApplyHP(-_map.getMeleeDamageValue());

	}

	public void MeleeAttackTrapJail(int _slot, int _key) {
		TrapInstance _trap = getTrapInstance(_slot, _key);
		if (_trap == null)
			return;
		if (_trap instanceof SummoningTrapInstance) {

			// TODO Send Packet Hit Trap
			BroadcastPacketToRoom(String.valueOf(C_Trap) + C_PacketSymbol
					+ String.valueOf(C_Trap_BeAttacked) + C_PacketSymbol
					+ String.valueOf(_slot) + C_PacketSymbol
					+ String.valueOf(_key));

			if (((SummoningTrapInstance) _trap).ApplyDamage(_map
					.getBulletDamageValue())
					&& _trap.SetupAutoDestroy(gameTime)) {
				// TODO Send Packet 陷阱關閉

				BroadcastPacketToRoom(String.valueOf(C_Trap) + C_PacketSymbol
						+ String.valueOf(C_Trap_Disable) + C_PacketSymbol
						+ String.valueOf(_slot) + C_PacketSymbol
						+ String.valueOf(_key));

			}
		}
	}

	public void BulletAttackTrapJail(String _bulletID, int _slot, int _key) {

		TrapInstance _trap = getTrapInstance(_slot, _key);
		if (_trap == null)
			return;

		if (_trap instanceof SummoningTrapInstance) {

			// TODO Send Packet Hit Trap
			BroadcastPacketToRoom(String.valueOf(C_Trap) + C_PacketSymbol
					+ String.valueOf(C_Trap_BeAttacked) + C_PacketSymbol
					+ String.valueOf(_slot) + C_PacketSymbol
					+ String.valueOf(_key));

			if (((SummoningTrapInstance) _trap).ApplyDamage(_map
					.getBulletDamageValue())
					&& _trap.SetupAutoDestroy(gameTime)) {
				// TODO Send Packet 陷阱關閉

				BroadcastPacketToRoom(String.valueOf(C_Trap) + C_PacketSymbol
						+ String.valueOf(C_Trap_Disable) + C_PacketSymbol
						+ String.valueOf(_slot) + C_PacketSymbol
						+ String.valueOf(_key));

			}
		}

		BroadcastPacketToRoom(String.valueOf(C_HunterFire) + C_PacketSymbol
				+ String.valueOf(C_HunterFire_Hit) + C_PacketSymbol
				+ String.valueOf(Hit_Jail) + C_PacketSymbol
				+ String.valueOf(_bulletID));

	}

	private void CheckAllTrapBuildUp() {
		for (TrapSlot _trapSlotList : _allTrapList.values()) {
			for (TrapInstance _trapInstance : _trapSlotList.getTrapList()
					.values()) {
				if (_trapInstance.IsBuildUp(gameTime)) {
					// Send Packet
					String _packet = String.valueOf(C_Trap) + C_PacketSymbol
							+ String.valueOf(C_Trap_BuildUp) + C_PacketSymbol
							+ _trapInstance.getSlotID() + C_PacketSymbol
							+ _trapInstance.getSlotKey();

					BroadcastPacketToRoom(_packet);
				}
			}
		}
	}

	private void CheckAllAutoDestroyTrap() {
		for (TrapSlot _trapSlotList : _allTrapList.values()) {
			for (TrapInstance _trapInstance : _trapSlotList.getTrapList()
					.values()) {

				if (_trapInstance instanceof TimingTrapInstance) {
					if (((TimingTrapInstance) _trapInstance)
							.CheckExpire(gameTime)) {

						_allTrapList.get(_trapInstance.getSlotID())
								.getTrapList()
								.remove(_trapInstance.getSlotKey());

						BroadcastPacketToRoom(String.valueOf(C_Trap)
								+ C_PacketSymbol
								+ String.valueOf(C_Trap_Destroy)
								+ C_PacketSymbol
								+ String.valueOf(_trapInstance.getSlotID())
								+ C_PacketSymbol
								+ String.valueOf(_trapInstance.getSlotKey()));
						// System.out.println("Destroy trap auto");
					}
				} else if (_trapInstance.CanAutoDestroyTrap(gameTime)) {

					_allTrapList.get(_trapInstance.getSlotID()).getTrapList()
							.remove(_trapInstance.getSlotKey());

					BroadcastPacketToRoom(String.valueOf(C_Trap)
							+ C_PacketSymbol + String.valueOf(C_Trap_Destroy)
							+ C_PacketSymbol
							+ String.valueOf(_trapInstance.getSlotID())
							+ C_PacketSymbol
							+ String.valueOf(_trapInstance.getSlotKey()));
					// System.out.println("Destroy trap auto");
				}
			}
		}
	}

	/** 玩家子彈們 */
	private Map<String, BulletInstance> _hunterBullets = Maps
			.newConcurrentMap();

	public BulletInstance getHunterBullets(String _id) {
		return _hunterBullets.get(_id);
	}

	public synchronized void HunterFire(PlayerInstance pc, String position,
			String rotation, String _bulletID) {
		_hunterBullets.put(_bulletID, new BulletInstance(pc.getAccountName(),
				_bulletID, gameTime));
		// TODO BroadCast To All : Fire
		String _retPacket = String.valueOf(C_HunterFire) + C_PacketSymbol
				+ String.valueOf(C_HunterFire_Fire) + C_PacketSymbol
				+ pc.getAccountName() + C_PacketSymbol
				+ String.valueOf(_bulletID) + C_PacketSymbol + position
				+ C_PacketSymbol + rotation;
		BroadcastPacketToRoom(_retPacket);
	}

	/** TD子彈 */
	private Map<String, BulletInstance> _monsterBullets = Maps
			.newConcurrentMap();

	public BulletInstance getMonsterBullets(String _id) {
		return _monsterBullets.get(_id);
	}

	public synchronized void MonsterFire(String id) {
		_monsterBullets.put(id, new BulletInstance("", id, gameTime));
	}

	/** Guardian火球 */
	private Map<String, BulletInstance> _guardianBullets = Maps
			.newConcurrentMap();

	public BulletInstance getGuardainBullets(String _id) {
		return _guardianBullets.get(_id);
	}

	public synchronized void GuardianFire(String id, String pos, String rot) {
		if (_guardianBullets.containsKey(id))
			return;
		_guardianBullets.put(id, new BulletInstance("", id, gameTime));
		this.BroadcastPacketToRoom(String.valueOf(C_GuardianFire)
				+ C_PacketSymbol + String.valueOf(C_GuardianFire_Fire)
				+ C_PacketSymbol + id + C_PacketSymbol + pos + C_PacketSymbol
				+ rot);
	}

	/**
	 * 遊戲狀態 -> 0 : 等待玩家, 1 : 倒數, 2 : 遊戲載入中, 3 : 遊戲準備中, 4 : 遊戲開始, 5 : 結束(勝利或失敗)
	 * */
	private GameState gameState;
	private float gameTimeRecord, gamePlayingTime;

	// 遊戲倒數中 - at state 2
	private int gameCountDown;

	// 遊戲載入地圖完畢，準備開始的倒數時間 - at state 3
	private final float gameStartReadyTime = 25;

	public boolean IsWaitingPlayers() {
		return gameState == GameState.Waiting;
	}

	public boolean IsReady() {
		return gameState == GameState.CountDown;
	}

	public boolean IsGaming() {
		return gameState.value > 1 && gameState.value < 5;
	}

	// Important!!!!!
	public boolean IsGameOver() {
		return gameState == GameState.GameOver;
	}

	private void GameStartCountDown() {
		gameTimeRecord = Float.NEGATIVE_INFINITY;
		gameState = GameState.CountDown;
	}

	private void GameLoading() {
		gameTimeRecord = gameTime;
		gameState = GameState.Loading;
	}

	// 載入完成呼叫函式,進入遊戲，倒數N秒後開始
	private void GameStartReady() {
		gameTimeRecord = gameTime;
		gameState = GameState.GameReady;
		gameCountDown = (int) gameStartReadyTime;
		// TODO 廣播開始遊戲封包
		for (PlayerInstance members : GuardWorld.getInstance()
				.getRoom(_hostName).get_membersList()) {
			members.SendClientPacket(String.valueOf(C_LoadMapDone)
					+ C_PacketSymbol + String.valueOf(C_LoadMapDone_Done));
		}
		// 更新guardian時間
		this._guardian.getActiveChar().SendClientPacket(
				String.valueOf(C_RequestRemaingTime) + C_PacketSymbol
						+ String.valueOf(getRemainingGameTime()));
	}

	// 倒數完畢 開始遊戲
	private void GameStart() {
		gameTimeRecord = gameTime;
		gameState = GameState.GameStart;
		gameCountDown = (int) _map.getGamePlayTime();
		// TODO 傳送遊戲開始封包
		BroadcastPacketToRoom(String.valueOf(C_GameStart));
		// 更新guardian時間
		this._guardian.getActiveChar().SendClientPacket(
				String.valueOf(C_RequestRemaingTime) + C_PacketSymbol
						+ String.valueOf(getRemainingGameTime()));
		for (HunterInstance _hunterInst : _hunterList) {
			if (!_hunterInst.IsSpawned()) {
				_hunterInst
						.getActiveChar()
						.SendClientPacket(
								String.valueOf(C_SelectPlayerSpawnPoint)
										+ C_PacketSymbol
										+ String.valueOf(C_SelectPlayerSpawnPoint_Request));
			}
		}
	}

	// 遊戲時間超過，轉換到遊戲結束狀態
	public void GameOver() {
		gameState = GameState.GameOver;
		gameTimeRecord = gameTime;
		// TODO 傳送遊戲結束封包，等待計算中的勝利結果。
	}

	// 檢查所有玩家載入地圖狀態
	public void CheckAllPlayerLoadMapDone() {
		boolean isAllPlayerLoadMapDone = true;

		for (PlayerInstance members : GuardWorld.getInstance()
				.getRoom(_hostName).get_membersList()) {
			isAllPlayerLoadMapDone &= members.IsLoadMapDone();
		}

		// 全體載入完畢 - 開始遊戲
		if (isAllPlayerLoadMapDone) {
			GameStartReady();
		}
	}

	// 分配玩者
	public void DispatchPlayer(final List<HunterInstance> hunterList,
			final GuardianInstance guardian) {
		this._hunterList = hunterList;
		this._guardian = guardian;
		this._treasure = new TreasureInstance(this._guardian);
	}

	// TODO 初始化各種地圖資訊
	public GameInstance(final String hostName, final GameMap map) {
		this._hostName = hostName;
		this._map = map;
		this.gameState = GameState.Waiting;
		this.gameTimeRecord = Float.NEGATIVE_INFINITY;
		this.gameCountDown = 3;
	}

	@Override
	public void run() {

		switch (gameState) {
		case Waiting:// 等待玩家
			GameStartCountDown();
			break;
		case CountDown:// 準備室倒數 - 準備開始遊戲
			if (gameCountDown >= 0) {
				if (gameTime - gameTimeRecord > 1) {
					String _retpacket = String.valueOf(C_Chat) + C_PacketSymbol
							+ C_Chat_ChatInRoomSystem + C_PacketSymbol
							+ String.valueOf(gameCountDown);

					BroadcastPacketToRoom(_retpacket);
					gameCountDown--;
					gameTimeRecord = gameTime;
				}
			} else {
				GameLoading();
				String _retpacket = String.valueOf(C_RoomReady)
						+ C_PacketSymbol + String.valueOf(C_RoomReady_Start);

				BroadcastPacketToRoom(_retpacket);
			}
			break;
		case Loading:// 載入中 - 目前do nothing

			break;
		case GameReady:// 遊戲準備中 - 倒數N秒 暫定20
			if (gameCountDown >= 0) {
				if (gameTime - gameTimeRecord > 1) {

					BroadcastPacketToRoom(String.valueOf(C_GameTimeAlert)
							+ C_PacketSymbol + C_GameTimeAlert_Start
							+ C_PacketSymbol + String.valueOf(gameCountDown));

					gameCountDown--;
					gameTimeRecord = gameTime;
				}
			} else {
				GameStart();
			}
			for (HunterInstance _hunter : _hunterList) {
				_hunter.Update(gameTime);
			}
			CheckHunterBulletExpire();
			CheckMonsterBulletExpire();
			CheckGuardianBulletExpire();
			CheckAllTrapBuildUp();
			CheckAllAutoDestroyTrap();
			break;
		case GameStart:// 遊戲開始
			// TODO 遊戲時間到檢查
			if (gameCountDown >= 0) {
				if (gameTime - gameTimeRecord > 1) {

					if (gameCountDown <= 20) {
						BroadcastPacketToRoom(String.valueOf(C_GameTimeAlert)
								+ C_PacketSymbol + C_GameTimeAlert_Over
								+ C_PacketSymbol
								+ String.valueOf(gameCountDown));
					}

					gameCountDown--;
					gameTimeRecord = gameTime;
				}
			} else {// Time out
				GameOver();
			}

			// TODO //守護神每N秒獎金計算，及其他與時間相關計算
			if (gameTime > gamePlayingTime + _map._guardianRewardInterval()) {

				_guardian.RewardGold();

				gamePlayingTime = gameTime;
			}
			for (HunterInstance _hunter : _hunterList) {
				_hunter.Update(gameTime);
			}
			CheckHunterBulletExpire();
			CheckMonsterBulletExpire();
			CheckGuardianBulletExpire();
			CheckAllTrapBuildUp();
			CheckAllAutoDestroyTrap();
			break;
		case GameOver:// 遊戲結束

			// TODO 結束遊戲，傳送封包，自行銷毀實體。
			GuardWorld.getInstance().GameOver(
					GuardWorld.getInstance().getRoom(_hostName)
							.get_membersList().get(0).getAccountName());

			break;
		}

		gameTime += 0.1;

	}

	/** 檢查獵人過時子彈 */
	private void CheckHunterBulletExpire() {
		if (_hunterBullets.size() == 0)
			return;
		for (BulletInstance _bullet : _hunterBullets.values()) {
			if (_bullet.CheckExpire(gameTime)) {
				// TODO 刪除子彈物件
				String _retPacket = String.valueOf(C_HunterFire)
						+ C_PacketSymbol + String.valueOf(C_HunterFire_Destroy)
						+ C_PacketSymbol
						+ String.valueOf(_bullet.getBulletInstanceID());
				BroadcastPacketToRoom(_retPacket);
				_hunterBullets.remove(_bullet.getBulletInstanceID());
			}
		}
	}

	/** 檢查TD過時子彈 */
	private void CheckMonsterBulletExpire() {
		if (this._monsterBullets.size() == 0)
			return;
		for (BulletInstance _bullet : _monsterBullets.values()) {
			if (_bullet.CheckExpire(gameTime)) {
				// TODO 刪除子彈物件
				BroadcastPacketToRoom(String.valueOf(C_MonsterFire)
						+ C_PacketSymbol
						+ String.valueOf(C_MonsterFire_Destroy)
						+ C_PacketSymbol + _bullet.getBulletInstanceID());
				_monsterBullets.remove(_bullet.getBulletInstanceID());
			}
		}
	}

	/** 檢查Guardian過時火球 */
	private void CheckGuardianBulletExpire() {
		if (this._guardianBullets.size() == 0)
			return;
		for (BulletInstance _bullet : _guardianBullets.values()) {
			if (_bullet.CheckExpire(gameTime)) {
				// TODO 刪除子彈物件
				BroadcastPacketToRoom(String.valueOf(C_GuardianFire)
						+ C_PacketSymbol
						+ String.valueOf(C_GuardianFire_Destroy)
						+ C_PacketSymbol + _bullet.getBulletInstanceID());
				_guardianBullets.remove(_bullet.getBulletInstanceID());
			}
		}
	}

	/** 計算遊戲結果 */
	public void CalcGameResult() {
		// TODO 計算勝利結果，傳送封包。
		if (_hunterList == null || _guardian == null) {
			return;
		}

		switch (_map.getGameMode()) {
		case GameMap.GameMode_Cooperation:

			for (HunterInstance _hunterInst : _hunterList) {
				_hunterInst
						.getActiveChar()
						.SendClientPacket(
								String.valueOf(C_GameOver)
										+ C_PacketSymbol
										+ String.valueOf(C_GameOver_Result)
										+ C_PacketSymbol
										+ String.valueOf(_map.getGameMode())
										+ C_PacketSymbol
										+ String.valueOf(PlayerInstance.PlayerType_Hunter)
										+ C_PacketSymbol
										+ String.valueOf(!_treasure
												.IsOwner(_guardian)
												&& _escapeFlag)// win flag
										+ C_PacketSymbol
										+ String.valueOf(_treasure
												.IsOwner(_guardian)));// guardian
																		// is
																		// owner
			}

			_guardian
					.getActiveChar()
					.SendClientPacket(
							String.valueOf(C_GameOver)
									+ C_PacketSymbol
									+ String.valueOf(C_GameOver_Result)
									+ C_PacketSymbol
									+ String.valueOf(_map.getGameMode())
									+ C_PacketSymbol
									+ String.valueOf(PlayerInstance.PlayerType_Guardian)
									+ C_PacketSymbol
									+ String.valueOf(!_escapeFlag)// win flag
									+ C_PacketSymbol
									+ String.valueOf(_treasure
											.IsOwner(_guardian)));// guardian is
																	// owner?

			break;
		case GameMap.GameMode_Greedy:

			for (HunterInstance _hunterInst : _hunterList) {
				_hunterInst
						.getActiveChar()
						.SendClientPacket(
								String.valueOf(C_GameOver)
										+ C_PacketSymbol
										+ String.valueOf(C_GameOver_Result)
										+ C_PacketSymbol
										+ String.valueOf(_map.getGameMode())
										+ C_PacketSymbol
										+ String.valueOf(PlayerInstance.PlayerType_Hunter)
										+ C_PacketSymbol
										+ String.valueOf(_treasure
												.IsOwner(_hunterInst)
												&& _escapeFlag)// win flag
										+ C_PacketSymbol
										+ String.valueOf(_treasure
												.IsOwner(_guardian)));// guardian
																		// is
																		// owner?
			}

			_guardian
					.getActiveChar()
					.SendClientPacket(
							String.valueOf(C_GameOver)
									+ C_PacketSymbol
									+ String.valueOf(C_GameOver_Result)
									+ C_PacketSymbol
									+ String.valueOf(_map.getGameMode())
									+ C_PacketSymbol
									+ String.valueOf(PlayerInstance.PlayerType_Guardian)
									+ C_PacketSymbol
									+ String.valueOf(!_escapeFlag)// win flag
									+ C_PacketSymbol
									+ String.valueOf(_treasure
											.IsOwner(_guardian)));// guardian is
																	// owner?

			break;
		}
		this.BroadcastPacketToRoom(String.valueOf(C_GameOver) + C_PacketSymbol
				+ String.valueOf(C_GameOver_ShowHero) + C_PacketSymbol
				+ _treasure.getTreasurePacket());

	}

	/**
	 * 開始遊戲計時器
	 * 
	 * @param delay
	 *            幾毫秒後開始
	 */
	public void startGameTimer(int delay) {
		timer.scheduleAtFixedRate(this, delay, 100);
		GameStartCountDown();
	}

	private void BroadcastPacketToRoom(String _packet) {
		GuardWorld
				.getInstance()
				.getRoom(
						GuardWorld.getInstance().getRoom(_hostName)
								.get_membersList().get(0).getAccountName())
				.broadcastPacketToRoom(_packet);
	}

	// 遊戲狀態 -> 0 : 等待玩家, 1 : 倒數, 2 : 遊戲載入中, 3 : 遊戲準備中, 4 : 遊戲開始, 5 : 結束(勝利或失敗)
	public enum GameState {
		Waiting(0), CountDown(1), Loading(2), GameReady(3), GameStart(4), GameOver(
				5);

		private int value;

		GameState(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

}
