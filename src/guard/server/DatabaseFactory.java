package guard.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatabaseFactory {
	/** 紀錄用 */
	private static Logger _log = Logger.getLogger(DatabaseFactory.class.getName());
	
	/** 資料庫的實例 */
	private static DatabaseFactory _instance;

	/** 資料庫連結的來源 */
	private ComboPooledDataSource _source;

	/* 連結資料庫相關的資訊 */
	/** 資料庫連結的驅動程式 */
	private static String _driver;

	/** 資料庫連結的位址 */
	private static String _url;

	/** 登入資料庫的使用者名稱 */
	private static String _user;

	/** 登入資料庫的密碼 */
	private static String _password;
	
	/**
	 * 取得資料庫的實例（第一次實例為 null 的時候才新建立一個).
	 * 
	 * @return L1DatabaseFactory
	 * @throws SQLException
	 */
	public static DatabaseFactory getInstance() throws SQLException {
		if (_instance == null) {
			_instance = new DatabaseFactory();
		}
		return _instance;
	}

	/**
	 * 設定資料庫登入的相關資訊
	 * 
	 * @param driver 
	 *            資料庫連結的驅動程式
	 * @param url
	 *            資料庫連結的位址
	 * @param user
	 *            登入資料庫的使用者名稱 
	 * @param password
	 *            登入資料庫的密碼
	 */
	public static void setDatabaseSettings(final String driver,
			final String url, final String user, final String password) {
		_driver = driver;
		_url = url;
		_user = user;
		_password = password;
	}

	/**
	 * 資料庫連結的設定與配置
	 * 
	 * @throws SQLException
	 */
	public DatabaseFactory() throws SQLException {
		try {
			_source = new ComboPooledDataSource();
			_source.setDriverClass(_driver);
			_source.setJdbcUrl(_url);
			_source.setUser(_user);
			_source.setPassword(_password);

			/* Test the connection */
			_source.getConnection().close();
		} catch (SQLException x) {
			_log.fine("Database Connection FAILED");
			// rethrow the exception
			throw x;
		} catch (Exception e) {
			_log.fine("Database Connection FAILED");
			throw new SQLException("could not init DB connection:" + e);
		}
	}

	/**
	 * 伺服器關閉的時候要關閉與資料庫的連結
	 */
	public void shutdown() {
		try {
			_source.close();
		} catch (Exception e) {
			_log.log(Level.INFO, "", e);
		}
		try {
			_source = null;
		} catch (Exception e) {
			_log.log(Level.INFO, "", e);
		}
	}

	/**
	 * 取得資料庫連結時的連線
	 * 
	 * @return Connection 連結對象
	 * @throws SQLException
	 */
	public Connection getConnection() {
		Connection con = null;

		while (con == null) {
			try {
				con = _source.getConnection();
			} catch (SQLException e) {
				_log.warning("L1DatabaseFactory: getConnection() failed, trying again "+ e);
			}
		}
		return con;
	}

}
