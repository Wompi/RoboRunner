package robowiki.console.scorer;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import robowiki.console.RoboRunnerConfig;
import robowiki.console.RunnerFunctions;

public class ScoreTableManager
{
	private ArrayList<RunnerScore>	myScoreTables;

	public ScoreTableManager()
	{
		refresh();
	}

	public ArrayList<RunnerScore> getScoreTables()
	{
		return myScoreTables;
	}

	public RunnerScore getScorerForName(String name)
	{
		for (RunnerScore scorer : myScoreTables)
		{
			if (scorer.getName().equalsIgnoreCase(name)) { return scorer; };
		}
		throw new IllegalStateException("No Scorer for name!");
	}

	public String getScorerNames()
	{
		refresh();
		String result = "";
		for (RunnerScore score : myScoreTables)
		{
			result += String.format("%s,", score.getName());
		}
		return result;
	}

	public RunnerScore getScoreTableByName(String name)
	{
		RunnerScore scoreTable = null;
		for (RunnerScore score : myScoreTables)
		{
			if (score.getName().equals(name))
			{
				scoreTable = score;
				break;
			}
		}
		return scoreTable;
	}

	private void refresh()
	{
		String path = RoboRunnerConfig.getInstance().getScorerPath();
		if (RunnerFunctions.checkPath(path, false, true, true))
		{
			try
			{
				ArrayList<String> scoreClassNames = readClassPackageNames(path);
				loadScoreClasses(scoreClassNames);
			}
			catch (IOException e)
			{
				e.printStackTrace(); // should not happen with the checkPath before - but who knows
			}
		}
	}

	private void loadScoreClasses(ArrayList<String> scoreClassNames)
	{
		ClassLoader loader = getClass().getClassLoader();

		myScoreTables = new ArrayList<RunnerScore>();
		for (String className : scoreClassNames)
		{
			//ConsoleWorker.formatConfig("Scorer: %s\n", className);
			try
			{
				Class<?> score = loader.loadClass(className);
				if (score.getSuperclass() == RunnerScore.class)
				{
					RunnerScore scoreTable = (RunnerScore) score.newInstance();
					myScoreTables.add(scoreTable);
					//ConsoleWorker.formatConfig("New Scorer: %s\n", scoreTable.getName());
				}
				//ConsoleWorker.formatConfig("File: %s - interface: %s (%s)\n", className, score.getName(), RunnerScore.class.getName());
			}
			catch (Exception e)
			{
				e.printStackTrace(); // TODO: don't need to be handled
			}
		}
	}

	private ArrayList<String> readClassPackageNames(final String scorerPath) throws IOException
	{
		final ArrayList<String> scoreClassPackageNames = new ArrayList<String>();

		//String[] jars = directory.list(new ScoreJarFileNameFilter()); //TODO: extract classes from JarFiles
		final String sep = System.getProperty("file.separator");
		new ScoreDirectoryTraverse()
		{
			@Override
			public void onFile(final File f)
			{
				String path = f.getPath();
				// this one cuts of the scorer path from the path and what is left is the package
				// separated by sep and ended with .class. Strip it of and ready is the package name 
				path = path.substring(scorerPath.length() + 1, path.length());
				String packageName = path.replace(".class", "").replace(sep, ".");
				scoreClassPackageNames.add(packageName);
			}
		}.traverse(new File(scorerPath), new ScoreTableFileFilter());

		return scoreClassPackageNames;
	}
}

class ScoreTableFileFilter implements java.io.FileFilter
{
	@Override
	public boolean accept(File f)
	{
		boolean result = false;
		try
		{
			boolean r1 = f.canRead();
			boolean r3 = !f.isHidden();
			boolean r4 = f.getName().endsWith(".class") || f.isDirectory();
			result = r1 && r3 && r4;
		}
		catch (SecurityException e)
		{
			e.printStackTrace(); // remove this in release
		}
		return result;
	}
}

class ScoreJarFileNameFilter implements FilenameFilter
{
	@Override
	public boolean accept(File dir, String name)
	{
		return name.endsWith(".jar");
	}
}

class ScoreDirectoryTraverse
{
	public final void traverse(final File f, FileFilter filter) throws IOException
	{
		if (f.isDirectory())
		{
			final File[] childs = f.listFiles(filter);

			for (File child : childs)
			{
				traverse(child, filter);
			}
			return;
		}
		onFile(f);
	}

	public void onFile(final File f)
	{}
}
