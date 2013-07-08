/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package guard.server.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;

public class AnnouncementsCycle {
	private int round = 0;
	private String line = null;
	private boolean firstboot = true;;
	private StringBuffer sb = new StringBuffer();
	private static AnnouncementsCycle _instance;

	/** 緩衝讀取 */
	private static BufferedReader buf;

	/** announcementsCycle文件的位置 */
	private static File dir = new File("data/announceCycle.txt");

	/** 紀錄上一次修改時間 */
	private static long lastmodify = dir.lastModified();

	/** 在公告首顯示公告修改時間 */
	private boolean AnnounceTimeDisplay = true;

	/** 容器 */
	List<String> list = new FastList<String>();

	private AnnouncementsCycle() {
		cycle();
	}

	public static AnnouncementsCycle getInstance() {
		if (_instance == null) {
			_instance = new AnnouncementsCycle();
		}
		return _instance;
	}

	/**
	 * 從announcementsCycle.txt將字串讀入
	 */
	private void scanfile() {
		try {
			fileEnsure(); // 先確保檔案存在
			if (dir.lastModified() > lastmodify || firstboot) { // 如果有修改過
				list.clear(); // 清空容器
				buf = new BufferedReader(new InputStreamReader(new FileInputStream(dir)));
				while ((line = buf.readLine()) != null) {
					if (line.startsWith("#")||line.isEmpty()) // 略過註解
						continue;
					sb.delete(0, sb.length()); // 清空 buffer [未來擴充用]
					list.add(line);
				}
				lastmodify = dir.lastModified(); // 回存修改時間
			} else {
				// 檔案沒修改過，不做任何事。
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				buf.close();
				firstboot = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 確保announcementsCycle.txt存在
	 * 
	 * @throws IOException
	 *             產生檔案錯誤
	 */
	private void fileEnsure() throws IOException {
		if (!dir.exists())
			dir.createNewFile();
	}

	private void cycle() {
		AnnouncementsCycleTask task = new AnnouncementsCycleTask();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(task, 15000, 15000 * 1); // 10分鐘公告一次
	}

	/**
	 * 處理廣播字串任務
	 */
	class AnnouncementsCycleTask implements Runnable {
		@Override
		public void run() {
			scanfile();
			// 啟用修改時間顯示 - 〈yyyy.MM.dd〉
			if (AnnounceTimeDisplay) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
				ShowAnnouncementsCycle("〈"+ formatter.format(new Date(lastmodify)) + "〉");
			}
			Iterator<String> iterator = list.listIterator();
			if (iterator.hasNext()) {
				round %= list.size();
				ShowAnnouncementsCycle(list.get(round));
				round++;
			}
			System.out.println("Announcement");
		}
	}

	/**
	 * 把字串廣播到伺服器上
	 */
	private void ShowAnnouncementsCycle(String announcement) {
		/*Collection<PcInstance> AllPlayer = CursedWorld.getInstance().getAllPlayers();
		for (PcInstance pc : AllPlayer){
			pc.sendpackets(ServerOpcodes.S_announcecycle);
			pc.sendpackets(announcement);
		}*/
	}
}
