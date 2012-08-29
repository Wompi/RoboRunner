package wompi;

import robowiki.console.scorer.EAspect;
import robowiki.console.scorer.ResultAspect;

public class SurvivalScore extends ResultAspect
{
	@Override
	public String getName()
	{
		return "SURVIVAL_SCORE";
	}

	@Override
	public String printResult()
	{
		return String.format(" SurvivalTest: %d\n", getValue(EAspect.SCORE));
	}
}
