package robowiki.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;
import robowiki.console.scorer.RunnerScore;
import robowiki.console.scorer.ScoreTableManager;

public class ResultsManager
{
	private final HashMap<Integer, HashMap<Integer, ArrayList<RoboRunnerResult>>>	myResults;

	private final ScoreTableManager													myScorer;

	public ResultsManager()
	{
		myResults = new HashMap<Integer, HashMap<Integer, ArrayList<RoboRunnerResult>>>();
		myScorer = new ScoreTableManager();
	}

	public void registerResult(RoboRunnerResult newResult)
	{
		HashMap<Integer, ArrayList<RoboRunnerResult>> listResults = myResults.get(newResult.myChallengeID);
		if (listResults == null)
		{
			myResults.put(newResult.myChallengeID, listResults = new HashMap<Integer, ArrayList<RoboRunnerResult>>());
		}
		ArrayList<RoboRunnerResult> results = listResults.get(newResult.myBotListID);
		if (results == null)
		{
			listResults.put(newResult.myBotListID, results = new ArrayList<RoboRunnerResult>());
		}
		results.add(newResult);
	}

	public void printAll()
	{
		for (Entry<Integer, HashMap<Integer, ArrayList<RoboRunnerResult>>> entryChallenge : myResults.entrySet())
		{
			Integer challengeID = entryChallenge.getKey();
			HashMap<Integer, ArrayList<RoboRunnerResult>> botListResults = entryChallenge.getValue();
			RunnerChallenge challenge;
			RunnerScore scorer;
			try
			{
				challenge = ChallengeManager.getInstance().getChallengeByID(challengeID);
				scorer = myScorer.getScorerForName(challenge.myScoreType);
			}
			catch (IllegalStateException e0)
			{
				ConsoleWorker.format("ERROR: challenge with ID=%d is somehow deleted.\n", challengeID);
				e0.printStackTrace();
				continue;
			}

			ConsoleWorker.format("Challenge results for: %s (ID:%d)\n", challenge.myName, challengeID);

			for (Entry<Integer, ArrayList<RoboRunnerResult>> entryResults : botListResults.entrySet())
			{
				Integer botListID = entryResults.getKey();
				ArrayList<RoboRunnerResult> results = entryResults.getValue();

				HashMap<String, RoboRunnerSummaryResult> sumTable = new HashMap<String, RoboRunnerSummaryResult>();
				int maxLen = 0;
				int challengerScore = 0;
				for (RoboRunnerResult result : results)
				{
					RoboRunnerSummaryResult sum = sumTable.get(result.myID);
					if (sum == null)
					{
						sumTable.put(result.myID, sum = new RoboRunnerSummaryResult());
						sum.registerInterests(scorer.getInterests());
					}

					sum.setResults(result);
					//sum.myBattleCount++;
					//					sum.mySurvival += result.mySurvival;
					//					sum.myBulletDmg += result.myBulletDmg;
					//					sum.mySurvivalBonus += result.mySurvivalBonus;
					//					sum.myBulletBonus += result.myBulletBonus;
					//					sum.myRamDmg += result.myRamDmg;
					//					sum.myRamBonus += result.myRamBonus;
					//					sum.myFirsts += result.myFirsts;
					//					sum.mySeconds += result.mySeconds;
					//					sum.myThirds += result.myThirds;
					//
					// TODO: revisit because of name changes to the challenger if it is a develop version this would fail 
					if (challenge.myChallenger.equals(result.myID)) challengerScore = sum.myScore;
				}

				List<RoboRunnerSummaryResult> sortedSum = new ArrayList<RoboRunnerSummaryResult>(sumTable.values());

				final EAspect sortType = scorer.getSortType();
				if (sortType != null)
				{
					Collections.sort(sortedSum, new Comparator<RoboRunnerSummaryResult>()
					{
						@Override
						public int compare(RoboRunnerSummaryResult o1, RoboRunnerSummaryResult o2)
						{
							// TODO: string aspects should be handled separate
							// TODO: general revisit - the aspects should be comparable so i don't need this here 
							RAspect sortType1 = o1.getResultForType(sortType);
							RAspect sortType2 = o2.getResultForType(sortType);

							return (int) (sortType1.getAvgValue() - sortType2.getAvgValue());
						}
					});
				}

				for (RoboRunnerSummaryResult sum : sortedSum)
				{
					scorer.setResults(sum.getResults());
					ConsoleWorker.format(scorer.getPrintString());

					//					double avgScore = challengerScore * 100.0 / (challengerScore + sum.myScore);
					//					ConsoleWorker.format("%30s %6d (%5.2f) %6d %6d %6d %4d %4d %4d %4d %4d \n", sum.myID, sum.myScore, avgScore, sum.myBulletDmg,
					//							sum.mySurvivalBonus, sum.myBulletBonus, sum.myRamDmg, sum.myRamBonus, sum.myFirsts, sum.mySeconds, sum.myThirds);
				}
			}
		}

	}
}
