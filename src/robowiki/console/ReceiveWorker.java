package robowiki.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReceiveWorker implements Runnable
{
	private final InputStreamReader	myInput;
	private final IMessageHandler	myHandler;
	private final String			myProcessName;

	public ReceiveWorker(InputStream in, IMessageHandler handler, String name) throws IOException
	{
		myInput = new InputStreamReader(in);
		myHandler = handler;
		myProcessName = name;
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
				newMsg = newMsg.replace("\n", "");
				String[] parse = newMsg.split(RoboRunnerDefines.MSG_SPLITER);

				if (parse.length == 4)
				{
					ProcessMessage msg = new ProcessMessage(parse[0]);
					msg.myCommand = parse[1]; // check if it is a command anyway .. normal output should be handled as info
					msg.myPriority = Integer.parseInt(parse[2]);
					msg.myResult = parse[3];
					myHandler.receiveMessage(msg);
				}
				else
				{
					// looks like we got some console printouts from the process, and we make it an info
					ProcessMessage msg = new ProcessMessage(myProcessName);
					msg.myCommand = RoboRunnerDefines.INFO; // check if it is a command anyway .. normal output should be handled as info
					msg.myPriority = 1;
					msg.myResult = newMsg;
					myHandler.receiveMessage(msg);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
