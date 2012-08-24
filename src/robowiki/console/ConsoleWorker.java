package robowiki.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConsoleWorker implements Runnable
{
	private final InputStreamReader	myInput;
	private final IMessageHandler	myHandler;
	private final String			myName;

	public ConsoleWorker(InputStream in, IMessageHandler handler, String name) throws IOException
	{
		myInput = new InputStreamReader(in);
		myHandler = handler;
		myName = name;
	}

	@Override
	public void run()
	{
		BufferedReader br = new BufferedReader(myInput);

		while (true)
		{
			try
			{
				String newMsg = br.readLine();

				// parse console input
				if (newMsg.equals(RoboRunnerDefines.INIT_REQUEST))
				{
					RunnerMessage msg = new RunnerMessage("CONSOLE");
					msg.myCommand = RoboRunnerDefines.INIT_REQUEST; // check if it is a command anyway .. normal output should be handled as info
					msg.myPriority = 1;
					msg.myResult = "you can now init";
					myHandler.sendMessage(msg);
				}
				else if (newMsg.equals(RoboRunnerDefines.RUN))
				{
					RunnerMessage setup = new RunnerMessage("CONSOLE");
					setup.myCommand = RoboRunnerDefines.RUN;
					setup.myPriority = 1;
					setup.myResult = "5";
					myHandler.sendMessage(setup);
				}
				else
				{
					System.out.format("Sorry dude i don't know what you talking about..\n");
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
