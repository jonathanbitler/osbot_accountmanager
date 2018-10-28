package osbot.threads;

import java.util.ArrayList;
import java.util.Iterator;

import osbot.database.DatabaseUtilities;

public class ThreadHandler {

	/**
	 * As long as this is true, the main thread will be running
	 */
	private static boolean programIsRunning = true;

	/**
	 * A list of all the threads in the program
	 */
	private static ArrayList<Thread> threadList = new ArrayList<Thread>();

	/**
	 * The thread for selenium trying to create accounts
	 */
	private static void createAccountsThread() {
		Thread createAccounts = new Thread(() -> {
			DatabaseUtilities.seleniumCreateAccountThread();
		});
		createAccounts.setName("createAccounts");
		createAccounts.start();

		threadList.add(createAccounts);
	}

	/**
	 * The thread for selenium trying to recover account
	 */
	private static void recoverAccountsThread() {
		Thread recoverAccounts = new Thread(() -> {
			DatabaseUtilities.seleniumRecoverAccount();
		});
		recoverAccounts.setName("recoverAccounts");
		recoverAccounts.start();

		threadList.add(recoverAccounts);
	}

	/**
	 * Manages all the threads currently running
	 */
	public static void runThreads() {
		Thread mainThread = new Thread(() -> {
			while (programIsRunning) {

				System.out.println(
						"Thread management: " + isThreadAlive("recoverAccounts") + " " + getThread("recoverAccounts"));
				System.out.println(
						"Thread management: " + isThreadAlive("createAccounts") + " " + getThread("createAccounts"));
				
				if (!isThreadAlive("recoverAccounts") || getThread("recoverAccounts") == null) {
					recoverAccountsThread();
					System.out.println("Started new thread: recover accounts");
				}

				if (!isThreadAlive("createAccounts") || getThread("createAccounts") == null) {
					createAccountsThread();
					System.out.println("Started new thread: createAccounts");
				}

				// Thread sleeping & checking every 30 seconds
				try {
					Thread.sleep(120_000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Checking every 120 seconds for threads to be alive or not");
			}
		});
		mainThread.setName("mainThread");
		mainThread.start();

		threadList.add(mainThread);
	}

	/**
	 * 
	 * @param threadName
	 * @return
	 */
	private static Thread getThread(String threadName) {
		for (Thread thread : threadList) {
			if (thread.getName().equalsIgnoreCase(threadName)) {
				return thread;
			}
		}
		return null;
	}

	/**
	 * Is the thread still alive or not?
	 * 
	 * @param threadName
	 * @return
	 */
	public static boolean isThreadAlive(String threadName) {
		for (Thread thread : threadList) {
			if (thread.getName().equalsIgnoreCase(threadName) && thread.isAlive()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private static void checkForAlive() {
		Iterator<Thread> threads = threadList.iterator();

		while (threads.hasNext()) {
			Thread thread = threads.next();

			if (!thread.isAlive()) {
				// Thread wasn't alive anymore, deleted it
				System.out.println("Removed thread:" + thread.getName() + " because it wasn't active anymore");
				threads.remove();
			}
		}
	}

	/**
	 * @return the threadList
	 */
	public static ArrayList<Thread> getThreadList() {
		return threadList;
	}

	/**
	 * @param threadList
	 *            the threadList to set
	 */
	public static void setThreadList(ArrayList<Thread> threadList) {
		ThreadHandler.threadList = threadList;
	}

	/**
	 * @return the programIsRunning
	 */
	public static boolean isProgramIsRunning() {
		return programIsRunning;
	}

	/**
	 * @param programIsRunning
	 *            the programIsRunning to set
	 */
	public static void setProgramIsRunning(boolean programIsRunning) {
		ThreadHandler.programIsRunning = programIsRunning;
	}
}
