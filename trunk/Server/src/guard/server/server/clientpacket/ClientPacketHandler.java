package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.*;
import static guard.server.server.clientpacket.ClientOpcodes.C_ArriveCheckPoint;
import static guard.server.server.clientpacket.ClientOpcodes.C_Chat;
import static guard.server.server.clientpacket.ClientOpcodes.C_CheckGameActivePlayer;
import static guard.server.server.clientpacket.ClientOpcodes.C_CreateRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_GameOver;
import static guard.server.server.clientpacket.ClientOpcodes.C_GameStart;
import static guard.server.server.clientpacket.ClientOpcodes.C_Gold;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterFire;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterInventory;
import static guard.server.server.clientpacket.ClientOpcodes.C_JoinRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_LeaveRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_LoadMapDone;
import static guard.server.server.clientpacket.ClientOpcodes.C_Login;
import static guard.server.server.clientpacket.ClientOpcodes.C_Logout;
import static guard.server.server.clientpacket.ClientOpcodes.C_MedicalBox;
import static guard.server.server.clientpacket.ClientOpcodes.C_MoveState;
import static guard.server.server.clientpacket.ClientOpcodes.C_NetDelay;
import static guard.server.server.clientpacket.ClientOpcodes.C_Portal;
import static guard.server.server.clientpacket.ClientOpcodes.C_Projectile;
import static guard.server.server.clientpacket.ClientOpcodes.C_RefreshAllPlayersList;
import static guard.server.server.clientpacket.ClientOpcodes.C_RefreshRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_RequestRemaingTime;
import static guard.server.server.clientpacket.ClientOpcodes.C_RoomReady;
import static guard.server.server.clientpacket.ClientOpcodes.C_SelectPlayerSpawnPoint;
import static guard.server.server.clientpacket.ClientOpcodes.C_Spawn;
import static guard.server.server.clientpacket.ClientOpcodes.C_SwitchPlayerType;
import static guard.server.server.clientpacket.ClientOpcodes.C_Trap;
import static guard.server.server.clientpacket.ClientOpcodes.C_Treasure;
import guard.server.server.ClientProcess;

import java.net.SocketException;

public class ClientPacketHandler {
	private final ClientProcess _client;

	public ClientPacketHandler(ClientProcess client) {
		_client = client;
	}

	public void handlePacket(final int op, final String packet) {
		try {
			switch (op) {
			case C_Login:
				new C_Login(_client, packet);
				break;
			case C_Logout:
				new C_Logout(_client, packet);
				break;
			case C_CreateRoom:
				new C_CreateRoom(_client, packet);
				break;
			case C_JoinRoom:
				new C_JoinRoom(_client, packet);
				break;
			case C_LeaveRoom:
				new C_LeaveRoom(_client, packet);
				break;
			case C_RefreshRoom:
				new C_RefreshRoom(_client, packet);
				break;
			case C_Chat:
				new C_Chat(_client, packet);
				break;
			case C_RoomReady:
				new C_RoomReady(_client, packet);
				break;
			case C_NetDelay:
				new C_NetDelay(_client);
				break;
			case C_SwitchPlayerType:
				new C_SwitchPlayerType(_client, packet);
				break;
			case C_SelectPlayerSpawnPoint:
				new C_SelectPlayerSpawnPoint(_client, packet);
				break;
			case C_ArriveCheckPoint:
				new C_ArriveCheckPoint(_client, packet);
				break;
			case C_RequestRemaingTime:
				new C_RequestRemaingTime(_client, packet);
				break;
			case C_MoveState:
				new C_MoveState(_client, packet);
				break;
			case C_RefreshAllPlayersList:
				new C_RefreshAllPlayersList(_client, packet);
				break;
			case C_LoadMapDone:
				new C_LoadMapDone(_client, packet);
				break;
			case C_GameStart:
				break;
			case C_GameOver:
				new C_GameOver(_client, packet);
				break;
			case C_Gold:
				new C_Gold(_client, packet);
				break;
			case C_HunterInventory:
				new C_HunterInventory(_client, packet);
				break;
			case C_HunterFire:
				new C_HunterFire(_client, packet);
				break;
			case C_Trap:
				new C_Trap(_client, packet);
				break;
			case C_Projectile:
				new C_Projectile(_client, packet);
				break;
			case C_ApplyDamage:
				new C_ApplyDamage(_client, packet);
				break;
			case C_Portal:
				new C_Portal(_client, packet);
				break;
			case C_CheckGameActivePlayer:
				new C_CheckGameActivePlayer(_client, packet);
				break;
			case C_Treasure:
				new C_Treasure(_client, packet);
				break;
			case C_Spawn:
				new C_Spawn(_client, packet);
				break;
			case C_MedicalBox:
				new C_MedicalBox(_client, packet);
				break;
			case C_MonsterFire:
				new C_MonsterFire(_client, packet);
				break;
			case C_UpgradeObject:
				new C_UpgradeObject(_client, packet);
				break;
			case C_GuardianFire:
				new C_GuardianFire(_client, packet);
				break;
			case C_SwapPosition:
				new C_SwapPosition(_client,packet);
				break;
			}
		} catch (NumberFormatException nf) {
			System.out.println("接收到一個null.");
		} catch (SocketException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
