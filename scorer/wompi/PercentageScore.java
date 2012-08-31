package wompi;

import java.util.HashMap;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;
import robowiki.console.aspects.RCustomAspect;
import robowiki.console.scorer.RunnerScore;

public class PercentageScore extends RunnerScore
{
	private final HashMap<EAspect, RCustomAspect>	myChallSum	= new HashMap<EAspect, RCustomAspect>();

	public PercentageScore()
	{
		myChallSum.put(EAspect.SCORE, new RCustomAspect()
		{
			@Override
			public EAspect type()
			{
				return EAspect.SCORE;
			}

			@Override
			public void setValue(double value)
			{
				myValue += value;
				myValues.add(value);
			}
		});

	}

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
		double bavg = botScore.getAvgValue();
		double cavg = challScore.getAvgValue();
		double cmin = challScore.getMinimum();
		double cmax = challScore.getMaximum();

		myChallSum.get(EAspect.SCORE).setValue((cavg * 100.0 / (cavg + bavg)));

		//@formatter:off
		return String.format("vs %35s min: %6.2f  avg: %6.2f  max:%6.2f  sd: %6.2f  se: %6.2f  battles: %d\n", 
				botName.getStringValue(), 
				(cmin *100.0/(cavg + bavg)), 
				(cavg * 100.0 / (cavg + bavg)), 
				(cmax *100.0/(cavg + bavg)),
				(botScore.getStandardDeviation()*100.0/cavg),
				(botScore.getStandardError()*100.0/cavg), 
				botCount.getIntValue());
		//@formatter:on
	}

	@Override
	public String getFooter()
	{
		RAspect chall = myChallSum.get(EAspect.SCORE);

		double avg = chall.getAvgValue();
		//@formatter:off
		return String.format("%s avg: %6.2f sd: %6.2f se: %6.2f\n", 
				getChallengerAspect(EAspect.NAME).getStringValue(),
				avg,
				chall.getStandardDeviation()*100.0/avg,
				chall.getStandardError()*100.0/avg);
		//@formatter:on
	}

}
