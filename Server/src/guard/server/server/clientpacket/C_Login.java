package guard.server.server.clientpacket;

import static guard.server.server.clientpacket.ClientOpcodes.C_Login;
import static guard.server.server.clientpacket.ClientOpcodes.C_PacketSymbol;
import guard.server.LoginController;
import guard.server.server.Account;
import guard.server.server.ClientProcess;
import guard.server.server.model.GuardWorld;
import guard.server.server.model.instance.PlayerInstance;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * 處理C_Login封包
 */
public class C_Login {
	public C_Login(ClientProcess _client, String packet) throws IOException,
			NoSuchAlgorithmException {
		// 從 CLIENT 取得帳號

		String accountName = packet.split(C_PacketSymbol)[1];// id

		String ip = _client.getIp();
		if (GuardWorld.getInstance().CheckAccountExists(accountName)) {
			_client.SendClientPacket(C_Login + C_PacketSymbol + "false");
			
			return;
		}
		Account account = Account.create(accountName, ip);

		try {

			LoginController.getInstance().login(_client, account);

			_client.setAccount(account);
			// TODO SERVER -> CLIENT 確認封包 (ClientOpcodes.C_Login)

			PlayerInstance pc = new PlayerInstance(_client);
			pc.setAccountName(account.getName());
			pc.setNetConnection(_client);
			_client.setActiveChar(pc);

			GuardWorld.getInstance().StorePlayer(pc);

			pc.SendClientPacket(C_Login + C_PacketSymbol + "true"
					+ C_PacketSymbol + account.getName());

			System.out.format("帳號: %s 已經登入\n", accountName);
		} catch (Exception e) {
			e.getStackTrace();
			return;
		}
	}
}
