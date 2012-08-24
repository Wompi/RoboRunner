package robowiki.console;

public class ProcessMessage extends RunnerMessage
{
	private final String	myDestination;

	public ProcessMessage(String destination)
	{
		myDestination = destination;
	}

	public String getDestination()
	{
		return myDestination;
	}

	@Override
	public String toString()
	{
		String SEP = RoboRunnerDefines.MSG_DELIMITER;
		return String.format("%s%s%s%s%d%s%s\n", myDestination, SEP, myCommand, SEP, myPriority, SEP, myResult);
	}

}
