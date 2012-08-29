package wompi;

import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.RoundEndedEvent;
import robowiki.console.scorer.IScorer;

public class PercentageScore implements IScorer
{

	@Override
	public String getName()
	{
		return "PERCENT_SCORE";
	}

	@Override
	public void onRoundEnded(RoundEndedEvent event)
	{
		System.out.format("TestOutput onRoundEnded\n");
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent completedEvent)
	{
		System.out.format("TestOutput onBattleCompleted\n");
	}
}
