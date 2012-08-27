package robowiki.console;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class ConsoleWorker implements Runnable
{
	private final RoboRunnerConsole			myHandler;
	private final String					myName;
	private static Console					myConsole;
	private static boolean					isInConfig;
	private static ArrayList<ConsoleOutput>	myDelayedOutput;

	public ConsoleWorker(RoboRunnerConsole handler, String name) throws IOException
	{
		myConsole = System.console();
		myHandler = handler;
		myName = name;
		myDelayedOutput = new ArrayList<ConsoleOutput>();
	}

	public static void format(String format, Object... args)
	{
		if (myConsole == null) return;
		if (!isInConfig)
		{
			myConsole.format("\r\r");
			myConsole.format(format, args);
			myConsole.format("> ");
			myConsole.flush();
		}
		else
		{
			ConsoleOutput delayed = new ConsoleOutput();
			delayed.myFormat = format;
			delayed.myArgs = args;
			myDelayedOutput.add(delayed);
		}
	}

	private void printDelayed()
	{
		if (myDelayedOutput.size() > 0)
		{
			//TODO: re visit maybe it needs some concurrency handling. New output could arrive while printing this 

			myConsole.format("\nDelayed output while you where configurering:\n");
			for (ConsoleOutput out : myDelayedOutput)
			{
				myConsole.format(out.myFormat, out.myArgs);
			}
			myConsole.flush();
			myDelayedOutput.clear();
		}
	}

	private void formatConfig(String format, Object... args)
	{
		myConsole.format(format, args);
		myConsole.flush();
	}

	@Override
	public void run()
	{
		BufferedReader br = new BufferedReader(myConsole.reader());
		//		Scanner scan = new Scanner(myConsole.reader());

		while (true)
		{
			try
			{
				format("");
				String command = br.readLine();

				isInConfig = true;
				if (command.equals(""))
				{} // do nothing on empty input
				else if (command.equalsIgnoreCase(RoboRunnerDefines.STOP)) processStop();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.STATUS)) processStatus();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.DEBUG)) processDebug();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.AUTORUN)) processAutoRun();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.HELP) || command.equalsIgnoreCase(RoboRunnerDefines.SHORT_HELP)) processHelp();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.RUN)) processRun();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.QUIT) || command.equalsIgnoreCase(RoboRunnerDefines.EXIT))
				{
					Runtime.getRuntime().exit(0);
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.CONFIG)) processConfig(br);
				else if (command.equalsIgnoreCase(RoboRunnerDefines.CHALLENGE)) processChallenge(br);
				else ConsoleWorker.format("Sorry dude i don't know what you'r talking about. Was it cheese?\n");
				isInConfig = false;
				printDelayed();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void processStatus()
	{
		RunnerMessage setup = new RunnerMessage();
		setup.myCommand = RoboRunnerDefines.STATUS_COMMAND;
		setup.myPriority = 1;
		setup.myResult = "xxx";
		myHandler.sendMessage(setup, RoboRunnerDefines.ALL_PROCESSES);
	}

	private void processRun()
	{
		myHandler.sendRunMessage();
	}

	private void processHelp()
	{
		formatConfig("All comands can be written upper or lower case.\n");

		formatConfig("\tHELP - this one. You can also type '?'\n");
		formatConfig("\tCONFIG - Leads you through the configuration settings. Should be the first step after install.\n");
		formatConfig("\tCHAL - Leads you through the challenge settings. You can change the challenge at any time.\n");
		formatConfig("\tRUN - runs the processes, if not initiaized the processes will be startet, after that it runs the current challenge\n");
		formatConfig("\tSTOP - stops all running challenges. The instances will not be killed.\n");
		formatConfig("\tKILL - kills all processes (NOT IMPLEMENTED YET) \n");
		formatConfig("\tDEBUG - toggles the debug output\n");
		formatConfig("\tAUTO - toggles the auto run. This means if you start the program it used the last challenge and runs it again\n");
		formatConfig("\tQUIT/EXIT  - terminates RoboRunner\n");
	}

	private void processAutoRun()
	{
		RoboRunnerConfig.getInstance().toggleAutoRun();
		formatConfig("AUTORUN is now %s\n", ((RoboRunnerConfig.getInstance().isAutoRun()) ? "on" : "off"));
	}

	private void processDebug()
	{
		RoboRunnerConfig.getInstance().toggleDebug();
		formatConfig("DEBUG is now %s\n", ((RoboRunnerConfig.getInstance().isDebug()) ? "on" : "off"));
	}

	private void processStop()
	{
		RunnerMessage setup = new RunnerMessage();
		setup.myCommand = RoboRunnerDefines.STOP_COMMAND;
		setup.myPriority = 1;
		setup.myResult = "Please stop all activities";
		myHandler.sendMessage(setup, RoboRunnerDefines.ALL_PROCESSES);
	}

	private void processChallenge(BufferedReader br) throws IOException
	{
		String challengeBot = null;
		formatConfig("Your challenge bot [%s]: ", challengeBot = RoboRunnerConfig.getInstance().getChallengeBot());
		String command;
		while (true)
		{
			command = br.readLine();
			if (!command.equals(""))
			{
				if (ChallengeManager.getInstance().copyBots(command))
				{
					RoboRunnerConfig.getInstance().setChallengeBot(command);
					challengeBot = command;
					break;
				}
				formatConfig("Try again: ");
			}
			else break;
		}

		formatConfig("Type challenge name [%s]: ", RoboRunnerConfig.getInstance().getChallengeName());

		RunnerChallenge challenge = null;
		while (true)
		{
			command = br.readLine();
			if (!command.equals(""))
			{
				if (RunnerFunctions.checkPath(command, false, false, true))
				{
					challenge = ChallengeManager.getInstance().getChallenge(command);
					RoboRunnerConfig.getInstance().setChallengeName(command);
					break;
				}
				formatConfig("Try again: ");
			}
			else
			{
				if ((command = RoboRunnerConfig.getInstance().getChallengeName()) != null)
				{
					challenge = ChallengeManager.getInstance().getChallenge(command);
				}
				break;
			}
		}

		String seasons = RoboRunnerConfig.getInstance().getBotListSeasons();
		formatConfig("Seasons per bot list (1-5000): [%s]: ", seasons);
		while (true)
		{
			try
			{
				command = br.readLine();
				if (!command.equals(""))
				{
					RoboRunnerConfig.getInstance().setBotListSeasons(command);
					seasons = RoboRunnerConfig.getInstance().getBotListSeasons(); // just in case he gave to much
				}
				break;
			}
			catch (NumberFormatException e)
			{
				formatConfig("It has to be a number.\n");
				formatConfig("Try again: ");
			}
		}

		if (seasons != null)
		{
			challenge.mySeasons = Integer.parseInt(seasons);
		}
		else
		{
			format("Sorry, you have no seasons configured. Try CONFIG again.\n");
			return;
		}

		if (challengeBot != null)
		{
			challenge.myChallenger = challengeBot;
			formatConfig("Challenge now:\n%s", challenge.toString());

			// TODO: revisit to change the process names
			ChallengeManager.getInstance().registerChallenge(challenge, RoboRunnerDefines.ALL_PROCESSES);
		}
		else
		{
			formatConfig("Sorry, you have no challenge bot. Try CONFIG again.\n");
			return;
		}
	}

	private void processConfig(BufferedReader br) throws IOException
	{
		formatConfig("Source RoboCode Path[%s]: ", RoboRunnerConfig.getInstance().getSourceRobocodePath());
		String command;
		while (true)
		{
			command = br.readLine();
			if (!command.equals(""))
			{
				if (RunnerFunctions.checkPath(command, false, true, true))
				{
					RoboRunnerConfig.getInstance().setSourceRobocodePath(command);
					break;
				}
				formatConfig("Try again: ");
			}
			else break;
		}

		String lastCount = RoboRunnerConfig.getInstance().getInstallCount();
		formatConfig("How many installations (1-20) [%s]: ", lastCount);
		while (true)
		{
			try
			{
				command = br.readLine();
				if (!command.equals(""))
				{
					// do this stuff only when the use has given a different number of installations
					if (lastCount == null || Integer.parseInt(lastCount) != Integer.parseInt(command))
					{
						RoboRunnerConfig.getInstance().setInstallCount(command);

						// this is important because it copy the source directories within the RoboRunner directory
						// if the RoboCode path is not a directory it breaks the CONFIG command
						// it only copy directories with certain names so it is a little save if the source RoboCode directory is / (root) or something 
						// but it is still a thread if someone give a wrong RoboCode directory. Well i have checked this and it is OK with any directory
						// what you give him, because if the directory don't have the lib, robots and what not directories it does not copy anything 
						if (!maintainCopys(Integer.parseInt(command))) { return; }
					}
				}
				break;
			}
			catch (NumberFormatException e)
			{
				formatConfig("It has to be a number.\n");
				formatConfig("Try again: ");
			}
		}

		formatConfig("Robot path [%s]: ", RoboRunnerConfig.getInstance().getSourceBotPath());
		while (true)
		{
			command = br.readLine();
			if (!command.equals(""))
			{
				if (RunnerFunctions.checkPath(command, true, true, true))
				{
					RoboRunnerConfig.getInstance().setSourceBotPath(command);
					break;
				}
				formatConfig("Try again: ");
			}
			else break;

		}
	}

	private boolean maintainCopys(int copys)
	{
		String sourceDir = RoboRunnerConfig.getInstance().getSourceRobocodePath();
		if (!RunnerFunctions.checkPath(sourceDir, false, true, true))
		{
			formatConfig("Sorry, you have not configured the robocode source path! Try CONFIG again.\n");
			return false;
		}

		File root = new File(RoboRunnerDefines.ROBOCODE_DIR_NAME);
		if (!root.exists())
		{
			formatConfig("Directory %s don't exist - i will make one.\n", RoboRunnerDefines.ROBOCODE_DIR_NAME);
			root.mkdir();
		}
		else
		{
			for (File directory : root.listFiles())
			{
				try
				{
					RunnerFunctions.deleteDirectory(directory);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		// copy the new robocode directories
		String destDir;

		File robocodeSrc = new File(sourceDir);

		FilenameFilter directoryFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				//ConsoleWorker.format("Filter: %s - %s\n",dir.getName(),name);
				if (name.equalsIgnoreCase("libs")) return true;
				if (name.equalsIgnoreCase("robots")) return true;
				if (name.equalsIgnoreCase("config")) return true;

				if (dir.getName().equalsIgnoreCase("libs")) return true;
				if (dir.getName().equalsIgnoreCase("config")) return true;
				return false;
			}
		};

		for (int i = 0; i < copys; i++)
		{
			destDir = String.format("%s%src%d", RoboRunnerDefines.ROBOCODE_DIR_NAME, System.getProperty("file.separator"), i);
			try
			{
				RunnerFunctions.copyFolder(robocodeSrc, new File(destDir), directoryFilter);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}
}

class ConsoleOutput
{
	String		myFormat;
	Object[]	myArgs;
}
