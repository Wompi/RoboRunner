package robowiki.console;

import java.util.concurrent.PriorityBlockingQueue;

public class QueueWorker implements Runnable
{
	private final PriorityBlockingQueue<ProcessMessage>	myInputQueue;
	private final IMessageHandler						myHandler;

	public QueueWorker(IMessageHandler handler)
	{
		myInputQueue = new PriorityBlockingQueue<ProcessMessage>();
		myHandler = handler;
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				ProcessMessage newEvent = myInputQueue.take();
				// make something with the event - some sort of event processor
				if (RoboRunnerConfig.getInstance().isDebug()) ConsoleWorker.format("processed:  %s", newEvent.toString());

				if (newEvent.myCommand.equals(RoboRunnerDefines.INFO))
				{
					ConsoleWorker.format("Info[%s]: %s\n", newEvent.getDestination(), newEvent.myResult);
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.BATTLE_FINISHED))
				{
					RunnerChallenge chall = ChallengeManager.getInstance().getChallengeFor(RoboRunnerDefines.ALL_PROCESSES);

					boolean hasMore = false;
					if (chall == null)
					{
						ConsoleWorker.format("Sorry, you have no challenge. Type CHAL to set it up and then RUN.\n");
					}
					else hasMore = chall.hasMoreBattles();

					// TODO: remove the yes/no it should be a proper output later
					ConsoleWorker.format("Finish[%s]: %s (%s)\n", newEvent.getDestination(), newEvent.myResult, (hasMore ? "yes" : "no"));
					if (hasMore)
					{
						RunnerMessage setup = new RunnerMessage();
						setup.myCommand = RoboRunnerDefines.WORKING_COMMAND;
						setup.myPriority = 1;
						setup.myResult = chall.getMessageString();
						myHandler.sendMessage(setup, newEvent.getDestination());
					}
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.RESULT)) processResult(newEvent);
				else if (newEvent.myCommand.equals(RoboRunnerDefines.SETUP_REQUEST))
				{
					ConsoleWorker.format("%s %s\n", newEvent.getDestination(), newEvent.myResult);
					RunnerChallenge chall = ChallengeManager.getInstance().getChallengeFor(RoboRunnerDefines.ALL_PROCESSES);
					if (chall != null)
					{
						if (chall.hasMoreBattles())
						{
							RunnerMessage setup = new RunnerMessage();
							setup.myCommand = RoboRunnerDefines.WORKING_COMMAND;
							setup.myPriority = 1;
							setup.myResult = chall.getMessageString();
							myHandler.sendMessage(setup, newEvent.getDestination());
						}
					}
					else
					{
						ConsoleWorker.format("Sorry, you have no challenge. Type CHAL to set it up and then RUN.\n");
					}
				}
			}
			catch (InterruptedException e)
			{}
		}
	}

	private void processResult(ProcessMessage newEvent)
	{
		// TODO: give the result to the challenge and make a nice output

		String[] parseResult = newEvent.myResult.split(RoboRunnerDefines.RES_SPLITTER);
		RoboRunnerResult result = new RoboRunnerResult();
		try
		{
			int i = 0;
			result.myChallengeID = Integer.parseInt(parseResult[i++]);
			result.myBotListID = Integer.parseInt(parseResult[i++]);
			result.mySeason = Integer.parseInt(parseResult[i++]);
			result.myScore = Integer.parseInt(parseResult[i++]);
			result.mySurvival = Integer.parseInt(parseResult[i++]);
			result.myBulletDmg = Integer.parseInt(parseResult[i++]);
			result.mySurvivalBonus = Integer.parseInt(parseResult[i++]);
			result.myBulletBonus = Integer.parseInt(parseResult[i++]);
			result.myRamDmg = Integer.parseInt(parseResult[i++]);
			result.myRamBonus = Integer.parseInt(parseResult[i++]);
			result.myFirsts = Integer.parseInt(parseResult[i++]);
			result.mySeconds = Integer.parseInt(parseResult[i++]);
			result.myThirds = Integer.parseInt(parseResult[i++]);
			result.myID = parseResult[i++];
			ConsoleWorker.format("Result[%s]: season %d arrived for %s. Type RESULT for the stats.\n", newEvent.getDestination(), result.mySeason,
					result.myID);
		}
		catch (NumberFormatException e0)
		{
			e0.printStackTrace();
		}
		ChallengeManager.getInstance().myResultManager.registerResult(result);
	}

	public void addMessage(ProcessMessage msg)
	{
		myInputQueue.add(msg);
	}
}
