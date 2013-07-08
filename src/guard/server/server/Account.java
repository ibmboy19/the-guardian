package guard.server.server;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Account {
	/** 使用者帳號名稱 */
	private String _name;

	/** 來源IP位址 */
	private String _ip;

	/** 密碼 */
	//private String _password;

	/** 紀錄用 */
	private static Logger _log = Logger.getLogger(Account.class.getName());

	/** 帳戶是否有效 (True 代表有效). */
	private boolean _isValid = true;

	/**
	 * 建構式
	 */
	private Account() {
	}

	/**
	 * 創立帳號
	 * @param name
	 * @param rawPassword
	 * @param ip
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static Account create(final String name, final String ip) throws NoSuchAlgorithmException,UnsupportedEncodingException {
		Account account = new Account();
		account._name = name;
		account._ip = ip;
		return account;
	}

	/**
	 * 取得帳號名稱
	 * 
	 * @return String
	 */
	public String getName() {
		return _name;
	}

	/**
	 * 取得連線時的 IP
	 * 
	 * @return String
	 */
	public String getIp() {
		return _ip;
	}

	/**
	 * 取得帳號使否有效 (True 為有效).
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		return _isValid;
	}

}
