package jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml;


/**
 * <p>
 * This file is property of DaimlerChrysler.
 * </p>
 * 
 * AEM_UTF8Tools provides somes static methods to convert Strings that
 * were saved in UTF8-format to disk and are now read in.
 * The conversion is especially needed to recover the original german umlauts.
 *  
 * @author cwiech8
 *
 */

public class AEM_UTF8Tools {

	/**
	 * Replaces all Occurences of UTF8-Description with their corresponding
	 * german umlauts (in lower and higher case) or with german ss
	 * 
	 * @param utfString -
	 *            string to convert
	 * @return - the converted string
	 */
	public static String decodeUTF8(String utfString) {
		if (utfString == null)
			return null;
		utfString = replace(utfString, "\u00C3\u00BC", "\u00FC"); //ue
		utfString = replace(utfString, "\u00C3\u201E", "\u00C4"); //Ae
		utfString = replace(utfString, "\u00C3\u00A4", "\u00E4"); //ae
		utfString = replace(utfString, "\u00C3\u0153", "\u00DC"); //Ue
		utfString = replace(utfString, "\u00C3\u00B6", "\u00F6"); //oe
		utfString = replace(utfString, "\u00C3\u2013", "\u00D6"); //Oe
		utfString = replace(utfString, "\u00C3\u0178", "\u00DF"); //ss
		
//		for Linux files
		utfString = replace(utfString, "\u00C3\u0152", "\u00FC");  //� OK
		utfString = replace(utfString, "\u00C3\u0084", "\u00C4");  //� OK
		utfString = replace(utfString, "\u00C3\u20AC", "\u00E4");  //� OK
		utfString = replace(utfString, "\u00C3\u009C", "\u00DC");  //� OK
		utfString = replace(utfString, "\u00C3\u00B6", "\u00F6");  //� OK --> same as Windows
		utfString = replace(utfString, "\u00C3\u0096", "\u00D6");  //� OK
		utfString = replace(utfString, "\u00C3\u009F", "\u00DF");  //� OK
		return utfString;
	}
	
	/**
	 * 
	 * FIXME: Move to util class (also used in NetWriter)
	 * 
	 * replaces occurences of some string with another one
	 * 
	 * @param s
	 *            The string holding the text to be examined
	 * @param toReplace
	 *            The Text which will be replaced from the input string
	 * @param replaceWith
	 *            the string each occurence of toReplace will be replaced with
	 * @return the worked over String
	 */
	public static String replace(String s, String toReplace, String replaceWith) {
		if (null == s) {
			return "";
		}
		int index = s.indexOf(toReplace);
		if (index > -1) {
			s = s.substring(0, index)
					+ replaceWith
					+ replace(s.substring(index + toReplace.length(), s
							.length()), toReplace, replaceWith);
		}
		return s;
	}
}
