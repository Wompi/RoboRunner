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
				System.out.format("processed:  %s", newEvent.toString());

				if (newEvent.myCommand.equals(RoboRunnerDefines.STARTED))
				{
					RunnerMessage msg = new RunnerMessage();
					msg.myCommand = RoboRunnerDefines.INIT_REQUEST; // check if it is a command anyway .. normal output should be handled as info
					msg.myPriority = 1;
					msg.myResult = "you can now init";
					myHandler.sendMessage(msg, newEvent.getDestination());
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.SETUP_REQUEST))
				{
					// TODO: build a configuration object where the necessary informations come from
					//MAIN|SETUP|1|35:1000:1000:botName1,botName2,....
					RunnerMessage setup = new RunnerMessage();
					setup.myCommand = RoboRunnerDefines.SETUP;
					setup.myPriority = 1;
					setup.myResult = "35:1000:1000:sample.SittingDuck 1.0,wompi.Wallaby*";
					myHandler.sendMessage(setup, newEvent.getDestination());
				}
				else if (newEvent.myCommand.equals(RoboRunnerDefines.READY))
				{
					RunnerMessage setup = new RunnerMessage();
					setup.myCommand = RoboRunnerDefines.RUN;
					setup.myPriority = 1;
					setup.myResult = "5";
					myHandler.sendMessage(setup, newEvent.getDestination());
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
