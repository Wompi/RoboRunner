package robowiki.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoboRunnerConsole implements IMessageHandler
{
	private QueueWorker								myQueue;
	private final HashMap<String, SendWorker>		mySendConnections;
	private final HashMap<String, ReceiveWorker>	myReceiveConnections;

	public static void main(String[] args)
	{
		try
		{
			System.out.format("Start ...\n");
			System.setProperty(RoboRunnerDefines.PROCESS_NAME_KEY, "MAIN");
			new RoboRunnerConsole();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public RoboRunnerConsole() throws IOException, InterruptedException
	{
		mySendConnections = new HashMap<String, SendWorker>();
		myReceiveConnections = new HashMap<String, ReceiveWorker>();
		final Thread tConsole = new Thread(new ConsoleWorker(System.in, this, "CONSOLE"));
		final Thread tQueue = new Thread(myQueue = new QueueWorker(this));
		tConsole.start();
		tQueue.start();

		List<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-cp");
		command.add(System.getProperty("java.class.path"));
		command.add("robowiki.console.BattleProcessConsole");

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Map<String, String> env = builder.environment();

		// TODO: put the RoboCode installations in the configuration class

		final List<Process> myProcesses = new ArrayList<Process>();
		for (int i = 0; i < 2; i++)
		{
			String processName = String.format("PRO_%d", i);
			env.put(RoboRunnerDefines.PROCESS_NAME_KEY, processName);
			final Process battleProcess = builder.start();

			SendWorker sendWorker = new SendWorker(battleProcess.getOutputStream(), processName);
			ReceiveWorker receiveWorker = new ReceiveWorker(battleProcess.getInputStream(), this, processName);
			mySendConnections.put(processName, sendWorker);
			myReceiveConnections.put(processName, receiveWorker);

			final Thread tSend = new Thread(sendWorker);
			final Thread tReceive = new Thread(receiveWorker);
			tSend.start();
			tReceive.start();
			System.out.format("Process [%s] started ..\n", processName);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.format("Shutdown...\n");
				for (Process proc : myProcesses)
				{
					proc.destroy();
				}
			}
		}));

		//		RunnerMessage init = new RunnerMessage("MAIN");
		//		init.myCommand = RoboRunnerDefines.INIT_SIGNAL;
		//		init.myResult = "you can now init";
		//		init.myPriority = 1;
		//
		//		sendMessage(init);

	}

	@Override
	public void receiveMessage(ProcessMessage msg)
	{
		myQueue.addMessage(msg);
	}

	@Override
	public void sendMessage(RunnerMessage msg, String processName)
	{
		if (processName.equals(RoboRunnerDefines.ALL_PROCESSES))
		{
			for (SendWorker worker : mySendConnections.values())
			{
				worker.addMessage(msg);
			}
		}
		else
		{
			SendWorker worker = mySendConnections.get(processName);
			worker.addMessage(msg);
		}
	}
}
