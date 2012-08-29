package robowiki.console;

import robocode.BattleResults;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.RoundStartedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;
import robowiki.console.process.ProcessConfiguration;

public class DefaultBattleAdaptor extends BattleAdaptor
{
	private final IMessageHandler		myHandler;
	private final ProcessConfiguration	myConfig;

	public DefaultBattleAdaptor(IMessageHandler handler, ProcessConfiguration config)
	{
		myHandler = handler;
		myConfig = config;
	}

	@Override
	public void onRoundStarted(RoundStartedEvent event)
	{
		if (event.getRound() == 0)
		{
			ITurnSnapshot startSnap = event.getStartSnapshot();
			IRobotSnapshot[] botSnaps = startSnap.getRobots();
			for (IRobotSnapshot botSnap : botSnaps)
			{
				System.out.format("DEBUG[%s][%d] ID=%d NAME=%s\n", RunnerFunctions.getProcessName(), event.getRound(), botSnap.getRobotIndex(),
						botSnap.getName());
			}
		}
	}

	@Override
	public void onBattleError(BattleErrorEvent e)
	{
		// TODO: maybe stop the engine here - or let the server decide if the battle should be stopped
		RunnerMessage error = new RunnerMessage();
		error.myCommand = RoboRunnerDefines.BATTLE_ERROR;
		error.myPriority = 1;
		error.myResult = e.getError();
		myHandler.sendMessage(error, null);
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent e)
	{
		BattleResults[] results = e.getIndexedResults();

		// PROCESS|RESULT|1|score:survival:bDmg:bBonus:rDmg:rBonus:first:second:third:botName
		String sep = RoboRunnerDefines.RES_SPLITTER;
		for (BattleResults r : results)
		{
			RunnerMessage result = new RunnerMessage();
			result.myCommand = RoboRunnerDefines.RESULT;
			result.myPriority = 1;
			//@formatter:off
			result.myResult = String.format("%d%s%d%s%d%s%d%s%d%s%d%s%d%s%d%s%d%s%d%s%d%s%d%s%d%s%s", 
					myConfig.getChallengeID(), sep,
					myConfig.getBotListID(), sep, 
					myConfig.getCurrentSeason(), sep, 
					r.getScore(), sep, 
					r.getSurvival(), sep, 
					r.getBulletDamage(), sep, 
					r.getLastSurvivorBonus(), sep, 
					r.getBulletDamageBonus(), sep, 
					r.getRamDamage(), sep, 
					r.getRamDamageBonus(), sep,
					r.getFirsts(), sep, 
					r.getSeconds(), sep, 
					r.getThirds(), sep, 
					r.getTeamLeaderName());
			myHandler.sendMessage(result, null);
			//@formatter:on
		}
	}
}
