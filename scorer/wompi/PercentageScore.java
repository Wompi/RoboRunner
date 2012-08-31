package wompi;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;
import robowiki.console.scorer.RunnerScore;

public class PercentageScore extends RunnerScore
{

	@Override
	public String getName()
	{
		return "PERCENT_SCORE";
	}

	@Override
	public EAspect getSortType()
	{
		return EAspect.SCORE;
	}

	@Override
	public EAspect[] getInterests()
	{
		return new EAspect[] { EAspect.SCORE, EAspect.NAME, EAspect.BATTLE_COUNT };
	}

	@Override
	public String getPrintString()
	{
		RAspect score = getAspect(EAspect.SCORE);
		RAspect name = getAspect(EAspect.NAME);
		RAspect count = getAspect(EAspect.BATTLE_COUNT);

		return String.format("%35s avg:%5d sd:%7.2f se:%7.2f battles:%d\n", name.getStringValue(), (int) score.getAvgValue(),
				score.getStandardDeviation(), score.getStandardError(), count.getIntValue());
	}
}
