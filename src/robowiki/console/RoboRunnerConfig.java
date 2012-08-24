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
	public static boolean			isFirstRun;

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
					System.out.format("Shutdown: store settings...\n");
					instance.store();
				}
			}));
		}
		return instance;
	}

	public String getSourceRobocodePath()
	{
		return myProperties.getProperty(RoboRunnerDefines.ROBOCODE_SOURCE_PATH_KEY);
	}

	public void setSourceRobocodePath(String path)
	{
		myProperties.setProperty(RoboRunnerDefines.ROBOCODE_SOURCE_PATH_KEY, path);
	}

	public String getInstallCount()
	{
		return myProperties.getProperty(RoboRunnerDefines.INSTALL_COUNT_KEY);
	}

	public String getSourceBotPath()
	{
		return myProperties.getProperty(RoboRunnerDefines.BOT_SOURCE_KEY);
	}

	public void setSourceBotPath(String path)
	{
		myProperties.setProperty(RoboRunnerDefines.BOT_SOURCE_KEY, path);
	}

	public void setInstallCount(String count) throws NumberFormatException
	{
		Integer check = Integer.parseInt(count);
		check = Math.max(1, Math.min(20, check));
		myProperties.setProperty(RoboRunnerDefines.INSTALL_COUNT_KEY, check.toString());
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
		// because this is a shutdown hook the file should not be written if the configuration is empty
		if (myProperties.size() == 0) return;

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
