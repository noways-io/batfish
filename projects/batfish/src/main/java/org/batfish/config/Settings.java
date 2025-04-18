package org.batfish.config;

import static org.batfish.storage.FileBasedStorage.getWorkLogPath;

import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.batfish.common.BaseSettings;
import org.batfish.common.BatfishLogger;
import org.batfish.common.BfConsts;
import org.batfish.grammar.GrammarSettings;
import org.batfish.identifiers.NetworkId;
import org.batfish.identifiers.QuestionId;
import org.batfish.identifiers.SnapshotId;
import org.batfish.main.Driver.RunMode;
import org.batfish.version.Versioned;

public final class Settings extends BaseSettings implements GrammarSettings {

  public static final String ARG_CHECK_BGP_REACHABILITY = "checkbgpsessionreachability";

  private static final String ARG_DATAPLANE_ENGINE_NAME = "dataplaneengine";

  private static final String ARG_DEBUG_FLAGS = "debugflags";

  private static final String ARG_PARSE_REUSE = "parsereuse";

  private static final String ARG_EXIT_ON_FIRST_ERROR = "ee";

  private static final String ARG_FLATTEN = "flatten";

  private static final String ARG_FLATTEN_DESTINATION = "flattendst";

  private static final String ARG_HELP = "help";

  private static final String ARG_HISTOGRAM = "histogram";

  private static final String ARG_IGNORE_UNKNOWN = "ignoreunknown";

  private static final String ARG_IGNORE_UNSUPPORTED = "ignoreunsupported";

  private static final String ARG_JOBS = "jobs";

  private static final String ARG_MAX_PARSER_CONTEXT_LINES = "maxparsercontextlines";

  private static final String ARG_MAX_PARSER_CONTEXT_TOKENS = "maxparsercontexttokens";

  private static final String ARG_MAX_PARSE_TREE_PRINT_LENGTH = "maxparsetreeprintlength";

  private static final String ARG_MAX_RUNTIME_MS = "maxruntime";

  private static final String ARG_NO_SHUFFLE = "noshuffle";

  private static final String ARG_PRECOMPUTE_AUTOCOMPLETE = "precompute-autocomplete";

  private static final String ARG_PRINT_PARSE_TREES = "ppt";

  private static final String ARG_PRINT_PARSE_TREE_LINE_NUMS = "printparsetreelinenums";

  public static final String ARG_RUN_MODE = "runmode";

  private static final String ARG_SEQUENTIAL = "sequential";

  private static final String ARG_THROW_ON_LEXER_ERROR = "throwlexer";

  private static final String ARG_THROW_ON_PARSER_ERROR = "throwparser";

  private static final String ARG_TIMESTAMP = "timestamp";

  private static final String ARG_VERSION = "version";

  private static final String ARGNAME_LOG_LEVEL = "level-name";

  private static final String ARGNAME_NAME = "name";

  private static final String ARGNAME_NUMBER = "number";

  private static final String ARGNAME_PATH = "path";

  private static final String ARGNAME_STRINGS = "string..";

  private static final String EXECUTABLE_NAME = "batfish";

  private static final String CAN_EXECUTE = "canexecute";

  private static final String DIFFERENTIAL_QUESTION = "diffquestion";

  public static final String TASK_ID = "taskid";

  private BatfishLogger _logger;

  public Settings() {
    this(new String[] {});
  }

  public Settings(String[] args) {
    super(
        getConfig(
            BfConsts.PROP_BATFISH_PROPERTIES_PATH,
            BfConsts.ABSPATH_CONFIG_FILE_NAME_BATFISH,
            ConfigurationLocator.class));
    initConfigDefaults();
    initOptions();
    parseCommandLine(args);
  }

  /**
   * Create a copy of some existing settings.
   *
   * @param other the {@link Settings to copy}
   */
  public Settings(Settings other) {
    super(other._config);
    _logger = other._logger;
    initOptions();
  }

  /**
   * Remove certain setting values
   *
   * @param keys a list of keys to clear
   */
  public void clearValues(String... keys) {
    for (String s : keys) {
      _config.clearProperty(s);
    }
  }

  public boolean canExecute() {
    return _config.getBoolean(CAN_EXECUTE);
  }

  public boolean debugFlagEnabled(String flag) {
    return getDebugFlags().contains(flag);
  }

  public boolean getAlwaysIncludeAnswerInWorkJsonLog() {
    return _config.getBoolean(BfConsts.ARG_ALWAYS_INCLUDE_ANSWER_IN_WORK_JSON_LOG);
  }

  public boolean getAnswer() {
    return _config.getBoolean(BfConsts.COMMAND_ANSWER);
  }

  public int getAvailableThreads() {
    return Math.min(Runtime.getRuntime().availableProcessors(), getJobs());
  }

  public NetworkId getContainer() {
    String id = _config.getString(BfConsts.ARG_CONTAINER);
    return id != null ? new NetworkId(id) : null;
  }

  public boolean getDataPlane() {
    return _config.getBoolean(BfConsts.COMMAND_DUMP_DP);
  }

  public List<String> getDebugFlags() {
    return Arrays.asList(_config.getStringArray(ARG_DEBUG_FLAGS));
  }

  public SnapshotId getDeltaTestrig() {
    String name = _config.getString(BfConsts.ARG_DELTA_TESTRIG);
    return name != null ? new SnapshotId(name) : null;
  }

  public boolean getDifferential() {
    return _config.getBoolean(BfConsts.ARG_DIFFERENTIAL);
  }

  public boolean getDiffQuestion() {
    return _config.getBoolean(DIFFERENTIAL_QUESTION);
  }

  @Override
  public boolean getDisableUnrecognized() {
    return _config.getBoolean(BfConsts.ARG_DISABLE_UNRECOGNIZED);
  }

  public boolean getExitOnFirstError() {
    return _config.getBoolean(ARG_EXIT_ON_FIRST_ERROR);
  }

  public boolean getFlatten() {
    return _config.getBoolean(ARG_FLATTEN);
  }

  public Path getFlattenDestination() {
    return Paths.get(_config.getString(ARG_FLATTEN_DESTINATION));
  }

  public boolean getHaltOnConvertError() {
    return _config.getBoolean(BfConsts.ARG_HALT_ON_CONVERT_ERROR);
  }

  public boolean getHaltOnParseError() {
    return _config.getBoolean(BfConsts.ARG_HALT_ON_PARSE_ERROR);
  }

  public boolean getHistogram() {
    return _config.getBoolean(ARG_HISTOGRAM);
  }

  public boolean getInitInfo() {
    return _config.getBoolean(BfConsts.COMMAND_INIT_INFO);
  }

  public int getJobs() {
    return _config.getInt(ARG_JOBS);
  }

  public @Nullable String getLogFile() {
    if (getTaskId() == null) {
      return null;
    }
    SnapshotId tr = getTestrig();
    if (getDeltaTestrig() != null && !getDifferential()) {
      tr = getDeltaTestrig();
    }
    return getWorkLogPath(getStorageBase(), getContainer(), tr, getTaskId()).toString();
  }

  public BatfishLogger getLogger() {
    return _logger;
  }

  public String getLogLevel() {
    return _config.getString(BfConsts.ARG_LOG_LEVEL);
  }

  public boolean getParseReuse() {
    return _config.getBoolean(ARG_PARSE_REUSE);
  }

  @Override
  public int getMaxParserContextLines() {
    return _config.getInt(ARG_MAX_PARSER_CONTEXT_LINES);
  }

  @Override
  public int getMaxParserContextTokens() {
    return _config.getInt(ARG_MAX_PARSER_CONTEXT_TOKENS);
  }

  @Override
  public int getMaxParseTreePrintLength() {
    return _config.getInt(ARG_MAX_PARSE_TREE_PRINT_LENGTH);
  }

  public int getMaxRuntimeMs() {
    return _config.getInt(ARG_MAX_RUNTIME_MS);
  }

  public boolean getPrecomputeAutocomplete() {
    return _config.getBoolean(ARG_PRECOMPUTE_AUTOCOMPLETE);
  }

  @Override
  public boolean getPrintParseTree() {
    return _config.getBoolean(ARG_PRINT_PARSE_TREES);
  }

  @Override
  public boolean getPrintParseTreeLineNums() {
    return _config.getBoolean(ARG_PRINT_PARSE_TREE_LINE_NUMS);
  }

  public @Nullable QuestionId getQuestionName() {
    String name = _config.getString(BfConsts.ARG_QUESTION_NAME);
    return name != null ? new QuestionId(name) : null;
  }

  public RunMode getRunMode() {
    return RunMode.valueOf(_config.getString(ARG_RUN_MODE).toUpperCase());
  }

  public boolean getSequential() {
    return _config.getBoolean(ARG_SEQUENTIAL);
  }

  public boolean getSerializeIndependent() {
    return _config.getBoolean(BfConsts.COMMAND_PARSE_VENDOR_INDEPENDENT);
  }

  public boolean getSerializeVendor() {
    return _config.getBoolean(BfConsts.COMMAND_PARSE_VENDOR_SPECIFIC);
  }

  public boolean getShuffleJobs() {
    return !_config.getBoolean(ARG_NO_SHUFFLE);
  }

  public String getSnapshotName() {
    return _config.getString(BfConsts.ARG_SNAPSHOT_NAME);
  }

  public @Nullable Path getStorageBase() {
    String storageBase = _config.getString(BfConsts.ARG_STORAGE_BASE);
    if (storageBase == null) {
      return null;
    }
    return Paths.get(storageBase);
  }

  public @Nullable String getTaskId() {
    return _config.getString(TASK_ID);
  }

  public String getTaskPlugin() {
    return _config.getString(BfConsts.ARG_TASK_PLUGIN);
  }

  public SnapshotId getTestrig() {
    String name = _config.getString(BfConsts.ARG_TESTRIG);
    return name != null ? new SnapshotId(name) : null;
  }

  @Override
  public boolean getThrowOnLexerError() {
    return _config.getBoolean(ARG_THROW_ON_LEXER_ERROR);
  }

  @Override
  public boolean getThrowOnParserError() {
    return _config.getBoolean(ARG_THROW_ON_PARSER_ERROR);
  }

  public boolean getTimestamp() {
    return _config.getBoolean(ARG_TIMESTAMP);
  }

  public boolean getVerboseParse() {
    return _config.getBoolean(BfConsts.ARG_VERBOSE_PARSE);
  }

  public List<String> ignoreFilesWithStrings() {
    List<String> l = _config.getList(String.class, BfConsts.ARG_IGNORE_FILES_WITH_STRINGS);
    return l == null ? ImmutableList.of() : l;
  }

  public boolean ignoreManagementInterfaces() {
    return _config.getBoolean(BfConsts.ARG_IGNORE_MANAGEMENT_INTERFACES);
  }

  public boolean ignoreUnknown() {
    return _config.getBoolean(ARG_IGNORE_UNKNOWN);
  }

  public boolean ignoreUnsupported() {
    return _config.getBoolean(ARG_IGNORE_UNSUPPORTED);
  }

  public String getDataPlaneEngineName() {
    return _config.getString(ARG_DATAPLANE_ENGINE_NAME);
  }

  private void initConfigDefaults() {
    setDefaultProperty(BfConsts.ARG_ALWAYS_INCLUDE_ANSWER_IN_WORK_JSON_LOG, false);
    setDefaultProperty(BfConsts.ARG_BDP_DETAIL, false);
    setDefaultProperty(BfConsts.ARG_BDP_MAX_OSCILLATION_RECOVERY_ATTEMPTS, 0);
    setDefaultProperty(BfConsts.ARG_BDP_MAX_RECORDED_ITERATIONS, 5);
    setDefaultProperty(BfConsts.ARG_BDP_PRINT_ALL_ITERATIONS, false);
    setDefaultProperty(BfConsts.ARG_BDP_PRINT_OSCILLATING_ITERATIONS, false);
    setDefaultProperty(BfConsts.ARG_BDP_RECORD_ALL_ITERATIONS, false);
    setDefaultProperty(CAN_EXECUTE, true);
    setDefaultProperty(BfConsts.ARG_CONTAINER, null);
    setDefaultProperty(ARG_DEBUG_FLAGS, ImmutableList.of());
    setDefaultProperty(DIFFERENTIAL_QUESTION, false);
    setDefaultProperty(ARG_DEBUG_FLAGS, ImmutableList.of());
    setDefaultProperty(BfConsts.ARG_DIFFERENTIAL, false);
    setDefaultProperty(BfConsts.ARG_DISABLE_UNRECOGNIZED, false);
    setDefaultProperty(ARG_EXIT_ON_FIRST_ERROR, false);
    setDefaultProperty(ARG_FLATTEN, false);
    setDefaultProperty(ARG_FLATTEN_DESTINATION, null);
    setDefaultProperty(BfConsts.ARG_HALT_ON_CONVERT_ERROR, false);
    setDefaultProperty(BfConsts.ARG_HALT_ON_PARSE_ERROR, false);
    setDefaultProperty(ARG_HELP, false);
    setDefaultProperty(ARG_HISTOGRAM, false);
    setDefaultProperty(BfConsts.ARG_IGNORE_FILES_WITH_STRINGS, ImmutableList.of());
    setDefaultProperty(BfConsts.ARG_IGNORE_MANAGEMENT_INTERFACES, true);
    setDefaultProperty(ARG_IGNORE_UNSUPPORTED, true);
    setDefaultProperty(ARG_IGNORE_UNKNOWN, true);
    setDefaultProperty(ARG_JOBS, Integer.MAX_VALUE);
    setDefaultProperty(BfConsts.ARG_LOG_LEVEL, "debug");
    setDefaultProperty(ARG_MAX_PARSER_CONTEXT_LINES, 10);
    setDefaultProperty(ARG_MAX_PARSER_CONTEXT_TOKENS, 10);
    setDefaultProperty(ARG_MAX_PARSE_TREE_PRINT_LENGTH, 0);
    setDefaultProperty(ARG_MAX_RUNTIME_MS, 0);
    setDefaultProperty(ARG_CHECK_BGP_REACHABILITY, true);
    setDefaultProperty(ARG_NO_SHUFFLE, false);
    setDefaultProperty(ARG_PARSE_REUSE, false);
    setDefaultProperty(ARG_PRECOMPUTE_AUTOCOMPLETE, true);
    setDefaultProperty(ARG_PRINT_PARSE_TREES, false);
    setDefaultProperty(ARG_PRINT_PARSE_TREE_LINE_NUMS, false);
    setDefaultProperty(BfConsts.ARG_QUESTION_NAME, null);
    setDefaultProperty(ARG_RUN_MODE, RunMode.WORKER.toString());
    setDefaultProperty(ARG_SEQUENTIAL, false);
    setDefaultProperty(BfConsts.ARG_SNAPSHOT_NAME, null);
    setDefaultProperty(BfConsts.ARG_STORAGE_BASE, null);
    setDefaultProperty(BfConsts.ARG_TASK_PLUGIN, null);
    setDefaultProperty(ARG_THROW_ON_LEXER_ERROR, true);
    setDefaultProperty(ARG_THROW_ON_PARSER_ERROR, true);
    setDefaultProperty(ARG_TIMESTAMP, false);
    setDefaultProperty(BfConsts.ARG_VERBOSE_PARSE, false);
    setDefaultProperty(ARG_VERSION, false);
    setDefaultProperty(BfConsts.COMMAND_ANSWER, false);
    setDefaultProperty(BfConsts.COMMAND_DUMP_DP, false);
    setDefaultProperty(BfConsts.COMMAND_INIT_INFO, false);
    setDefaultProperty(BfConsts.COMMAND_PARSE_VENDOR_INDEPENDENT, false);
    setDefaultProperty(BfConsts.COMMAND_PARSE_VENDOR_SPECIFIC, false);
    setDefaultProperty(ARG_DATAPLANE_ENGINE_NAME, "ibdp");
  }

  private void initOptions() {

    addBooleanOption(
        BfConsts.ARG_ALWAYS_INCLUDE_ANSWER_IN_WORK_JSON_LOG,
        "always include answer to question in work json log");

    addBooleanOption(
        BfConsts.ARG_BDP_DETAIL,
        "Set to true to print/record detailed protocol-specific information about routes in each"
            + "iteration rather than only protocol-independent information.");

    addOption(
        BfConsts.ARG_BDP_MAX_OSCILLATION_RECOVERY_ATTEMPTS,
        "Max number of recovery attempts when oscillation occurs during data plane computations",
        ARGNAME_NUMBER);

    addOption(
        BfConsts.ARG_BDP_MAX_RECORDED_ITERATIONS,
        "Max number of iterations to record when debugging BDP. To avoid extra fixed-point"
            + "computation when oscillations occur, set this at least as high as the length of the"
            + "cycle.",
        ARGNAME_NUMBER);

    addBooleanOption(
        BfConsts.ARG_BDP_PRINT_ALL_ITERATIONS,
        "Set to true to print all iterations when oscillation occurs. Make sure to either set max"
            + "recorded iterations to minimum necessary value, or simply record all iterations");

    addBooleanOption(
        BfConsts.ARG_BDP_PRINT_OSCILLATING_ITERATIONS,
        "Set to true to print only oscillating iterations when oscillation occurs. Make sure to"
            + "set max recorded iterations to minimum necessary value.");

    addBooleanOption(
        BfConsts.ARG_BDP_RECORD_ALL_ITERATIONS,
        "Set to true to record all iterations, including during oscillation. Ignores max recorded "
            + "iterations value.");

    addBooleanOption(
        ARG_CHECK_BGP_REACHABILITY,
        "whether to check BGP session reachability during data plane computation");

    addOption(BfConsts.ARG_CONTAINER, "ID of network", ARGNAME_NAME);

    addListOption(ARG_DEBUG_FLAGS, "a list of flags to enable debugging code", "debug flags");

    addOption(BfConsts.ARG_DELTA_TESTRIG, "name of delta testrig", ARGNAME_NAME);

    addBooleanOption(
        BfConsts.ARG_DIFFERENTIAL,
        "force treatment of question as differential (to be used when not answering question)");

    addBooleanOption(
        BfConsts.ARG_DISABLE_UNRECOGNIZED, "disable parser recognition of unrecognized stanzas");

    addBooleanOption(
        ARG_EXIT_ON_FIRST_ERROR,
        "exit on first parse error (otherwise will exit on last parse error)");

    addBooleanOption(ARG_FLATTEN, "flatten hierarchical juniper configuration files");

    addOption(
        ARG_FLATTEN_DESTINATION,
        "output path to test rig in which flat juniper (and all other) configurations will be "
            + "placed",
        ARGNAME_PATH);

    addBooleanOption(
        BfConsts.COMMAND_INIT_INFO, "include parse/convert initialization info in answer");

    addBooleanOption(
        BfConsts.ARG_HALT_ON_CONVERT_ERROR,
        "Halt on conversion error instead of proceeding with successfully converted configs");

    addBooleanOption(
        BfConsts.ARG_HALT_ON_PARSE_ERROR,
        "Halt on parse error instead of proceeding with successfully parsed configs");

    addBooleanOption(ARG_HELP, "print this message");

    addOption(
        BfConsts.ARG_IGNORE_FILES_WITH_STRINGS,
        "ignore configuration files containing these strings",
        ARGNAME_STRINGS);

    addBooleanOption(
        BfConsts.ARG_IGNORE_MANAGEMENT_INTERFACES,
        "infer and ignore interfaces that are part of the management network");

    addBooleanOption(
        ARG_IGNORE_UNKNOWN, "ignore configuration files with unknown format instead of crashing");

    addBooleanOption(
        ARG_IGNORE_UNSUPPORTED,
        "ignore configuration files with unsupported format instead of crashing");

    addOption(ARG_JOBS, "number of threads used by parallel jobs executor", ARGNAME_NUMBER);

    addOption(BfConsts.ARG_LOG_LEVEL, "log level", ARGNAME_LOG_LEVEL);

    addBooleanOption(ARG_HISTOGRAM, "build histogram of unimplemented features");

    addOption(
        ARG_MAX_PARSER_CONTEXT_LINES,
        "max number of surrounding lines to print on parser error",
        ARGNAME_NUMBER);

    addOption(
        ARG_MAX_PARSER_CONTEXT_TOKENS,
        "max number of context tokens to print on parser error",
        ARGNAME_NUMBER);

    addOption(
        ARG_MAX_PARSE_TREE_PRINT_LENGTH,
        "max number of characters to print for parsetree pretty print "
            + "(<= 0 is treated as no limit)",
        ARGNAME_NUMBER);

    addOption(ARG_MAX_RUNTIME_MS, "maximum time (in ms) to allow a task to run", ARGNAME_NUMBER);

    addBooleanOption(ARG_NO_SHUFFLE, "do not shuffle parallel jobs");

    addBooleanOption(ARG_PARSE_REUSE, "reuse parse results when appropriate");

    addBooleanOption(ARG_PRECOMPUTE_AUTOCOMPLETE, "pre-compute autocomplete results");

    addBooleanOption(ARG_PRINT_PARSE_TREES, "print parse trees");

    addBooleanOption(
        ARG_PRINT_PARSE_TREE_LINE_NUMS, "print line numbers when printing parse trees");

    addOption(BfConsts.ARG_QUESTION_NAME, "name of question", ARGNAME_NAME);

    addOption(
        ARG_RUN_MODE,
        "mode to run in",
        Arrays.stream(RunMode.values()).map(Object::toString).collect(Collectors.joining("|")));

    addBooleanOption(ARG_SEQUENTIAL, "force sequential operation");

    addOption(BfConsts.ARG_SNAPSHOT_NAME, "name of snapshot", ARGNAME_NAME);

    addOption(BfConsts.ARG_STORAGE_BASE, "path to the storage base", ARGNAME_PATH);

    addBooleanOption(
        BfConsts.ARG_SYNTHESIZE_TOPOLOGY,
        "synthesize topology from interface ip subnet information");

    addOption(BfConsts.ARG_TASK_PLUGIN, "fully-qualified name of task plugin class", ARGNAME_NAME);

    addOption(BfConsts.ARG_TESTRIG, "ID of snapshot", ARGNAME_NAME);

    addBooleanOption(ARG_THROW_ON_LEXER_ERROR, "throw exception immediately on lexer error");

    addBooleanOption(ARG_THROW_ON_PARSER_ERROR, "throw exception immediately on parser error");

    addBooleanOption(ARG_TIMESTAMP, "print timestamps in log messages");

    addBooleanOption(
        BfConsts.ARG_VERBOSE_PARSE,
        "(developer option) include parse/convert data in init-testrig answer");

    addBooleanOption(BfConsts.COMMAND_ANSWER, "answer provided question");

    addBooleanOption(BfConsts.COMMAND_DUMP_DP, "compute and serialize data plane");

    addBooleanOption(
        BfConsts.COMMAND_PARSE_VENDOR_INDEPENDENT, "serialize vendor-independent configs");

    addBooleanOption(BfConsts.COMMAND_PARSE_VENDOR_SPECIFIC, "serialize vendor configs");

    addBooleanOption(ARG_VERSION, "print the version number of the code and exit");

    addOption(
        ARG_DATAPLANE_ENGINE_NAME,
        "name of the dataplane generation engine to use.",
        "dataplane engine name");

    // deprecated and ignored
    for (String deprecatedStringArg :
        new String[] {
          "analysisname",
          "analyze",
          "coordinatorhost",
          "coordinatorpoolport",
          "deltaenv",
          "diffactive",
          "enable_cisco_nx_parser",
          "env",
          "flattenonthefly",
          "gsidregex",
          "gsinputrole",
          "gsremoteas",
          "logtee",
          "nosimplify",
          "outputenv",
          "parentpid",
          "pedanticsuppress",
          "ppa",
          "redflagsuppress",
          "servicebindhost",
          "servicehost",
          "servicename",
          "serviceport",
          "ssldisable",
          "sslkeystorefile",
          "sslkeystorepassword",
          "ssltrustallcerts",
          "ssltruststorefile",
          "ssltruststorepassword",
          "stext",
          "tracingagenthost",
          "tracingagentport",
          "unimplementedsuppress",
          "venv",
          "z3timeout",
        }) {
      addOption(deprecatedStringArg, DEPRECATED_ARG_DESC, "ignored");
    }
    for (String deprecatedBooleanArg :
        new String[] {
          "gs", "register", "tracingenable",
        }) {
      addBooleanOption(deprecatedBooleanArg, DEPRECATED_ARG_DESC);
    }
  }

  public void parseCommandLine(String[] args) {
    initCommandLine(args);
    _config.setProperty(CAN_EXECUTE, true);

    // SPECIAL OPTIONS
    getStringOptionValue(BfConsts.ARG_LOG_LEVEL);
    if (getBooleanOptionValue(ARG_HELP)) {
      _config.setProperty(CAN_EXECUTE, false);
      printHelp(EXECUTABLE_NAME);
      return;
    }

    if (getBooleanOptionValue(ARG_VERSION)) {
      _config.setProperty(CAN_EXECUTE, false);
      System.out.print(Versioned.getVersionsString());
      return;
    }

    // REGULAR OPTIONS
    getBooleanOptionValue(BfConsts.ARG_ALWAYS_INCLUDE_ANSWER_IN_WORK_JSON_LOG);
    getBooleanOptionValue(BfConsts.COMMAND_ANSWER);
    getBooleanOptionValue(BfConsts.ARG_BDP_RECORD_ALL_ITERATIONS);
    getBooleanOptionValue(BfConsts.ARG_BDP_DETAIL);
    getIntOptionValue(BfConsts.ARG_BDP_MAX_OSCILLATION_RECOVERY_ATTEMPTS);
    getIntOptionValue(BfConsts.ARG_BDP_MAX_RECORDED_ITERATIONS);
    getBooleanOptionValue(BfConsts.ARG_BDP_PRINT_ALL_ITERATIONS);
    getBooleanOptionValue(BfConsts.ARG_BDP_PRINT_OSCILLATING_ITERATIONS);
    getBooleanOptionValue(ARG_CHECK_BGP_REACHABILITY);
    getStringOptionValue(BfConsts.ARG_CONTAINER);
    getBooleanOptionValue(BfConsts.COMMAND_DUMP_DP);
    getStringListOptionValue(ARG_DEBUG_FLAGS);
    getStringOptionValue(BfConsts.ARG_DELTA_TESTRIG);
    getBooleanOptionValue(BfConsts.ARG_DIFFERENTIAL);
    getBooleanOptionValue(BfConsts.ARG_DISABLE_UNRECOGNIZED);
    getBooleanOptionValue(ARG_EXIT_ON_FIRST_ERROR);
    getBooleanOptionValue(ARG_FLATTEN);
    getPathOptionValue(ARG_FLATTEN_DESTINATION);
    getBooleanOptionValue(BfConsts.ARG_HALT_ON_CONVERT_ERROR);
    getBooleanOptionValue(BfConsts.ARG_HALT_ON_PARSE_ERROR);
    getBooleanOptionValue(ARG_HISTOGRAM);
    getStringListOptionValue(BfConsts.ARG_IGNORE_FILES_WITH_STRINGS);
    getBooleanOptionValue(BfConsts.ARG_IGNORE_MANAGEMENT_INTERFACES);
    getBooleanOptionValue(ARG_IGNORE_UNKNOWN);
    getBooleanOptionValue(ARG_IGNORE_UNSUPPORTED);
    getBooleanOptionValue(BfConsts.COMMAND_INIT_INFO);
    getIntOptionValue(ARG_JOBS);
    getIntOptionValue(ARG_MAX_PARSER_CONTEXT_LINES);
    getIntOptionValue(ARG_MAX_PARSER_CONTEXT_TOKENS);
    getIntOptionValue(ARG_MAX_PARSE_TREE_PRINT_LENGTH);
    getIntOptionValue(ARG_MAX_RUNTIME_MS);
    getBooleanOptionValue(ARG_PRINT_PARSE_TREES);
    getBooleanOptionValue(ARG_PRINT_PARSE_TREE_LINE_NUMS);
    getStringOptionValue(BfConsts.ARG_QUESTION_NAME);
    getStringOptionValue(ARG_RUN_MODE);
    getBooleanOptionValue(ARG_SEQUENTIAL);
    getBooleanOptionValue(BfConsts.COMMAND_PARSE_VENDOR_INDEPENDENT);
    getBooleanOptionValue(BfConsts.COMMAND_PARSE_VENDOR_SPECIFIC);
    getBooleanOptionValue(ARG_NO_SHUFFLE);
    getBooleanOptionValue(ARG_PARSE_REUSE);
    getStringOptionValue(BfConsts.ARG_SNAPSHOT_NAME);
    getPathOptionValue(BfConsts.ARG_STORAGE_BASE);
    getStringOptionValue(BfConsts.ARG_TASK_PLUGIN);
    getStringOptionValue(BfConsts.ARG_TESTRIG);
    getBooleanOptionValue(ARG_THROW_ON_LEXER_ERROR);
    getBooleanOptionValue(ARG_THROW_ON_PARSER_ERROR);
    getBooleanOptionValue(ARG_TIMESTAMP);
    getBooleanOptionValue(BfConsts.ARG_VERBOSE_PARSE);
    getStringOptionValue(ARG_DATAPLANE_ENGINE_NAME);
  }

  public void setCanExecute(boolean canExecute) {
    _config.setProperty(CAN_EXECUTE, canExecute);
  }

  public void setContainer(String container) {
    _config.setProperty(BfConsts.ARG_CONTAINER, container);
  }

  public void setDebugFlags(List<String> debugFlags) {
    _config.setProperty(ARG_DEBUG_FLAGS, debugFlags);
  }

  public void setDeltaTestrig(SnapshotId testrig) {
    _config.setProperty(BfConsts.ARG_DELTA_TESTRIG, testrig != null ? testrig.getId() : null);
  }

  public void setDiffQuestion(boolean diffQuestion) {
    _config.setProperty(DIFFERENTIAL_QUESTION, diffQuestion);
  }

  @Override
  public void setDisableUnrecognized(boolean b) {
    _config.setProperty(BfConsts.ARG_DISABLE_UNRECOGNIZED, b);
  }

  public void setHaltOnConvertError(boolean haltOnConvertError) {
    _config.setProperty(BfConsts.ARG_HALT_ON_CONVERT_ERROR, haltOnConvertError);
  }

  public void setHaltOnParseError(boolean haltOnParseError) {
    _config.setProperty(BfConsts.ARG_HALT_ON_PARSE_ERROR, haltOnParseError);
  }

  public void setIgnoreFilesWithStrings(@Nonnull List<String> ignored) {
    _config.setProperty(BfConsts.ARG_IGNORE_FILES_WITH_STRINGS, ignored);
  }

  public void setInitInfo(boolean initInfo) {
    _config.setProperty(BfConsts.COMMAND_INIT_INFO, initInfo);
  }

  public void setLogger(BatfishLogger logger) {
    _logger = logger;
  }

  public void setMaxParserContextLines(int maxParserContextLines) {
    _config.setProperty(ARG_MAX_PARSER_CONTEXT_LINES, maxParserContextLines);
  }

  public void setMaxParserContextTokens(int maxParserContextTokens) {
    _config.setProperty(ARG_MAX_PARSER_CONTEXT_TOKENS, maxParserContextTokens);
  }

  public void setMaxParseTreePrintLength(int maxParseTreePrintLength) {
    _config.setProperty(ARG_MAX_PARSE_TREE_PRINT_LENGTH, maxParseTreePrintLength);
  }

  public void setMaxRuntimeMs(int runtimeMs) {
    _config.setProperty(ARG_MAX_RUNTIME_MS, runtimeMs);
  }

  @Override
  public void setPrintParseTree(boolean printParseTree) {
    _config.setProperty(ARG_PRINT_PARSE_TREES, printParseTree);
  }

  @Override
  public void setPrintParseTreeLineNums(boolean printParseTreeLineNums) {
    _config.setProperty(ARG_PRINT_PARSE_TREE_LINE_NUMS, printParseTreeLineNums);
  }

  public void setRunMode(RunMode runMode) {
    _config.setProperty(ARG_RUN_MODE, runMode.toString());
  }

  public void setSequential(boolean sequential) {
    _config.setProperty(ARG_SEQUENTIAL, sequential);
  }

  public void setStorageBase(Path storageBase) {
    _config.setProperty(BfConsts.ARG_STORAGE_BASE, storageBase.toString());
  }

  public void setTaskId(String taskId) {
    _config.setProperty(TASK_ID, taskId);
  }

  public void setTestrig(String testrig) {
    _config.setProperty(BfConsts.ARG_TESTRIG, testrig);
  }

  @Override
  public void setThrowOnLexerError(boolean throwOnLexerError) {
    _config.setProperty(ARG_THROW_ON_LEXER_ERROR, throwOnLexerError);
  }

  @Override
  public void setThrowOnParserError(boolean throwOnParserError) {
    _config.setProperty(ARG_THROW_ON_PARSER_ERROR, throwOnParserError);
  }

  public void setVerboseParse(boolean verboseParse) {
    _config.setProperty(BfConsts.ARG_VERBOSE_PARSE, verboseParse);
  }

  public void setDataplaneEngineName(String name) {
    _config.setProperty(ARG_DATAPLANE_ENGINE_NAME, name);
  }

  public void setQuestionName(QuestionId questionName) {
    _config.setProperty(
        BfConsts.ARG_QUESTION_NAME, questionName != null ? questionName.getId() : null);
  }

  public void setSnapshotName(String snapshotName) {
    _config.setProperty(BfConsts.ARG_SNAPSHOT_NAME, snapshotName);
  }
}
