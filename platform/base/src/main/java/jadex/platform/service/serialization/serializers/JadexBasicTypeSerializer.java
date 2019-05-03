package jadex.platform.service.serialization.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.JsonReadContext;
import jadex.transformation.jsonserializer.processors.JsonWriteContext;

/**
 * 
 */
public class JadexBasicTypeSerializer implements ISerializer, IStringConverter
{
	//-------- constants --------
	
	/** The serializer id. */
	public static final int SERIALIZER_ID = 3;

	public static final String TYPE = IStringConverter.TYPE_BASIC;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	protected BasicTypeConverter converter;
	
	//-------- methods --------
	
	/**
	 *  Get the serializer id.
	 *  @return The serializer id.
	 */
	public int getSerializerId()
	{
		return SERIALIZER_ID;
	}
	
	 /** The basic type converter. */
//  public static BasicTypeConverter BASICCONVERTER;
  
	public JadexBasicTypeSerializer()
	{
		converter = new BasicTypeConverter();
  		converter.addConverter(IComponentIdentifier.class, new IStringObjectConverter()
		{
			public Object convertString(String val, Object context) throws Exception
			{
				return new ComponentIdentifier(val);
			}
		});
  		converter.addConverter(ClassInfo.class, new IStringObjectConverter()
		{
			public Object convertString(String val, Object context) throws Exception
			{
				return new ClassInfo(val);
			}
		});
	}
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs, Object usercontext)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Convert a string to an object.
	 *  @param val The string.
	 *  @param type The target type.
	 *  @param context The context.
	 *  @return The object.
	 */
	public Object convertString(String val, Class<?> type, ClassLoader cl, Object context)
	{
		try
		{
			return converter.convertString(val, type, context);
		}
		catch(Exception e)
		{
			return SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Convert an object to a string.
	 *  @param val The object.
	 *  @param type The encoding type.
	 *  @param context The context.
	 *  @return The object.
	 */
	public String convertObject(Object val, Class<?> type, ClassLoader cl, Object context)
	{
		try
		{
			return converter.convertObject(val, type, context);
		}
		catch(Exception e)
		{
			SUtil.throwUnchecked(e);
			return null;
		}
	}
	
	/**
	 *  Get the type of string that can be processed (xml, json, plain).
	 *  @return The object.
	 */
	public String getType()
	{
		return TYPE;
	}
	
	/**
	 *  Test if the type can be converted.
	 *  @param clazz The class.
	 *  @return True if can be converted.
	 */
	public boolean isSupportedType(Class<?> clazz)
	{
		return converter.isSupportedType(clazz);
	}
}