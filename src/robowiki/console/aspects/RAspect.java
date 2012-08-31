package robowiki.console.aspects;

import robowiki.console.RoboRunnerResult;

public abstract class RAspect
{
	double	myValue;

	public abstract EAspect type();

	public abstract void setValue(RoboRunnerResult value);

	public final int getIntValue()
	{
		return (int) myValue;
	}

	public String getStringValue()
	{
		return Double.toString(myValue);
	}

	public double getAvgValue()
	{
		return myValue;
	}

	public double getStandardDeviation()
	{
		return 0;
	}

	public double getStandardError()
	{
		return 0;
	}

	public static RAspect getAspect(final EAspect type)
	{
		switch (type)
		{
			case NAME:
				return new RStringAspect()
				{
					@Override
					public EAspect type()
					{
						return type;
					}

					@Override
					public void setValue(RoboRunnerResult value)
					{
						myValue = value.myID;
					}
				};
			case SURVIVAL:
				return new RSumAspect()
				{
					@Override
					public EAspect type()
					{
						return type;
					}

					@Override
					public void setValue(RoboRunnerResult value)
					{
						myValue += value.mySurvival;
						myValues.add((double) value.myScore);
					}
				};

			case SCORE:
				return new RSumAspect()
				{
					@Override
					public EAspect type()
					{
						return type;
					}

					@Override
					public void setValue(RoboRunnerResult value)
					{
						myValue += value.myScore;
						myValues.add((double) value.myScore);
					}
				};

			case BATTLE_COUNT:
				return new RAspect()
				{

					@Override
					public EAspect type()
					{
						return type;
					}

					@Override
					public void setValue(RoboRunnerResult value)
					{
						myValue++;
					}
				};
		}
		throw new IllegalArgumentException("Unknown type!");
	}
}
