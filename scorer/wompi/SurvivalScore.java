package wompi;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;
import robowiki.console.scorer.RunnerScore;

public class SurvivalScore extends RunnerScore
{
	@Override
	public String getName()
	{
		return "SURVIVAL_SCORE";
	}

	@Override
	public EAspect getSortType()
	{
		return EAspect.SURVIVAL;
	}

	@Override
	public EAspect[] getInterests()
	{
		return new EAspect[] { EAspect.SURVIVAL, EAspect.NAME, EAspect.BATTLE_COUNT };
	}

	@Override
	public String getPrintString()
	{
		RAspect score = getAspect(EAspect.SURVIVAL);
		RAspect name = getAspect(EAspect.NAME);
		RAspect count = getAspect(EAspect.BATTLE_COUNT);

		return String.format("%25s avg:%5d sd:%7.2f se:%7.2f battles:%d\n", name.getStringValue(), (int) score.getAvgValue(),
				score.getStandardDeviation(), score.getStandardError(), count.getIntValue());
	}
}
