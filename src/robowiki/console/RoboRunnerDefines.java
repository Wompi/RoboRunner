package robowiki.console;

public class RoboRunnerDefines
{
	// message related
	public static final String	STARTED						= "START";
	public static final String	RESULT						= "RESULT";
	public static final String	INIT_REQUEST				= "INIT_REQUEST";
	public static final String	SETUP_REQUEST				= "SETUP_REQUEST";
	public static final String	STOP_REQUEST				= "STOP_REQUEST";
	public static final String	SETUP						= "SETUP";
	public static final String	RUN							= "RUN";
	public static final String	READY						= "READY";
	public static final String	BATTLE_ERROR				= "BATTLE_ERROR";
	public static final String	UNKNOWN						= "UNKNOWN";
	public static final String	INFO						= "INFO";

	// console related
	public static final String	CONFIG						= "CONFIG";
	public static final String	QUIT						= "QUIT";
	public static final String	EXIT						= "EXIT";
	public static final String	START						= "START";
	public static final String	CHALLENGE					= "CHAL";
	public static final String	HELP						= "HELP";
	public static final String	SHORT_HELP					= "?";
	public static final String	STOP						= "STOP";
	public static final String	DEBUG						= "DEBUG";

	public static final String	MSG_DELIMITER				= "|";
	public static final String	MSG_SPLITER					= "\\|";								// beware of the RegExp meaning
	public static final String	RES_SPLITTER				= ":";
	public static final String	BOT_SPLITTER				= ",";
	public static final String	INTERNAL_PATH_SPLITTER		= ",";

	// general related
	public static final String	MAIN_PROPERTIES_NAME		= "roborunner.properties";
	public static final String	PROCESS_NAME_KEY			= "roborunner.processname";
	public static final String	PROCESS_PATH_KEY			= "roborunner.processpath";

	public static final String	VERSION_KEY					= "roborunner.version";
	public static final String	ROBOCODE_SOURCE_PATH_KEY	= "roborunner.robocode.source";

	public static final String	ALL_PROCESSES				= "ALL_PROCESSES";
	public static final String	ROBOCODE_DIR_NAME			= "robocodes";
	public static final String	JAR_DIR_NAME				= "libs";
	public static final String	INSTALL_COUNT_KEY			= "roborannuer.robocode.installations";
	public static final String	BOT_SOURCE_KEY				= "roborunner.botsource";
	public static final String	CHALLENGE_NAME_KEY			= "roborunner.challenge";
	public static final String	CHALLENGE_BOT_KEY			= "roborunner.challengebot";
	public static final String	BOTLIST_SEASONS_KEY			= "roborunner.botlist.seasons";
}
