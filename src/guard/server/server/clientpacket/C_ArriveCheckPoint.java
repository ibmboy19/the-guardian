package guard.server.server.clientpacket;

import static guard.server.server.model.instance.PlayerInstance.PlayerType_Guardian;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_ArriveCheckPoint {
	/** 獵人選擇一個重生點，才可以看到其他玩家或自身 */
	public C_ArriveCheckPoint(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;
		// 過濾 若發送者為Guardian不處理
		if (pc.getPlayerType() == PlayerType_Guardian)
			return;
		GameRoom room = pc.getRoom();
		if (room == null)
			return;
		GameInstance game = room.getGame();
		if (game == null)
			return;
		if (pc.IsGuardian())
			return;
		HunterInstance _hunter = (HunterInstance) pc.getWRPlayerInstance();

		game.ArriveCheckPoint(_packet, _hunter);

	}
}
