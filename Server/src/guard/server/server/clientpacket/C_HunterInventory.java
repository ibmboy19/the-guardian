package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.instance.HunterInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_HunterInventory {
	public static final int C_HunterInventory_BuyItem = 0;
	public static final int C_HunterInventory_UseItem = 1;

	public C_HunterInventory(ClientProcess _client, String _packet) {

		PlayerInstance _pc = _client.getActiveChar();
		if (_pc == null)
			return;
		if (_pc.getRoom() == null)
			return;
		if (_pc.IsGuardian())
			return;

		HunterInstance _hunter = (HunterInstance) _pc.getWRPlayerInstance();

		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_HunterInventory_BuyItem:
			_hunter.BuyItem(Integer.valueOf(_packet.split(C_PacketSymbol)[2]),
					Integer.valueOf(_packet.split(C_PacketSymbol)[3]));
			break;
		case C_HunterInventory_UseItem:
			_hunter.UseItem(Integer.valueOf(_packet.split(C_PacketSymbol)[2]));
			break;
		}

	}
}
