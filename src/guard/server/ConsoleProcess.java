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
package guard.server;

import java.util.Scanner;

/**
 * cmd 互動命令 處理程序
 */
public class ConsoleProcess extends Thread {
	/** 使用者輸入 */
	private Scanner UserInput = new Scanner(System.in);
	/** 開機後是否開啟此功能 */
	private boolean onStarup = true;
	/** 程序是否繼續 */
	private boolean stillrun = true;

	Runtime rt = Runtime.getRuntime();

	public ConsoleProcess() {
		onStarup = true;
		if (!onStarup)
			return;
		System.out.println("→提示: 互動指令聽取中..." + "\n" + ">");
	}

	/**
	 * 指令執行(有引數)
	 * 
	 * @param cmd
	 *            指令名稱
	 * @param line
	 *            指令引數
	 */
	private void execute(String cmd, String line) {
		if (cmd == null || line == null) {
			System.out.println("error, please input cmd words or args.");
			return;
		}
		if (cmd.equalsIgnoreCase("chat")) {// cmd與遊戲內對話功能

		} else {
			System.out.println("error, doesn't have the command.");
			return;
		}

	}

	/**
	 * 指令執行
	 * 
	 * @param cmd
	 *            指令名稱
	 */
	private void execute(String cmd) {
		if (cmd == null) {
			System.out.println("error, please input cmd words.");
			return;
		}
		if (cmd.equalsIgnoreCase("lookup")) {// cmd查看遊戲內對話功能
			// TODO 開啟另一個視窗並顯示遊戲內對話
		} else {
			System.out.println("error, doesn't have the command.");
			return;
		}
	}

	@Override
	public void run() {
		while (onStarup && stillrun) {
			String action = UserInput.nextLine();

			String word[] = action.split(" ");
			if (word.length == 1) {
				execute(word[0]);
			}
			if (word.length == 2) {
				execute(word[0], word[1]);

			}
		}
		System.out.println("→提示: 互動指令聽取中..." + "\n" + ">");
	}
}
