package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_JoinRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.instance.PlayerInstance;

public class C_JoinRoom {
	/** Join Code -> 0 : join fail, 1 : other pc join, 2 : pc join */
	public static final int C_JoinRoom_JoinFail = 0;
	public static final int C_JoinRoom_OtherJoin = 1;
	public static final int C_JoinRoom_PCJoin = 2;

	public static String getJoinFailedPacket() {
		return String.valueOf(C_JoinRoom) + C_PacketSymbol
				+ String.valueOf(C_JoinRoom_JoinFail);
	}

	public C_JoinRoom(ClientProcess _client, String _packet) {
		PlayerInstance pc = _client.getActiveChar();
		/** 驗證房間正確性 */
		// 已在房間
		if (pc.isInRoom()) {
			// 加入失敗，回傳訊息
			pc.SendClientPacket(getJoinFailedPacket());
			return;
		}
		GameRoom _room = GuardWorld.getInstance().getRoom(
				_packet.split(C_PacketSymbol)[1]);
		// 不存在的房間 或 房間正準備開始
		if (_room == null || _room.IsLocked()) {
			// 不存在或上鎖的房間，回傳加入失敗訊息
			pc.SendClientPacket(getJoinFailedPacket());
			return;
		}

		// 驗證地圖碼
		if (_room.getMap().getMapCheckCode()
				.equals(_packet.split(C_PacketSymbol)[2])) {
			// 驗證通過，嘗試加入房間
			_room.joinRoom(pc);

		} else {
			// 驗證失敗，回傳加入失敗訊息
			pc.SendClientPacket(getJoinFailedPacket());
		}

	}
}
