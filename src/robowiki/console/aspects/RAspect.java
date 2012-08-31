package robowiki.console.aspects;

import robowiki.console.RoboRunnerResult;

public abstract class RAspect
{
	protected double	myValue;

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

	public double getMinimum()
	{
		return myValue;
	}

	public double getMaximum()
	{
		return myValue;
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
						myValues.add((double) value.mySurvival);
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
			case SURVIVAL_BONUS:
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
						myValue += value.mySurvivalBonus;
						myValues.add((double) value.mySurvivalBonus);
					}
				};
			case BULLET_DMG:
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
						myValue += value.myBulletDmg;
						myValues.add((double) value.myBulletDmg);
					}
				};
			case BULLET_BONUS:
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
						myValue += value.myBulletBonus;
						myValues.add((double) value.myBulletBonus);
					}
				};
			case RAM_DMG:
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
						myValue += value.myRamDmg;
						myValues.add((double) value.myRamDmg);
					}
				};
			case RAM_BONUS:
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
						myValue += value.myRamBonus;
						myValues.add((double) value.myRamBonus);
					}
				};
			case FIRST:
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
						myValue += value.myFirsts;
						myValues.add((double) value.myFirsts);
					}
				};
			case SECOND:
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
						myValue += value.mySeconds;
						myValues.add((double) value.mySeconds);
					}
				};
			case THIRD:
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
						myValue += value.myThirds;
						myValues.add((double) value.myThirds);
					}
				};
		}
		throw new IllegalArgumentException("Unknown type!");
	}
}
