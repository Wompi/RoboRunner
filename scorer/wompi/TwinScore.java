package wompi;

import java.util.HashMap;

import robowiki.console.aspects.EAspect;
import robowiki.console.aspects.RAspect;
import robowiki.console.aspects.RCustomAspect;
import robowiki.console.scorer.RunnerScore;

public class TwinScore extends RunnerScore
{
	private final HashMap<EAspect, RCustomAspect>	myChallSum	= new HashMap<EAspect, RCustomAspect>();

	public TwinScore()
	{
		myChallSum.put(EAspect.FIRST, new RCustomAspect()
		{
			@Override
			public EAspect type()
			{
				return EAspect.FIRST;
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
	public EAspect[] getInterests()
	{
		return new EAspect[] { EAspect.NAME, EAspect.FIRST, EAspect.BATTLE_COUNT };
	}

	@Override
	public EAspect getSortType()
	{
		return EAspect.FIRST;
	}

	@Override
	public String getPrintString()
	{

		RAspect challFirst = getChallengerAspect(EAspect.FIRST);

		RAspect botName = getAspect(EAspect.NAME);
		RAspect botFirst = getAspect(EAspect.FIRST);
		RAspect botCount = getAspect(EAspect.BATTLE_COUNT);

		double bavg = botFirst.getAvgValue();

		double cmin = challFirst.getMinimum();
		double cmax = challFirst.getMaximum();
		double cavg = challFirst.getAvgValue();

		myChallSum.get(EAspect.FIRST).setValue(cavg * 100.0 / (cavg + bavg));

		//@formatter:off
		return String.format("vs %50s min: %6.2f avg: %6.2f max: %6.2f sd: %6.2f se: %6.2f battles: %d\n",
				botName.getStringValue(),
				cmin*100.0/(cavg+bavg),
				cavg*100.0/(cavg+bavg),
				cmax*100.0/(cavg+bavg),
				(challFirst.getStandardDeviation()*100.0/cavg),
				(challFirst.getStandardError()*100/cavg),
				botCount.getIntValue()
				);
		//@formatter:on
	}

	@Override
	public String getFooter()
	{
		RAspect chall = myChallSum.get(EAspect.FIRST);

		double avg = chall.getAvgValue();
		//@formatter:off
		return String.format("%s avg: %6.2f sd: %6.2f se: %6.2f\n", 
				getChallengerAspect(EAspect.NAME).getStringValue(),
				avg,
				chall.getStandardDeviation()*100.0/avg,
				chall.getStandardError()*100.0/avg);
		//@formatter:on
	}

	@Override
	public String getName()
	{
		return "TWIN_SCORE";
	}

}
