package robowiki.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoboRunnerConsole implements IMessageHandler
{
	private QueueWorker			myQueue;
	private final SendWorker	sendWorker;
	private ReceiveWorker		receiveWorker;

	public static void main(String[] args)
	{
		try
		{
			System.out.format("Start ...\n");
			new RoboRunnerConsole();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public RoboRunnerConsole() throws IOException, InterruptedException
	{
		final Thread tConsole = new Thread(new ConsoleWorker(System.in, this, "CONSOLE"));
		final Thread tQueue = new Thread(myQueue = new QueueWorker(this));
		tConsole.start();
		tQueue.start();

		List<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-cp");
		command.add(System.getProperty("java.class.path"));
		command.add("robowiki.console.BattleProcessConsole");

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		final Process battleProcess = builder.start();
		final Thread tSend = new Thread(sendWorker = new SendWorker(battleProcess.getOutputStream()));
		final Thread tReceive = new Thread(receiveWorker = new ReceiveWorker(battleProcess.getInputStream(), this, "MAIN"));

		tSend.start();
		tReceive.start();

		System.out.format("Process started ..\n");

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.format("Shutdown...\n");
				battleProcess.destroy();
			}
		}));

		//		RunnerMessage init = new RunnerMessage("MAIN");
		//		init.myCommand = RoboRunnerDefines.INIT_SIGNAL;
		//		init.myResult = "you can now init";
		//		init.myPriority = 1;
		//
		//		sendMessage(init);

	}

	@Override
	public void receiveMessage(RunnerMessage msg)
	{
		myQueue.addMessage(msg);
	}

	@Override
	public void sendMessage(RunnerMessage msg)
	{
		sendWorker.addMessage(msg);
	}
}
