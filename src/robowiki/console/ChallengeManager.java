package robowiki.console;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import robowiki.runner.BotList;
import robowiki.runner.ChallengeConfig.BotListGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ChallengeManager
{
	private static ChallengeManager					instance;
	private final static String						DEFAULT_GROUP	= "";

	private final HashMap<String, RunnerChallenge>	myChallengeMap;

	private ChallengeManager()
	{
		myChallengeMap = new HashMap<String, RunnerChallenge>();
	}

	public RunnerChallenge getChallenge(String name)
	{
		RunnerChallenge result = load(name);
		makeAllBotList(result);
		copyAllBots(result);
		return result;
	}

	public static ChallengeManager getInstance()
	{
		if (instance == null)
		{
			instance = new ChallengeManager();
		}
		return instance;
	}

	public void registerChallenge(RunnerChallenge chall, String destination)
	{
		myChallengeMap.put(destination, chall);
	}

	public RunnerChallenge getChallengeFor(String destination)
	{
		// TODO: start the saved challenges if possible automatic
		return myChallengeMap.get(destination);
	}

	public boolean copyBots(String botName)
	{

		botName = botName.replace(System.getProperty("line.separator"), "");

		String sep = System.getProperty("file.separator");
		String workDir = System.getProperty("user.dir");
		if (!workDir.endsWith(sep)) workDir += sep;

		String botJar = getBotJarName(botName);
		String sourceJar = null;

		String botSource = RoboRunnerConfig.getInstance().getSourceBotPath();
		if (botSource == null)
		{
			System.out.format("Sorry, you have no bot path configured. Type CONFIG for setup\n");
			return false;
		}
		String[] botSourceField = botSource.split(RoboRunnerDefines.INTERNAL_PATH_SPLITTER);

		for (String botsDir : botSourceField)
		{
			if (!botsDir.endsWith(sep)) botsDir += sep;
			String path = String.format("%s%s", botsDir, botJar);

			if (RunnerFunctions.checkPath(path, false, false))
			{
				sourceJar = path;
				break;
			}
		}

		if (sourceJar == null)
		{
			System.out.format("Sorry, can't find %s in:\n", botJar);
			for (String botsDir : botSourceField)
			{
				System.out.format("%s\n", botsDir);
			}
			System.out.format("Check spelling, or copy the bot jar to the source directory\n", botJar);
			return false;
		}

		int processCount;
		try
		{
			processCount = Integer.parseInt(RoboRunnerConfig.getInstance().getInstallCount());
		}
		catch (Exception e)
		{
			System.out.format("Sorry, the configuration is not valid. Type CONFIG to setup the values again.\n");
			return false;
		}

		for (int i = 0; i < processCount; i++)
		{
			String internalBotPath = String.format("%s%s%src%d%srobots%s%s", workDir, RoboRunnerDefines.ROBOCODE_DIR_NAME, sep, i, sep, sep, botJar);

			try
			{
				if (!RunnerFunctions.checkPath(internalBotPath, false, false))
				{
					RunnerFunctions.copyFolder(new File(sourceJar), new File(internalBotPath), null);
				}
			}
			catch (IOException e)
			{
				System.out.format("Sorry, something went wrong, could not copy %s -> %s\n", sourceJar, internalBotPath);
				return false;
			}
		}
		return true;
	}

	private void copyAllBots(RunnerChallenge chall)
	{
		for (BotList list : chall.myBots)
		{
			List<String> names = list.getBotNames();
			for (String name : names)
			{

				copyBots(name);
			}
		}
	}

	private void makeAllBotList(RunnerChallenge chall)
	{
		if (chall == null) return;
		chall.myBots = Lists.newArrayList();
		for (BotListGroup group : chall.myBotGroups)
		{
			for (BotList list : group.referenceBots)
			{
				chall.myBots.add(list);
			}
		}
	}

	private RunnerChallenge load(String challName)
	{
		try
		{
			RunnerChallenge result = new RunnerChallenge();
			Scanner parser = new Scanner(new File(challName));

			int line = 0;
			result.myBotGroups = Lists.newArrayList();
			List<BotList> groupBots = Lists.newArrayList();
			String groupName = DEFAULT_GROUP;
			Integer width = null;
			Integer height = null;

			while (parser.hasNextLine())
			{
				String newLine = parser.nextLine().trim();
				if (line == 0)
				{
					result.myName = newLine;
					line++;
					continue;
				}
				else if (line == 1)
				{
					result.myScoreType = newLine;
					line++;
					continue;
				}
				else if (line == 2)
				{
					result.myRounds = Integer.parseInt(newLine.toLowerCase().replaceAll("rounds", ""));
					line++;
					continue;
				}
				int maxBots = 1;
				if (newLine.matches("^\\d+$"))
				{
					int value = Integer.parseInt(newLine);
					if (width == null)
					{
						width = value;
					}
					else if (height == null)
					{
						height = value;
					}
				}
				else if (newLine.length() > 0 && !newLine.contains("#"))
				{
					if (newLine.contains("{"))
					{
						groupName = newLine.replace("{", "").trim();
					}
					else if (newLine.contains("}"))
					{
						result.myBotGroups.add(new BotListGroup(groupName, groupBots));
						groupName = DEFAULT_GROUP;
						groupBots = Lists.newArrayList();
					}
					else
					{
						List<String> botList = Lists.newArrayList(newLine.split(" *, *"));
						Iterables.removeIf(botList, new Predicate<String>()
						{
							@Override
							public boolean apply(String botName)
							{
								if (botName.contains(".") && botName.contains(" "))
								{
									return false;
								}
								else
								{
									System.out.println("WARNING: " + botName + " doesn't look " + "like a bot name, ignoring.");
									return true;
								}
							}
						});
						maxBots = Math.max(maxBots, 1 + botList.size());
						BotList buffy = new BotList(botList);
						groupBots.add(buffy);
					}
				}
			}

			if (width != null) result.myW = width;
			if (height != null) result.myH = height;

			// TODO: revisit latter
			//			if (scoringStyle == ScoringStyle.MOVEMENT_CHALLENGE && maxBots > 2) { throw new RuntimeException(
			//					"Movement Challenge scoring doesn't work " + "for battles with more than 2 bots."); }

			if (!groupBots.isEmpty())
			{
				result.myBotGroups.add(new BotListGroup(groupName, groupBots));
			}

			return result;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private String getBotJarName(String bot)
	{
		bot = bot.replaceAll(" ", "_");
		if (!bot.endsWith(".jar")) bot += ".jar";
		return bot;
	}
}
