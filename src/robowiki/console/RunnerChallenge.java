package robowiki.console;

import java.util.List;

import robowiki.runner.BotList;
import robowiki.runner.ChallengeConfig.BotListGroup;

public class RunnerChallenge
{
	public String				myName;
	public int					myH;
	public int					myW;
	public int					myRounds;
	public String				myChallenger;
	public boolean				isSmartBattle;
	public int					mySeasons;

	// not really needed because the score type can be changed with the output. 
	// it collects all data and then you can output whatever you want
	public String				myScoreType;

	public List<BotListGroup>	myBotGroups;
	public List<BotList>		myBots;

	public RunnerChallenge()
	{
		// defaults
		myH = 800;
		myW = 600;
		myRounds = 35;
	}

	/**
	 * Important before calling this make a hasMoreBattles check. 
	 * Otherwise this will cause you an Exception and shows you that you are wrong. Thats why no null check is implemented here  
	 * 
	 * @return
	 */
	public String getMessageString()
	{
		//35:1000:1000:botName1,botName2,....
		String sep = RoboRunnerDefines.RES_SPLITTER;
		BotList nextBotList = getBattleList();
		String bots = String.format("%s%s%s", myChallenger, RoboRunnerDefines.BOT_SPLITTER, nextBotList.toString());
		String result = String.format("%d%s%d%s%d%s%d%s%d%s%d%s%s", this.hashCode(), sep, nextBotList.hashCode(), sep, nextBotList.useCount, sep,
				myRounds, sep, myW, sep, myH, sep, bots);
		ConsoleWorker.format("Season [%d-%d] for: %s\n", nextBotList.useCount, mySeasons, bots);
		return result;
	}

	@Override
	public String toString()
	{
		String result = "";
		result += String.format("\t%-13s %s\n", "Challenger:", myChallenger);
		result += String.format("\t%-13s %s\n", "Challenge:", myName);
		result += String.format("\t%-13s %d x %d\n", "Battlefield:", myW, myH);
		result += String.format("\t%-13s %d\n", "Rounds:", myRounds);
		result += String.format("\t%-13s %d\n", "Seasons:", mySeasons);
		result += String.format("\t%-13s %s\n", "Scoring:", myScoreType);
		result += String.format("\t%-13s %s\n", "Smart Battles:", ((isSmartBattle) ? "on" : "off"));
		return result;
	}

	private BotList getBattleList()
	{
		BotList minUsedList = null;
		for (BotList botList : myBots)
		{
			if (minUsedList == null || minUsedList.useCount > botList.useCount)
			{
				minUsedList = botList;
			}
		}
		minUsedList.useCount++;
		return minUsedList;
	}

	public void resetUseCount()
	{
		for (BotList list : myBots)
		{
			list.useCount = 0;
		}
	}

	public boolean hasMoreBattles()
	{
		for (BotList list : myBots)
		{
			if (list.useCount < mySeasons) { return true; }
		}
		return false;
	}
}
