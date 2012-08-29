package robowiki.console.scorer;

import robowiki.console.RoboRunnerResult;

abstract class RAspect
{
	int	myValue;

	public abstract EAspect type();

	public abstract void setValue(RoboRunnerResult value);

	public final int getValue()
	{
		return myValue;
	}

	public static RAspect getAspect(final EAspect type)
	{
		switch (type)
		{
			case SCORE:
				return new RAspect()
				{
					@Override
					public void setValue(RoboRunnerResult value)
					{
						myValue = value.myScore;
					}

					@Override
					public EAspect type()
					{
						return type;
					}
				};
				//			case SURVIVAL:
				//				return new RAspect()
				//				{
				//					@Override
				//					public int value(RoboRunnerResult value)
				//					{
				//						return value.mySurvival;
				//					}
				//
				//					@Override
				//					public EAspect type()
				//					{
				//						return type;
				//					}
				//				};
		}
		throw new IllegalArgumentException("Unknown type!");
	}
}
