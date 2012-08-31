package wompi;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;
import robowiki.console.scorer.RunnerScore;

public class PatternChallenge extends RunnerScore
{

	@Override
	public EAspect[] getInterests()
	{
		return new EAspect[] { EAspect.NAME, EAspect.BULLET_DMG, EAspect.BATTLE_COUNT };
	}

	@Override
	public EAspect getSortType()
	{
		return EAspect.BULLET_DMG;
	}

	@Override
	public String getPrintString()
	{
		RAspect chalDmg = getChallengerAspect(EAspect.BULLET_DMG);
		RAspect botName = getAspect(EAspect.NAME);
		RAspect botDmg = getAspect(EAspect.BULLET_DMG);
		RAspect botCount = getAspect(EAspect.BATTLE_COUNT);

		double min = (chalDmg.getMinimum() / botDmg.getMinimum()) * 100.0;
		double avg = (chalDmg.getAvgValue() / botDmg.getAvgValue()) * 100.0;
		double max = (chalDmg.getMaximum() / botDmg.getMaximum()) * 100.0;

		return String.format("%25s min: %6.2f avg: %6.2f max: %6.2f battles: %d\n", botName.getStringValue(), min, avg, max, botCount.getIntValue());
	}

	@Override
	public String getName()
	{
		return "PATTERN_CHALLENGE";
	}

}
