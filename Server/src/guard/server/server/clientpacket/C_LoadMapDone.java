package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_LoadMapDone;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Guardian;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Hunter;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_LoadMapDone {

	/**
	 * Chat Code -> 0 : Init 載入完畢，初始化資料 , 1 : Done 全體載入完成 ,
	 */
	public static final int C_LoadMapDone_Init = 0;
	public static final int C_LoadMapDone_Done = 1;

	public C_LoadMapDone(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;
		GameRoom _room = pc.getRoom();
		if (_room == null)
			return;
		GameInstance _game = _room.getGame();
		if (_game == null)
			return;

		pc.LoadMapDone();

		String _retpacket = String.valueOf(C_LoadMapDone) + C_PacketSymbol
				+ String.valueOf(C_LoadMapDone_Init) + C_PacketSymbol
				+ String.valueOf(_game.getMap().getGameMode());
		// TODO 回傳玩家資訊
		switch (pc.getPlayerType()) {
		case PlayerType_Hunter:
			break;
		case PlayerType_Guardian:
			_retpacket += C_PacketSymbol + pc.getPlayerModelDataPacket();
			break;
		}

		pc.SendClientPacket(_retpacket);

		_game.CheckAllPlayerLoadMapDone();

	}
}
