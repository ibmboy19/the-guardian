package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import static guard.server.server.clientpacket.ClientOpcodes.C_RefreshAllPlayersList;
import guard.server.server.ClientProcess;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.instance.PlayerInstance;

public class C_RefreshAllPlayersList {
	public C_RefreshAllPlayersList(ClientProcess _client, String _packet) {
		// TODO 回傳所有不在房間的玩家資訊給該玩家
		/** 不顯示的玩家：已加入房間 */
		String _retPacket = String.valueOf(C_RefreshAllPlayersList);
		for (PlayerInstance _pc : GuardWorld.getInstance().getAllPlayers()) {
			if (!_pc.isInRoom()) {
				_retPacket += C_PacketSymbol + _pc.getPlayerInfoPacket();
			}
		}
		_client.getActiveChar().SendClientPacket(_retPacket);
	}
}
