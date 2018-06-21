package jadex.wfms.acm.types;

public class Person
{
	public Person()
	{
	}
	
	public Person(String name, String orga, String email)
	{
		this.name = name;
		this.orga = orga;
		this.email = email;
	}
	
	public String name;
	
	public String orga;
	
	public String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrga() {
		return orga;
	}

	public void setOrga(String orga) {
		this.orga = orga;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
