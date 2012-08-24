package robowiki.console;

public class RunnerFunctions
{

	// This is a little awkward but for now it has to go
	// The ProcessName can be set with the environment change to the ProcessBuilder but the main program can not
	// so it uses the properties
	public static String getProcessName()
	{
		String name = System.getenv(RoboRunnerDefines.PROCESS_NAME_KEY);
		if (name == null)
		{
			name = System.getProperty(RoboRunnerDefines.PROCESS_NAME_KEY);
		}
		return name;
	}
}
