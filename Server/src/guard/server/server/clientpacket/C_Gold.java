package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_Gold;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.instance.PlayerInstance;

public class C_Gold {
	public static final int C_Gold_Effect = 0;//effect on
	public static final int C_Gold_Normal = 1;//effect off

	public C_Gold(ClientProcess _client, String _packet) {
		PlayerInstance _pc = _client.getActiveChar();
		if (_pc == null)
			return;
		if (_pc.getWRPlayerInstance() == null)
			return;		
		_pc.SendClientPacket(C_Gold + C_PacketSymbol
				+ String.valueOf(C_Gold_Normal) + C_PacketSymbol
				+ String.valueOf(_pc.getWRPlayerInstance().getGold()));
		
	}
}
