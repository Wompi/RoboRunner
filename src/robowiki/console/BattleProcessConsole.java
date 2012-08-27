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
		// TODO: general - think about making the messages reusable. this would prevent a lot of garbage collection
		// maybe put it in a map and just grab the one you need set the values and go. Be careful with queued messages
		// maybe a send state to prevent overwriting messages that are not send yet
		// TODO: re visit the threads maybe a executor service i more sound

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
		Thread streamWatcher = new Thread(new ReceiveWorker(System.in, this, RunnerFunctions.getProcessName()));
		streamWatcher.start();
	}

	@Override
	public void receiveMessage(ProcessMessage msg)
	{
		if (msg.myCommand.equals(RoboRunnerDefines.RUN_COMMAND)) // run is a user driven message
		{
			if (proccedInitialization())
			{
				// ask the server to give a current setup or send a message that the process is still running
				proccedSetupRequest();
			}
		}
		else if (msg.myCommand.equals(RoboRunnerDefines.WORKING_COMMAND)) proccedWorkingRequest(msg); // internal message
		else if (msg.myCommand.equals(RoboRunnerDefines.STOP_COMMAND)) proccedStopRequest(); // stop is a user driven request
		else if (msg.myCommand.equals(RoboRunnerDefines.STATUS_COMMAND)) proccedStatusRequest(); // stop is a user driven request
		else
		{
			RunnerMessage unknown = new RunnerMessage();
			unknown.myCommand = RoboRunnerDefines.WARN;
			unknown.myResult = "hey dude stop sending me " + msg.myCommand + " stuff with '" + msg.myResult + "'. I have no clue about it!";
			unknown.myPriority = 1;
			sendMessage(unknown, null);
		}
	}

	private void proccedStatusRequest()
	{
		// TODO: implement some result line breaks to make the output a little fluffier, don't use \n this will break the messaging
		RunnerMessage status = new RunnerMessage();
		status.myCommand = RoboRunnerDefines.INFO;
		status.myPriority = 1;
		status.myResult = String.format("Name: %s Initialized: %s Running: %s RoboCode Version: %s  Path: %s  RobotPath: %s",
				RunnerFunctions.getProcessName(), (myState.isInitialized ? "true" : "false"), (myState.isRunning ? "true" : "false"),
				(myEngine != null ? myEngine.getVersion() : "not initialized"), RobocodeEngine.getCurrentWorkingDir(), RobocodeEngine.getRobotsDir());
		sendMessage(status, null);
	}

	private void proccedStopRequest()
	{
		RunnerMessage stop = new RunnerMessage();
		if (myState.isRunning)
		{
			myEngine.abortCurrentBattle();
			stop.myCommand = RoboRunnerDefines.INFO;
			stop.myPriority = 1;
			stop.myResult = "I'm stopping all battles. This can take a while, wait for response.";
		}
		else
		{
			stop.myCommand = RoboRunnerDefines.INFO;
			stop.myPriority = 1;
			stop.myResult = "I'm already stopped. Send me a new RUN";
		}
		sendMessage(stop, null);
		myState.isStopped = true;
	}

	private boolean proccedInitialization()
	{
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
					proccedSetupRequest();
				}
			}).start();
			return false;
		}
		return true;
	}

	private boolean proccedSetupRequest()
	{
		if (!myState.isRunning)
		{
			RunnerMessage setup = new RunnerMessage();
			setup.myCommand = RoboRunnerDefines.SETUP_REQUEST;
			setup.myResult = String.format("I'm initialized and ask for some setup - type STATUS if you want to know more");
			setup.myPriority = 1;
			sendMessage(setup, null);
			return true;
		}
		RunnerMessage info = new RunnerMessage();
		info.myCommand = RoboRunnerDefines.INFO;
		info.myPriority = 1;
		info.myResult = "Dude i'm still running - send me STOP first.";
		sendMessage(info, null);
		return false;
	}

	private void proccedWorkingRequest(ProcessMessage msg)
	{
		// TODO: i guess it is possible if the messages are coming slow that this one need an init and stop check
		// try to hit RUN RUN RUN in the console to evaluate this
		if (!parseConfig(msg)) return;

		RunnerMessage setup = new RunnerMessage();
		setup.myCommand = RoboRunnerDefines.INFO;
		setup.myResult = String.format("I'm bussy with battles now.");
		setup.myPriority = 1;
		sendMessage(setup, null);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (myBattleListener != null) myEngine.removeBattleListener(myBattleListener);

				// the BattleAdaptor sends result messages to the server 
				myEngine.addBattleListener(myBattleListener = new DefaultBattleAdaptor(BattleProcessConsole.this));
				BattlefieldSpecification bField = new BattlefieldSpecification(myConfig.getW(), myConfig.getH());
				RobotSpecification[] bots = myEngine.getLocalRepository(myConfig.getBots());
				BattleSpecification spec = new BattleSpecification(myConfig.getRounds(), bField, bots);

				myState.isRunning = true;
				myEngine.runBattle(spec, true);

				// NOTE: because of a bug within the engine at cleanup i wait just a couple of seconds and then go on
				//				try
				//				{
				//					Thread.sleep(1000);
				//				}
				//				catch (InterruptedException ex)
				//				{
				//					Thread.currentThread().interrupt();
				//				}

				RunnerMessage result = new RunnerMessage();
				if (!myState.isStopped)
				{
					// TODO: re visit maybe this could be part of the battle adaptor, but this would loose the STOP request in the current
					// implementation - or you give the stop request to the battle adaptor but that is bad design i guess 
					result.myCommand = RoboRunnerDefines.BATTLE_FINISHED;
					result.myPriority = 1;
					result.myResult = "Battle is over ... do you have more?";
				}
				else
				{
					result.myCommand = RoboRunnerDefines.INFO;
					result.myPriority = 1;
					result.myResult = "I stopped all battles now.";
				}
				sendMessage(result, null);

				myState.isRunning = false;
				myState.isStopped = false;
			}
		}).start();
	}

	private boolean parseConfig(ProcessMessage msg)
	{
		// destination|command|prio|seasons|rounds|fieldw|fieldh|botname1,... 
		//MAIN|WORKING_COMMAND|1|10:35:1000:1000:botName1,botName2,....
		if (myConfig == null) myConfig = new ProcessConfiguration();
		String[] parseResult = msg.myResult.split(RoboRunnerDefines.RES_SPLITTER);
		try
		{
			int i = 0;
			myConfig.setSeasons(parseResult[i++]);
			myConfig.setRounds(parseResult[i++]);
			myConfig.setW(parseResult[i++]);
			myConfig.setH(parseResult[i++]);
			myConfig.setBots(parseResult[i++]);
		}
		catch (NumberFormatException e0)
		{
			RunnerMessage warn = new RunnerMessage();
			warn.myCommand = RoboRunnerDefines.WARN;
			warn.myPriority = 1;
			warn.myResult = "The setup you have send me is bogus!";
			sendMessage(warn, null);
			return false;
		}
		catch (Exception e1)
		{
			e1.printStackTrace(); // not sure if this can happen
			return false;
		}
		return true;

	}

	@Override
	public void sendMessage(RunnerMessage msg, String processName)
	{
		// TODO: not sure if this should be processed with a queue, but i guess not
		// the processName is not realy used for this IMessagehandler.. i let it in, maybe i change my mind about the design later 
		System.out.format(msg.toString());
	}
}
