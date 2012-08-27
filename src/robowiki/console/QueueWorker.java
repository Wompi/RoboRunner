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
				else if (newEvent.myCommand.equals(RoboRunnerDefines.RESULT))
				{
					// TODO: give the result to the challenge and make a nice output
					ConsoleWorker.format("Result[%s]: %s\n", newEvent.getDestination(), newEvent.myResult);
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.SETUP_REQUEST))
				{
					ConsoleWorker.format("%s %s\n", newEvent.getDestination(), newEvent.myResult);
					RunnerChallenge chall = ChallengeManager.getInstance().getChallengeFor(RoboRunnerDefines.ALL_PROCESSES);
					if (chall != null)
					{
						RunnerMessage setup = new RunnerMessage();
						setup.myCommand = RoboRunnerDefines.WORKING_COMMAND;
						setup.myPriority = 1;
						setup.myResult = chall.getMessageString();
						myHandler.sendMessage(setup, newEvent.getDestination());
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

	public void addMessage(ProcessMessage msg)
	{
		myInputQueue.add(msg);
	}
}
