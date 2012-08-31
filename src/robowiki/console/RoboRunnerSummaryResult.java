package robowiki.console;

import java.util.HashMap;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;

public class RoboRunnerSummaryResult extends RoboRunnerResult
{
	private final HashMap<EAspect, RAspect>	myResults;

	public RoboRunnerSummaryResult()
	{
		myResults = new HashMap<EAspect, RAspect>();
	}

	public void registerInterests(EAspect[] types)
	{
		for (EAspect type : types)
		{
			myResults.put(type, RAspect.getAspect(type));
		}
	}

	public void setResults(RoboRunnerResult result)
	{
		for (RAspect aspect : myResults.values())
		{
			aspect.setValue(result);
		}
	}

	public RAspect getResultForType(EAspect type)
	{
		return myResults.get(type);
	}

	public HashMap<EAspect, RAspect> getResults()
	{
		// TODO: revisit - maybe immutable are new instance
		return myResults;
	}
}
