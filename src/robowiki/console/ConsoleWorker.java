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
	private final IMessageHandler	myHandler;
	private final String			myName;

	public ConsoleWorker(InputStream in, IMessageHandler handler, String name) throws IOException
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
				{
					// do nothing on emty input
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.INIT_REQUEST))
				{
					RunnerMessage msg = new RunnerMessage();
					msg.myCommand = RoboRunnerDefines.INIT_REQUEST; // check if it is a command anyway .. normal output should be handled as info
					msg.myPriority = 1;
					msg.myResult = "you can now init";
					myHandler.sendMessage(msg, RoboRunnerDefines.ALL_PROCESSES);
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.RUN))
				{
					// TODO: parse the input to the current process and not to all
					RunnerMessage setup = new RunnerMessage();
					setup.myCommand = RoboRunnerDefines.RUN;
					setup.myPriority = 1;
					setup.myResult = "5";
					myHandler.sendMessage(setup, RoboRunnerDefines.ALL_PROCESSES);
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.QUIT) || command.equalsIgnoreCase(RoboRunnerDefines.EXIT))
				{
					Runtime.getRuntime().exit(0);
				}
				else if (command.equalsIgnoreCase(RoboRunnerDefines.CONFIG)) processConfig(br);
				else
				{
					System.out.format("Sorry dude i don't know what you talking about..\n");
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
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
		System.out.format("Who many installations (1-20) [%s]: ", lastCount);
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

	private boolean maintainCopys(int copys)
	{
		String sourceDir = RoboRunnerConfig.getInstance().getSourceRobocodePath();
		if (!RunnerFunctions.checkPath(sourceDir, false, true))
		{
			System.out.format("Sorry, you have not configured the robocode source path! Try CONFIG again.\n");
			return false;
		}

		File root = new File("robocodes");
		if (!root.exists())
		{
			System.out.format("installation ./robocodes don't exist - i will make one\n");
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
			destDir = String.format("robocodes%src%d", System.getProperty("file.separator"), i + 1);
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
