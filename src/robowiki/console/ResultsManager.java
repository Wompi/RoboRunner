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

	public final ScoreTableManager													myScorer;

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

	public void printAll(String newScore)
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

				try
				{
					scorer = myScorer.getScorerForName(newScore);
				}
				catch (IllegalStateException e0)
				{
					// TODO: a little to sloppy. If the score name not exists or is null take the default one 
					scorer = myScorer.getScorerForName(challenge.myScoreType);
				}
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
				//Integer botListID = entryResults.getKey();
				ArrayList<RoboRunnerResult> results = entryResults.getValue();

				HashMap<String, RoboRunnerSummaryResult> sumTable = new HashMap<String, RoboRunnerSummaryResult>();
				RoboRunnerSummaryResult challenger = null;
				for (RoboRunnerResult result : results)
				{
					RoboRunnerSummaryResult sum = sumTable.get(result.myID);
					if (sum == null)
					{
						sumTable.put(result.myID, sum = new RoboRunnerSummaryResult());
						sum.registerInterests(scorer.getInterests());
						// TODO: revisit because of name changes to the challenger if it is a develop version this would fail 
						if (challenge.myChallenger.equals(result.myID)) challenger = sum;
					}
					sum.setResults(result);
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

							return (int) (sortType2.getAvgValue() - sortType1.getAvgValue());
						}
					});
				}

				for (RoboRunnerSummaryResult sum : sortedSum)
				{
					scorer.setChallengerResults(challenger.getResults());
					scorer.setResults(sum.getResults());
					ConsoleWorker.format(scorer.getPrintString());
				}
			}
		}

	}
}
