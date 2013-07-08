package guard.server.server.clientpacket;


import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_SwapPosition;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_SwapPosition {

	public static final int C_SwapPosition_Request = 0;
	public static final int C_SwapPosition_Yes = 1;
	public static final int C_SwapPosition_No = 2;

	public C_SwapPosition(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;
		
		GameRoom room = pc.getRoom();
		if (room == null)
			return;
		
		GameInstance game = room.getGame();
		if (game == null)
			return;
		
		PlayerInstance target = GuardWorld.getInstance().getPlayer(_packet.split(C_PacketSymbol)[3]);
		if(target == null)
			return;
		
		switch(Integer.valueOf(_packet.split(C_PacketSymbol)[1])){
		case C_SwapPosition_Request:		
			target.SendClientPacket(_packet);
			
			break;
		case C_SwapPosition_No:
			pc.SendClientPacket(C_SwapPosition+C_PacketSymbol+
					C_SwapPosition_No+C_PacketSymbol+
					pc.getAccountName()+C_PacketSymbol+
					target.getAccountName());
			target.SendClientPacket(C_SwapPosition+C_PacketSymbol+
					C_SwapPosition_No+C_PacketSymbol+
					target.getAccountName()+C_PacketSymbol+
					pc.getAccountName());
			break;
		case C_SwapPosition_Yes:
			pc.SendClientPacket(C_SwapPosition+C_PacketSymbol+
					C_SwapPosition_Yes+C_PacketSymbol+
					pc.getAccountName()+C_PacketSymbol+
					target.getAccountName());
			target.SendClientPacket(C_SwapPosition+C_PacketSymbol+
					C_SwapPosition_Yes+C_PacketSymbol+
					target.getAccountName()+C_PacketSymbol+
					pc.getAccountName());
			break;
		}
		
		
		
	}
}
