package robowiki.console.scorer;

import java.util.HashMap;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;

public abstract class RunnerScore
{
	private HashMap<EAspect, RAspect>	myInterests;

	public RunnerScore()
	{
		myInterests = new HashMap<EAspect, RAspect>();
	}

	public EAspect getSortType()
	{
		return null;
	}

	public abstract EAspect[] getInterests();

	public final void setResults(HashMap<EAspect, RAspect> results)
	{
		myInterests = results;
	}

	protected final RAspect getAspect(EAspect type)
	{
		RAspect interest = myInterests.get(type);
		if (interest == null) throw new IllegalAccessError("You have called for an aspect that you have not registered!");
		return interest;
	}

	public abstract String getPrintString();

	public abstract String getName();
}
