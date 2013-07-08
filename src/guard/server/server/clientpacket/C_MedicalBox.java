package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_ApplyDamage;
import static guard.server.server.clientpacket.ClientOpcodes.C_MedicalBox;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.server.ClientProcess;
import guard.server.server.model.GameRoom;
import guard.server.server.model.instance.GameInstance;
import guard.server.server.model.instance.PlayerInstance;

public class C_MedicalBox {

	public static final int C_MedicalBox_Init = 0;
	public static final int C_MedicalBox_Destroy = 1;

	public C_MedicalBox(ClientProcess _client, String _packet) {

		PlayerInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}

		GameRoom room = pc.getRoom();
		if (room == null) {
			return;
		}

		GameInstance game = room.getGame();
		if (game == null) {
			return;
		}

		switch (Integer.valueOf(_packet.split(C_PacketSymbol)[1])) {
		case C_MedicalBox_Init:
			// System.out.println(_packet);
			game.InitMedicalBox(_packet.split(C_PacketSymbol)[2]);
			// boreadcast and spawn Medical Box
			room.broadcastPacketToRoom(_packet);
			break;
		case C_MedicalBox_Destroy:
			if (game.CheckMedicalBox(Integer.valueOf(_packet
					.split(C_PacketSymbol)[2]))) {
				new C_ApplyDamage(_client, String.valueOf(C_ApplyDamage)
						+ C_PacketSymbol + _packet.split(C_PacketSymbol)[3]);
				room.broadcastPacketToRoom(String.valueOf(C_MedicalBox)
						+ C_PacketSymbol + String.valueOf(C_MedicalBox_Destroy)
						+ C_PacketSymbol + _packet.split(C_PacketSymbol)[2]);

			}
			break;
		}

	}
}
