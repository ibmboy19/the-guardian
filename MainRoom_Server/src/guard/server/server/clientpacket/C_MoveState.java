package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_MoveState {

	public static final int C_MoveState_Move = 1;// 移動

	public C_MoveState(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		if (pc == null)
			return;
		GameRoom room = pc.getRoom();
		if (room == null)
			return;
		if (pc.IsGuardian())
			return;
		HunterInstance hunter = (HunterInstance) pc.getWRPlayerInstance();

		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_MoveState_Move:
			hunter.UpdateMoveState(
					Integer.valueOf(_packet.split(C_PacketSymbol)[5]),
					Integer.valueOf(_packet.split(C_PacketSymbol)[6]),
					Integer.valueOf(_packet.split(C_PacketSymbol)[8]) == 1);
			room.broadcastPacketToRoom(_packet);
			break;
		}

	}
}
