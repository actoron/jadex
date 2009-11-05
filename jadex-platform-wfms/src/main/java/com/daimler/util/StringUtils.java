// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This file is property of DaimlerChrysler.
 * </p>
 *
 * <code>StringUtils</code> provides some static accessible common methods for
 * Strings
 *
 * @author cwiech8
 *
 */

public class StringUtils {

	/**
	 * Returns <code>true</code> if the given String contains german umlauts.
	 *
	 * @param s
	 *            String to examine
	 * @return <code>true</code> if the String contains umlauts
	 */
	public static boolean containsUmlauts(final String s) {
		boolean b = false;
		if (s.contains("\u00FC")) {
			b = true;
		} else if (s.contains("\u00C4")) {
			b = true;
		} else if (s.contains("\u00E4")) {
			b = true;
		} else if (s.contains("\u00DC")) {
			b = true;
		} else if (s.contains("\u00F6")) {
			b = true;
		} else if (s.contains("\u00D6")) {
			b = true;
		} else if (s.contains("\u00DF")) {
			b = true;
		}
		return b;
	}

    /**
     * Masks all occurences of quotes in a String with '\"'.
     * 
     * @param s
     * @return
     */
    public static String maskQuotes(String s) {
        if (s == null) {
            return "null";
        }
        return s.replace("\"", "\\\"");
    }
    
    /**
     * Removes all masked quotes in a String.
     * 
     * @param s
     * @return
     */
    public static String unmaskQuotes(String s) {
        if (s == null) {
            return "null";
        }
        return s.replace("\\\"", "\"");
    }
    
	/**
	 * Removes quotes around a String.
	 *
	 * @param s -
	 *            String to maipulate
	 * @return s without eventually enclosing quotes
	 */
	public static String removeEnclosingQuotes(final String s) {
		String sRet = s;
		while (sRet.startsWith("\"") && sRet.endsWith("\"")) {
			sRet = sRet.substring(1, sRet.length() - 1);
		}
		return sRet;
	}

	/**
	 *
	 * Checks whether the <code>string</code> is contained in the array of
	 * Strings ignoring case
	 *
	 * @param string
	 *            the String for that occurence should be checked
	 * @param strings
	 *            the array of strings
	 * @return <code>true</code> if <code>string</code> is contained in
	 *         <code>strings</code>
	 * 			<code>true</code> else
	 *
	 */
	public static boolean isStringContainedIgnoreCase(String string,
			String[] strings) {
		if (string == null || strings == null)
			return false;
		List l = Arrays.asList(strings);
		Iterator it = l.iterator();
		while (it.hasNext()) {
			if (string.equalsIgnoreCase((String) it.next()))
				return true;
		}
		return false;
	}

	/**
	 * Checks whether the <code>string</code> is contained in the array of
	 * Strings
	 *
	 * @param string
	 *            the String for that occurence should be checked
	 * @param strings
	 *            the array of strings
	 * @return <code>true</code> if <code>string</code> is contained in
	 *         <code>strings</code>
	 * 			<code>true</code> else
	 */
	public static boolean isStringContained(String string, String[] strings) {
		if (string == null || strings == null)
			return false;
		List l = Arrays.asList(strings);
		Iterator it = l.iterator();
		while (it.hasNext()) {
			if (string.equals(it.next()))
				return true;
		}
		return false;
	}

	/**
	 * Removes german umlauts like (e.g. \u00E4, \u00F6, \u00FC, \u00DF) from
	 * <code>s</code>. They will be replaced with standard transcriptions
	 * (e.g. \u00E4 = 'ae', \u00DF = 'ss').
	 *
	 * @param s
	 *            String to remove the umlauts from
	 * @return String with removed umlauts
	 */
	public static String removeUmlauts(String s) {
		s = replace(s, "\u00FC", "ue");
		s = replace(s, "\u00C4", "Ae");
		s = replace(s, "\u00E4", "ae");
		s = replace(s, "\u00DC", "Ue");
		s = replace(s, "\u00F6", "oe");
		s = replace(s, "\u00D6", "Oe");
		s = replace(s, "\u00DF", "ss");
		return s;
	}

	/**
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
		if (s == null) {
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

    /**
     * changes the reserved characters in XML to their numeric descriptors.
     *
     * @param s
     *            The Text to be converted
     * @return the converted Text
     */
    public static String toXMLString(String s) {
        if (s == null) {
        	return "null";
        }
    	s = replace(s, "&", "&#38;");
        s = replace(s, "<", "&#60;");
        s = replace(s, ">", "&#62;");
        s = replace(s, "`", "&#39;");
        s = replace(s, "\"", "\\\"");
        return s;
    }

	/**
	 * Returns the number of occurences for <code>searchSequence</code> in
	 * <code>s</code>.
	 *
	 * @param s
	 *            String where the <code>searchSequence</code> is searched in
	 * @param searchSequence
	 *            String to look for
	 * @return int i >= 0 - Number of occurences for <code>searchSequence</code>
	 *         in <code>s</code>.
	 */
	public static int getOccurenceCount(String s, String searchSequence) {
		if (s == null || searchSequence == null) {
			return 0;
		}
		int iRet = 0;
		int i = -1;
		while ((i = s.indexOf(searchSequence, i + 1)) > -1) {
			iRet++;
		}
		return iRet;
	}
}
