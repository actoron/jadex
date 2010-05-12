/*
 * JavaXMLWriter.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 11, 2006.  
 * Last revision $Revision: 4047 $ by:
 * $Author: walczak $ on $Date: 2006-02-17 13:48:54 +0100 (Fr, 17 Feb 2006) $.
 */
package nuggets;

import nuggets.util.Base64;
import nuggets.util.ICharStream;

/** JavaXMLWriter - writes a raw XML with java packages for ontology purpose.
 * This can be used only with java.
 * @author walczak
 * @since  Jan 11, 2006
 */
public class JavaXMLWriter implements IWriter {

	private final ICharStream out;

	private boolean open_tag;

	/** 
	 * Constructor for JavaXMLWriter.
	 * @param out
	 */
	public JavaXMLWriter(ICharStream out) {
		this.out = out;
	}

	/** 
	 * This writes the prolog of the message
	 * @param o 
	 */
	public void start(String o) {
			// out.write(XML_VERSION_1_0_ENCODING_UTF_8); // needs to be written by the specified stream
			out.write(N_ROOT);
			if (o!=null) {
				out.write(" xmlns=\"");
				out.write(o);
				out.write('\"');
			}
			out.write('>');
			open_tag=false;
	}

	/**
	 * This writes the epilog of the message 
	 *
	 */
	public void end() {
			out.write(END_N_ROOT);
			out.flush();
	}

	/** 
	 * @param tag
	 * @param id
	 */
	public void start(String tag, String id) {
			closeTag();
			out.write('\n');
			out.write('<');
			writeIdentifier(tag);
			if (id!=null) {
				out.write(' ');
				out.write(NUGGET_ID);
				out.write('=');
				out.write('\"');
				out.write(id);
				out.write('\"');
			}
			open_tag = true;
	}

	/** 
	 * 
	 * @param tag 
	 */
	public void end(String tag) {
			if (open_tag) {
				out.write("/>");
			} else {
               out.write('<');
               out.write('/');
   			   writeIdentifier(tag);
               out.write('>');
            }
			open_tag=false;
	}

	/** Name must be in the XML range
	 * @param name
	 * @param value
	 * @see nuggets.IWriter#put(java.lang.String, java.lang.String)
	 */
	public void put(String name, String value) {
			out.write(' ');
			out.write(name);
			out.write('=');
			out.write('"');
			writeAttributeValue(value);
			out.write('"');
	}

	/** Name and value must be in the XML range!
	 * @param name
	 * @param value
	 * @see nuggets.IWriter#put(java.lang.String, java.lang.String)
	 */
	public void putRef(String name, String value) {
		    out.write(' ');
			out.write(REFERENCE);
			out.write(name);
			out.write('=');
			out.write('"');
			out.write(value);
			out.write('"');
	}

	/** Adds a reference to the object
	 * @param token
	 */
	public void addToken(String token) {
			if (open_tag) closeTag();
			else out.write(' ');
			out.write(token);
	}
	
	/** 
	 * @param text
	 * @see nuggets.IWriter#write(java.lang.String)
	 */
	public void write(String text)
	{
		if (text.length()>0) {
			closeTag();
			writeValue(text);
		}
	}
	
	/** 
	 * @param chars
	 * @param start
	 * @param len
	 * @see nuggets.IWriter#write(char[], int, int)
	 */
	public void write(char[] chars, int start, int len)
	{
		if (len>0) {
			closeTag();
			writeValue(chars, start, len);
		} 
	}
	
	/** 
	 * @param ba
	 * @see nuggets.IWriter#write(byte[])
	 */
	public void write(byte[] ba)
	{
		if (ba.length>0) {
			closeTag();
			Base64.encode(ba, out);
		} 
	}
	

	private void closeTag()  {
		if (open_tag) {
			out.write('>');
			open_tag = false;
		}
	}
	
	/**
	 * Must take care of non XML character, embeded markup and "" signs 
	 * @param b
	 */
	void writeIdentifier(final String value)  {
		int len = value.length();
		char c = value.charAt(0);
        if (c=='_') {
           out.write('_');
           out.write('_');  
        } else if (Character.isUnicodeIdentifierStart(c)) {
			out.write(c);
		} else {
			out.write('_');
			out.write(Integer.toHexString(c));
			out.write('_');
		}
		
		for (int i = 1; i < len; i++) {
			c = value.charAt(i);
            if (c=='_') {
               out.write('_');
               out.write('_');  
            } else if (c=='.' || Character.isUnicodeIdentifierPart(c)) {
				out.write(c);
    		} else {
    			out.write('_');
    			out.write(Integer.toHexString(c));
    			out.write('_');
    		}
		}
	}

	
	/**
	 * Must take care of non XML character, embeded markup and "" signs 
	 * @param b
	 */
	void writeAttributeValue(final String value)  {
		int len = value.length();
		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);
            if (c=='\\') {
               out.write('\\');
               out.write('\\');  
            } else if (isAttributeValueChar(c)) {
				out.write(c);
			} else {
				out.write('\\');
				out.write('u');
				String h=Integer.toHexString(c);
				for(int j=h.length(); j<4; j++) out.write('0');
				out.write(h);
			}
		}
	}

	/**
	 * Must take care of non XML character, embeded markup and "" signs 
	 * @param b
	 */
	void writeValue(final String value)  {
		int len = value.length();
		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);
            if (c=='\\') {
               out.write('\\');
               out.write('\\');  
            } else if (isValueChar(c)) {
				out.write(c);
			} else {
				out.write('\\');
				out.write('u');
				String h=Integer.toHexString(c);
				for(int j=h.length(); j<4; j++) out.write('0');
				out.write(h);
			}
		}
	}
	
	/** 
	 * @param chars
	 * @param start
	 * @param len
	 */
	void writeValue(final char[] chars, final int start, final int len) 
	{
		int end = start+len;
		for (int i = start; i < end; i++) {
			char c = chars[i];
            if (c=='\\') {
               out.write('\\');
               out.write('\\');  
            } else if (isValueChar(c)) {
				out.write(c);
			} else {
				out.write('\\');
				out.write('u');
				String h=Integer.toHexString(c);
				for(int j=h.length(); j<4; j++) out.write('0');
				out.write(h);
			}
		}
	}
	
	private static final boolean isValueChar(char c) {
		return ((c >= 0x20   && c < 0x7f) 
				|| (c >= 0xA0   && c < 0xD800)
				|| (c >= 0xE000 && c < 0xFDD0)) 
				&& !(c == '<' || c == '>' || c == '&');
	}
	
	private static final boolean isAttributeValueChar(char c) {
		return ((c >= 0x20   && c < 0x7f) 
				|| (c >= 0xA0   && c < 0xD800)
				|| (c >= 0xE000 && c < 0xFDD0)) 
				&& !(c == '<' || c == '>' || c == '&' || c == '\"' );
	}
	
// ---------------- prefabricated byte arrays

	/** <code>N_ROOT</code>: */
	private static final char[]	N_ROOT	= "<n:root xmlns:n=\"JADEX_XML_encoder\"  xmlns:r=\"reference_ids\"".toCharArray();
	

	/** <code>END_N_ROOT</code>: */
	private static final char[]	END_N_ROOT	= "\n</n:root>\n".toCharArray();
	

	/** <code>REFERENCE</code>: */
	static final char[]	REFERENCE	= "r:".toCharArray();
	
	/** <code>NUGGET_ID</code>: */
	static final char[]	NUGGET_ID	= "n:id".toCharArray();


}
