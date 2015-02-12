package edu.hawaii.jmotif.viztree;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.hawaii.jmotif.discord.SAXSequiturDiscord;
import edu.hawaii.jmotif.gi.GrammarRuleRecord;
import edu.hawaii.jmotif.gi.GrammarRules;
import edu.hawaii.jmotif.gi.sequitur.SequiturFactory;
import edu.hawaii.jmotif.sax.NumerosityReductionStrategy;
import edu.hawaii.jmotif.timeseries.TSException;
import edu.hawaii.jmotif.timeseries.TSUtils;

public class Tinker {

  private static final String fname = "data/ecg0606_1.csv";
  private static final int saxWindowSize = 128;
  private static final int saxPAASize = 3;
  private static final int saxAlphabetSize = 3;
  private static final NumerosityReductionStrategy numerosityReductionStrategy = NumerosityReductionStrategy.EXACT;
  private static final double normalizationThreshold = 0.05;

  // locale, charset, etc
  //
  final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  private static final String CR = "\n";
  private static final String SPACE = " ";

  // logging stuff
  //
  private static Logger consoleLogger;
  private static Level LOGGING_LEVEL = Level.INFO;

  // static block - we instantiate the logger
  //
  static {
    consoleLogger = (Logger) LoggerFactory.getLogger(SAXSequiturDiscord.class);
    consoleLogger.setLevel(LOGGING_LEVEL);
  }

  public static void main(String[] args) throws NumberFormatException, IOException, TSException {

    // get the timeseries
    double[] timeseries = TSUtils.readFileColumn(fname, 0, -1);
    consoleLogger.info("loaded " + timeseries.length + " points from " + fname);

    HashMap<String, Integer> words = new HashMap<String, Integer>();
    // discretize it and get the grammar
    GrammarRules saxData = SequiturFactory.series2SequiturRules(timeseries, saxWindowSize,
        saxPAASize, saxAlphabetSize, numerosityReductionStrategy, normalizationThreshold);
    consoleLogger.info("inferred " + saxData.size() + " grammar rules:");
    StringBuffer sb = new StringBuffer();
    sb.append("inferred ").append(saxData.size()).append(" grammar rules:").append(CR);
    for (GrammarRuleRecord r : saxData) {
      sb.append(r.getRuleName()).append(": ").append(r.getRuleString()).append("-> ")
          .append(r.getExpandedRuleString()).append(CR);
      if (0 != r.getRuleNumber()) {
        words.put(r.getExpandedRuleString(), r.getOccurrences().size());
      }
    }
    consoleLogger.info(sb.toString());

    // words not in rules
    GrammarRuleRecord r0 = saxData.get(0);
    String[] split = r0.getRuleString().trim().split("\\s");
    for (String s : split) {
      if (s.startsWith("R")) {
        continue;
      }
      Integer w = words.get(s);
      if (null == w) {
        words.put(s, 1);
      }
      else {
        words.put(s, w + 1);
      }
    }

    // build the tree
    Tree tree = new Tree();
    for (Entry<String, Integer> w : words.entrySet()) {
      // tree.put(w);
      tree.put(w.getKey(), w.getValue());
    }

    tree.assignLevels();

    tree.printBreadthFirst();

    tree.draw();

  }
}
