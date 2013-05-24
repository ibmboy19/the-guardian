package guard.server.server.clientpacket;

import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import static guard.server.server.model.instance.PlayerInstance.PlayerType_Guardian;
import guard.server.server.model.instance.PlayerInstance;

public class C_SelectPlayerSpawnPoint {
	/** 獵人選擇一個重生點，才可以看到其他玩家或自身 */
	public C_SelectPlayerSpawnPoint(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;
		//過濾 若發送者為Guardian不處理
		if(pc.getPlayerType() == PlayerType_Guardian)
			return;
		GameRoom room = pc.getRoom();
		if (room == null)
			return;
		
		room.broadcastPacketToRoom(_packet);
	}
}
