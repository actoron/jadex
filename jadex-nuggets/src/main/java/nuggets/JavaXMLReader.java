/*
 * JavaXMLWriter.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 11, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;

import java.io.IOException;
import java.io.Reader;

import nuggets.util.Base64;
import nuggets.util.CharStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * JavaXMLWriter - writes a raw XML with java packages for ontology purpose.
 * This can be used only with java.
 * 
 * @author walczak
 * @since Jan 11, 2006
 */
public class JavaXMLReader implements IReader
{
	private XmlPullParser	xpp;
	
	private final CharStream cs = new CharStream();

	/** Initializes the Reader
	 * Constructor for JavaXMLReader.
	 */
	public JavaXMLReader()  {
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			xpp = factory.newPullParser();
		}
		catch(XmlPullParserException e)
		{
			throw new PersistenceException(e);
		}
	}
	
	/**
	 * @param rdr
	 * @see nuggets.IReader#start(java.io.Reader)
	 */
	public void start(Reader rdr)
	{
		try
		{
			xpp.setInput(rdr);
			int i;
			while((i = xpp.next())!=XmlPullParser.START_TAG) {
				if (i==XmlPullParser.END_DOCUMENT) throw new PersistenceException("Input ended befor the first element");
			}
			String name;
			if (!"n:root".equals(name = xpp.getName())) {
				throw new PersistenceException("Encountered wrong element: "+name);
			}
			attributes=readAttributes();
		}
		catch(XmlPullParserException e)
		{
			throw new PersistenceException(e);
		}
		catch(IOException e)
		{
			throw new PersistenceException(e);
		}
	}
	
	final Buffer buffer = new Buffer();
	
	final int[]	oAndL = new int[2];

	private String[]	attributes;

	/**
	 * @return a 
	 * @see nuggets.IReader#getText()
	 */
	public Buffer getText()
	{
		int i;
		try
		{
			while((i = xpp.next())!=XmlPullParser.TEXT) {
				if (i==XmlPullParser.END_DOCUMENT || i==XmlPullParser.END_TAG) {
					buffer.set(null, 0, 0);
					return buffer;
				}
			}
			buffer.set(xpp.getTextCharacters(oAndL), oAndL[0], oAndL[1]);
			return buffer;
		}
		catch(XmlPullParserException e)
		{
			throw new PersistenceException(e);
		}
		catch(IOException e)
		{
			throw new PersistenceException(e);
		}
	}
	
	/** 
	 * @return byte array from the text element encoded in Base64
	 * @see nuggets.IReader#getData()
	 */
	public byte[] getData()
	{
		int i;
		try
		{
			while((i = xpp.next())!=XmlPullParser.TEXT) {
				if (i==XmlPullParser.END_DOCUMENT) {
					return null;
				}
			}
			buffer.set(xpp.getTextCharacters(oAndL), oAndL[0], oAndL[1]);
			return Base64.decode(buffer.chars, buffer.start, buffer.len);
		}
		catch(XmlPullParserException e)
		{
			throw new PersistenceException(e);
		}
		catch(IOException e)
		{
			throw new PersistenceException(e);
		}
	}

	/**
	 * @return the name of the next element or null
	 * @see nuggets.IReader#nextElement()
	 */
	public String nextElement()
	{
		int i;
		try
		{
			while((i = xpp.next())!=XmlPullParser.START_TAG) {
				if (i==XmlPullParser.END_DOCUMENT) return null;
			}
			attributes=readAttributes();
			return decodeTag(xpp.getName());
		}
		catch(XmlPullParserException e)
		{
			throw new PersistenceException(e);
		}
		catch(IOException e)
		{
			throw new PersistenceException(e);
		}
	}

	

	/** 
	 * @return the attributes decoded
	 */
	private String[] readAttributes()
	{
		int count=xpp.getAttributeCount();
		String[] attr=new String[count];
		for(int i=0; i<count; i++) {	
			String a=xpp.getAttributeName(i);
			if (a.equals(NUGGET_ID)) continue;
			if (a.startsWith(REFERENCE)) attr[i]=a.substring(REFERENCE_LENGTH);
			else attr[i]=a;
		}
		return attr;
	}

	/** 
	 * @return the value of n:id
	 * @see nuggets.IReader#getID()
	 */
	public String getID()
	{
		return xpp.getAttributeValue(null, NUGGET_ID);
	}
	

	/** 
	 * @return the number of attributes
	 * @see nuggets.IReader#getAttributeCount()
	 */
	public int getAttributeCount()
	{
		return attributes.length;
	}
	

	/** 
	 * @param attribute
	 * @return the value of this attribute
	 * @see nuggets.IReader#getAttributeValue(java.lang.String)
	 */
	public String getAttributeValue(String attribute)
	{
		for(int i=0;i<attributes.length;i++) {
			if (attributes[i]==attribute||attribute.equals(attributes[i])) {
				return decodeText(xpp.getAttributeValue(i));
			}
		}
		return null;
	}

	/** 
	 * @param attribute
	 * @return the index of this attribute or -1
	 * @see nuggets.IReader#getAttributeIndex(java.lang.String)
	 */
	public int getAttributeIndex(String attribute)
	{
		for(int i=0;i<attributes.length;i++) {
			if (attributes[i]==attribute||attribute.equals(attributes[i])) {
				return i;
			}
		}
		return -1;
	}

	/** 
	 * @param i
	 * @return the name of attribute i or null if under this i is no attribute
	 * @see nuggets.IReader#getAttributeName(int)
	 */
	public String getAttributeName(int i)
	{
		return attributes[i];
	}

	/** 
	 * @param i
	 * @return true if this attribute starts with id:
	 * @see nuggets.IReader#isReferenceAttribute(int)
	 */
	public boolean isReferenceAttribute(int i)
	{
		String a = xpp.getAttributeName(i);
		return a!=null && a.startsWith(REFERENCE); 
	}
	
	/** 
	 * @param i
	 * @return the decoded value of this attribute 
	 * @see nuggets.IReader#getAttributeValue(int)
	 */
	public String getAttributeValue(int i)
	{
		return decodeText(xpp.getAttributeValue(i));
	}
	
	
	 /**
		 * It takes a name and chages all character not writtable as XML
		 * 
		 * @param name
		 * @return all characters not being a part of unicode identifier are
		 *         encoded into "\\uHHHH" pattern.
		 */
	String decodeTag(String name)
	{
		cs.reset();

		int len = name.length();
		loop: for(int i = 0; i < len; i++)
		{
			char c = name.charAt(i);
			if(c == '_' && i + 1 < len)
			{			
				if (name.charAt(i+1)!='_') { 
					for(int j = i + 1; j < len; j++)
						if(name.charAt(j) == '_')
						{
							cs.write((char)Integer.parseInt(name.substring(i + 1, j), 16));
							i = j;
							continue loop;
						}
				} else {
					i++;
				}
			}
			cs.write(c);
		}
		return cs.toString();
	}

	/**
	 * @param chars
	 * @param start
	 * @param len
	 * @return a string as decodec from the chars
	 * @see nuggets.IReader#decodeText(char[], int, int)
	 */
	public String decodeText(char[] chars, int start, int len)
	{
		char aChar;
		cs.reset();
		int end = start + len;

		for(int x = start; x < end;)
		{
			aChar = chars[x++];
			if(aChar == '\\')
			{
				aChar = chars[x++];
				if(aChar == 'u')
				{
					// Read the xxxx
					int value = 0;
					for(int i = 0; i < 4; i++)
					{
						aChar = chars[x++];
						switch(aChar)
						{
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + aChar + 10 - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + aChar + 10 - 'A';
								break;
							default:
								throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					cs.write((char)value);
				}
				else
				{
					// if(aChar == 't') aChar = '\t';
					// else if(aChar == 'r') aChar = '\r';
					// else if(aChar == 'n') aChar = '\n';
					// else if(aChar == 'f') aChar = '\f';
					cs.write(aChar);
				}
			}
			else cs.write(aChar);
		}
		return cs.toString();
	}
	
	/**
	 * @param string
	 * @return a string as decodec from the chars
	 * @see nuggets.IReader#decodeText(char[], int, int)
	 */
	public String decodeText(String string)
	{
		char aChar;
		cs.reset();
		int end = string.length();

		for(int x = 0; x < end;)
		{
			aChar = string.charAt(x++);
			if(aChar == '\\')
			{
				aChar = string.charAt(x++);
				if(aChar == 'u')
				{
					// Read the xxxx
					int value = 0;
					for(int i = 0; i < 4; i++)
					{
						aChar = string.charAt(x++);
						switch(aChar)
						{
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + aChar + 10 - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + aChar + 10 - 'A';
								break;
							default:
								throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					cs.write((char)value);
				}
				else
				{
					// if(aChar == 't') aChar = '\t';
					// else if(aChar == 'r') aChar = '\r';
					// else if(aChar == 'n') aChar = '\n';
					// else if(aChar == 'f') aChar = '\f';
					cs.write(aChar);
				}
			}
			else cs.write(aChar);
		}
		return cs.toString();
	}

	private static final String	NUGGET_ID	= new String(JavaXMLWriter.NUGGET_ID);

	private static final String	REFERENCE	= new String(JavaXMLWriter.REFERENCE);

	private static final int	REFERENCE_LENGTH	= REFERENCE.length();

}

/*
 * $Log$
 * Revision 1.6  2006/03/20 18:15:24  walczak
 * removed dependency on sax
 *
 * Revision 1.5  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.4  2006/02/15 10:42:31  walczak
 * removed StringBuffer from XMLReader,
 * removed "new" from XMLReadr,
 * made generated Delegates use set method,
 * Revision 1.3 2006/02/14 17:39:05 walczak new
 * version of nuggets Revision 1.2 2006/01/20 18:11:02 walczak
 * ------------------------ Revision 1.1 2006/01/18 13:59:47 walczak Introduced
 * the nuggets package.
 */