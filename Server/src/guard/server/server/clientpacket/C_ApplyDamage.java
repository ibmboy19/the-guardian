package guard.server.server.clientpacket;

import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_ApplyDamage {
	public C_ApplyDamage(ClientProcess _client,String _packet){
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}
		GameRoom room = pc.getRoom();
		if (room == null) {
			return;
		}
		GameInstance game = room.getGame();
		if (game == null) {
			return;
		}
		if (pc.IsGuardian())
			return;
		HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();
		
	}
}
