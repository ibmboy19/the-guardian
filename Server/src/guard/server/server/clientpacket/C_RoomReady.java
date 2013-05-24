package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Guardian;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Hunter;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.GuardianInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;
import guard.server.server.utils.collections.Lists;

import java.util.List;

public class C_RoomReady {

	/**
	 * Ready Code -> 0 : Ready ,1 : Start
	 * 
	 * */
	public static final int C_RoomReady_Ready = 0;
	public static final int C_RoomReady_Start = 1;

	public C_RoomReady(ClientProcess _client, String _packet) {

		/**
		 * 每個房間內的玩者都需要按Ready 若全數玩家都為Ready，Host會顯示Start
		 * 
		 * Host一旦按下Start 會開始分配Guardian與Hunter
		 * 
		 * 分配Guardian 或 Hunter給PC物件
		 * 
		 * 分配完成後開始遊戲
		 * 
		 * */
		PlayerInstance pc = _client.getActiveChar();
		/**
		 * 不合法狀況 : 玩家不在房間中 房間為空 玩家已經準備好
		 * */
		if (!pc.isInRoom())
			return;
		GameRoom _room = pc.getRoom();
		if (_room == null)
			return;
		switch (Integer.parseInt(_packet.split(C_PacketSymbol)[1])) {
		case C_RoomReady_Ready:
			/**
			 * Server接收到準備狀態封包 TODO 檢查玩家狀態，檢查房間狀態。
			 * */
			if (pc.IsReady())
				return;
			// 玩家準備好
			pc.Ready();
			// 檢查房間狀態
			_room.CheckReadyState();
			break;
		case C_RoomReady_Start://
			/**
			 * 
			 * Server接收到HOST開始遊戲的封包。 TODO 產生玩家對應的角色模組 : Guardian或Hunter。 TODO
			 * 傳送Start封包，遊戲使用的模組給玩家
			 * 
			 * Server接收到HOST開始遊戲的封包 TODO 產生玩家對應的角色模組 : Guardian或Hunter TODO
			 * 傳送Start封包，遊戲使用的模組給玩家
			 * 
			 * */
			if (_room.getGame() != null && _room.getGame().IsReady())
				return;

			/* 角色模組 */
			List<HunterInstance> _hunterList = Lists.newList();
			GuardianInstance _guardian = null;
			/* 分派角色模組 */
			for (PlayerInstance member : _room.getMembers()) {
				switch (member.getPlayerType()) {
				case PlayerType_Hunter:// Hunter
					_hunterList.add(new HunterInstance(_room, member));
					member.setwrPlayerInstance(_hunterList.get(_hunterList
							.size() - 1));
					break;
				case PlayerType_Guardian:// Guardian
					_guardian = new GuardianInstance(_room, member);
					member.setwrPlayerInstance(_guardian);
					break;
				}
			}
			_room.getGame().DispatchPlayer(_hunterList,_guardian);
			_room.getGame().startGameTimer(0);
			break;
		}

	}
}
