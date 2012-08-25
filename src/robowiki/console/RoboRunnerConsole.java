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
	private final HashMap<String, RunnerChallenge>	myChallMap;

	public static void main(String[] args)
	{
		try
		{
			String version = "1.3.0";
			System.setProperty(RoboRunnerDefines.PROCESS_NAME_KEY, "MAIN");
			System.setProperty(RoboRunnerDefines.VERSION_KEY, version);
			System.out.format("Welcome to RoboRunner %s. Type HELP or '?' if unsure.\n", version);
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
		myChallMap = new HashMap<String, RunnerChallenge>();
		if (RoboRunnerConfig.getInstance().isFirstRun())
		{
			System.out.format("Looks like this is your first run - type CONFIG to setup the system.\n");
		};
		final Thread tQueue = new Thread(myQueue = new QueueWorker(this));
		tQueue.start();
		final Thread tConsole = new Thread(new ConsoleWorker(System.in, this, "CONSOLE"));
		tConsole.start();
	}

	public void startProcesses() throws IOException
	{
		if (mySendConnections.size() > 0)
		{
			System.out.format("System is still started use RUN instead\n");
			return; //TODO: maybe send a auto run 
		}

		int processCount;
		try
		{
			processCount = Integer.parseInt(RoboRunnerConfig.getInstance().getInstallCount());
		}
		catch (Exception e)
		{
			System.out.format("Sorry, the configuration is not valid. Type CONFIG to setup the values.\n");
			return;
		}

		String workDir = System.getProperty("user.dir");
		String sep = System.getProperty("file.separator");
		if (!workDir.endsWith(sep)) workDir += sep;

		final List<Process> myProcesses = new ArrayList<Process>();
		for (int i = 0; i < processCount; i++)
		{
			String processName = String.format("PRO_%d", i);
			String processPath = String.format("%s%s%src%d", workDir, RoboRunnerDefines.ROBOCODE_DIR_NAME, sep, i);
			String cp = String.format("%s%s%s%s%s", processPath, sep, "libs", sep, "robocode.jar");
			cp += String.format("%s%s%s%s%s", System.getProperty("path.separator"), workDir, "libs", sep, "roborunner.jar");

			List<String> command = new ArrayList<String>();
			command.add("java");
			command.add("-cp");
			command.add(cp);
			command.add("robowiki.console.BattleProcessConsole");

			ProcessBuilder builder = new ProcessBuilder(command);
			builder.redirectErrorStream(true);
			Map<String, String> env = builder.environment();

			if (!RunnerFunctions.checkPath(processPath, false, true))
			{
				// this should not be happen - only if the user deletes the installation directories manually
				System.out.format("Sorry, your installations are not valid. Type CONFIG to setup the values.\n");
				return;
			}

			env.put(RoboRunnerDefines.PROCESS_NAME_KEY, processName);
			env.put(RoboRunnerDefines.PROCESS_PATH_KEY, processPath);
			final Process battleProcess = builder.start();

			SendWorker sendWorker = new SendWorker(battleProcess.getOutputStream(), processName);
			mySendConnections.put(processName, sendWorker);

			final Thread tSend = new Thread(sendWorker);
			final Thread tReceive = new Thread(new ReceiveWorker(battleProcess.getInputStream(), this, processName));
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
