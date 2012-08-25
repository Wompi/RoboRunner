package robowiki.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConsoleWorker implements Runnable
{
	private final InputStreamReader	myInput;
	private final RoboRunnerConsole	myHandler;
	private final String			myName;

	public ConsoleWorker(InputStream in, RoboRunnerConsole handler, String name) throws IOException
	{
		myInput = new InputStreamReader(in);
		myHandler = handler;
		myName = name;
	}

	@Override
	public void run()
	{
		BufferedReader br = new BufferedReader(myInput);

		while (true)
		{
			try
			{
				System.out.format("> ");
				String command = br.readLine();

				if (command.equals(""))
				{} // do nothing on empty input
				else if (command.equalsIgnoreCase(RoboRunnerDefines.START)) processStart();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.INIT_REQUEST))
				{
					RunnerMessage msg = new RunnerMessage();
					msg.myCommand = RoboRunnerDefines.INIT_REQUEST;
					msg.myPriority = 1;
					msg.myResult = "you can now init";
					myHandler.sendMessage(msg, RoboRunnerDefines.ALL_PROCESSES);
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.STOP)) processStop();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.DEBUG)) processDebug();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.HELP) || command.equalsIgnoreCase(RoboRunnerDefines.SHORT_HELP)) processHelp();
				else if (command.equalsIgnoreCase(RoboRunnerDefines.RUN))
				{
					// TODO: parse the input to the current process and not to all
					RunnerChallenge chall = ChallengeManager.getInstance().getChallengeFor(RoboRunnerDefines.ALL_PROCESSES);
					if (chall != null)
					{
						RunnerMessage setup = new RunnerMessage();
						setup.myCommand = RoboRunnerDefines.SETUP;
						setup.myPriority = 1;
						setup.myResult = chall.getMessageString();
						myHandler.sendMessage(setup, RoboRunnerDefines.ALL_PROCESSES);
					}
					else
					{
						System.out.format("Sorry, you have no challenge. Type CHAL to set it up and then RUN.\n");
					}
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.QUIT) || command.equalsIgnoreCase(RoboRunnerDefines.EXIT))
				{
					Runtime.getRuntime().exit(0);
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.CONFIG)) processConfig(br);
				else if (command.equalsIgnoreCase(RoboRunnerDefines.CHALLENGE)) processChallenge(br);
				else System.out.format("Sorry dude i don't know what you'r talking about. Was it cheese?\n");

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void processHelp()
	{
		System.out.format("All comands can be written upper or lower case.\n");

		System.out.format("\tHELP - this one. You can also type '?'\n");
		System.out.format("\tCONFIG - Leads you through the configuration settings. Should be the first step after install.\n");
		System.out.format("\tCHAL - Leads you through the challenge settings. You can change the challenge at any time.\n");
		System.out
				.format("\tSTART - starts all processes. First it initializes the instances, then checks the config, setup and runs the challenge\n");
		System.out.format("\tRUN - if a challenge is finished, you can set up another or run the same again (no new initialization)\n");
		System.out.format("\tSTOP - stops all running challenges. The instances will not be killed.\n");
		System.out.format("\tINIT - just starts all instances (NOT IMPLEMENTED YET)\n");
		System.out.format("\tKILL - kills all processes (NOT IMPLEMENTED YET) \n");
		System.out.format("\tDEBUG - toggles the debug output\n");
	}

	private void processDebug()
	{
		RoboRunnerConfig.getInstance().toggleDebug();
		System.out.format("DEBUG is now %s\n", ((RoboRunnerConfig.getInstance().isDebug()) ? "on" : "off"));
	}

	private void processStart()
	{
		try
		{
			myHandler.startProcesses();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void processStop()
	{
		RunnerMessage setup = new RunnerMessage();
		setup.myCommand = RoboRunnerDefines.STOP_REQUEST;
		setup.myPriority = 1;
		setup.myResult = "Please stop all activities";
		myHandler.sendMessage(setup, RoboRunnerDefines.ALL_PROCESSES);

	}

	private void processChallenge(BufferedReader br) throws IOException
	{
		String challengeBot = null;
		System.out.format("Your challenge bot [%s]: ", challengeBot = RoboRunnerConfig.getInstance().getChallengeBot());
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
				System.out.format("Try again: ");
			}
			else break;
		}

		System.out.format("Type challenge name [%s]: ", RoboRunnerConfig.getInstance().getChallengeName());

		RunnerChallenge challenge = null;
		while (true)
		{
			command = br.readLine();
			if (!command.equals(""))
			{
				if (RunnerFunctions.checkPath(command, false, false))
				{
					challenge = ChallengeManager.getInstance().getChallenge(command);
					RoboRunnerConfig.getInstance().setChallengeName(command);
					break;
				}
				System.out.format("Try again: ");
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
		System.out.format("Seasons per bot list (1-5000): [%s]: ", seasons);
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
				System.out.format("It has to be a number.\n");
				System.out.format("Try again: ");
			}
		}

		if (seasons != null)
		{
			challenge.mySeasonsBotlist = Integer.parseInt(seasons);
		}
		else
		{
			System.out.format("Sorry, you have no seasons configured. Try CONFIG again.\n");
			return;
		}

		if (challengeBot != null)
		{
			challenge.myChallenger = challengeBot;
			System.out.format("Challenge now:\n%s", challenge.toString());

			// TODO: revisit to change the process names
			ChallengeManager.getInstance().registerChallenge(challenge, RoboRunnerDefines.ALL_PROCESSES);
		}
		else
		{
			System.out.format("Sorry, you have no challenge bot. Try CONFIG again.\n");
			return;
		}
	}

	private void processConfig(BufferedReader br) throws IOException
	{
		System.out.format("Source RoboCode Path[%s]: ", RoboRunnerConfig.getInstance().getSourceRobocodePath());
		String command;
		while (true)
		{
			command = br.readLine();
			if (!command.equals(""))
			{
				if (RunnerFunctions.checkPath(command, false, true))
				{
					RoboRunnerConfig.getInstance().setSourceRobocodePath(command);
					break;
				}
				System.out.format("Try again: ");
			}
			else break;
		}

		String lastCount = RoboRunnerConfig.getInstance().getInstallCount();
		System.out.format("How many installations (1-20) [%s]: ", lastCount);
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
				System.out.format("It has to be a number.\n");
				System.out.format("Try again: ");
			}
		}

		System.out.format("Robot path [%s]: ", RoboRunnerConfig.getInstance().getSourceBotPath());
		while (true)
		{
			command = br.readLine();
			if (!command.equals(""))
			{
				if (RunnerFunctions.checkPath(command, true, true))
				{
					RoboRunnerConfig.getInstance().setSourceBotPath(command);
					break;
				}
				System.out.format("Try again: ");
			}
			else break;

		}
	}

	//	private boolean maintainRobocode(String sourceDir)
	//	{
	//		if (!RunnerFunctions.checkPath(sourceDir, false, true)) { return false; }
	//
	//		File robocodeSrc = new File(sourceDir);
	//
	//		FilenameFilter directoryFilter = new FilenameFilter()
	//		{
	//			@Override
	//			public boolean accept(File dir, String name)
	//			{
	//				if (name.equalsIgnoreCase(RoboRunnerDefines.JAR_DIR_NAME)) return true;
	//				if (dir.getName().equalsIgnoreCase(RoboRunnerDefines.JAR_DIR_NAME)) return true;
	//				return false;
	//			}
	//		};
	//
	//		String destDir = System.getProperty("user.dir");
	//		try
	//		{
	//			RunnerFunctions.copyFolder(robocodeSrc, new File(destDir), directoryFilter);
	//		}
	//		catch (IOException e)
	//		{
	//			e.printStackTrace();
	//		}
	//		return true;
	//	}

	private boolean maintainCopys(int copys)
	{
		String sourceDir = RoboRunnerConfig.getInstance().getSourceRobocodePath();
		if (!RunnerFunctions.checkPath(sourceDir, false, true))
		{
			System.out.format("Sorry, you have not configured the robocode source path! Try CONFIG again.\n");
			return false;
		}

		File root = new File(RoboRunnerDefines.ROBOCODE_DIR_NAME);
		if (!root.exists())
		{
			System.out.format("Directory %s don't exist - i will make one.\n", RoboRunnerDefines.ROBOCODE_DIR_NAME);
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
				//System.out.format("Filter: %s - %s\n",dir.getName(),name);
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
