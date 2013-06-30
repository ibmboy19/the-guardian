package guard.server.server.clientpacket;

import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_GameOver {
	
	public static final int C_GameOver_Result = 0;
	public static final int C_GameOver_ShowHero = 1;
	
	public C_GameOver(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;

		GameRoom room = pc.getRoom();
		if (room == null)
			return;

		GameInstance game = room.getGame();
		if (game == null)
			return;

		if (pc.IsGuardian())
			return;

		HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();

		if (game.getTreasure().IsOwner(hunter)) {
			game.GameOver();
		}

	}
}
