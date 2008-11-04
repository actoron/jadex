package jadex.bdi.planlib.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *  AIM The class provides utilities to connect to the AIM instant messaging
 *  network and send messages using TOC version 2.
 */
public class AIM
{

	/** The host address of the TOC server. */
	public String			tocHost		= "toc.oscar.aol.com";

	/** The port used to connect to the TOC server. */
	public int				tocPort		= 9898;

	/** The OSCAR authentication server. */
	public String			authHost	= "login.oscar.aol.com";

	/** The OSCAR authentication server's port. */
	public int				authPort	= 5190;

	/** What language to use. */
	public String			language	= "english";

	/** The version of the client. */
	public String			version		= "TIC:TOC";

	/** The string used to "roast" passwords. See the roastPassword
	 *  method for more info. */
	public String			roastString	= "Tic/Toc";

	/** The sequence number used for FLAP packets. */
	protected short			sequence;

	/** The connection to the TOC server. */
	protected Socket		connection;

	/** An InputStream to the connection. */
	protected InputStream	is;

	/** An OutputStream to the connection. */
	protected OutputStream	os;

	/** Screen name of current user. */
	protected String		id;

	private boolean	loggedin;

	private String	error;

	/** The ID number for a SIGNON packet(FLAP). */
	public static final int	SIGNON		= 1;

	/** The ID number for a DATA packet (flap). */
	public static final int	DATA		= 2;

	/**
	 * Constructor for AIM.
	 * 
	 * @param id
	 * @param password
	 */
	public AIM(String id, String password)
	{
		loggedin=login(id, password);
	}

	/**
	 * Log in to TOC
	 * 
	 * @param id The screen name to login with
	 * @param password The screen name's password
	 * @return true on success
	 */
	public boolean login(String id, String password)
	{
		this.id = id;
		try
		{
			connection = new Socket(tocHost, tocPort);
			is = connection.getInputStream();
			os = connection.getOutputStream();
			sendRaw("FLAPON\r\n\r\n");
			getFlap();
			sendFlapSignon();
			String command = "toc2_signon " + authHost + " " + authPort + " " + id + " "
					+ roastPassword(password) + " " + language + " \"" + this.version + "\" 160 "
					+ calculateCode(id, password);


			sendFlap(DATA, command);
			String str = getFlap();

			if(str.toUpperCase().startsWith("ERROR:"))
			{
				error=str;
				return false;
			}

			this.sendFlap(DATA, "toc_add_buddy " + this.id);
			this.sendFlap(DATA, "toc_init_done");
			this.sendFlap(DATA, "toc_set_caps 09461343-4C7F-11D1-8222-444553540000 09461348-4C7F-11D1-8222-444553540000");
			this.sendFlap(DATA, "toc_add_permit ");
			this.sendFlap(DATA, "toc_add_deny ");
			return true;
		}
		catch(IOException ioe)
		{
			error = ioe.getMessage();
		}
		return false;
	}

	/**
	 * Logout of toc and close the socket
	 */
	public void logout()
	{
		try
		{
			loggedin=false;
			connection.close();
		}
		catch(IOException e)
		{
			/* NOP */
		}
	}

	/**
	 * Called to roast the password. Passwords are roasted when sent to the
	 * host. This is done so they aren't sent in "clear text" over the wire,
	 * although they are still trivial to decode. Roasting is performed by first
	 * xoring each byte in the password with the equivalent modulo byte in the
	 * roasting string. The result is then converted to ascii hex, and prepended
	 * with "0x". So for example the password "password" roasts to
	 * "0x2408105c23001130"
	 * 
	 * @param str The password to roast
	 * @return The password roasted
	 */
	protected String roastPassword(String str)
	{
		byte xor[] = roastString.getBytes();
		int xorIndex = 0;
		String rtn = "0x";

		for(int i = 0; i < str.length(); i++)
		{
			String hex = Integer.toHexString(xor[xorIndex] ^ (int)str.charAt(i));
			if(hex.length() == 1) hex = "0" + hex;
			rtn += hex;
			xorIndex++;
			if(xorIndex == xor.length) xorIndex = 0;
		}
		return rtn;
	}

	/**
	 * Calculate a login security code from the user id and password.
	 * 
	 * @param uid The user id to encode
	 * @param pwd The password to encoude
	 * @return The code, which is used to login
	 */
	protected int calculateCode(String uid, String pwd)
	{
		int sn = uid.charAt(0) - 96;
		int pw = pwd.charAt(0) - 96;

		int a = sn * 7696 + 738816;
		int b = sn * 746512;
		int c = pw * a;

		return (c - a + b + 71665152);
	}

	/**
	 * Send a string over the socket as raw bytes
	 * 
	 * @param str The string to send
	 * @exception java.io.IOException
	 */
	protected void sendRaw(String str) throws IOException
	{
		os.write(str.getBytes());
	}

	/**
	 * Write a little endian word
	 * 
	 * @param word A word to write
	 * @exception java.io.IOException
	 */
	protected void writeWord(short word) throws IOException
	{
		os.write((byte)((word >> 8) & 0xff));
		os.write((byte)(word & 0xff));

	}

	/**
	 * Send a FLAP signon packet
	 * 
	 * @exception java.io.IOException
	 */
	protected void sendFlapSignon() throws IOException
	{
		int length = 8 + id.length();
		sequence++;
		os.write((byte)'*');
		os.write((byte)SIGNON);
		writeWord(sequence);
		writeWord((short)length);

		os.write(0);
		os.write(0);
		os.write(0);
		os.write(1);

		os.write(0);
		os.write(1);

		writeWord((short)id.length());
		os.write(id.getBytes());
		os.flush();

	}

	/**
	 * Send a FLAP packet
	 * 
	 * @param type The type DATA or SIGNON
	 * @param str The string message to send
	 * @exception java.io.IOException
	 */
	protected void sendFlap(int type, String str) throws IOException
	{
		int length = str.length() + 1;
		sequence++;
		os.write((byte)'*');
		os.write((byte)type);
		writeWord(sequence);
		writeWord((short)length);
		os.write(str.getBytes());
		os.write(0);
		os.flush();
	}

	/**
	 * Get a FLAP packet
	 * 
	 * @return The data as a string
	 * @exception java.io.IOException
	 */
	protected String getFlap() throws IOException
	{
		if(is.read() != '*') return null;
		is.read();
		is.read();
		is.read();
		int length = (is.read() * 0x100) + is.read();
		byte b[] = new byte[length];
		is.read(b);
		return new String(b);
	}

	/**
	 * Send a IM
	 * 
	 * @param to Screen name to send an IM to
	 * @param msg The instant message
	 */
	public void send(String to, String msg)
	{
		try
		{
			this.sendFlap(DATA, "toc_send_im " + normalize(to) + " \"" + encode(msg) + "\"");
		}
		catch(java.io.IOException e)
		{
			/* NOP */
		}
	}

	/**
	 * Called to normalize a screen name. This removes all spaces and converts
	 * the name to lower case.
	 * 
	 * @param name The screen name
	 * @return The normalized screen name
	 */

	protected String normalize(String name)
	{
		String rtn = "";
		for(int i = 0; i < name.length(); i++)
		{
			if(name.charAt(i) == ' ') continue;
			rtn += Character.toLowerCase(name.charAt(i));
		}

		return rtn;

	}

	/**
	 * Called to encode a message. Convert carige returns to <br>'s and put \'s
	 * infront of quotes, etc.
	 * 
	 * @param str The string to be encoded
	 * @return The string encoded
	 */
	protected String encode(String str)
	{
		String rtn = "";
		for(int i = 0; i < str.length(); i++)
		{
			switch(str.charAt(i))
			{
				case '\r':
					rtn += "<br>";
					break;
				case '{':
				case '}':
				case '\\':
				case '"':
					rtn += "\\";

				default:
					rtn += str.charAt(i);
			}
		}
		return rtn;

	}

	/** Getter for error
	 * @return Returns error.
	 */
	public String getError()
	{
		return this.error;
	}

	/** Getter for loggedin
	 * @return Returns loggedin.
	 */
	public boolean isLoggedin()
	{
		return this.loggedin;
	}
}