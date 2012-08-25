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
				if (RoboRunnerConfig.getInstance().isDebug()) System.out.format("processed:  %s", newEvent.toString());

				if (newEvent.myCommand.equals(RoboRunnerDefines.STARTED))
				{
					RunnerMessage msg = new RunnerMessage();
					msg.myCommand = RoboRunnerDefines.INIT_REQUEST; // check if it is a command anyway .. normal output should be handled as info
					msg.myPriority = 1;
					msg.myResult = "you can now init";
					myHandler.sendMessage(msg, newEvent.getDestination());
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.INFO))
				{
					System.out.format("Info[%s]: %s\n", newEvent.getDestination(), newEvent.myResult);
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.RESULT))
				{
					// TODO: give the result to the challenge and make a nice output
					System.out.format("Result[%s]: %s\n", newEvent.getDestination(), newEvent.myResult);
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.SETUP_REQUEST))
				{
					System.out.format("%s %s\n", newEvent.getDestination(), newEvent.myResult);
					RunnerChallenge chall = ChallengeManager.getInstance().getChallengeFor(RoboRunnerDefines.ALL_PROCESSES);
					if (chall != null)
					{
						RunnerMessage setup = new RunnerMessage();
						setup.myCommand = RoboRunnerDefines.SETUP;
						setup.myPriority = 1;
						setup.myResult = chall.getMessageString();
						myHandler.sendMessage(setup, newEvent.getDestination());
					}
					else
					{
						System.out.format("Sorry, you have no challenge. Type CHAL to set it up and then RUN.\n");
					}
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.READY))
				{
					RunnerChallenge chall = ChallengeManager.getInstance().getChallengeFor(RoboRunnerDefines.ALL_PROCESSES);
					if (chall != null)
					{
						RunnerMessage setup = new RunnerMessage();
						setup.myCommand = RoboRunnerDefines.RUN;
						setup.myPriority = 1;
						setup.myResult = Integer.toString(chall.mySeasonsBotlist);
						myHandler.sendMessage(setup, newEvent.getDestination());
					}
					else
					{
						System.out.format("Sorry, you have no challenge. Type CHAL to set it up and then RUN.\n");
					}
				}
			}
			catch (InterruptedException e)
			{}
		}
	}

	public void addMessage(ProcessMessage msg)
	{
		myInputQueue.add(msg);
	}
}
