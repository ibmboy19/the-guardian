package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_ArriveCheckPoint;
import static guard.server.server.clientpacket.ClientOpcodes.C_Chat;
import static guard.server.server.clientpacket.ClientOpcodes.C_CreateRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_Gold;
import static guard.server.server.clientpacket.ClientOpcodes.C_HunterInventory;
import static guard.server.server.clientpacket.ClientOpcodes.C_JoinRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_LeaveRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_LoadMapDone;
import static guard.server.server.clientpacket.ClientOpcodes.C_Login;
import static guard.server.server.clientpacket.ClientOpcodes.C_Logout;
import static guard.server.server.clientpacket.ClientOpcodes.C_MoveState;
import static guard.server.server.clientpacket.ClientOpcodes.C_NetDelay;
import static guard.server.server.clientpacket.ClientOpcodes.C_RefreshAllPlayersList;
import static guard.server.server.clientpacket.ClientOpcodes.C_RefreshRoom;
import static guard.server.server.clientpacket.ClientOpcodes.C_RequestRemaingTime;
import static guard.server.server.clientpacket.ClientOpcodes.C_RoomReady;
import static guard.server.server.clientpacket.ClientOpcodes.C_SelectPlayerSpawnPoint;
import static guard.server.server.clientpacket.ClientOpcodes.C_SwitchPlayerType;
import guard.server.server.ClientProcess;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.instance.PlayerInstance;

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
				break;
			case C_RequestRemaingTime:
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
			case C_Gold:
				new C_Gold(_client, packet);
				break;
			case C_HunterInventory:
				new C_HunterInventory(_client, packet);
				break;
			}
		} catch (NumberFormatException nf) {
			System.out.println("接收到一個null.");
		} catch (SocketException e) {			
			System.out.println(e);
			//TODO ConnectionReset 處理，暫用
			PlayerInstance pc = _client.getActiveChar();
			//case 房間中
			if(pc != null){
				if(pc.isInRoom())					
					pc.getRoom().leaveRoom(pc);
				GuardWorld.getInstance().RemovePlayer(pc);
			}
			//case 遊戲中
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
