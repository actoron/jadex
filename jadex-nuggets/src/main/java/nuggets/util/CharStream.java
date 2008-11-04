package nuggets.util;

import java.io.IOException;
import java.io.Writer;


/*
 * CharStream.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Feb 2, 2006.  
 * Last revision $Revision: 4191 $ by:
 * $Author: walczak $ on $Date: 2006-04-27 09:24:34 +0200 (Do, 27 Apr 2006) $.
 */


/**
 * This class implements a character stream. The data can
 * be retrieved using toCharArray() and toString() and with writeTo(). It is not synchronized.
 */
public class CharStream implements ICharStream
{
	/**
	 * The buffer where data is stored.
	 */
	protected char	buffer[];

	/**
	 * The number of chars in the buffer.
	 */
	protected int	count;

	/**
	 * Creates a new CharStream.
	 */
	public CharStream()
	{
		this(32);
	}

	/**
	 * Creates a new CharStream with the specified initial size.
	 * 
	 * @param initialSize an int specifying the initial buffer size.
	 */
	public CharStream(int initialSize)
	{
		buffer = new char[initialSize];
	}

	/**
	 * Writes a character to the buffer.
	 * 
	 * @param c
	 */
	public void write(int c)
	{
		assureCapacity(count + 1);
		buffer[count++] = (char)c;
	}

	/**
	 * Writes a character to the buffer
	 * 
	 * @param c
	 */
	public void write(char c)
	{
		assureCapacity(count + 1);
		buffer[count++] = c;
	}

	/**
	 * @param string
	 */
	public void write(String string)
	{
		int len = string.length();
		assureCapacity(count + len);
		string.getChars(0, len, buffer, count);
		count += len;
	}

	/**
	 * Write characters to the buffer
	 * 
	 * @param chars
	 */
	public void write(char[] chars)
	{
		write(chars, 0, chars.length);
	}

	/**
	 * Writes characters to the buffer.
	 * 
	 * @param c the data to be written
	 * @param off the start offset in the data
	 * @param len the number of chars that are written
	 */
	public void write(char c[], int off, int len)
	{
		if(len == 0) return;
		assureCapacity(count + len);
		System.arraycopy(c, off, buffer, count, len);
		count += len;
	}

	/**
	 * Write a portion of a string to the buffer.
	 * 
	 * @param str String to be written from
	 * @param off Offset from which to start reading characters
	 * @param len Number of characters to be written
	 */
	public void write(String str, int off, int len)
	{
		assureCapacity(count + len);
		str.getChars(off, off + len, buffer, count);
		count += len;
	}

	private final void assureCapacity(int cap)
	{
		if(cap > buffer.length)
		{
			int new_length = buffer.length << 1;
			if(new_length < cap) new_length = cap;
			char newbuf[] = new char[new_length];
			System.arraycopy(buffer, 0, newbuf, 0, count);
			buffer = newbuf;
		}
	}


	/**
	 * Writes the contents of the buffer to another character stream.
	 * 
	 * @param out the output stream to write to
	 * @throws IOException If an I/O error occurs.
	 */
	public void writeTo(Writer out) throws IOException
	{
		out.write(buffer, 0, count);
		out.flush();
	}

	/**
	 * Resets the buffer.
	 */
	public void reset()
	{
		count = 0;
	}

	/**
	 * @return chars copied from the input data.
	 */
	public char[] toCharArray()
	{
		char newbuf[] = new char[count];
		System.arraycopy(buffer, 0, newbuf, 0, count);
		return newbuf;
	}

	/**
	 * @return the size of the buffer.
	 */
	public int size()
	{
		return count;
	}

	/**
	 * @return the string as defined by input.
	 */
	public String toString()
	{
		return new String(buffer, 0, count);
	}

	/**
	 * NOP
	 */
	public void flush()
	{ /*NOP*/ }

	/**
	 * NOP
	 */
	public void close()
	{ /*NOP*/ }


}


/*
 * $Log$
 * Revision 1.3  2006/04/27 07:24:34  walczak
 * ---------------
 *
 * Revision 1.2  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.1  2006/02/14 17:39:05  walczak
 * new version of nuggets
 *
 */