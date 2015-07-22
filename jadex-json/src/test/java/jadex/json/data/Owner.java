package jadex.json.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.processors.write.JsonBeanProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonMapProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonToStringProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

public class Owner
{
	protected String name;
	
	protected int age;
	
	protected URI uri;

	protected Class<?> clazz;
	
	public Owner()
	{
	}
	
	public Owner(String name, int age, URI uri, Class< ? > clazz)
	{
		this.name = name;
		this.age = age;
		this.uri = uri;
		this.clazz = clazz;
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
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		procs.add(new JsonToStringProcessor());
		procs.add(new JsonMapProcessor());
		procs.add(new JsonBeanProcessor());
		
//		String str = "{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\"}";
//		String str = "{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\",\"owner\":{\"name\":\"Hugo\",\"age\":55,\"url\":\"http://www.google.de\"}}";
//		String str = "{\"__classname\":\"data.Car\",\"type\":\"Fiat\",\"model\":500,\"color\":\"white\",\"owner\":{\"name\":\"Hugo\",\"age\":55,\"uri\":\"http://www.google.de\",\"clazz\":\"java.lang.String\"}}";
//		String str = "{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\",\"owner\":{\"name\":\"Hugo\",\"age\":55,\"url\":\"http://www.google.de\"}}";
		String str = "{\"__classname\":\"jadex.json.data.Transporter\",\"cars\":[{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\"}]}";
		
		Car car1 = new Car("fiat", 500, "white");
		Car car2 = new Car("vw", 80, "grey");
		
		List<Car> cars = new ArrayList<Car>();
		cars.add(car1);
		cars.add(car2);
		
		Transporter tr = new Transporter();
//		tr.setCars(cars);
		tr.setCars(new Car[]{car1, car2});
		
		Map<Car, Car> map = new HashMap<Car, Car>();
		map.put(car1, car1);
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(tr));   
		
		Traverser traverser = new Traverser();
		JsonWriteContext wr = new JsonWriteContext(false);
		traverser.traverse(map, null, procs, null, wr);
	
		System.out.println(wr.getString());
	}
}
