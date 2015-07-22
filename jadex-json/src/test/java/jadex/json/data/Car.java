package jadex.json.data;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.read.JsonArrayProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonBeanProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonClassProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonCollectionProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonMapProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonPrimitiveProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonReadContext;
import jadex.transformation.jsonserializer.processors.read.JsonURIProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonURLProcessor;

/**
 * 
 */
public class Car
{
	protected String type;
	
	protected int model;
		
	protected String color;
	
	protected Owner owner;

	public Car()
	{
	}
	
	public Car(String type, int model, String color)
	{
		this.type = type;
		this.model = model;
		this.color = color;
	}

	/**
	 *  Get the type. 
	 *  @return The type
	 */ 
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the model. 
	 *  @return The model
	 */
	public int getModel()
	{
		return model;
	}

	/**
	 *  Set the model.
	 *  @param model The model to set
	 */
	public void setModel(int model)
	{
		this.model = model;
	}

	/**
	 *  Get the color. 
	 *  @return The color
	 */
	public String getColor()
	{
		return color;
	}

	/**
	 *  Set the color.
	 *  @param color The color to set
	 */
	public void setColor(String color)
	{
		this.color = color;
	}
	
	/**
	 *  Get the owner. 
	 *  @return The owner
	 */
	public Owner getOwner()
	{
		return owner;
	}

	/**
	 *  Set the owner.
	 *  @param owner The owner to set
	 */
	public void setOwner(Owner owner)
	{
		this.owner = owner;
	}

//	public String toString() 
//	{
//		return "Car [type=" + type + ", model=" + model + ", color=" + color + ", owner=" + owner + "]";
//	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		procs.add(new JsonCollectionProcessor());
		procs.add(new JsonArrayProcessor());
		procs.add(new JsonURIProcessor());
		procs.add(new JsonURLProcessor());
		procs.add(new JsonClassProcessor());
		procs.add(new JsonMapProcessor());
		procs.add(new JsonBeanProcessor());
		procs.add(new JsonPrimitiveProcessor());
		
//		String str = "{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\"}";
//		String str = "{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\",\"owner\":{\"name\":\"Hugo\",\"age\":55,\"url\":\"http://www.google.de\"}}";
//		String str = "{\"__classname\":\"data.Car\",\"type\":\"Fiat\",\"model\":500,\"color\":\"white\",\"owner\":{\"name\":\"Hugo\",\"age\":55,\"uri\":\"http://www.google.de\",\"clazz\":\"java.lang.String\"}}";
//		String str = "{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\",\"owner\":{\"name\":\"Hugo\",\"age\":55,\"url\":\"http://www.google.de\"}}";
		String str = "{\"__classname\":\"jadex.json.data.Transporter\",\"cars\":[{\"type\":\"Fiat\",\"model\":500,\"color\":\"white\"}]}";
		
		JsonValue value = Json.parse(str);
		JsonTraverser traverser = new JsonTraverser();
		Object ret = traverser.traverse(value, null, procs, null, new JsonReadContext());
	
		System.out.println(str);
		System.out.println(ret);
	}
}


//ENCODER_HANDLERS = new ArrayList<ITraverseProcessor>();
//ENCODER_HANDLERS.add(new LegacyNumberCodec());
//ENCODER_HANDLERS.add(new StringCodec());
//ENCODER_HANDLERS.add(new LegacyArrayCodec());


//ENCODER_HANDLERS.add(new EnumerationCodec());
//ENCODER_HANDLERS.add(new MultiCollectionCodec());
//ENCODER_HANDLERS.add(new LRUCodec());

//if(!SReflect.isAndroid())
//{
//	ENCODER_HANDLERS.add(new ColorCodec());
//	ENCODER_HANDLERS.add(new ImageCodec());
//	ENCODER_HANDLERS.add(new RectangleCodec());
//}


//ENCODER_HANDLERS.add(new TupleCodec());
//ENCODER_HANDLERS.add(new DateCodec());
//ENCODER_HANDLERS.add(new CalendarCodec());
//ENCODER_HANDLERS.add(new InetAddressCodec());
//ENCODER_HANDLERS.add(new LoggingLevelCodec());
//ENCODER_HANDLERS.add(new LogRecordCodec());
//ENCODER_HANDLERS.add(new EnumCodec());
//ENCODER_HANDLERS.add(new UUIDCodec());
//ENCODER_HANDLERS.add(new TimestampCodec());
//ENCODER_HANDLERS.add(new CertificateCodec());
//ENCODER_HANDLERS.add(new StackTraceElementCodec());
//ENCODER_HANDLERS.add(new ThrowableCodec());


//ENCODER_HANDLERS.add(new CollectionCodec());
//ENCODER_HANDLERS.add(new ClassCodec());
//ENCODER_HANDLERS.add(new MapCodec());
//ENCODER_HANDLERS.add(new URLCodec());
//ENCODER_HANDLERS.add(new URICodec());
//ENCODER_HANDLERS.add(new BeanCodec());


