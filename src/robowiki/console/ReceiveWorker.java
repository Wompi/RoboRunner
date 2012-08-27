package robowiki.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This one right now only supports single line messages
 * 
 * TODO: re visit to make it multi line capable
 * 
 * 
 * @author Wompi
 *
 */
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

				// TODO: re visit maybe it needs another check later
				if (parse.length == 4)
				{
					ProcessMessage msg = new ProcessMessage(parse[0]);
					msg.myCommand = parse[1];
					msg.myPriority = Integer.parseInt(parse[2]);
					msg.myResult = parse[3];
					myHandler.receiveMessage(msg);
				}
				else
				{
					// looks like we got some console printouts from the process, and we make it an info
					// TODO: if debug is off it swallows the error messages as well - maybe change the error stream to something separate
					if (RoboRunnerConfig.getInstance().isDebug())
					{
						ProcessMessage msg = new ProcessMessage(myProcessName);
						msg.myCommand = RoboRunnerDefines.INFO;
						msg.myPriority = 1;
						msg.myResult = newMsg;
						myHandler.receiveMessage(msg);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
