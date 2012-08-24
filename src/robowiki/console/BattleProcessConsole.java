package robowiki.console;

import java.io.File;
import java.io.IOException;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robowiki.console.process.ProcessConfiguration;

public class BattleProcessConsole implements IMessageHandler
{
	private RobocodeEngine			myEngine;
	private ProcessConfiguration	myConfig;

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
		Thread streamWatcher = new Thread(new ReceiveWorker(System.in, this, "PROCESS"));
		streamWatcher.start();

		RunnerMessage start = new RunnerMessage("PROCESS");
		start.myCommand = RoboRunnerDefines.STARTED;
		start.myResult = "ok i'm ready to go";
		start.myPriority = 1;
		sendMessage(start);

	}

	@Override
	public void receiveMessage(RunnerMessage msg)
	{
		if (msg.myCommand.equals(RoboRunnerDefines.INIT_REQUEST))
		{
			/// lets start the engine
			String result = "i'm still initilized - send me some setup";
			if (myEngine == null)
			{
				// one enhancement would be to made this in a thread then the messages can still be processed like setups or something else
				// this leads to message blocking 
				File path = new File("/Volumes/Data/roborunner-1.2.1/robocodes/r1");
				myEngine = new RobocodeEngine(path);
				result = "i'm now fully initialized - send me some setup";
			}

			RunnerMessage setup = new RunnerMessage("PROCESS");
			setup.myCommand = RoboRunnerDefines.SETUP_REQUEST;
			setup.myResult = result;
			setup.myPriority = 1;
			sendMessage(setup);
		}
		else if (msg.myCommand.equals(RoboRunnerDefines.SETUP))
		{
			// give the engine something to setup it's state
			if (checkEngineState())
			{
				// parse setup
				// i need numRounds|battleField_w|battleField_h|bots,..,..,.... (seasons comes with the run message)
				//MAIN|SETUP|1|35:1000:1000:botName1,botName2,....
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
					RunnerMessage info = new RunnerMessage("PROCESS");
					info.myCommand = RoboRunnerDefines.INFO;
					info.myPriority = 1;
					info.myResult = "The setup you have send me is bogus!";
					sendMessage(info);
					return;
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

				RunnerMessage setup = new RunnerMessage("PROCESS");
				setup.myCommand = RoboRunnerDefines.READY;
				setup.myResult = String.format("i'm ready - my setup is: rounds=%d w=%d h=%d bots=(%s)", myConfig.getRounds(), myConfig.getW(),
						myConfig.getH(), myConfig.getBots());
				setup.myPriority = 1;
				sendMessage(setup);
			}
		}
		else if (msg.myCommand.equals(RoboRunnerDefines.RUN))
		{
			// here we can start the battles 
			// i need seasons to know how much cycles the engine should run
			// this should be made configurable to increase or decrease the cycles while running .. maybe because the challenge has a high standard error or something
			if (checkEngineState() && checkSetupState())
			{
				myConfig.setSeasons(msg.myResult);

				for (int i = 0; i < myConfig.getSeasons(); i++)
				{
					myEngine.addBattleListener(new BattleAdaptor()
					{}); // TODO: write a battleadaptor for the results
					BattlefieldSpecification bField = new BattlefieldSpecification(myConfig.getW(), myConfig.getH());
					RobotSpecification[] bots = myEngine.getLocalRepository(myConfig.getBots());
					BattleSpecification spec = new BattleSpecification(myConfig.getRounds(), bField, bots);
					myEngine.runBattle(spec, true);

					RunnerMessage result = new RunnerMessage("PROCESS");
					result.myCommand = RoboRunnerDefines.RESULT;
					result.myPriority = 1;
					result.myResult = String.format("Season [%d-%d] is over now. I'm to lazy to give you the results! Just guess who won.", i + 1,
							myConfig.getSeasons());
					sendMessage(result);
				}
			}
		}
		else
		{
			RunnerMessage unknown = new RunnerMessage("PROCESS");
			unknown.myCommand = RoboRunnerDefines.UNKNOWN;
			unknown.myResult = "hey dude stop sending me " + msg.myCommand + " stuff. I have no clue about it!";
			unknown.myPriority = 1;
			sendMessage(unknown);
		}
	}

	@Override
	public void sendMessage(RunnerMessage msg)
	{
		System.out.format(msg.toString());
	}

	private boolean checkEngineState()
	{
		if (myEngine == null)
		{
			RunnerMessage info = new RunnerMessage("PROCESS");
			info.myCommand = RoboRunnerDefines.INFO;
			info.myPriority = 1;
			info.myResult = "Dude what about initializtion?";
			sendMessage(info);
			return false;
		}
		return true;
	}

	private boolean checkSetupState()
	{
		if (myConfig == null)
		{
			RunnerMessage info = new RunnerMessage("PROCESS");
			info.myCommand = RoboRunnerDefines.INFO;
			info.myPriority = 1;
			info.myResult = "Dude what about setup?";
			sendMessage(info);
			return false;
		}
		return true;
	}

}
