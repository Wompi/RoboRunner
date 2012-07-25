package robowiki.runner;

import static robowiki.runner.RunnerUtil.getCombinedArgs;
import static robowiki.runner.RunnerUtil.parseArgument;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotResults;
import robowiki.runner.BattleRunner.BotSet;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BattleProcess {
  private static final Joiner COMMA_JOINER = Joiner.on(",");
  private static final Joiner COLON_JOINER = Joiner.on(":::");

  private BattlefieldSpecification _battlefield;
  private int _numRounds;
  private RobocodeEngine _engine;
  private BattleListener _listener;

  public static void main(String[] args) {
    args = getCombinedArgs(args);
    String robocodePath =
        Preconditions.checkNotNull(parseArgument("path", args),
            "Pass a path to Robocode with -path");
    int numRounds = Integer.parseInt(
        Preconditions.checkNotNull(parseArgument("rounds", args),
            "Pass number of rounds width with -rounds"));
    int width = Integer.parseInt(
        Preconditions.checkNotNull(parseArgument("width", args),
            "Pass battlefield width with -width"));
    int height = Integer.parseInt(
        Preconditions.checkNotNull(parseArgument("height", args),
            "Pass battlefield height with -height"));
    BattleProcess process =
        new BattleProcess(robocodePath, numRounds, width, height);
    while (true) {
      BufferedReader stdin = new BufferedReader(new java.io.InputStreamReader(System.in));
      try {
        String line = stdin.readLine();
        String result = process.runBattle(getBotSet(line));
        System.out.println("BATTLE RESULT: " + result);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static BotSet getBotSet(String line) {
    return new BotSet(Lists.newArrayList(line.split(",")));
  }

  public BattleProcess(String robocodePath, int numRounds,
      int battleFieldWidth, int battleFieldHeight) {
    _numRounds = numRounds;
    _battlefield =
        new BattlefieldSpecification(battleFieldWidth, battleFieldHeight);
    _engine = new RobocodeEngine(new File(robocodePath));
    _listener = new BattleListener();
    _engine.addBattleListener(_listener);
    _engine.setVisible(false);
  }

  public String runBattle(BotSet botSet) {
    BattleSpecification battleSpec = new BattleSpecification(
        _numRounds, _battlefield, 
    _engine.getLocalRepository(COMMA_JOINER.join(botSet.getBotNames())));
    _engine.runBattle(battleSpec, true);
    Map<String, RobotResults> resultsMap =
        Maps.newHashMap(_listener.getRobotResultsMap());
    _listener.clear();
    return battleResultString(resultsMap);
  }

  private String battleResultString(Map<String, RobotResults> resultsMap) {
    Set<String> resultStrings = Sets.newHashSet();
    for (Map.Entry<String, RobotResults> resultsEntry : resultsMap.entrySet()) {
      resultStrings.add(resultsEntry.getKey() + "::"
          + resultsEntry.getValue().getScore());
    }
    return COLON_JOINER.join(resultStrings);
  }
}
