package robowiki.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RunnerFunctions
{
	// This is a little awkward but for now it has to go
	// The ProcessName can be set with the environment change to the ProcessBuilder but the main program can not
	// so it uses the properties
	public static String getProcessName()
	{
		String name = System.getenv(RoboRunnerDefines.PROCESS_NAME_KEY);
		if (name == null)
		{
			name = System.getProperty(RoboRunnerDefines.PROCESS_NAME_KEY);
		}
		return name;
	}

	// with this function you can check if a path exists. Useful for configuration stuff
	public static boolean checkPath(String path, boolean isMulti, boolean isDirectoryCheck, boolean shouldExist)
	{
		if (path == null)
		{
			ConsoleWorker.format("path - null does not exist, try again\n");
			return false;
		}

		// just in case it comes from somewhere with a line break
		path = path.replace(System.getProperty("line.separator"), "");

		boolean result = true;
		String[] pathField;
		if (isMulti) pathField = path.split(RoboRunnerDefines.INTERNAL_PATH_SPLITTER);
		else
		{
			pathField = new String[1];
			pathField[0] = path;
		}

		for (String splitPath : pathField)
		{
			splitPath = splitPath.trim();
			File pathCheck = new File(splitPath);
			if (isDirectoryCheck)
			{
				//				String sep = System.getProperty("file.separator");
				//				if (!splitPath.endsWith(sep))
				//				{
				//					splitPath += sep;
				//				}
				if (!pathCheck.isDirectory())
				{
					ConsoleWorker.format("Sorry, %s is not a directory.\n", splitPath);
					result = false;
				}
			}
			else
			{
				if (!pathCheck.exists())
				{
					if (shouldExist) ConsoleWorker.format("Sorry, %s does not exist.\n", splitPath);
					result = false;
				}
			}
		}
		return result;
	}

	// useful if you want to get rid of old RoboCode installations within the RoboRunner environment (be careful with that it could remove important directories)
	public static void deleteDirectory(File file) throws IOException
	{
		if (file.isDirectory())
		{
			if (file.list().length == 0)
			{
				file.delete();
				ConsoleWorker.format("Directory is deleted : %s\n", file.getAbsolutePath());
			}
			else
			{
				String files[] = file.list();
				for (String temp : files)
				{
					File fileDelete = new File(file, temp);
					deleteDirectory(fileDelete);
				}
				if (file.list().length == 0)
				{
					file.delete();
					ConsoleWorker.format("Directory is deleted : %s\n", file.getAbsolutePath());
				}
			}
		}
		else
		{
			file.delete();
			ConsoleWorker.format("File is deleted : %s\n", file.getAbsolutePath());
		}
	}

	public static void copyFolder(File src, File dest, FilenameFilter directoryFilter) throws IOException
	{
		if (src.isDirectory())
		{
			if (directoryFilter == null)
			{
				ConsoleWorker.format("ERROR: src i a directory and needs a filter to work\n");
				return;
			}

			if (!dest.exists())
			{
				dest.mkdir();
				ConsoleWorker.format("Directory copied from %s to %s\n", src, dest);
			}
			String files[] = src.list(directoryFilter);

			for (String file : files)
			{
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile, directoryFilter);
			}
		}
		else
		{
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0)
			{
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
			ConsoleWorker.format("File copied from %s to %s\n", src, dest);
		}
	}

}
