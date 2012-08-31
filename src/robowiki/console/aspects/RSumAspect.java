package robowiki.console.aspects;

import java.util.ArrayList;

abstract class RSumAspect extends RAspect
{
	protected ArrayList<Double>	myValues;

	public RSumAspect()
	{
		myValues = new ArrayList<Double>();
	}

	@Override
	public double getAvgValue()
	{
		double sum = 0;
		for (double value : myValues)
		{
			sum += value;
		}
		return (sum / myValues.size());
	}

	@Override
	public double getStandardDeviation()
	{
		double avg = getAvgValue();
		double sumSquares = 0;
		for (double value : myValues)
		{
			sumSquares += Math.pow(avg - value, 2);
		}
		return Math.sqrt(sumSquares / myValues.size());
	}

	@Override
	public double getStandardError()
	{
		return getStandardDeviation() / Math.sqrt(myValues.size());
	}

	@Override
	public double getMinimum()
	{
		double min = Double.MAX_VALUE;
		for (double value : myValues)
		{
			min = Math.min(min, value);
		}
		return min;
	}

	@Override
	public double getMaximum()
	{
		double max = Double.MIN_VALUE;
		for (double value : myValues)
		{
			max = Math.max(max, value);
		}
		return max;
	}
}
