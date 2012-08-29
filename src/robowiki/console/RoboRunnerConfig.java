package robowiki.console;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class RoboRunnerConfig
{
	private static RoboRunnerConfig	instance;

	private static Properties		myProperties;
	private boolean					isFirstRun;
	private boolean					hasChanged;

	private RoboRunnerConfig()
	{

		try
		{
			load(getPropertiesPath());
		}
		catch (Exception e0)
		{
			isFirstRun = true;
		}
	}

	public static RoboRunnerConfig getInstance()
	{
		if (instance == null)
		{
			instance = new RoboRunnerConfig();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// because this is a shutdown hook the file should not be written if the configuration is not changed
					if (RoboRunnerConfig.getInstance().hasChanged)
					{
						System.out.format("Shutdown: store settings...\n"); // Shutdown Hooks are going to the System out and not to the console
						instance.store();
					}
				}
			}));
		}
		return instance;
	}

	public void toggleDebug()
	{
		// TODO: brrr re visit this - it looks horrible
		boolean debug = isDebug();
		if (debug) setDebug("1");
		else setDebug("0");
	}

	public void toggleAutoRun()
	{
		// TODO: brrr re visit this - it looks horrible
		boolean run = isAutoRun();
		if (run) setAutoRun("1");
		else setAutoRun("0");
	}

	public boolean isFirstRun()
	{
		return isFirstRun;
	}

	public boolean isDebug()
	{
		String value = myProperties.getProperty(RoboRunnerDefines.DEBUG_KEY);
		if (value == null) return false;
		return Boolean.parseBoolean(value);
	}

	public boolean isAutoRun()
	{
		String value = myProperties.getProperty(RoboRunnerDefines.AUTORUN_KEY);
		if (value == null) return false;
		return Boolean.parseBoolean(value);
	}

	public void setDebug(String value) throws NumberFormatException
	{
		Integer bool = Math.max(0, Math.min(1, Integer.parseInt(value)));
		myProperties.setProperty(RoboRunnerDefines.DEBUG_KEY, (bool == 0) ? "true" : "false");
		hasChanged = true;
	}

	public void setAutoRun(String value) throws NumberFormatException
	{
		Integer bool = Math.max(0, Math.min(1, Integer.parseInt(value)));
		myProperties.setProperty(RoboRunnerDefines.AUTORUN_KEY, (bool == 0) ? "true" : "false");
		hasChanged = true;
	}

	public String getScorerPath()
	{
		// hard coded to prevent unusual behavior but ready to make it changeable
		return myProperties.getProperty(RoboRunnerDefines.SCORER_PATH_KEY, RoboRunnerDefines.SCORE_DIR_NAME);
	}

	public String getSourceRobocodePath()
	{
		return myProperties.getProperty(RoboRunnerDefines.ROBOCODE_SOURCE_PATH_KEY);
	}

	public void setSourceRobocodePath(String path)
	{
		myProperties.setProperty(RoboRunnerDefines.ROBOCODE_SOURCE_PATH_KEY, path);
		hasChanged = true;
	}

	public String getInstallCount()
	{
		return myProperties.getProperty(RoboRunnerDefines.INSTALL_COUNT_KEY);
	}

	public void setInstallCount(String count) throws NumberFormatException
	{
		Integer check = Integer.parseInt(count);
		check = Math.max(1, Math.min(20, check));
		myProperties.setProperty(RoboRunnerDefines.INSTALL_COUNT_KEY, check.toString());
		hasChanged = true;
	}

	public String getSourceBotPath()
	{
		return myProperties.getProperty(RoboRunnerDefines.BOT_SOURCE_KEY);
	}

	public void setSourceBotPath(String path)
	{
		myProperties.setProperty(RoboRunnerDefines.BOT_SOURCE_KEY, path);
		hasChanged = true;
	}

	public String getChallengeName()
	{
		return myProperties.getProperty(RoboRunnerDefines.CHALLENGE_NAME_KEY);
	}

	public void setChallengeName(String name)
	{
		myProperties.setProperty(RoboRunnerDefines.CHALLENGE_NAME_KEY, name);
		hasChanged = true;
	}

	public String getChallengeBot()
	{
		return myProperties.getProperty(RoboRunnerDefines.CHALLENGE_BOT_KEY);
	}

	public void setChallengeBot(String name)
	{
		myProperties.setProperty(RoboRunnerDefines.CHALLENGE_BOT_KEY, name);
		hasChanged = true;
	}

	public String getBotListSeasons()
	{
		return myProperties.getProperty(RoboRunnerDefines.BOTLIST_SEASONS_KEY);
	}

	public void setBotListSeasons(String seasons)
	{
		Integer check = Integer.parseInt(seasons);
		check = Math.max(1, Math.min(5000, check));
		myProperties.setProperty(RoboRunnerDefines.BOTLIST_SEASONS_KEY, check.toString());
		hasChanged = true;
	}

	private static void load(String fileName) throws IOException
	{
		FileInputStream is = null;
		try
		{
			myProperties = new Properties();
			is = new FileInputStream(new File(fileName));
			myProperties.load(new BufferedInputStream(is));
		}
		finally
		{
			if (is != null) is.close();
		}
	}

	private void store()
	{
		FileOutputStream fos = null;

		try
		{
			fos = new FileOutputStream(getPropertiesPath());
			myProperties.store(
					fos,
					String.format("RoboRunner properties version %s. Last update %s", System.getProperty(RoboRunnerDefines.VERSION_KEY),
							new Date().toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	private String getPropertiesPath()
	{
		String sep = System.getProperty("file.separator");
		String workDir = System.getProperty("user.dir");
		if (workDir.endsWith(sep)) sep = "";
		return String.format("%s%s%s", workDir, sep, RoboRunnerDefines.MAIN_PROPERTIES_NAME);
	}
}
