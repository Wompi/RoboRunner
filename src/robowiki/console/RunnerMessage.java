package robowiki.console;

public class RunnerMessage implements Comparable<RunnerMessage>
{
	private final String	mySource;
	String					myCommand;
	String					myResult;
	int						myPriority;

	public RunnerMessage()
	{
		mySource = RunnerFunctions.getProcessName();
	}

	@Override
	public int compareTo(RunnerMessage o)
	{
		// TODO Auto-generated method stub
		return myPriority - o.myPriority;
	}

	@Override
	public String toString()
	{
		String SEP = RoboRunnerDefines.MSG_DELIMITER;
		return String.format("%s%s%s%s%d%s%s\n", mySource, SEP, myCommand, SEP, myPriority, SEP, myResult);
	}
}
