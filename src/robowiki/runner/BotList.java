package robowiki.runner;

import java.util.List;

import robowiki.console.RoboRunnerDefines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class BotList
{
	private final List<String>	_botNames;

	// how many uses has this list in this session
	// similar to the skipped thingy but a little easier to maintain
	public int					useCount;

	public BotList(String botName)
	{
		_botNames = Lists.newArrayList(botName);
	}

	public BotList(List<String> botNames)
	{
		_botNames = Lists.newArrayList(botNames);
	}

	public List<String> getBotNames()
	{
		return ImmutableList.copyOf(_botNames);
	}

	@Override
	public String toString()
	{
		String result = "";
		for (String name : _botNames)
		{
			result += String.format("%s%s", name, RoboRunnerDefines.BOT_SPLITTER);
		}
		return result;
	}
}
