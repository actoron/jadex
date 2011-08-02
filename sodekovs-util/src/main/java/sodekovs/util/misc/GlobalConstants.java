package sodekovs.util.misc;

/**
 * Contains constants used within this project.
 * @author Ante Vilenica
 *
 */
public class GlobalConstants {

	public static final String BDI_AGENT = "BDIAgent";
	public static final String ISPACE_OBJECT = "ISpaceObject";
	
	//GUNPLOT
	public static final String DB_GNUPLOT_SCHEMA = "Gnuplot";
	public static final String DB_GNUPLOT_LOGTABLE = "Logs";
	public static final String GNUPLOT_EXE_FILEPATH = "C:\\Program Files\\GnuPlot\\gp443win32\\gnuplot\\binary\\gnuplot.exe";
	public static final String LOGGING_DIRECTORY = System.getProperty("user.dir") + "\\Benchmarking-Schedule-Logs";
	
	//Capital Bike Share - Derby DB Properties
	public static final String CAPITAL_BIKE_SHARE_SCHEMA = "CapBikeShare";
	public static final String CAPITAL_BIKE_SHARE_LOGTABLE = "Logs";
	
	//Properties for HAW-MySQL DB
	public static final String BIKE_DB_TABLE = "bikes";
	public static final String BIKE_DB_STATION_SCHEMA = "station";
	public static final String BIKE_DB_STATIONS = "stations";
	public static final String BIKE_DB_STATIONS_XML = "stationsxml";
}
