package jadex.json.data;

import java.net.URI;

public class Owner
{
	protected String name;
	
	protected int age;
	
	protected URI uri;

	protected Class<?> clazz;
	
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
	 *  Get the age. 
	 *  @return The age
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 *  Set the age.
	 *  @param age The age to set
	 */
	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 *  Get the uri. 
	 *  @return The uri
	 */
	public URI getUri()
	{
		return uri;
	}

	/**
	 *  Set the uri.
	 *  @param uri The uri to set
	 */
	public void setUri(URI uri)
	{
		this.uri = uri;
	}
	
	/**
	 *  Get the clazz. 
	 *  @return The clazz
	 */
	public Class<?> getClazz()
	{
		return clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz to set
	 */
	public void setClazz(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	public String toString()
	{
		return "Owner [name=" + name + ", age=" + age + ", uri=" + uri + ", clazz=" + clazz + "]";
	}
}
