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

	public String getMessageString()
	{
		//35:1000:1000:botName1,botName2,....
		String sep = RoboRunnerDefines.RES_SPLITTER;
		String bots = String.format("%s%s%s", myChallenger, RoboRunnerDefines.BOT_SPLITTER, getBattleList().toString());
		String result = String.format("%d%s%d%s%d%s%d%s%s", mySeasons, sep, myRounds, sep, myW, sep, myH, sep, bots);
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
		result += String.format("\t%-13s %d\n", "Seasons per bot(s):", mySeasons);
		result += String.format("\t%-13s %s\n", "Scoring:", myScoreType);
		result += String.format("\t%-13s %s\n", "Smart Battles:", ((isSmartBattle) ? "on" : "off"));

		// debug
		//		for (BotList list : myBots)
		//		{
		//			result += String.format("\t%-13s %s\n", "Botlist: ", list.toString());
		//		}
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
}
