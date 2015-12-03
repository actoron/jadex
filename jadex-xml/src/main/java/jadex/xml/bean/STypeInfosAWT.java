package jadex.xml.bean;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.stax.QName;

/**
 * Provides static Methods for generating TypeInfos that depend on AWT Classes.
 */
public class STypeInfosAWT
{
	protected static final Color TRANSPARENT_WHITE = new Color(255, 255, 255, 0);

	public static Set<TypeInfo> getWriterTypeInfos()
	{

		Set typeinfos = new HashSet();

		// java.util.Color
		IObjectStringConverter coconv = new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				return "" + ((Color) val).getRGB();
			}
		};
		TypeInfo ti_color = new TypeInfo(null, new ObjectInfo(Color.class), new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String) null,
				AccessInfo.THIS), new AttributeConverter(null, coconv))));
		typeinfos.add(ti_color);

		// java.awt.image.RenderedImage
		IObjectStringConverter imgconv = new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				try
				{
					byte[] buf = SGUI.imageToStandardBytes((Image) val, "image/png");
					return new String(Base64.encode(buf));
				} 
				catch (Exception e)
				{
					// todo: use context report
					throw new RuntimeException(e);
				}
			}
		};

		// java.lang.Class
		IObjectStringConverter oclconv = new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				String ret = SReflect.getClassName(val.getClass());
				return ret;
			}
		};

		// Image
		TypeInfo ti_image = new TypeInfo(new XMLInfo(new QName("typeinfo:java.awt.image", "Image")), new ObjectInfo(Image.class), new MappingInfo(null,
			new AttributeInfo[]{ 
			new AttributeInfo(new AccessInfo("imgdata", AccessInfo.THIS), new AttributeConverter(null, imgconv)),
			new AttributeInfo(new AccessInfo("data", null, AccessInfo.IGNORE_READWRITE)),
			new AttributeInfo(new AccessInfo("classname", AccessInfo.THIS), new AttributeConverter(null, oclconv)) }, null));
		typeinfos.add(ti_image);

		// java.awt.Rectangle
		TypeInfo ti_rect = new TypeInfo(null, new ObjectInfo(Rectangle.class), new MappingInfo(null, new AttributeInfo[]
		{ new AttributeInfo(new AccessInfo("x", null)), new AttributeInfo(new AccessInfo("y", null)), new AttributeInfo(new AccessInfo("width", null)),
				new AttributeInfo(new AccessInfo("height", null)) }, null));
		typeinfos.add(ti_rect);

		return typeinfos;
	}

	public static Set<TypeInfo> getReaderTypeInfos()
	{
		Set<TypeInfo> typeinfos = new HashSet<TypeInfo>();

		// java.util.Color
		IStringObjectConverter coconv = new IStringObjectConverter()
		{
			public Object convertString(String val, Object context)
			{
				return Color.decode(val);
			}
		};

		TypeInfo ti_color = new TypeInfo(new XMLInfo(new QName[]
		{ new QName(SXML.PROTOCOL_TYPEINFO + "java.awt", "Color") }), null, new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String) null,
				AccessInfo.THIS), new AttributeConverter(coconv, null))));
		typeinfos.add(ti_color);

		// Image
		TypeInfo ti_image = new TypeInfo(new XMLInfo(new QName[]
		{ new QName(SXML.PROTOCOL_TYPEINFO + "java.awt.image", "Image") }), new ObjectInfo(new IBeanObjectCreator()
		{
			public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
			{
				Image ret = null;
				String encdata = (String) rawattributes.get("imgdata");
				byte[] data = Base64.decode(encdata.getBytes());

				String classname = (String) rawattributes.get("classname");
				ret = SGUI.imageFromBytes(data, SReflect.findClass(classname, null, context.getClassLoader()));

				// if(classname.indexOf("Toolkit")!=-1)
				// {
				// Toolkit t = Toolkit.getDefaultToolkit();
				// ret = t.createImage(data);
				// }
				// else
				// {
				// ret = ImageIO.read(new ByteArrayInputStream(data));
				// }
				return ret;
			}
		}), new MappingInfo(null, new AttributeInfo[]
		{ new AttributeInfo(new AccessInfo("imgdata", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("classname", null, AccessInfo.IGNORE_READWRITE)) }));
		typeinfos.add(ti_image);

		// java.awt.Rectangle
		TypeInfo ti_rect = new TypeInfo(new XMLInfo(new QName[]
		{ new QName(SXML.PROTOCOL_TYPEINFO + "java.awt", "Rectangle") }), new ObjectInfo(new IBeanObjectCreator()
		{
			public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
			{
				int x = (int) Double.parseDouble((String) rawattributes.get("x"));
				int y = (int) Double.parseDouble((String) rawattributes.get("y"));
				int w = (int) Double.parseDouble((String) rawattributes.get("width"));
				int h = (int) Double.parseDouble((String) rawattributes.get("height"));
				return new Rectangle(x, y, w, h);
			}
		}), new MappingInfo(null, new AttributeInfo[]
		{ new AttributeInfo(new AccessInfo("x", null, AccessInfo.IGNORE_READWRITE)), new AttributeInfo(new AccessInfo("y", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("width", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("height", null, AccessInfo.IGNORE_READWRITE)), }));
		typeinfos.add(ti_rect);

		return typeinfos;
	}
}
