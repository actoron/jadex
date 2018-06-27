package jadex.bdiv3.android;

public class LogClassWriter
{

	public static final String LOG_CLASSNAME = "Ljadex/bdiv3/android/LogClassWriter;";
	
	public static void log(Object o) {
		System.out.println(o == null ? "null" : o.toString());
	}

}
