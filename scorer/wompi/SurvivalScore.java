package wompi;

import java.io.Serializable;

import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.RoundEndedEvent;
import robowiki.console.scorer.IScorer;

public class SurvivalScore implements IScorer, Serializable
{
	private static final long	serialVersionUID	= -355855149226346165L;

	@Override
	public String getName()
	{
		return "SURVIVAL_SCORE";
	}

	@Override
	public void onRoundEnded(RoundEndedEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent completedEvent)
	{
		// TODO Auto-generated method stub

	}
}
