package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_RequestRemaingTime;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_RequestRemaingTime {
	public C_RequestRemaingTime(ClientProcess _client, String _packet) {

		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;
		GameRoom room = pc.getRoom();
		if (room == null)
			return;
		GameInstance game = room.getGame();
		if (game == null)
			return;
		if(game.IsGaming())
			pc.SendClientPacket(String.valueOf(C_RequestRemaingTime)
				+ C_PacketSymbol + String.valueOf(game.getRemainingGameTime()));
		
		
	}
}
