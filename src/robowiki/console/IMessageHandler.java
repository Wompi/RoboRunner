package robowiki.console;

public interface IMessageHandler
{
	public void receiveMessage(ProcessMessage msg);

	public void sendMessage(RunnerMessage msg, String pocessName);
}
