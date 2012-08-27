package robowiki.console;

public class RoboRunnerDefines
{
	// message related
	public static final String	RESULT						= "RESULT";							// proc -> server
	public static final String	SETUP_REQUEST				= "SETUP_REQUEST";						// proc -> server
	public static final String	STOP_COMMAND				= "STOP_OMMAND";						// server -> proc
	public static final String	WORKING_COMMAND				= "WORKING_COMMAND";					// server -> proc
	public static final String	RUN_COMMAND					= "RUN_COMMAND";						// server -> proc
	public static final String	BATTLE_ERROR				= "BATTLE_ERROR";						// proc --> server
	public static final String	STATUS_COMMAND				= "STATUS_COMMAND";					// proc <--> server
	public static final String	INFO						= "INFO";								//proc --> server
	public static final String	WARN						= "WARN";								// proc --> server
	public static final String	ERROR						= "ERROR";								// proc --> server

	// console related
	public static final String	CONFIG						= "CONFIG";
	public static final String	QUIT						= "QUIT";
	public static final String	EXIT						= "EXIT";
	public static final String	RUN							= "RUN";
	public static final String	CHALLENGE					= "CHAL";
	public static final String	HELP						= "HELP";
	public static final String	SHORT_HELP					= "?";
	public static final String	STOP						= "STOP";
	public static final String	DEBUG						= "DEBUG";
	public static final String	AUTORUN						= "AUTO";								// TODO: maybe put debug and auto in a SET command
	public static final String	STATUS						= "STATUS";

	public static final String	MSG_DELIMITER				= "|";
	public static final String	MSG_SPLITER					= "\\|";								// beware of the RegExp meaning
	public static final String	RES_SPLITTER				= ":";
	public static final String	BOT_SPLITTER				= ",";
	public static final String	INTERNAL_PATH_SPLITTER		= ",";

	// general related
	public static final String	MAIN_PROPERTIES_NAME		= "roborunner.properties";
	public static final String	ALL_PROCESSES				= "ALL_PROCESSES";
	public static final String	ROBOCODE_DIR_NAME			= "robocodes";
	public static final String	JAR_DIR_NAME				= "libs";

	// property entries
	public static final String	PROCESS_NAME_KEY			= "roborunner.processname";
	public static final String	PROCESS_PATH_KEY			= "roborunner.processpath";
	public static final String	VERSION_KEY					= "roborunner.version";
	public static final String	ROBOCODE_SOURCE_PATH_KEY	= "roborunner.robocode.source";
	public static final String	INSTALL_COUNT_KEY			= "roborannuer.robocode.installations";
	public static final String	BOT_SOURCE_KEY				= "roborunner.botsource";
	public static final String	CHALLENGE_NAME_KEY			= "roborunner.challenge";
	public static final String	CHALLENGE_BOT_KEY			= "roborunner.challengebot";
	public static final String	BOTLIST_SEASONS_KEY			= "roborunner.botlist.seasons";
	public static final String	DEBUG_KEY					= "roborunner.debug";
	public static final String	AUTORUN_KEY					= "roborunner.autorun";

}
