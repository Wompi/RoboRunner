package robowiki.console;

import java.io.File;
import java.io.IOException;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.IBattleListener;
import robowiki.console.process.ProcessConfiguration;

public class BattleProcessConsole implements IMessageHandler
{
	private RobocodeEngine			myEngine;
	private ProcessConfiguration	myConfig;
	private IBattleListener			myBattleListener;
	private final EngineState		myState;

	public static void main(String[] args)
	{
		try
		{
			new BattleProcessConsole();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public BattleProcessConsole() throws IOException
	{
		myState = new EngineState();
		Thread streamWatcher = new Thread(new ReceiveWorker(System.in, this, "PROCESS"));
		streamWatcher.start();

		RunnerMessage start = new RunnerMessage();
		start.myCommand = RoboRunnerDefines.STARTED;
		start.myResult = "ok i'm ready to go";
		start.myPriority = 1;
		sendMessage(start, null);

	}

	@Override
	public void receiveMessage(ProcessMessage msg)
	{
		if (msg.myCommand.equals(RoboRunnerDefines.INIT_REQUEST))
		{
			/// lets start the engine
			if (!myState.isInitialized)
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						File path = new File(System.getenv(RoboRunnerDefines.PROCESS_PATH_KEY));
						myEngine = new RobocodeEngine(path);
						myState.isInitialized = true;
						RunnerMessage setup = new RunnerMessage();
						if (!myState.hasSetup)
						{
							setup.myCommand = RoboRunnerDefines.SETUP_REQUEST;
							setup.myResult = String.format("i'm now fully initialized with %s", RobocodeEngine.getCurrentWorkingDir());
							setup.myPriority = 1;
							sendMessage(setup, null);
						}
						else
						{
							setup = new RunnerMessage();
							setup.myCommand = RoboRunnerDefines.READY;
							setup.myResult = String.format("i'm ready for battles now");
							setup.myPriority = 1;
							sendMessage(setup, null);
						}
					}
				}).start();
			}
			else
			{
				if (!myState.hasSetup)
				{
					RunnerMessage setup = new RunnerMessage();
					setup.myCommand = RoboRunnerDefines.INFO;
					setup.myResult = "i'm still initilized - send me some setup";
					setup.myPriority = 1;
					sendMessage(setup, null);
				}
				else
				{
					RunnerMessage setup = new RunnerMessage();
					setup.myCommand = RoboRunnerDefines.INFO;
					setup.myResult = "i'm still initilized and setup - send me the start command";
					setup.myPriority = 1;
					sendMessage(setup, null);
				}
			}
		}
		else if (msg.myCommand.equals(RoboRunnerDefines.SETUP))
		{
			// give the engine something to setup it's state
			// parse setup
			// i need numRounds|battleField_w|battleField_h|bots,..,..,.... (seasons comes with the run message)
			//MAIN|SETUP|1|35:1000:1000:botName1,botName2,....
			if (myState.isRunning)
			{
				RunnerMessage info = new RunnerMessage();
				info.myCommand = RoboRunnerDefines.INFO;
				info.myPriority = 1;
				info.myResult = "Not a good idear to change the setup while running - send me STOP first";
				sendMessage(info, null);
				return;
			}

			if (myConfig == null) myConfig = new ProcessConfiguration();
			String[] parseResult = msg.myResult.split(RoboRunnerDefines.RES_SPLITTER);
			try
			{
				myConfig.setRounds(parseResult[0]);
				myConfig.setW(parseResult[1]);
				myConfig.setH(parseResult[2]);
				myConfig.setBots(parseResult[3]);
			}
			catch (NumberFormatException e0)
			{
				e0.printStackTrace(); // can be deleted
				RunnerMessage info = new RunnerMessage();
				info.myCommand = RoboRunnerDefines.INFO;
				info.myPriority = 1;
				info.myResult = "The setup you have send me is bogus!";
				sendMessage(info, null);
				return;
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

			if (myState.isInitialized)
			{
				RunnerMessage setup = new RunnerMessage();
				setup.myCommand = RoboRunnerDefines.READY;
				setup.myResult = String.format("i'm ready for battles now");
				setup.myPriority = 1;
				sendMessage(setup, null);
			}
			myState.hasSetup = true;
		}
		else if (msg.myCommand.equals(RoboRunnerDefines.STOP_REQUEST))
		{
			if (myState.isRunning)
			{
				myEngine.abortCurrentBattle();
				RunnerMessage result = new RunnerMessage();
				result.myCommand = RoboRunnerDefines.INFO;
				result.myPriority = 1;
				result.myResult = "I'm stopping all battles. This can take a while, wait for response.";
				sendMessage(result, null);
				myState.isStopped = true;
			}
		}
		else if (msg.myCommand.equals(RoboRunnerDefines.RUN))
		{
			// here we can start the battles 
			// i need seasons to know how much cycles the engine should run
			// this should be made configurable to increase or decrease the cycles while running .. maybe because the challenge has a high standard error or something
			if (!myState.isInitialized)
			{
				RunnerMessage info = new RunnerMessage();
				info.myCommand = RoboRunnerDefines.INFO;
				info.myPriority = 1;
				info.myResult = "Dude what about initializtion?";
				sendMessage(info, null);
				return;
			}
			if (!myState.hasSetup)
			{
				RunnerMessage info = new RunnerMessage();
				info.myCommand = RoboRunnerDefines.INFO;
				info.myPriority = 1;
				info.myResult = "Dude what about setup?";
				sendMessage(info, null);
				return;
			}

			if (myState.isRunning)
			{
				RunnerMessage info = new RunnerMessage();
				info.myCommand = RoboRunnerDefines.INFO;
				info.myPriority = 1;
				info.myResult = "Dude i'm still running - send me STOP first.";
				sendMessage(info, null);
				return;

			}

			RunnerMessage setup = new RunnerMessage();
			setup.myCommand = RoboRunnerDefines.INFO;
			setup.myResult = String.format("battle starts setup: rounds=%d w=%d h=%d bots=(%s)", myConfig.getRounds(), myConfig.getW(),
					myConfig.getH(), myConfig.getBots());
			setup.myPriority = 1;
			sendMessage(setup, null);

			myConfig.setSeasons(msg.myResult);

			// TODO: let this run in a thread to keep the communication open
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					if (myBattleListener != null) myEngine.removeBattleListener(myBattleListener);
					myEngine.addBattleListener(myBattleListener = new DefaultBattleAdaptor(BattleProcessConsole.this));
					BattlefieldSpecification bField = new BattlefieldSpecification(myConfig.getW(), myConfig.getH());
					RobotSpecification[] bots = myEngine.getLocalRepository(myConfig.getBots());
					BattleSpecification spec = new BattleSpecification(myConfig.getRounds(), bField, bots);

					for (int i = 0; i < myConfig.getSeasons(); i++)
					{
						myState.isRunning = true;
						myEngine.runBattle(spec, true);

						// NOTE: because of a bug within the engine at cleanup i wait just a couple of seconds and then go on
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException ex)
						{
							Thread.currentThread().interrupt();
						}

						if (!myState.isStopped)
						{
							RunnerMessage result = new RunnerMessage();
							result.myCommand = RoboRunnerDefines.INFO;
							result.myPriority = 1;
							result.myResult = String.format("Season [%d-%d] is over now.", i + 1, myConfig.getSeasons());
							sendMessage(result, null);
						}
						else
						{
							RunnerMessage result = new RunnerMessage();
							result.myCommand = RoboRunnerDefines.INFO;
							result.myPriority = 1;
							result.myResult = "I stopped all battles now.";
							sendMessage(result, null);
							break;
						}
					}
					myState.isRunning = false;
					myState.isStopped = false;
				}
			}).start();
		}
		else
		{
			RunnerMessage unknown = new RunnerMessage();
			unknown.myCommand = RoboRunnerDefines.UNKNOWN;
			unknown.myResult = "hey dude stop sending me " + msg.myCommand + " stuff. I have no clue about it!";
			unknown.myPriority = 1;
			sendMessage(unknown, null);
		}
	}

	@Override
	public void sendMessage(RunnerMessage msg, String processName)
	{
		// TODO: not sure if this should be processed with a queue, but i guess not
		// the processName is not realy used for this IMessagehandler.. i let it in, maybe i change my mind about the design later 
		System.out.format(msg.toString());
	}
}
