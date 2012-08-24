package robowiki.console;

public interface IMessageHandler
{
	public void receiveMessage(RunnerMessage msg);

	public void sendMessage(RunnerMessage msg);
}
