package robowiki.console.process;

public class ProcessConfiguration
{
	int		myChallengeID;
	int		myCurrentSeason;
	int		myH;
	int		myW;
	int		myRounds;
	String	myBots;			// separated by ','

	public void setChallengeID(String value) throws NumberFormatException
	{
		myChallengeID = Integer.parseInt(value);
	}

	public void setCurrentSeason(String value) throws NumberFormatException
	{
		myCurrentSeason = Integer.parseInt(value);
	}

	public void setW(String bW) throws NumberFormatException
	{
		myW = Math.max(400, Math.min(5000, Integer.parseInt(bW)));
	}

	public void setH(String bH) throws NumberFormatException
	{
		myH = Math.max(400, Math.min(5000, Integer.parseInt(bH)));
	}

	public void setRounds(String rounds) throws NumberFormatException
	{
		myRounds = Math.max(1, Math.min(10000, Integer.parseInt(rounds))); // maybe i should'nt cap the max rounds - well i do it anyway
	}

	public void setBots(String bots)
	{
		myBots = bots; // TODO: make a check to see if all bots available , the message has a \n on the end so get rid of it
	}

	public int getW()
	{
		return myW;
	}

	public int getH()
	{
		return myH;
	}

	public int getRounds()
	{
		return myRounds;
	}

	public int getChallengeID()
	{
		return myChallengeID;
	}

	public int getCurrentSeason()
	{
		return myCurrentSeason;
	}

	public String getBots()
	{
		return myBots;
	}
}
