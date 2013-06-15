package guard.server.server.clientpacket;

public class ClientOpcodes {

	public static final String C_PacketSymbol = "\t";

	// 玩家進入遊戲大廳
	public static final int C_Login = 0;
	// 離開遊戲，清除PC物件
	public static final int C_Logout = 1;
	// 創造房間，載入MapCheckCode
	public static final int C_CreateRoom = 2;
	// 加入房間，通知房間其他玩家有別的玩家加入，通知加入的玩家有其他玩家。
	public static final int C_JoinRoom = 3;
	// 離開房間，若為Host，則BreakRoom，不為Host則告知其他玩者離開，回傳該玩家離開封包。
	// Leave Room的Leave Code >>> 0: break up, 1: 自離,2:他人離開
	public static final int C_LeaveRoom = 4;
	// 刷新目前正在準備的房間
	public static final int C_RefreshRoom = 5;
	// type 0: 準備室的文字對話; type 1: 遊戲進行中的無線電通話系統
	public static final int C_Chat = 6;
	// 按下準備/開始的按鈕，不為Host就是Ready鍵，若為Host則開始遊戲
	public static final int C_RoomReady = 7;
	// 要求伺服器測試網路延遲
	public static final int C_NetDelay = 8;
	// 在準備室中，切換遊戲進行時要扮演的角色: 守護者/獵人
	public static final int C_SwitchPlayerType = 9;
	// 獵人重生點選擇，每個CheckPoint都有ID，但是不是每個CheckPoint都可當作起始點。
	public static final int C_SelectPlayerSpawnPoint = 10;
	// 獵人到達檢查點
	public static final int C_ArriveCheckPoint = 11;
	// 要求當前剩餘遊戲時間
	public static final int C_RequestRemaingTime = 12;
	// 所有移動.跑步.跳躍.蹲下，透明度的設置會透過該封包實現。
	public static final int C_MoveState = 13;
	// 刷新當前上線的玩家清單
	public static final int C_RefreshAllPlayersList = 14;
	// 載入地圖完成
	public static final int C_LoadMapDone = 15;
	// 開始遊戲 - 載入場景完畢且倒數完畢後，開始遊戲
	public static final int C_GameStart = 16;
	// 遊戲結束 - 正常的遊戲結束
	public static final int C_GameOver = 18;
	// 金錢更新
	public static final int C_Gold = 19;
	// 獵人狀態
	public static final int C_HunterState = 20;
	// 獵人道具欄
	public static final int C_HunterInventory = 21;
	// 獵人開槍 -> type 0 : 開火 ; type 1 : 命中 ; type 2 : 銷毀
	public static final int C_HunterFire = 22;
	// 陷阱 -> type 0 : 建造; type 1 : 建造完成 ; 2 : 觸發 ; 3 : 銷毀
	public static final int C_Trap = 23;
	// 投擲道具
	public static final int C_Projectile = 24;
	// 一般傷害物件
	public static final int C_ApplyDamage = 25;
	//傳送門
	public static final int C_Portal = 26;
	//檢查遊玩人數
	public static final int C_CheckGameActivePlayer = 27;
	//寶藏
	public static final int C_Treasure = 28;

}
