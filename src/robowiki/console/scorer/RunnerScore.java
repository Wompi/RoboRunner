package robowiki.console.scorer;

import java.util.HashMap;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;

public abstract class RunnerScore
{
	private HashMap<EAspect, RAspect>	myInterests;
	private HashMap<EAspect, RAspect>	myChallenger;

	public RunnerScore()
	{
		myInterests = new HashMap<EAspect, RAspect>();
		myChallenger = new HashMap<EAspect, RAspect>();
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

	public final void setChallengerResults(HashMap<EAspect, RAspect> challenger)
	{
		myChallenger = challenger;
	}

	protected final RAspect getChallengerAspect(EAspect type)
	{
		RAspect interest = myChallenger.get(type);
		if (interest == null) throw new IllegalAccessError("You have called for an aspect that you have not registered!");
		return interest;
	}

	protected final RAspect getAspect(EAspect type)
	{
		RAspect interest = myInterests.get(type);
		if (interest == null) throw new IllegalAccessError("You have called for an aspect that you have not registered!");
		return interest;
	}

	public String getFooter()
	{
		return "";
	}

	public abstract String getPrintString();

	public abstract String getName();
}
