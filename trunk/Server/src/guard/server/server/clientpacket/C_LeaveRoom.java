package guard.server.server.clientpacket;

import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.PlayerInstance;

public class C_LeaveRoom {
	/**
	 * Leave Code -> 0 : break up, 1 : leave, 2 : other pc leave
	 * 
	 * */
	public static final int C_LeaveRoom_BreakUp = 0;
	public static final int C_LeaveRoom_OtherLeave = 1;
	public static final int C_LeaveRoom_PCLeave = 2;

	public C_LeaveRoom(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		// Check IN ROOM
		if (!pc.isInRoom()) {
			return;
		}
		GameRoom _room = pc.getRoom();
		if (_room.IsLocked()) {
			return;
		}
		_room.leaveRoom(pc);
	}
}
