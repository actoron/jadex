package jadex.micro.tutorial;

/**
 *  Simple user profile struct.
 */
public class UserProfileD3
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The age. */
	protected int age;
	
	/** The gender. */
	protected boolean gender;
	
	/** The description. */
	protected String description;

	//-------- constructors --------

	/**
	 *  Create a new user profile.
	 */
	public UserProfileD3()
	{
		// Empty bean constrcutor.
	}

	/**
	 *  Create a new user profile.
	 */
	public UserProfileD3(String name, int age, boolean gender, String description)
	{
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.description = description;
	}

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the age.
	 *  @return The age.
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 *  Set the age.
	 *  @param age The age to set.
	 */
	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 *  Get the gender.
	 *  @return The gender.
	 */
	public boolean isGender()
	{
		return gender;
	}

	/**
	 *  Set the gender.
	 *  @param gender The gender to set.
	 */
	public void setGender(boolean gender)
	{
		this.gender = gender;
	}

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "UserProfileD3(name=" + name + ", age=" + age + ", gender="
			+ gender + ", description=" + description + ")";
	}
	
}
