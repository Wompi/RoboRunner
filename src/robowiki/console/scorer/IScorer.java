package robowiki.console.scorer;

import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.RoundEndedEvent;

public interface IScorer
{
	public String getName();

	public void onRoundEnded(RoundEndedEvent event);

	public void onBattleCompleted(BattleCompletedEvent completedEvent);
}
