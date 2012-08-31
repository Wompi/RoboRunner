package robowiki.console.aspects;

public abstract class RStringAspect extends RAspect
{
	String	myValue;

	@Override
	public String getStringValue()
	{
		return myValue;
	}
}
