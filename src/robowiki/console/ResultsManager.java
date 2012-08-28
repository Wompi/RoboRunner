package robowiki.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ResultsManager
{
	private final HashMap<Integer, ArrayList<RoboRunnerResult>>	myResults;

	public ResultsManager()
	{
		myResults = new HashMap<Integer, ArrayList<RoboRunnerResult>>();
	}

	public void registerResult(RoboRunnerResult newResult)
	{
		ArrayList<RoboRunnerResult> results = myResults.get(newResult.myChallenge);
		if (results == null)
		{
			myResults.put(newResult.myChallenge, results = new ArrayList<RoboRunnerResult>());
		}
		results.add(newResult);
	}

	public void printAll()
	{
		HashMap<String, RoboRunnerResult> sumTable = new HashMap<String, RoboRunnerResult>();
		int maxLen = 0;
		for (ArrayList<RoboRunnerResult> results : myResults.values())
		{
			for (RoboRunnerResult result : results)
			{
				RoboRunnerResult sum = sumTable.get(result.myID);
				if (sum == null)
				{
					sumTable.put(result.myID, sum = new RoboRunnerResult());
					sum.myID = result.myID;
					sum.myChallenge = result.myChallenge;
					maxLen = Math.max(maxLen, sum.myID.length());
				}
				sum.myScore += result.myScore;
				sum.mySurvival += result.mySurvival;
				sum.myBulletDmg += result.myBulletDmg;
				sum.mySurvivalBonus += result.mySurvivalBonus;
				sum.myBulletBonus += result.myBulletBonus;
				sum.myRamDmg += result.myRamDmg;
				sum.myRamBonus += result.myRamBonus;
				sum.myFirsts += result.myFirsts;
				sum.mySeconds += result.mySeconds;
				sum.myThirds += result.myThirds;
			}
		}

		List<RoboRunnerResult> sortedSum = new ArrayList<RoboRunnerResult>(sumTable.values());
		Collections.sort(sortedSum, new Comparator<RoboRunnerResult>()
		{
			@Override
			public int compare(RoboRunnerResult o1, RoboRunnerResult o2)
			{
				return o2.myScore - o1.myScore;
			}
		});

		for (RoboRunnerResult sum : sortedSum)
		{
			ConsoleWorker.format("%" + maxLen + "s %6d %6d %6d %6d %4d %4d %4d %4d %4d \n", sum.myID, sum.myScore, sum.myBulletDmg,
					sum.mySurvivalBonus, sum.myBulletBonus, sum.myRamDmg, sum.myRamBonus, sum.myFirsts, sum.mySeconds, sum.myThirds);
		}
	}
}
