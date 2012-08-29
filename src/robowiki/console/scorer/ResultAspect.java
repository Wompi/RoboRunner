package robowiki.console.scorer;

import java.util.HashMap;

import robowiki.console.RoboRunnerResult;

public abstract class ResultAspect
{
	private final HashMap<EAspect, RAspect>	myInterests	= new HashMap<EAspect, RAspect>();

	public void setValues(RoboRunnerResult result)
	{
		for (RAspect aspect : myInterests.values())
		{
			aspect.setValue(result);
		}
	}

	protected final int getValue(EAspect type)
	{
		RAspect interest = myInterests.get(type);
		if (interest == null)
		{
			myInterests.put(type, interest = RAspect.getAspect(type));
		}
		return interest.getValue();
	}

	public abstract String printResult();

	public abstract String getName();
}
