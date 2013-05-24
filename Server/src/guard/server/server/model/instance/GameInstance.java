package guard.server.server.model.instance;

import static guard.server.server.clientpacket.C_Chat.C_Chat_ChatInRoomSystem;
import static guard.server.server.clientpacket.C_LoadMapDone.C_LoadMapDone_Done;
import static guard.server.server.clientpacket.C_RoomReady.C_RoomReady_Start;
import static guard.server.server.clientpacket.ClientOpcodes.C_Chat;
import static guard.server.server.clientpacket.ClientOpcodes.C_Gold;
import static guard.server.server.clientpacket.ClientOpcodes.C_LoadMapDone;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_RoomReady;
import guard.server.server.model.GameMap;
import guard.server.server.model.GuardWorld;
import guard.server.server.utils.collections.Lists;

import java.util.List;
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

	private final String _hostName;

	/** 使用的地圖 */
	private final GameMap _map;

	public GameMap getMap() {
		return _map;
	}

	/**
	 * 遊戲狀態 -> 0 : 等待玩家, 1 : 倒數, 2 : 遊戲載入中, 3 : 遊戲準備中, 4 : 遊戲開始, 5 : 結束(勝利或失敗)
	 * */
	private int gameState;
	private float gameTimeRecord, gamePlayingTime;

	// 遊戲倒數中 - at state 2
	private int gameCountDown;

	// 遊戲載入地圖完畢，準備開始的倒數時間 - at state 3
	private final float gameStartReadyTime = 20;

	public boolean IsReady() {
		return gameState == 1;
	}

	private void GameReady() {
		gameState = 1;
		gameTimeRecord = Float.NEGATIVE_INFINITY;
	}

	private void GameLoading() {
		gameState = 2;
		gameTimeRecord = gameTime;
	}

	// 載入完成呼叫函式,進入遊戲，倒數N秒後開始
	private void GameStartReady() {
		gameState = 3;
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
		gameState = 4;
		gameTimeRecord = gameTime;
		// TODO 傳送遊戲開始封包
	}

	// 遊戲時間超過，轉換到遊戲結束狀態
	private void GameOver() {
		gameState = 5;
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

	/* 玩家部分 */
	private List<HunterInstance> _hunterList = Lists.newList();
	private GuardianInstance _guardian = null;

	// 分配玩者
	public void DispatchPlayer(final List<HunterInstance> hunterList,final GuardianInstance guardian) {
		this._hunterList = hunterList;
		this._guardian = guardian;
	}

	// TODO 初始化各種地圖資訊
	public GameInstance(final String hostName, final GameMap map) {
		this._hostName = hostName;
		this._map = map;
		// this._hunterList = _hunterList;
		// this._guardian = _guardian;
		this.gameState = 0;
		this.gameTimeRecord = Float.NEGATIVE_INFINITY;
		this.gameCountDown = 5;

	}

	@Override
	public void run() {

		switch (gameState) {
		case 0:// 等待玩家
			GameReady();
			break;
		case 1:// 準備室倒數 - 準備開始遊戲
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
		case 2:// 載入中 - 目前do nothing

			break;
		case 3:// 遊戲準備中 - 倒數N秒 暫定20
			if (gameTime - gameTimeRecord > gameStartReadyTime) {
				GameStart();
			}
			break;
		case 4:// 遊戲開始
				// TODO 遊戲時間到檢查
			if (gameTime >= _map.getGamePlayTime() + gameTimeRecord) {
				// TODO 傳送時間到(Time's up)封包

				GuardWorld
						.getInstance()
						.getRoom(
								GuardWorld.getInstance().getRoom(_hostName)
										.get_membersList().get(0)
										.getAccountName())
						.broadcastPacketToRoom("");
				GameOver();
			}
			// TODO //守護神每N秒獎金計算，及其他與時間相關計算
			else if (gameTime > gamePlayingTime
					+ _map._guardianRewardInterval()) {

				_guardian.RewardGold();
				_guardian.getActiveChar().SendClientPacket(
						C_Gold
								+ C_PacketSymbol
								+ String.valueOf(_guardian.getActiveChar()
										.getPlayerType()) + C_PacketSymbol
								+ String.valueOf(_guardian.getGold()));

				gamePlayingTime = gameTime;
			}
			break;
		case 5:// 遊戲結束
				// TODO 計算勝利結果，傳送封包。

			// TODO 結束遊戲，傳送封包，自行銷毀實體。
			break;
		}

		gameTime += 0.1;

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
	 * 取得遊戲時間
	 * 
	 * @return float
	 */
	public float getTime() {
		return gameTime;
	}
	/**
	 * 自行銷毀 case 正常結束 case 市長中離 case guardian中離 case 所有hunter中離
	 * */

	/**
	 * 玩家中離
	 * 
	 * 
	 * */

}
