package robowiki.console.aspects;

import robowiki.console.RoboRunnerResult;

public abstract class RCustomAspect extends RSumAspect
{
	public abstract void setValue(double value);

	@Override
	public final void setValue(RoboRunnerResult value)
	{
		// not needed here
	}

}
