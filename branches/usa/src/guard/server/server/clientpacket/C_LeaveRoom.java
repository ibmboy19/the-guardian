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

		GameRoom room = pc.getRoom();
		if (room == null) {
			System.out.println("leave null room");
			return;
		}

		// 只有室長可以在房間上鎖時解散房間
		if (room.IsLocked() && !room.isLeader(pc)) {
			System.out.println("try leave locked room , not leader");
			return;
		}

		// 遊戲即將開始，不能中離
		if (room.getGame().IsReady()) {
			System.out.println("game starts in few secs..");
			return;
		}

		room.leaveRoom(pc);
	}
}
