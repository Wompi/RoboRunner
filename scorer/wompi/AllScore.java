package wompi;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;
import robowiki.console.scorer.RunnerScore;

public class AllScore extends RunnerScore
{
	@Override
	public EAspect[] getInterests()
	{
		return new EAspect[] { EAspect.SCORE, EAspect.SURVIVAL, EAspect.SURVIVAL_BONUS, EAspect.BULLET_DMG, EAspect.BULLET_BONUS, EAspect.RAM_DMG,
				EAspect.RAM_BONUS, EAspect.FIRST, EAspect.SECOND, EAspect.THIRD, EAspect.NAME };
	}

	@Override
	public EAspect getSortType()
	{
		return EAspect.SCORE;
	}

	@Override
	public String getPrintString()
	{
		RAspect botScore = getAspect(EAspect.SCORE);
		RAspect botSurv = getAspect(EAspect.SURVIVAL);
		RAspect botSurvB = getAspect(EAspect.SURVIVAL_BONUS);
		RAspect botDmg = getAspect(EAspect.BULLET_DMG);
		RAspect botDmgB = getAspect(EAspect.BULLET_BONUS);
		RAspect botRam = getAspect(EAspect.RAM_DMG);
		RAspect botRamB = getAspect(EAspect.RAM_BONUS);
		RAspect botFirst = getAspect(EAspect.FIRST);
		RAspect botSecond = getAspect(EAspect.SECOND);
		RAspect botThird = getAspect(EAspect.THIRD);
		RAspect botName = getAspect(EAspect.NAME);

		//@formatter:off
		return String.format("%35s %6d %6d %6d %6d %6d %6d %6d %4d %4d %4d\n",
				botName.getStringValue(),
				(int)botScore.getAvgValue(),
				(int)botSurv.getAvgValue(),
				(int)botSurvB.getAvgValue(),
				(int)botDmg.getAvgValue(),
				(int)botDmgB.getAvgValue(),
				(int)botRam.getAvgValue(),
				(int)botRamB.getAvgValue(),
				(int)botFirst.getAvgValue(),
				(int)botSecond.getAvgValue(),
				(int)botThird.getAvgValue()
				);
		//@formatter:on
	}

	@Override
	public String getName()
	{
		return "ALL_SCORE";
	}

}
