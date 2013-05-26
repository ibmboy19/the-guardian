package guard.server.server.model.instance;

import static guard.server.server.clientpacket.C_Chat.C_Chat_ChatInRoomSystem;
import static guard.server.server.clientpacket.C_HunterFire.C_HunterFire_Destroy;
import static guard.server.server.clientpacket.C_HunterFire.C_HunterFire_Fire;
import static guard.server.server.clientpacket.C_LoadMapDone.C_LoadMapDone_Done;
import static guard.server.server.clientpacket.C_RoomReady.C_RoomReady_Start;
import static guard.server.server.clientpacket.ClientOpcodes.C_Chat;
import static guard.server.server.clientpacket.ClientOpcodes.C_Gold;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterFire;
import static guard.server.server.clientpacket.ClientOpcodes.C_LoadMapDone;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_RoomReady;
import guard.server.server.model.GameMap;
import guard.server.server.model.GuardWorld;
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

	/**
	 * 取得遊戲時間
	 * 
	 * @return float
	 */
	public float getTime() {
		return gameTime;
	}

	/** 取得遊戲地圖 */
	public GameMap getMap() {
		return _map;
	}

	/** 玩家部分 */
	private List<HunterInstance> _hunterList = Lists.newList();
	private GuardianInstance _guardian = null;

	/** 陷阱們 */
	private Map<Long, TrapInstance> _trapList = Maps.newConcurrentMap();

	/** 子彈們 */
	private int _bulletCounter = 0;
	private Map<Integer, BulletInstance> _bulletList = Maps.newConcurrentMap();

	public BulletInstance getBullet(int _id) {
		return _bulletList.get(_id);
	}

	public synchronized void HunterFire(PlayerInstance pc, String position,
			String rotation) {
		HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();
		if (!hunter.CanFire())
			return;

		int _bulletID = _bulletCounter;
		_bulletList.put(_bulletID, new BulletInstance(pc.getAccountName(),
				_bulletID, gameTime));
		hunter.Fire();
		// TODO BroadCast To All : Fire
		String _retPacket = String.valueOf(C_HunterFire) + C_PacketSymbol
				+ String.valueOf(C_HunterFire_Fire) + C_PacketSymbol
				+ pc.getAccountName() + C_PacketSymbol
				+ String.valueOf(_bulletID) + C_PacketSymbol + position
				+ C_PacketSymbol + rotation;
		GuardWorld
				.getInstance()
				.getRoom(
						GuardWorld.getInstance().getRoom(_hostName)
								.get_membersList().get(0).getAccountName())
				.broadcastPacketToRoom(_retPacket);
		_bulletCounter++;
	}

	public void HunterFireHit(int bulletID) {

	}

	/**
	 * 遊戲狀態 -> 0 : 等待玩家, 1 : 倒數, 2 : 遊戲載入中, 3 : 遊戲準備中, 4 : 遊戲開始, 5 : 結束(勝利或失敗)
	 * */
	private GameState gameState;
	private float gameTimeRecord, gamePlayingTime;

	// 遊戲倒數中 - at state 2
	private int gameCountDown;

	// 遊戲載入地圖完畢，準備開始的倒數時間 - at state 3
	private final float gameStartReadyTime = 20;

	public boolean IsReady() {
		return gameState == GameState.CountDown;
	}

	private void GameReady() {
		gameState = GameState.CountDown;
		gameTimeRecord = Float.NEGATIVE_INFINITY;
	}

	private void GameLoading() {
		gameState = GameState.Loading;
		gameTimeRecord = gameTime;
	}

	// 載入完成呼叫函式,進入遊戲，倒數N秒後開始
	private void GameStartReady() {
		gameState = GameState.GameReady;
		gameTimeRecord = gameTime;
		// TODO 廣播開始遊戲封包
		for (PlayerInstance members : GuardWorld.getInstance()
				.getRoom(_hostName).get_membersList()) {
			members.SendClientPacket(String.valueOf(C_LoadMapDone)
					+ C_PacketSymbol + String.valueOf(C_LoadMapDone_Done));
		}
	}

	// 倒數完畢 開始遊戲
	private void GameStart() {
		gameState = GameState.GameStart;
		gameTimeRecord = gameTime;
		// TODO 傳送遊戲開始封包
	}

	// 遊戲時間超過，轉換到遊戲結束狀態
	private void GameOver() {
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
	}

	// TODO 初始化各種地圖資訊
	public GameInstance(final String hostName, final GameMap map) {
		this._hostName = hostName;
		this._map = map;
		this.gameState = GameState.Waiting;
		this.gameTimeRecord = Float.NEGATIVE_INFINITY;
		this.gameCountDown = 5;

	}

	@Override
	public void run() {

		switch (gameState) {
		case Waiting:// 等待玩家
			GameReady();
			break;
		case CountDown:// 準備室倒數 - 準備開始遊戲
			if (gameCountDown >= 0) {
				if (gameTime - gameTimeRecord > 1) {
					String _retpacket = String.valueOf(C_Chat) + C_PacketSymbol
							+ C_Chat_ChatInRoomSystem + C_PacketSymbol
							+ String.valueOf(gameCountDown);

					GuardWorld
							.getInstance()
							.getRoom(
									GuardWorld.getInstance().getRoom(_hostName)
											.get_membersList().get(0)
											.getAccountName())
							.broadcastPacketToRoom(_retpacket);
					gameCountDown--;
					gameTimeRecord = gameTime;
				}
			} else {
				GameLoading();
				String _retpacket = String.valueOf(C_RoomReady)
						+ C_PacketSymbol + String.valueOf(C_RoomReady_Start);

				GuardWorld
						.getInstance()
						.getRoom(
								GuardWorld.getInstance().getRoom(_hostName)
										.get_membersList().get(0)
										.getAccountName())
						.broadcastPacketToRoom(_retpacket);
			}
			break;
		case Loading:// 載入中 - 目前do nothing

			break;
		case GameReady:// 遊戲準備中 - 倒數N秒 暫定20
			if (gameTime - gameTimeRecord > gameStartReadyTime) {
				GameStart();
			}
			CheckAllBulletExpire();
			break;
		case GameStart:// 遊戲開始
			// TODO 遊戲時間到檢查
			if (gameTime >= _map.getGamePlayTime() + gameTimeRecord) {
				// TODO 傳送時間到(Time's up)封包

				GameOver();
			}
			// TODO //守護神每N秒獎金計算，及其他與時間相關計算
			else if (gameTime > gamePlayingTime
					+ _map._guardianRewardInterval()) {

				_guardian.RewardGold();

				gamePlayingTime = gameTime;
			}
			CheckAllBulletExpire();
			break;
		case GameOver:// 遊戲結束
			// TODO 計算勝利結果，傳送封包。

			// TODO 結束遊戲，傳送封包，自行銷毀實體。
			break;
		}

		gameTime += 0.1;

	}

	/** 檢查所有過時子彈 */
	private void CheckAllBulletExpire() {
		if (_bulletList.size() == 0)
			return;
		for (BulletInstance _bullet : _bulletList.values()) {
			if (_bullet.CheckExpire(gameTime)) {
				// TODO 刪除子彈物件
				String _retPacket = String.valueOf(C_HunterFire)
						+ C_PacketSymbol + String.valueOf(C_HunterFire_Destroy)
						+ C_PacketSymbol
						+ String.valueOf(_bullet.getBulletInstanceID());
				GuardWorld
						.getInstance()
						.getRoom(
								GuardWorld.getInstance().getRoom(_hostName)
										.get_membersList().get(0)
										.getAccountName())
						.broadcastPacketToRoom(_retPacket);
				_bulletList.remove(_bullet.getBulletInstanceID());
			}
		}
		System.out.println("所有bullet數量: " + _bulletList.size());
	}

	/**
	 * 開始遊戲計時器
	 * 
	 * @param delay
	 *            幾毫秒後開始
	 */
	public void startGameTimer(int delay) {
		timer.scheduleAtFixedRate(this, delay, 100);
		GameReady();
	}

	/**
	 * 自行銷毀 case 正常結束 case 市長中離 case guardian中離 case 所有hunter中離
	 * */

	/**
	 * 玩家中離
	 * 
	 * 
	 * */

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
