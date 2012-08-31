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
		RAspect challScore = getChallengerAspect(EAspect.SCORE);
		RAspect botScore = getAspect(EAspect.SCORE);
		RAspect botName = getAspect(EAspect.NAME);
		RAspect botCount = getAspect(EAspect.BATTLE_COUNT);
		double avgValue = botScore.getAvgValue();
		double avgChallenger = challScore.getAvgValue();
		double minValue = botScore.getMinimum();
		double maxValue = botScore.getMaximum();
		//@formatter:off
		return String.format("%35s min: %6.2f  avg: %6.2f  max:%6.2f  sd: %6.2f  se: %6.2f  battles: %d\n", 
				botName.getStringValue(), 
				(minValue *100.0/(avgChallenger + minValue)), 
				(avgValue * 100.0 / (avgChallenger + avgValue)), 
				(maxValue *100.0/(avgChallenger + maxValue)),
				(botScore.getStandardDeviation()*100.0/avgValue),
				(botScore.getStandardError()*100.0/avgValue), 
				botCount.getIntValue());
		//@formatter:on
	}
}
