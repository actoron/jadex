package org.activecomponents.shortmessages;

import jadex.commons.SUtil;

/**
 *  The user struct.
 */
public class User
{
	/** The name. */
	protected String name;
	
	/** The email. */
	protected String email;
	
	/** The pass. */
	protected String password;
	
	/** The online state. */
	protected boolean online;
	
	/**
	 *  Create a new user.
	 *  @param name The name.
	 *  @param email The email.
	 *  @param password The pass.
	 */
	public User()
	{
	}
	
	/**
	 *  Create a new user.
	 *  @param name The name.
	 *  @param email The email.
	 *  @param password The pass.
	 */
	public User(String name, String email, String password)
	{
		this.name = name;
		this.email = email;
		this.password = password;
	}

	/**
	 *  Get the name.
	 *  @return The name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the email.
	 *  @return The email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 *  Set the email.
	 *  @param email The email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 *  Get the password.
	 *  @return The password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 *  Set the password.
	 *  @param password The password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 *  Get the online.
	 *  @return The online
	 */
	public boolean isOnline()
	{
		return online;
	}

	/**
	 *  Set the online.
	 *  @param online The online to set
	 */
	public void setOnline(boolean online)
	{
		this.online = online;
	}

	/**
	 *  Get the hashcode.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
//		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	/**
	 *  Test if equal.
	 *  @param obj The The other object.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		
		if(obj instanceof User)
		{
			User u = (User)obj;
			ret = SUtil.equals(u.getEmail(), getEmail());// && SUtil.equals(u.getPassword(), getPassword());
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "User(name=" + name + ", email=" + email+")";
	}
}
