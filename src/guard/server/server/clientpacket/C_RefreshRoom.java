package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_RefreshRoom;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.GuardWorld;

public class C_RefreshRoom {
	public C_RefreshRoom(ClientProcess _client, String _packet) {
		// TODO 回傳所有人數未滿的房間給該玩家
		/** 不顯示的房間 : 1.人數滿的 */
		String _retPacket = String.valueOf(C_RefreshRoom);
		String _roomInfoPacket = "";
		for (GameRoom _room : GuardWorld.getInstance().getAllRooms()) {
			if (_room.isVacancy() && _room.getGame().IsWaitingPlayers()) {
				_roomInfoPacket += _room.getGameRoomInfoPacket()+";";
			}
		}
		System.out.println("all rooms count : "+GuardWorld.getInstance().getAllRooms().size());
		if(_roomInfoPacket != ""){
			_roomInfoPacket = _roomInfoPacket.substring(0, _roomInfoPacket.length()-1);
			_retPacket += C_PacketSymbol + _roomInfoPacket;
		}
		_client.getActiveChar().SendClientPacket(_retPacket);
	}
}
