package org.jumbune.common.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Classpath;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.common.beans.DataQualityTimeLineConfig;
import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.DoNotInstrument;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.Feature;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.Validation;
import org.jumbune.common.beans.XmlElementBean;
import org.jumbune.common.beans.cluster.LogSummaryLocation;
import org.jumbune.common.beans.dsc.DataSourceCompValidationInfo;
import org.jumbune.common.beans.profiling.ProfilingParam;
import org.jumbune.common.utils.ClasspathUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.JobUtil;
import org.jumbune.utils.beans.LogLevel;


/**
 * This class is the bean for the json file.
 */
public class JobConfig implements Config {

	/** The Constant logger. */
	private static final Logger LOGGER = LogManager.getLogger(JobConfig.class);
	
	/** The operating cluster. */
	private String operatingCluster;
	
	/** The additional parameters. */
	private String parameters;

	/**
	 * This is the user who will submit job on Hadoop and submitted job will have access to the data folder with this user.
	 */
	private String operatingUser;
	
	/** The Constant DEFAULT_PARTITION_SAMPLE_INTERVAL. */
	private static final int DEFAULT_PARTITION_SAMPLE_INTERVAL = 1000;

	/** * enable Data QualityTimeline feature *. */
	private Enable enableDataQualityTimeline = Enable.FALSE;
	
	/** The distributed hdfs path. */
	private String distributedHDFSPath;

	/* Jumbune Modules */
	/** The hadoop job profile. */
	private Enable hadoopJobProfile = Enable.FALSE;

	/** The enable data validation. */
	private Enable enableDataValidation = Enable.FALSE;

	/** The debug analysis. */
	private Enable debugAnalysis = Enable.FALSE;

	/** The enable static job profiling. */
	private Enable enableStaticJobProfiling = Enable.FALSE;
	
	/** The activated. */
	private Feature activated;
	
	/* Profiling configurations */
	/** The profiling params. */
	private ProfilingParam profilingParams;

	/* Job configurations */
	/** The jobs. */
	private List<JobDefinition> jobs;

	/** The include class jar. */
	private Enable includeClassJar = Enable.FALSE;

	/** The input file. */
	private String inputFile;

	/* Jar debugging configuration */
	/** The do not instrument. */
	private DoNotInstrument doNotInstrument;

	/** The mapper super classes. */
	private String[] mapperSuperClasses;

	/** The reducer super classes. */
	private String[] reducerSuperClasses;

	/** The debugger conf. */
	private DebuggerConf debuggerConf;

	/**  The logKeyValues. */
	private Enable logKeyValues = Enable.FALSE;

	/** The classpath. */
	private Classpath classpath;

	/** The regex validations. */
	private List<Validation> regexValidations;

	/** The user validations. */
	private List<Validation> userValidations;

	/** It tells after how many keys the partitioning time should be calculated. */
	private int partitionerSampleInterval;

	/** The slaveWorkingDirectory. */
	private String slaveWorkingDirectory;

	/** Location of HDFS path where data to be validated is kept. */
	private String hdfsInputPath;

	/**  String specifying name of jumbune Job. */
	private String jumbuneJobName;

	/**  Launches a new job from Jumbune if set to TRUE *. */
	private Enable runJobFromJumbune = Enable.FALSE;

	/**  Specify the job name of an existing job on Hadoop cluster *. */
	private String existingJobName;

	/** The Jar is from Jumbune System is isLocalSystemJar is FALSE otherwise from local system. */
	private Enable isLocalSystemJar = Enable.FALSE;

	/** The enable data profiling. */
	private Enable enableDataProfiling = Enable.FALSE;

	/** The criteria based data profiling. */
	private Enable criteriaBasedDataProfiling = Enable.FALSE;

	/** The data profiling bean. */
	private DataProfilingBean dataProfilingBean;

	/** The data quality time line. */
	private DataQualityTimeLineConfig dataQualityTimeLineConfig ;
	
	/** The XML Data Validation. */
	private Enable enableXmlDataValidation = Enable.FALSE;
	
	/** The enable json data validation. */
	private Enable enableJsonDataValidation = Enable.FALSE;
	
	private Enable isDataSourceComparisonEnabled = Enable.FALSE;
	
	private DataSourceCompValidationInfo dataSourceCompValidationInfo;
	
	/** The field validation list. */
	private List<Map<String, String>> fieldValidationList;
	
	/** The xml element bean list. */
	private List<XmlElementBean> xmlElementBeanList ;
	
	/** String specifying time of jumbune task to be scheduled. */
	private String jumbuneScheduleTaskTiming;
	
	private String tempDirectory;

	/**
	 * Instantiates a new Job Config.
	 */
	protected JobConfig() {
		LOGGER.debug("JUMBUNE_HOME is set to : " + JumbuneInfo.getHome());
		if (!JobUtil.validateFileSystemLocation(JumbuneInfo.getHome())) {
			LOGGER.error("Environment variable \'JUMBUNE_HOME\' is either not set or is in incorrect format");
			System.exit(1);
		}
	}
	
	/**
	 * Gets the enable xml data validation.
	 *
	 * @return the enableXmlDataValidation
	 */
	public Enable getEnableXmlDataValidation() {
		return enableXmlDataValidation;
	}

	/**
	 * Sets the enable xml data validation.
	 *
	 * @param enableXmlDataValidation the enableXmlDataValidation to set
	 */
	public void setEnableXmlDataValidation(Enable enableXmlDataValidation) {
		this.enableXmlDataValidation = enableXmlDataValidation;
	}
	
	/**
	 * Gets the checks if is local system jar.
	 *
	 * @return if isLocalSystemJar is TRUE or FALSE
	 */
	public Enable getIsLocalSystemJar() {
		return isLocalSystemJar;
	}
	
	/**
	 * Sets the checks if is local system jar.
	 *
	 * @param x the new checks if is local system jar
	 */
	public void setIsLocalSystemJar(Enable x) {
		this.isLocalSystemJar = x;
	}

	/**
	 * Gets the formatted jumbune job name.
	 * 
	 * @return the formatted jumbune job name
	 */
	public String getFormattedJumbuneJobName() {
		String jobNameTemp = getJumbuneJobName();
		if (jobNameTemp == null) {
			return null;
		}

		if (!jobNameTemp.endsWith(File.separator)) {
			jobNameTemp += File.separator;
		}
		return jobNameTemp;	
	}

	/**
	 * Gets the jumbune job name.
	 * 
	 * @return the jumbune job name
	 */
	public String getJumbuneJobName() {
		return jumbuneJobName;
	}

	/**
	 * Sets the jumbune job name.
	 * 
	 * @param jumbuneJobName
	 *            the new jumbune job name
	 */
	public void setJumbuneJobName(final String jumbuneJobName) {
		String jumbuneJobNameTemp = jumbuneJobName;
		jumbuneJobNameTemp = jumbuneJobNameTemp.trim();
		this.jumbuneJobName = jumbuneJobNameTemp;
	}

	
	/**
	 * Gets the activated.
	 *
	 * @return the activated
	 */
	public Feature getActivated() {
		return activated;
	}

	/**
	 * Sets the activated.
	 *
	 * @param activated the new activated
	 */
	public void setActivated(Feature activated) {
		this.activated = activated;
	}

	/**
	 * Gets the hadoop job profile.
	 * 
	 * @return the hadoop job profile
	 */
	public Enable getHadoopJobProfile() {
		return hadoopJobProfile;
	}

	/**
	 * Sets the hadoop job profile.
	 * 
	 * @param hadoopJobProfile
	 *            the new hadoop job profile
	 */
	public void setHadoopJobProfile(Enable hadoopJobProfile) {
		this.hadoopJobProfile = hadoopJobProfile;
	}

	/**
	 * Gets the enable data validation.
	 * 
	 * @return the enable data validation
	 */
	public Enable getEnableDataValidation() {
		return enableDataValidation;
	}

	/**
	 * Sets the enable data validation.
	 * 
	 * @param enableDataValidation
	 *            the new enable data validation
	 */
	public void setEnableDataValidation(Enable enableDataValidation) {
		this.enableDataValidation = enableDataValidation;		
	}

	/**
	 * Gets the debug analysis.
	 * 
	 * @return the debug analysis
	 */
	public Enable getDebugAnalysis() {
		return debugAnalysis;
	}

	/**
	 * Sets the debug analysis.
	 * 
	 * @param debugAnalysis
	 *            the new debug analysis
	 */
	public void setDebugAnalysis(Enable debugAnalysis) {
		this.debugAnalysis = debugAnalysis;
	}

	/**
	 * Sets the input file.
	 * 
	 * @param inputFile
	 *            the new input file
	 */
	public final void setInputFile(String inputFile) {
		this.inputFile = JobUtil.getAndReplaceHolders(inputFile);
	}

	/**
	 * Gets the include class jar.
	 * 
	 * @return the include class jar
	 */
	public Enable getIncludeClassJar() {
		return includeClassJar;
	}

	/**
	 * Sets the include class jar.
	 * 
	 * @param includeClassJar
	 *            the new include class jar
	 */
	public void setIncludeClassJar(Enable includeClassJar) {
		this.includeClassJar = includeClassJar;
	}

	/**
	 * Gets the mapper super classes.
	 * 
	 * @return the mapper super classes
	 */
	public final String[] getMapperSuperClasses() {
		return mapperSuperClasses;
	}

	/**
	 * Sets the mapper super classes.
	 * 
	 * @param mapperSuperClasses
	 *            the new mapper super classes
	 */
	public final void setMapperSuperClasses(String[] mapperSuperClasses) {
		if (mapperSuperClasses != null && mapperSuperClasses.length > 0) {
			this.mapperSuperClasses = Arrays.copyOf(mapperSuperClasses,
					mapperSuperClasses.length);
		}
	}

	/**
	 * Gets the reducer super classes.
	 * 
	 * @return the reducer super classes
	 */
	public final String[] getReducerSuperClasses() {
		return reducerSuperClasses;
	}

	/**
	 * Sets the reducer super classes.
	 * 
	 * @param reducerSuperClasses
	 *            the new reducer super classes
	 */
	public final void setReducerSuperClasses(String[] reducerSuperClasses) {
		if (reducerSuperClasses != null && reducerSuperClasses.length > 0) {
			this.reducerSuperClasses = Arrays.copyOf(reducerSuperClasses,
					reducerSuperClasses.length);
		}
	}

	/**
	 * Gets the jobs.
	 * 
	 * @return the jobs
	 */
	public final List<JobDefinition> getJobs() {
		return (jobs != null) ? jobs : new ArrayList<JobDefinition>();
	}

	/**
	 * Gets the do not instrument.
	 * 
	 * @return the do not instrument
	 */
	public final DoNotInstrument getDoNotInstrument() {
		return doNotInstrument;
	}

	/**
	 * Sets the do not instrument.
	 * 
	 * @param doNotInstrument
	 *            the new do not instrument
	 */
	public final void setDoNotInstrument(DoNotInstrument doNotInstrument) {
		this.doNotInstrument = doNotInstrument;
	}

	/**
	 * Gets the partitioner sample interval.
	 * 
	 * @return the partitioner sample interval
	 */
	public final int getPartitionerSampleInterval() {
		return partitionerSampleInterval;
	}

	/**
	 * Sets the partitioner sample interval.
	 * 
	 * @param partitionerSampleInterval
	 *            the new partitioner sample interval
	 */
	public final void setPartitionerSampleInterval(
			final int partitionerSampleInterval) {
		int partionerSampleInterval = partitionerSampleInterval;
		if (partionerSampleInterval < 0) {
			partionerSampleInterval = DEFAULT_PARTITION_SAMPLE_INTERVAL;
		}
		this.partitionerSampleInterval = partionerSampleInterval;
	}

	/**
	 * Gets the debugger conf.
	 * 
	 * @return the debugger conf
	 */
	public DebuggerConf getDebuggerConf() {
		return debuggerConf;
	}

	/**
	 * Sets the debugger conf.
	 * 
	 * @param debuggerConf
	 *            the new debugger conf
	 */
	public void setDebuggerConf(DebuggerConf debuggerConf) {
		this.debuggerConf = debuggerConf;
	}

	/**
	 * Sets the log key values.
	 * 
	 * @param logKeyValues
	 *            the log key values
	 */
	public void setLogKeyValues(Enable logKeyValues) {
		this.logKeyValues = logKeyValues;
	}

	/**
	 * Gets the log key values.
	 *
	 * @return logKeyValues
	 */
	public Enable getLogKeyValues() {
		return logKeyValues;
	}

	/**
	 * Sets the classpath.
	 * 
	 * @param classpath
	 *            the new classpath
	 */
	public void setClasspath(Classpath classpath) {
		this.classpath = classpath;
	}

	/**
	 * Gets the regex validations.
	 * 
	 * @return the regex validations
	 */
	public List<Validation> getRegexValidations() {
		return (regexValidations != null) ? regexValidations
				: new ArrayList<Validation>();
	}

	/**
	 * Sets the regex validations.
	 * 
	 * @param regexValidations
	 *            the new regex validations
	 */
	public void setRegexValidations(List<Validation> regexValidations) {
		this.regexValidations = regexValidations;
	}

	/**
	 * Gets the user validations.
	 * 
	 * @return the user validations
	 */
	public List<Validation> getUserValidations() {
		return (userValidations != null) ? userValidations
				: new ArrayList<Validation>();
	}

	/**
	 * Sets the user validations.
	 * 
	 * @param userValidations
	 *            the new user validations
	 */
	public void setUserValidations(List<Validation> userValidations) {
		this.userValidations = userValidations;
	}

	public Enable getIsDataSourceComparisonEnabled(){
		return isDataSourceComparisonEnabled;
	}

	public void setIsDataSourceComparisonEnabled(Enable isDataSourceComparisonEnabled){
		this.isDataSourceComparisonEnabled = isDataSourceComparisonEnabled;
	}
	
	/**
	 * Gets the input file.
	 * 
	 * @return the input file
	 */
	public String getInputFile() {
		String searchStr = "akepath";
		if (inputFile != null && inputFile.indexOf(searchStr) > 0) {
			String split[] = inputFile.split(searchStr);
			inputFile = split[1];
		}
		return inputFile;
	}

	/**
	 * Sets the jobs.
	 * 
	 * @param jobs
	 *            the new jobs
	 */
	public void setJobs(List<JobDefinition> jobs) {
		this.jobs = jobs;
	}

	/**
	 * Gets the profiling params.
	 * 
	 * @return the profiling params
	 */
	public ProfilingParam getProfilingParams() {
		return profilingParams;
	}

	/**
	 * Sets the profiling params.
	 * 
	 * @param profilingParams
	 *            the new profiling params
	 */
	public void setProfilingParams(ProfilingParam profilingParams) {
		this.profilingParams = profilingParams;
	}

	/**
	 * Gets the hdfs input path.
	 * 
	 * @return the hdfs input path
	 */
	public final String getHdfsInputPath() {
		return hdfsInputPath;
	}

	/**
	 * Sets the hdfs input path.
	 * 
	 * @param hdfsInputPath
	 *            the new hdfs input path
	 */
	public final void setHdfsInputPath(String hdfsInputPath) {
		this.hdfsInputPath = hdfsInputPath;
	}

	/**
	 * gets the runJobFromJumbune.
	 *
	 * @return the run job from jumbune
	 */
	public Enable getRunJobFromJumbune() {
		return runJobFromJumbune;
	}

	/**
	 * sets the runJobFromJumbune.
	 *
	 * @param runJobFromJumbune the new run job from jumbune
	 */
	public void setRunJobFromJumbune(Enable runJobFromJumbune) {
		this.runJobFromJumbune = runJobFromJumbune;
	}

	/**
	 * gets the existingJobName.
	 *
	 * @return the existing job name
	 */
	public String getExistingJobName() {
		return existingJobName;
	}

	/**
	 * sets the existingJobName.
	 *
	 * @param existingJobName the new existing job name
	 */
	public void setExistingJobName(String existingJobName) {
		this.existingJobName = existingJobName;
	}

	/**
	 * Sets the distributed hdfs path.
	 * 
	 * @param distributedHDFSPath
	 *            the distributedHDFSPath to set
	 */
	public void setDistributedHDFSPath(String distributedHDFSPath) {
		this.distributedHDFSPath = distributedHDFSPath;
	}

	/**
	 * Gets the distributed hdfs path.
	 * 
	 * @return the distributedHDFSPath
	 */
	public String getDistributedHDFSPath() {
		return distributedHDFSPath;
	}

	/**
	 * Sets the enable static job profiling.
	 * 
	 * @param enableStaticJobProfiling
	 *            the enableStaticJobProfiling to set
	 */
	public void setEnableStaticJobProfiling(Enable enableStaticJobProfiling) {
		this.enableStaticJobProfiling = enableStaticJobProfiling;
	}

	/**
	 * Gets the enable static job profiling.
	 * 
	 * @return the enableStaticJobProfiling
	 */
	public Enable getEnableStaticJobProfiling() {
		return enableStaticJobProfiling;
	}

	/**
	 * Sets the enable data profiling.
	 *
	 * @param enableDataProfiling the new enable data profiling
	 */
	public void setEnableDataProfiling(Enable enableDataProfiling) {
		this.enableDataProfiling = enableDataProfiling;
		
	}

	/**
	 * Gets the enable data profiling.
	 *
	 * @return the enable data profiling
	 */
	public Enable getEnableDataProfiling() {
		return enableDataProfiling;
	}

	/**
	 * Sets the data profiling bean.
	 *
	 * @param dataProfilingBean the new data profiling bean
	 */
	public void setDataProfilingBean(DataProfilingBean dataProfilingBean) {
		this.dataProfilingBean = dataProfilingBean;
	}

	/**
	 * Gets the data profiling bean.
	 *
	 * @return the data profiling bean
	 */
	public DataProfilingBean getDataProfilingBean() {
		return dataProfilingBean;
	}

	/**
	 * Sets the criteria based data profiling.
	 *
	 * @param criteriaBasedDataProfiling the new criteria based data profiling
	 */
	public void setCriteriaBasedDataProfiling(Enable criteriaBasedDataProfiling) {
		this.criteriaBasedDataProfiling = criteriaBasedDataProfiling;
	}

	/**
	 * Gets the criteria based data profiling.
	 *
	 * @return the criteria based data profiling
	 */
	public Enable getCriteriaBasedDataProfiling() {
		return criteriaBasedDataProfiling;
	}

	/**
	 * Gets the enable data quality timeline.
	 *
	 * @return the enableDataQualityTimeline
	 */
	public Enable getEnableDataQualityTimeline() {
		return enableDataQualityTimeline;
	}

	/**
	 * Sets the enable data quality timeline.
	 *
	 * @param enableDataQualityTimeline the enableDataQualityTimeline to set
	 */
	public void setEnableDataQualityTimeline(Enable enableDataQualityTimeline) {
		this.enableDataQualityTimeline = enableDataQualityTimeline;
	}

	/**
	 * Gets the data quality time line.
	 *
	 * @return the dataQualityTimeLine
	 */
	public DataQualityTimeLineConfig getDataQualityTimeLineConfig() {
		return dataQualityTimeLineConfig;
	}

	/**
	 * Sets the data quality time line.
	 *
	 * @param dataQualityTimeLineConfig the dataQualityTimeLine to set
	 */
	public void setDataQualityTimeLineConfig(DataQualityTimeLineConfig dataQualityTimeLineConfig) {
		this.dataQualityTimeLineConfig = dataQualityTimeLineConfig;
	}
	
	
	/**
	 * Gets the jumbune job loc.
	 *
	 * @return the jumbune job loc
	 */
	public final String getJumbuneJobLoc() {
		return (getJobJarLoc() + getFormattedJumbuneJobName());
	}

	/**
	 * Gets the log level.
	 *
	 * @param instrumentType the instrument type
	 * @return the log level
	 */
	public String getLogLevel(String instrumentType) {
		DebuggerConf id=null;
		Map<String, LogLevel> map=null;
		LogLevel level = null;

		try {
			id = getDebuggerConf();

		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			System.exit(1);
		}
		map = id.getLogLevel();
		if (map == null) {
			return null;
		}
		level = map.get(instrumentType);
		return (level != null && Constants.LOG_LEVEL_TRUE
				.equalsIgnoreCase(level.toString())) ? Constants.LOG_LEVEL_INFO
				.toLowerCase() : null;
	}

	/**
	 * Checks if is instrument enabled.
	 *
	 * @param instrumentType the instrument type
	 * @return true, if is instrument enabled
	 */
	public boolean isInstrumentEnabled(String instrumentType) {
		boolean isInstrumentedEnabled = false;

		isInstrumentedEnabled = getLogLevel(instrumentType) != null ? true
				: false;

		return isInstrumentedEnabled;
	}


	/**
	 * Gets the profiled output file.
	 *
	 * @return the profiled output file
	 */
	public final String getProfiledOutputFile() {
		return	getJumbuneJobLoc()
				+ Constants.PROFILED_JAR_LOC + getFileName(inputFile)
				+ Constants.PROFILED_FILE_SUFFIX + Constants.JAR;
		
	}

	/**
	 * Gets the instrument output file.
	 *
	 * @return the instrument output file
	 */
	public String getInstrumentOutputFile() {
		return getJumbuneJobLoc()
				+ Constants.INSTRUMENTED_JAR_LOC + getFileName(inputFile)
				+ Constants.INSTRUMENTED_FILE_SUFFIX + Constants.JAR;
	}

	/**
	 * Gets the include anyways list.
	 *
	 * @return the include anyways list
	 */
	public List<String> getIncludeAnywaysList() {
		if (getDoNotInstrument() != null) {
			return getDoNotInstrument().getIncludeAnyways();
		}
		return null;
	}

	/**
	 * Gets the complete do not instrument list.
	 *
	 * @return the complete do not instrument list
	 */
	public List<String> getCompleteDoNotInstrumentList() {
		List<String> completeListOfExclusions = new ArrayList<String>();

		if (getDoNotInstrument() != null
				&& getDoNotInstrument().getPackages() != null) {
			completeListOfExclusions.addAll(getDoNotInstrument()
					.getPackages());
		}

		if (getDoNotInstrument() != null
				&& getDoNotInstrument().getClasses() != null) {
			completeListOfExclusions.addAll(getDoNotInstrument()
					.getClasses());
		}
		return completeListOfExclusions;
	}

	/**
	 * Gets the regex.
	 *
	 * @return the regex
	 */
	public List<Validation> getRegex() {
		List<Validation> regex = getRegexValidations();
		if (regex == null) {
			LOGGER.error("Fatal error: regex are not defined in json");
			System.exit(1);
		}
		return regex;
	}

	/**
	 * <p>
	 * This method provides regex for map/reduce key for the given class
	 * </p>
	 * .
	 *
	 * @param strClassName
	 *            Class name
	 * @return String Regex for key
	 */
	public String getMapReduceKeyRegex(String strClassName) {
		Validation validation = Validation.getValidation(getRegex(),
				strClassName);
		if (validation != null) {
			return validation.getKey();
		}
		return null;
	}

	/**
	 * <p>
	 * This method provides regex for map/reduce value for the given class
	 * </p>
	 * .
	 *
	 * @param strClassName
	 *            Class name
	 * @return String Regex for value
	 */
	public String getMapReduceValueRegex(String strClassName) {
		Validation validation = Validation.getValidation(getRegex(),
				strClassName);
		if (validation != null) {
			return validation.getValue();
		}
		return null;
	}

	/**
	 * <p>
	 * This method provides validation for map/reduce key for the given class
	 * </p>
	 * .
	 *
	 * @param strClassName
	 *            class name
	 * @return String validation class
	 */
	public String getMapReduceKeyValidator(String strClassName) {
		Validation validation = Validation.getValidation(getUserValidations(),
				strClassName);
		if (validation != null) {
			return validation.getKey();
		}
		return null;
	}

	/**
	 * <p>
	 * This method provides validation for map/reduce value for the given class
	 * </p>
	 * .
	 *
	 * @param strClassName
	 *            class name
	 * @return String validation class
	 */
	public String getMapReduceValueValidator(String strClassName) {
		Validation validation = Validation.getValidation(getUserValidations(),
				strClassName);
		if (validation != null) {
			return validation.getValue();
		}
		return null;
	}

	/**
	 * Gets the shell user report location.
	 *
	 * @return the shell user report location
	 */
	public String getShellUserReportLocation() {
		return getJumbuneJobLoc()
				+ Constants.SUMMARY_FILE_LOC;
	}

	/**
	 * Gets the classpath.
	 *
	 * @return the classpath
	 */
	public Classpath getClasspath() {	
			return classpath;
			}

	/**
	 * This method provides the way in which dependencies are supplied to
	 * Jumbune.
	 * <ul>
	 * <li>User dependencies could be supplied:
	 * <ol>
	 * <li>Thick jar
	 * <li>Placed in HADOOP_LIB folder on all nodes
	 * <li>Placed on master node
	 * <li>Placed on all slave nodes
	 * </ol>
	 * <li>Not valid for Jumbune dependencies.
	 * </ul>
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param type
	 *            Dependency type
	 * @return int The source
	 */
	public int getClasspathSourceType(int type) {
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getSource();
		}
		return -1;
	}


	/**
	 * Gets the classpath folders.
	 *
	 * @param type
	 *            the type
	 * @return the classpath folders
	 */
	public String[] getClasspathFolders(int type) {
		// 1 = user defined, 2 = framework defined
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getFolders();
		} else {
			return getClasspath().getJumbuneSupplied().getFolders();
		}
	}

	/**
	 * Gets the classpath excludes.
	 *
	 * @param type
	 *            the type
	 * @return the classpath excludes
	 */
	public String[] getClasspathExcludes(int type) {
		// 1 = user defined, 2 = framework defined
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getExcludes();
		} else {
			return getClasspath().getJumbuneSupplied().getExcludes();
		}
	}

	/**
	 * Checks if is hadoop job profile enabled.
	 *
	 * @return true, if is hadoop job profile enabled
	 */
	public boolean isHadoopJobProfileEnabled() {
		return getEnableStaticJobProfiling().equals(Enable.TRUE) ? true
				: false;
	}

	/**
	 * Gets the hadoop job profile params.
	 *
	 * @return the hadoop job profile params
	 */
/*	public String getHadoopJobProfileParams() {
		return "\'" + Constants.H_PROFILE_PARAM
				+ getProfilingParams().getHadoopJobProfileParams()
				+ "\'";
	}
*/

	/**
	 * Gets the max if block nesting level.
	 *
	 * @return the max if block nesting level
	 */
	public int getMaxIfBlockNestingLevel() {
		return getDebuggerConf().getMaxIfBlockNestingLevel();
	}

	/**
	 * Gets the master consolidated log location.
	 *
	 * @return the master consolidated log location
	 */
	public String getMasterConsolidatedLogLocation() {
		return getJumbuneJobLoc()
				+ Constants.CONSOLIDATED_LOG_LOC;
	}

	/**
	 * Gets the master consolidated dv location.
	 *
	 * @return the master consolidated dv location
	 */
	public String getMasterConsolidatedDVLocation() {
		return getJumbuneJobLoc()
				+ Constants.CONSOLIDATED_DV_LOC;
	}
	
	/**
	 * Gets the master consolidated dv location for Xml.
	 *
	 * @return the master consolidated dv location  for Xml.
	 */
	public String getMasterConsolidatedXmlDVLocation() {
		return getJumbuneJobLoc()
				+ Constants.CONSOLIDATED_XML_DV_LOC;
	}

	
	/**
	 * Gets the master consolidated json dv location.
	 *
	 * @return the master consolidated json dv location
	 */
	public String getMasterConsolidatedJsonDVLocation(){
		return getJumbuneJobLoc()
				+ Constants.CONSOLIDATED_JSON_DV_LOC;
	}
	
	/**
	 * Gets the file name.
	 *
	 * @param inputFileLoc
	 *            the input file loc
	 * @return the file name
	 */
	private String getFileName(String inputFileLoc) {
		return inputFileLoc.substring(inputFileLoc.lastIndexOf('/') + 1,
				inputFileLoc.lastIndexOf('.'));
	}

	/**
	 * Gets the data validation result location.
	 *
	 * @return the data validation result location
	 */
	public final String getDataValidationResultLocation() {

		return getJumbuneJobLoc()
				+ "datavalidation/dataviolations";

	}

	/**
	 * Gets the classpath files.
	 *
	 * @param type
	 *            the type
	 * @return the classpath files
	 */
	public String[] getClasspathFiles(int type) {
		// 1 = user defined, 2 = framework defined
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getFiles();
		} else {
			return getClasspath().getJumbuneSupplied().getFiles();
		}
	}

	/**
	 * Gets the job jar loc.
	 *
	 * @return the job jar loc
	 */
	public String getJobJarLoc() {
		return JumbuneInfo.getHome() + Constants.JOB_JARS_LOC;
	}
	
	/**
	 * Gets the log summary location.
	 *
	 * @return the log summary location
	 */
	public LogSummaryLocation getLogSummaryLocation() {
		LogSummaryLocation logSummarylocation = new LogSummaryLocation();
		logSummarylocation.setPureJarCounterLocation(getJumbuneJobLoc()
				+ Constants.SUMMARY_FILE_LOC + Constants.PURE_JAR_COUNTER);
		logSummarylocation.setPureJarProfilingCountersLocation(getJumbuneJobLoc()
				+ Constants.SUMMARY_FILE_LOC + Constants.PURE_PROFILING);
		logSummarylocation.setInstrumentedJarCountersLocation(getJumbuneJobLoc()
				+ Constants.SUMMARY_FILE_LOC
				+ Constants.INSTRUMENTED_JAR_COUNTER);
		logSummarylocation.setLogsConsolidatedSummaryLocation(getJumbuneJobLoc()
				+ Constants.SUMMARY_FILE_LOC + Constants.M_SUMMARY_FILE);
		logSummarylocation.setProfilingFilesLocation(getJumbuneJobLoc()
				+ Constants.PROFILING_FILE_LOC);
		return logSummarylocation;
	}


	/**
	 * <p>
	 * This method provides the location of userLib folder on master.
	 * </p>
	 * 
	 * @return String location of userLib on master
	 * 
	 * @see Constants#USER_LIB_LOC
	 */
	public String getUserLibLocationAtMaster() {
		return JumbuneInfo.getHome() + Constants.USER_LIB_LOC;
	}

	/**
	 * <p>
	 * This method provides whether main class is defined in job jar manifest
	 * file or not
	 * </p>
	 * .
	 *
	 * @return boolean true if main class is defined in jar manifest
	 */
	public boolean isMainClassDefinedInJobJar() {
		return this.getIncludeClassJar().getEnumValue();
	}

	/**
	 * Gets the operating cluster.
	 *
	 * @return the operating cluster
	 */
	public String getOperatingCluster() {
		return operatingCluster;
	}

	/**
	 * Sets the operating cluster.
	 *
	 * @param operatingCluster the new operating cluster
	 */
	public void setOperatingCluster(String operatingCluster) {
		this.operatingCluster = operatingCluster;
	}
	
	/**
	 * Gets the operating user.
	 *
	 * @return the operating user
	 */
	public String getOperatingUser() {
		return operatingUser;
	}

	/**
	 * Sets the operating user.
	 *
	 * @param operatingUser the new operating user
	 */
	public void setOperatingUser(String operatingUser) {
		this.operatingUser = operatingUser;
	}

	/**
	 * Gets the enable json data validation.
	 *
	 * @return the enable json data validation
	 */
	public Enable getEnableJsonDataValidation() {
		return enableJsonDataValidation;
	}

	/**
	 * Sets the enable json data validation.
	 *
	 * @param enableJsonDataValidation the new enable json data validation
	 */
	public void setEnableJsonDataValidation(Enable enableJsonDataValidation) {
		this.enableJsonDataValidation = enableJsonDataValidation;
	}

	/**
	 * Gets the field validation list.
	 *
	 * @return the field validation list
	 */
	public List<Map<String, String>> getFieldValidationList() {
		return fieldValidationList;
	}

	/**
	 * Sets the field validation list.
	 *
	 * @param fieldValidationList the field validation list
	 */
	public void setFieldValidationList(List<Map<String, String>> fieldValidationList) {
		this.fieldValidationList = fieldValidationList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	public List<XmlElementBean> getXmlElementBeanList() {
		return xmlElementBeanList;
	}

	public void setXmlElementBeanList(List<XmlElementBean> xmlElementBeanList) {
		this.xmlElementBeanList = xmlElementBeanList;
	}
	
	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parameters the new parameters
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public DataSourceCompValidationInfo getDataSourceCompValidationInfo() {
		return dataSourceCompValidationInfo;
	}

	public void setDataSourceCompValidationInfo(DataSourceCompValidationInfo dataSourceCompValidationInfo) {
		this.dataSourceCompValidationInfo = dataSourceCompValidationInfo;
	}
	
	/**
	 * Gets the jumbune schedule task timing.
	 *
	 * @return the jumbune schedule task timing
	 */
	public String getJumbuneScheduleTaskTiming() {
		return jumbuneScheduleTaskTiming;
	}

	/**
	 * Sets the jumbune schedule task timing.
	 *
	 * @param jumbuneScheduleTaskTiming the new jumbune schedule task timing
	 */
	public void setJumbuneScheduleTaskTiming(String jumbuneScheduleTaskTiming) {
		this.jumbuneScheduleTaskTiming = jumbuneScheduleTaskTiming;
	}
	
	public String getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	@Override
	public String toString() {
		return "JobConfig [operatingCluster=" + operatingCluster + ", operatingUser=" + operatingUser
				+ ", enableDataQualityTimeline=" + enableDataQualityTimeline + ", distributedHDFSPath="
				+ distributedHDFSPath + ", hadoopJobProfile=" + hadoopJobProfile + ", enableDataValidation="
				+ enableDataValidation + ", debugAnalysis=" + debugAnalysis + ", enableStaticJobProfiling="
				+ enableStaticJobProfiling + ", activated=" + activated + ", profilingParams=" + profilingParams
				+ ", jobs=" + jobs + ", includeClassJar=" + includeClassJar + ", inputFile=" + inputFile
				+ ", doNotInstrument=" + doNotInstrument + ", mapperSuperClasses=" + Arrays.toString(mapperSuperClasses)
				+ ", reducerSuperClasses=" + Arrays.toString(reducerSuperClasses) + ", debuggerConf=" + debuggerConf
				+ ", logKeyValues=" + logKeyValues + ", classpath=" + classpath + ", regexValidations="
				+ regexValidations + ", userValidations=" + userValidations + ", partitionerSampleInterval="
				+ partitionerSampleInterval + ", slaveWorkingDirectory=" + slaveWorkingDirectory + ", hdfsInputPath="
				+ hdfsInputPath + ", jumbuneJobName=" + jumbuneJobName + ", runJobFromJumbune=" + runJobFromJumbune
				+ ", existingJobName=" + existingJobName + ", isLocalSystemJar=" + isLocalSystemJar
				+ ", enableDataProfiling=" + enableDataProfiling + ", criteriaBasedDataProfiling="
				+ criteriaBasedDataProfiling + ", dataProfilingBean=" + dataProfilingBean
				+ ", dataQualityTimeLineConfig=" + dataQualityTimeLineConfig + ", enableXmlDataValidation="
				+ enableXmlDataValidation + ", enableJsonDataValidation=" + enableJsonDataValidation
				+ ", fieldValidationList=" + fieldValidationList + ", xmlElementBeanList=" + xmlElementBeanList + ", parameters=" + parameters +"]";
	}	
	
}