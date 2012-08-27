package robowiki.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoboRunnerConsole implements IMessageHandler
{
	private QueueWorker							myQueue;
	private final HashMap<String, SendWorker>	mySendConnections;

	public static void main(String[] args)
	{
		try
		{
			String version = "1.3.0";
			System.setProperty(RoboRunnerDefines.PROCESS_NAME_KEY, "MAIN");
			System.setProperty(RoboRunnerDefines.VERSION_KEY, version);
			System.out.format("\nWelcome to RoboRunner %s. Type HELP or '?' if unsure.\n\n", version);
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
		if (RoboRunnerConfig.getInstance().isFirstRun())
		{
			ConsoleWorker.format("Looks like this is your first run - type CONFIG to setup the system.\n");
		};
		final Thread tQueue = new Thread(myQueue = new QueueWorker(this));
		tQueue.start();
		final Thread tConsole = new Thread(new ConsoleWorker(this, "CONSOLE"));
		tConsole.start();

		if (RoboRunnerConfig.getInstance().isAutoRun()) sendRunMessage();
	}

	public void sendRunMessage()
	{
		// TODO: parse the input to the current process and not to all
		// because of the two places where this can be called (autorun|console) it is implemented in the main class
		try
		{
			startProcesses();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		RunnerMessage setup = new RunnerMessage();
		setup.myCommand = RoboRunnerDefines.RUN_COMMAND;
		setup.myPriority = 1;
		setup.myResult = "xxx";
		sendMessage(setup, RoboRunnerDefines.ALL_PROCESSES);
	}

	public void startProcesses() throws IOException
	{
		// NOTE: the run command from the console tries to start the processes as well with a recall of this command the connections should
		// still be available. the kill command can delete the processes and after this it should build a new process
		// TODO: make it dynamic so that we can kill just one process and restart it with run after this automatically
		if (mySendConnections.size() > 0) { return; }

		int processCount;
		try
		{
			processCount = Integer.parseInt(RoboRunnerConfig.getInstance().getInstallCount());
		}
		catch (Exception e)
		{
			ConsoleWorker.format("Sorry, the configuration is not valid. Type CONFIG to setup the values.\n");
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
			command.add("-Xmx512M");

			// TODO: do a little research what other OS look like and maybe give the appropriate name to them 
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
			{
				command.add("-Xdock:icon=robocode.ico");
				command.add(String.format("-Xdock:name=RoboRunnerProcess %d", i));
			}
			command.add("-cp");
			command.add(cp);
			command.add("robowiki.console.BattleProcessConsole");

			ProcessBuilder builder = new ProcessBuilder(command);
			builder.redirectErrorStream(true);
			Map<String, String> env = builder.environment();

			if (!RunnerFunctions.checkPath(processPath, false, true, true))
			{
				// this should not be happen - only if the user deletes the installation directories manually
				ConsoleWorker.format("Sorry, your installations are not valid. Type CONFIG to setup the values.\n");
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
			ConsoleWorker.format("Process [%s] started ..\n", processName);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.format("Shutdown...\n"); // Shutdown Hooks are going to the System out and not to the console
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
