package jadex.transformation.jsonserializer.processors.write;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.IRootObjectContext;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonWriteContext implements IRootObjectContext
{
	protected StringBuffer buffer = new StringBuffer();
	
	protected boolean writeclass = true;
	
	protected boolean writeid = true;
	
	protected Map<Class<?>, Set<String>> excludes;
	
	protected int objectcnt = 0;
	
	protected Map<Object, Integer> knownobjects = new IdentityHashMap<Object, Integer>();
	
	protected Object rootobject;
	
	protected Object currentinputobject;
	
	protected Object usercontext;
	
	/**
	 *  Get the rootobject.
	 *  @return the rootobject.
	 */
	public Object getRootObject()
	{
		return rootobject;
	}
	
	/**
	 *  Create a new write context.
	 */
	public JsonWriteContext(boolean writeclass)
	{
		this.writeclass = writeclass;
	}
	
	/**
	 *  Create a new write context.
	 */
	public JsonWriteContext(boolean writeclass, Map<Class<?>, Set<String>> excludes)
	{
		this.writeclass = writeclass;
		this.excludes = excludes;
	}
	
	/**
	 *  Create a new write context.
	 */
	public JsonWriteContext(boolean writeclass, boolean writeid, Map<Class<?>, Set<String>> excludes)
	{
		this.writeclass = writeclass;
		this.writeid = writeid;
		this.excludes = excludes;
	}

	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext write(String str)
	{
		buffer.append(str);
		return this;
	}
	
	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext writeString(String str)
	{
		buffer.append(encodeJsonString(str));
		return this;
	}
	
	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext writeNameString(String name, String str)
	{
		buffer.append(encodeJsonString(name));
		buffer.append(":");
		buffer.append(encodeJsonString(str));
		return this;
	}
	
	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext writeNameValue(String name, Object val)
	{
		buffer.append(encodeJsonString(name));
		buffer.append(":");
		buffer.append(val);
		return this;
	}
	
	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext writeNameValue(String name, int val)
	{
		buffer.append(encodeJsonString(name));
		buffer.append(":");
		buffer.append(val);
		return this;
	}
	
	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext writeNameValue(String name, long val)
	{
		buffer.append(encodeJsonString(name));
		buffer.append(":");
		buffer.append(val);
		return this;
	}
	
	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext writeNameValue(String name, boolean val)
	{
		buffer.append(encodeJsonString(name));
		buffer.append(":");
		buffer.append(val);
		return this;
	}
	
	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext writeNameValue(String name, Class<?> val)
	{
		buffer.append(encodeJsonString(name));
		buffer.append(":");
		buffer.append("\"").append(SReflect.getClassName(val)).append("\"");
		return this;
	}
	
	/**
	 *  Write the classname.
	 *  @param object
	 */
	public void writeClass(Class<?> clazz)
	{
		write("\"").write(JsonTraverser.CLASSNAME_MARKER).write("\"");
		write(":");
//		write("\"").write(SReflect.getClassName(clazz)).write("\"");
		write("\"").write(STransformation.registerClass(clazz)).write("\"");
	}
	
	/**
	 *  Write the classname.
	 *  @param object
	 */
	public void writeId()
	{
		write("\"").write(JsonTraverser.ID_MARKER).write("\"");
		write(":");
		write(""+(objectcnt-1));
//		write("\"").write(""+(objectcnt-1)).write("\"");
	}
	
	/**
	 * 
	 */
	public String getString()
	{
//		Charset.forName("UTF-8").encode(buffer.toString()).array();

		return buffer.toString();
	}

	/**
	 *  Get the writeclass. 
	 *  @return The writeclass
	 */
	public boolean isWriteClass()
	{
		return writeclass;
	}
	
	/**
	 *  Get the writeid. 
	 *  @return The writeid
	 */
	public boolean isWriteId() 
	{
		return writeid;
	}
	
	/**
	 *  Returns the user context.
	 *  @return The user context.
	 */
	public Object getUserContext()
	{
		return usercontext;
	}
	
	/**
	 *  Sets the user context.
	 *  @param usercontext The user context.
	 */
	public void setUserContext(Object usercontext)
	{
		this.usercontext = usercontext;
	}

	/**
	 * 
	 */
	public void addObject(Object obj)
	{
		if (rootobject == null)
			rootobject = obj;
		knownobjects.put(obj, new Integer(objectcnt++));
//		traversed.put(obj, new Integer(objectcnt++));
//		System.out.println("obs: "+traversed);
	}
	
	public Integer getObjectId(Object object)
	{
		return knownobjects.get(object);
	}
	
	/**
	 * 
	 */
	public void incObjectCount()
	{
		objectcnt++;
	}
	
	/**
	 *  Test if a property should be excluded from serialization.
	 */
	public boolean isPropertyExcluded(Class<?> clazz, String name)
	{
		boolean ret = false;
		if(excludes!=null)
		{
			Set<String> exs = excludes.get(clazz);
			ret = exs!=null && exs.contains(name);
		}
		return ret;
	}
	
	
	
	/**
	 * @return the currentinputobject
	 */
	public Object getCurrentInputObject()
	{
		return currentinputobject;
	}

	/**
	 *  Sets the currentinputobject.
	 *  @param currentinputobject The currentinputobject to set
	 */
	public void setCurrentInputObject(Object currentinputobject)
	{
		this.currentinputobject = currentinputobject;
	}

	/**
	 * 
	 */
	public static String encodeJsonString(String string) 
	{
		if(string == null || string.length() == 0)
			return "\"\"";

		char c = 0;
		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;

		sb.append('"');
		for(i = 0; i < len; i += 1) 
		{
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				// if (b == '<') {
				sb.append('\\');
				// }
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if(c < ' ') 
				{
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} 
				else 
				{
					sb.append(c);
				}
			}
		}
		
		sb.append('"');
		return sb.toString();
	}
	
	/**
	 * Try.style monad for stateful write to the write context, handling first writes specially.
	 *
	 */
	public static class TryWrite
	{
		/** First write state. */
		protected boolean first = true;
		
		/** Write only first attempt. */
		protected boolean onlyfirst = false;
		
		/** Wrute context. */
		protected JsonWriteContext context;
		
		/**
		 *  Initialized the monad.
		 *  
		 *  @param context Write context.
		 */
		public TryWrite(JsonWriteContext context)
		{
			this(context, false);
		}
		
		/**
		 *  Initialized the monad.
		 *  
		 *  @param context Write context.
		 *  @param onlyfirst Write only on first write instead of every write except the first.
		 */
		public TryWrite(JsonWriteContext context, boolean onlyfirst)
		{
			this.context = context;
			this.onlyfirst = onlyfirst;
		}
		
		/**
		 *  Write or not depending on state.
		 *  
		 *  @param value Value to write.
		 *  @return The context for convenience.
		 */
		public JsonWriteContext write(String value)
		{
			if (first == onlyfirst)
			{
				context.write(value);
			}
			first = false;
			return context;
		}
	}
}
