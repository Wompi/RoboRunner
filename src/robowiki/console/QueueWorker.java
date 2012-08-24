package robowiki.console;

import java.util.concurrent.PriorityBlockingQueue;

public class QueueWorker implements Runnable
{
	private final PriorityBlockingQueue<RunnerMessage>	myInputQueue;
	private final IMessageHandler						myHandler;

	public QueueWorker(IMessageHandler handler)
	{
		myInputQueue = new PriorityBlockingQueue<RunnerMessage>();
		myHandler = handler;
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				RunnerMessage newEvent = myInputQueue.take();
				// make something with the event - some sort of event processor
				System.out.format("processed:  %s", newEvent.toString());
				if (newEvent.myCommand.equals(RoboRunnerDefines.SETUP_REQUEST))
				{
					// TODO: build a configuration object where the necessary informations come from
					//MAIN|SETUP|1|35:1000:1000:botName1,botName2,....
					RunnerMessage setup = new RunnerMessage("MAIN");
					setup.myCommand = RoboRunnerDefines.SETUP;
					setup.myPriority = 1;
					setup.myResult = "35:1000:1000:mld.DustBunny 3.8,wompi.Wallaby*";
					myHandler.sendMessage(setup);
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.READY))
				{
					RunnerMessage setup = new RunnerMessage("MAIN");
					setup.myCommand = RoboRunnerDefines.RUN;
					setup.myPriority = 1;
					setup.myResult = "5";
					myHandler.sendMessage(setup);
				}

			}
			catch (InterruptedException e)
			{}
		}
	}

	public void addMessage(RunnerMessage msg)
	{
		myInputQueue.add(msg);
	}
}
