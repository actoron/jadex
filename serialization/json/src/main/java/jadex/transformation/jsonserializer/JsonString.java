package jadex.transformation.jsonserializer;

/**
 *  Class representing a string containing JSON.
 *  This class can be used as parameter or return
 *  value in services to circumvent the conversion
 *  stage and directly accept or return raw JSON.
 *
 */
public class JsonString
{
	/**
	 *  The concrete JSON string.
	 */
	protected String json;
	
	/**
	 *  Creates the JsonString object.
	 *  @param json The JSON.
	 */
	public JsonString(String json)
	{
		this.json = json;
	}
	
	/**
	 *  Hash code based on the JSON.
	 */
	public int hashCode()
	{
		return json.hashCode();
	}
	
	/**
	 *  Matches based on the internal JSON string.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof JsonString)
		{
			((JsonString) obj).json.equals(json);
		}
		return false;
	}
	
	/**
	 *  Returns the actual JSON string.
	 *  @return The JSON string.
	 */
	public String toString()
	{
		return json;
	}
}
