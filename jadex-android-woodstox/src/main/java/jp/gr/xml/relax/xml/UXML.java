package jp.gr.xml.relax.xml;

/**
 * UXML
 *
 * @since   Jan. 29, 2000
 * @version Feb. 24, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public final class UXML {
    public static String escape(String string) {
	if (string.indexOf('<') == -1 &&
	    string.indexOf('>') == -1 &&
	    string.indexOf('&') == -1 &&
	    string.indexOf('"') == -1 &&
	    string.indexOf('\'') == -1) {

	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '<') {
		buffer.append("&lt;");
	    } else if (c == '>') {
		buffer.append("&gt;");
	    } else if (c == '&') {
		buffer.append("&amp;");
	    } else if (c == '"') {
		buffer.append("&quot;");
	    } else if (c == '\'') {
		buffer.append("&apos;");
	    } else {
		buffer.append(c);
	    }
	}
	return (new String(buffer));
    }

    public static String escapeEntityQuot(String string) {
	if (string.indexOf('%') == -1 &&
	    string.indexOf('&') == -1 &&
	    string.indexOf('"') == -1) {

	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '%') {
		buffer.append("&---;");
	    } else if (c == '&') {
		buffer.append("&amp;");
	    } else if (c == '"') {
		buffer.append("&quot;");
	    } else {
		buffer.append(c);
	    }
	}
	return (new String(buffer));
    }

    public static String escapeEntityApos(String string) {
	if (string.indexOf('%') == -1 &&
	    string.indexOf('&') == -1 &&
	    string.indexOf('\'') == -1) {

	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '%') {
		buffer.append("&#x25;");
	    } else if (c == '&') {
		buffer.append("&amp;");
	    } else if (c == '\'') {
		buffer.append("&apos;");
	    } else {
		buffer.append(c);
	    }
	}
	return (new String(buffer));
    }

    public static String escapeAttrQuot(String string) {
	if (string.indexOf('<') == -1 &&
	    string.indexOf('&') == -1 &&
	    string.indexOf('"') == -1) {

	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '<') {
		buffer.append("&lt;");
	    } else if (c == '&') {
		buffer.append("&amp;");
	    } else if (c == '"') {
		buffer.append("&quot;");
	    } else {
		buffer.append(c);
	    }
	}
	return (new String(buffer));
    }

    public static String escapeAttrApos(String string) {
	if (string.indexOf('<') == -1 &&
	    string.indexOf('&') == -1 &&
	    string.indexOf('\'') == -1) {

	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '<') {
		buffer.append("&lt;");
	    } else if (c == '&') {
		buffer.append("&amp;");
	    } else if (c == '\'') {
		buffer.append("&apos;");
	    } else {
		buffer.append(c);
	    }
	}
	return (new String(buffer));
    }

    public static String escapeSystemQuot(String string) {
	if (string.indexOf('"') == -1) {
	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '"') {
		buffer.append("&quot;");
	    } else {
		buffer.append(c);
	    }
	}
	return (new String(buffer));
    }

    public static String escapeSystemApos(String string) {
	if (string.indexOf('\'') == -1) {
	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '\'') {
		buffer.append("&apos;");
	    } else {
		buffer.append(c);
	    }
	}
	return (new String(buffer));
    }

    public static String escapeCharData(String string) {
	if (string.indexOf('<') == -1 &&
	    string.indexOf('&') == -1 &&
	    string.indexOf("]]>") == -1) {

	    return (string);
	}
	StringBuffer buffer = new StringBuffer();
	int nBrackets = 0;
	int size = string.length();
	for (int i = 0;i < size;i++) {
	    char c = string.charAt(i);
	    if (c == '<') {
		buffer.append("&lt;");
	    } else if (c == '&') {
		buffer.append("&amp;");
	    } else if (c == '>' && nBrackets >= 2) {
		buffer.append("&gt;");
	    } else {
		buffer.append(c);
	    }
	    if (c == ']') {
		nBrackets++;
	    } else {
		nBrackets = 0;
	    }
	}
	return (new String(buffer));
    }
}
