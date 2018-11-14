package jadex.xml.tutorial.jibx.example21;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IObjectObjectConverter;
import jadex.xml.IReturnValueCommand;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubObjectConverter;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanAccessInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;
import jadex.xml.stax.QName;


/**
 *  Main class to execute tutorial lesson c (taken from Jibx website).
 */
public class Main
{
	/**
	 *  Main method for using the xml reader/writer.
	 */
	public static void main(String[] args) throws Exception
	{
		Set typeinfos = new HashSet();
		
		typeinfos.add(new TypeInfo(new XMLInfo("name"), new ObjectInfo(Name.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("first-name", "firstName")),
			new AttributeInfo(new AccessInfo("last-name", "lastName"))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("entry"), new ObjectInfo(HashMap.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("key", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("customer", null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("directory"), new ObjectInfo(Directory.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("entry", null, null, null, 
			new BeanAccessInfo(Directory.class.getField("customerMap"),
			null, "customerMap", new IReturnValueCommand()
			{
				public Object execute(Object args)
				{
					return ((Map)args).get("key");
				}
			})),
			new SubObjectConverter(new IObjectObjectConverter()
			{
				public Object convertObject(Object val, IContext context)
				{
					return ((Map)val).get("customer");
				}
			}, null))
		})));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/jibx/example21/data0.xml", null);
		Object object1 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object 1: "+object1);

		
		typeinfos = new HashSet();
		
		String uri = "http://www.sosnoski.com";
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "name")}), new ObjectInfo(Name.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(uri, "first-name"), "firstName")),
			new AttributeInfo(new AccessInfo(new QName(uri, "last-name"), "lastName"))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "customer")}), new ObjectInfo(Customer.class)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "entry")}), new ObjectInfo(HashMap.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(uri, "key"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)))},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "customer"), null, null, null, new BeanAccessInfo(AccessInfo.THIS)))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "directory")}), new ObjectInfo(Directory.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri, "entry"), null, null, null, 
			new BeanAccessInfo(Directory.class.getField("customerMap"),
			null, "customerMap", new IReturnValueCommand()
			{
				public Object execute(Object args)
				{
					return ((Map)args).get("key");
				}
			})),
			new SubObjectConverter(new IObjectObjectConverter()
			{
				public Object convertObject(Object val, IContext context)
				{
					return ((Map)val).get("customer");
				}
			}, null))
		})));	
		
		xmlreader = new Reader(false, false, false, null);
		is = SUtil.getResource("jadex/xml/tutorial/jibx/example21/data1.xml", null);
		Object object2 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		System.out.println("Read object 2: "+object2);
	}
}
