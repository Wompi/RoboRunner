package robowiki.console;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.PriorityBlockingQueue;

public class SendWorker implements Runnable
{
	private final OutputStreamWriter					myOutPut;
	private final PriorityBlockingQueue<RunnerMessage>	mySendQueue;
	private final String								myProcessName;

	public SendWorker(OutputStream out, String name) throws IOException
	{
		myOutPut = new OutputStreamWriter(out);
		mySendQueue = new PriorityBlockingQueue<RunnerMessage>();
		myProcessName = name;
	}

	@Override
	public void run()
	{
		BufferedWriter bw = new BufferedWriter(myOutPut);

		while (true)
		{
			try
			{
				RunnerMessage sendMsg = mySendQueue.take();
				bw.append(sendMsg.toString());
				bw.flush();
				System.out.format("send from: %s", sendMsg.toString());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{}
		}
	}

	public void addMessage(RunnerMessage msg)
	{
		mySendQueue.add(msg);
	}
}
