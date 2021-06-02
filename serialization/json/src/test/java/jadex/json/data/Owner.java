package jadex.json.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.JsonBeanProcessor;
import jadex.transformation.jsonserializer.processors.JsonMapProcessor;
import jadex.transformation.jsonserializer.processors.JsonToStringProcessor;

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
//		String str = "{\"__classname\":\"jadex.json.data.Transporter\",\"cars\":[{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\"}]}";
		
		Car car1 = new Car("fiat", 500, "white");
		Car car2 = new Car("vw", 80, "grey");
		Car car3 = new Car("tt", 888, "black");
		car3.setCar(car2); // car3
		car2.setCar(car1);
		car1.setCar(car3);
		
		List<Car> cars = new ArrayList<Car>();
		cars.add(car1);
		cars.add(car2);
		
		Transporter tr1 = new Transporter();
//		tr.setCars(cars);
		tr1.setCars(new Car[]{new Car("a", 700, "aa")});//, car2, car1, car2});
		Transporter tr2 = new Transporter();
		tr2.setCars(new Car[]{car1, car2, car1, car2});
		
		Map<Car, Car> map = new HashMap<Car, Car>();
		map.put(car1, car1);
		
//		Gson gson = new Gson();
//		System.out.println(gson.toJson(car3));   
		
		String str = JsonTraverser.objectToString(car3, null, false);
		System.out.println(str);

		str = JsonTraverser.objectToString(tr1, null, false);
		System.out.println(str);
	
		str = JsonTraverser.objectToString(tr2, null, false);
		System.out.println(str);
		
		Object o = JsonTraverser.objectFromString(str, null, null);
		System.out.println(o);
		System.out.println(car3.equals(o));
		
//		System.out.println(JsonTraverser.objectToString(map, null, false));
//		byte[] ar = JsonTraverser.objectToByteArray(car3, null);
		
//		Traverser traverser = new Traverser();
//		JsonWriteContext wr = new JsonWriteContext(false);
//		traverser.traverse(car3, null, procs, null, wr);
	
//		System.out.println(new String(ar));
		
		
		str = "[{\"peerId\":\"907a59db-0a02-4585-bd7e-3d4ae6ce5da3\",\"hostName\":\"IP 127.0.0.1\",\"connectDate\":{\"value\":1462275136476},\"awarenessInfo\":{\"delay\":-1,\"excludes\":[],\"sender\":{\"addresses\":[\"local-mtp://LarsNB_8b5\",\"tcp-mtp://2001:0:5ef5:79fb:38a0:4d7:3da1:e779:49208\",\"tcp-mtp://192.168.56.1:49208\",\"tcp-mtp://192.168.130.25:49208\",\"relay-http://localhost:8080/relay/\"],\"name\":\"LarsNB_8b5\"},\"includes\":[],\"state\":\"online\",\"properties\":{\"jadex.date\":\"1458821173797\",\"java.specification.version\":\"1.8\",\"java.vendor\":\"Oracle Corporation\",\"jadex.version\":\"3.0-DEVELOPMENT\",\"awamechanism\":\"RelayDiscoveryAgent\",\"os.arch\":\"amd64\",\"os.name\":\"Windows 8.1\",\"os.version\":\"6.3\"}},\"hostIP\":\"127.0.0.1\",\"dBId\":1,\"id\":\"LarsNB_8b5\",\"properties\":{\"__ref\":8}}]";
		o = JsonTraverser.objectFromString(str, null, null);
		System.out.println("read: "+o);
	}
}
