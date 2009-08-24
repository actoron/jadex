package jadex.adapter.base.envsupport;

import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector3Double;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Ellipse;
import jadex.adapter.base.envsupport.observer.graphics.drawable.IDrawable;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Rectangle;
import jadex.adapter.base.envsupport.observer.graphics.drawable.RegularPolygon;
import jadex.adapter.base.envsupport.observer.graphics.drawable.RotatingPrimitive;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Text;
import jadex.adapter.base.envsupport.observer.graphics.drawable.TexturedRectangle;
import jadex.adapter.base.envsupport.observer.graphics.drawable.Triangle;
import jadex.adapter.base.envsupport.observer.graphics.layer.ColorLayer;
import jadex.adapter.base.envsupport.observer.graphics.layer.GridLayer;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;
import jadex.adapter.base.envsupport.observer.graphics.layer.TiledLayer;
import jadex.adapter.base.envsupport.observer.perspective.IPerspective;
import jadex.adapter.base.envsupport.observer.perspective.Perspective2D;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.StyleSheet;
import jadex.commons.xml.QName;

/**
 *  Java representation of environment space type for xml description.
 */
public class MEnvSpaceType	extends MSpaceType
{
	//-------- constants --------
	
	/** The border object placement. */
	public static final String	OBJECTPLACEMENT_BORDER	= "border";
	
	/** The center object placement. */
	public static final String	OBJECTPLACEMENT_CENTER	= "center";
	
	//-------- attributes --------
	
	/** The properties. */
	protected Map properties;
	
	//-------- methods --------
	
	/**
	 *  Add a property.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public void addProperty(String key, Object value)
	{
		if(properties==null)
			properties = new MultiCollection();
		properties.put(key, value);
	}
	
	/**
	 *  Get a property.
	 *  @param key The key.
	 *  @return The value.
	 */
	public List getPropertyList(String key)
	{
		return properties!=null? (List)properties.get(key):  null;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return properties;
	}

//	/**
//	 *  Get a string representation of this AGR space type.
//	 *  @return A string representation of this AGR space type.
//	 */
//	public String	toString()
//	{
//		StringBuffer	sbuf	= new StringBuffer();
//		sbuf.append(SReflect.getInnerClassName(getClass()));
//		sbuf.append("(name=");
//		sbuf.append(getName());
//		sbuf.append(", dimensions=");
//		sbuf.append(getDimensions());
//		sbuf.append(", agent action types=");
//		sbuf.append(getMEnvAgentActionTypes());
//		sbuf.append(", space action types=");
//		sbuf.append(getMEnvSpaceActionTypes());
//		sbuf.append(", class=");
//		sbuf.append(getClazz());
//		sbuf.append(")");
//		return sbuf.toString();
//	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		ITypeConverter typeconv = new ClassConverter();
		ITypeConverter colorconv = new ColorConverter();
		ITypeConverter tcolorconv = new TolerantColorConverter();
		ITypeConverter expconv = new ExpressionConverter();
//		ITypeConverter tdoubleconv = new TolerantDoubleTypeConverter();
		ITypeConverter tintconv = new TolerantIntegerTypeConverter();
		ITypeConverter nameconv = new NameAttributeToObjectConverter();
		
		String uri =  "http://jadex.sourceforge.net/jadex-envspace"; 
		
		TypeInfo ti_po = new TypeInfo(null, "abstract_propertyobject", MultiCollection.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "property"), "properties", null, null, null, ""))
			}
		);
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "objecttype")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo(new QName("name"), "name", null, null, null, "")}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "envspacetype")}, MEnvSpaceType.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, "property"),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, "property"),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, "property"),
			new BeanAttributeInfo("depth", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, "property"),
			new BeanAttributeInfo("border", null, null, null, null, "property"),
			new BeanAttributeInfo("neighborhood", null, null, null, null, "property")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "property"), "properties", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "objecttype"), "objecttypes", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "avatarmapping"), "avatarmappings", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "actiontype"), "actiontypes", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "processtype"), "processtypes", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "tasktype"), "tasktypes", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "perceptgenerator"), "perceptgenerators", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "perceptprocessor"), "perceptprocessors", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "view"), "views", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "spaceexecutor"), null, null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "perspective"), "perspectives", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "percepttype"), "percepttypes", null, null, null, "property"))
		}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "avatarmapping")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("agenttype", null, null, null, null, ""),
			new BeanAttributeInfo("objecttype", null, null, null, null, ""),
			new BeanAttributeInfo("createavatar", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("createagent", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("killavatar", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("killagent", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "")
			}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "percepttype")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("objecttype", "objecttypes", null, null, null, ""),
			new BeanAttributeInfo("agenttype", "agenttypes", null, null, null, "")
			}, null, null,
			new SubobjectInfo[]{
//			new SubobjectInfo("percepttype/objecttypes/objecttype", new BeanAttributeInfo("objecttype", "objecttypes", null, "")),
//			new SubobjectInfo("percepttype/agenttypes/agenttype", new BeanAttributeInfo("percepttype", "agenttypes", new ITypeConverter()
			new SubobjectInfo(new QName[]{new QName(uri, "objecttypes")}, new BeanAttributeInfo(new QName(uri, "objecttype"), "objecttypes", null, null, null, "")),
			new SubobjectInfo(new QName[]{new QName(uri, "agenttypes")}, new BeanAttributeInfo(new QName(uri, "agenttype"), "agenttypes", null, nameconv, null, ""))
			}));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "actiontype")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("name", null, null, null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "processtype")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("name", null, null, null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "tasktype")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("name", null, null, null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri,"perceptgenerator")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("name", null, null, null, null, "")
			}, null));

		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri,"perceptprocessor")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{	
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("agenttype", null, null, null, null, "")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "percepttype"), "percepttypes", null, nameconv, null, "")),
			}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri,"perceptprocessor"), new QName(uri, "percepttype")}, HashMap.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "view")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("objecttype", null, null, null, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Map sourceview = (Map)args.get("sourceview");
					IEnvironmentSpace space = (IEnvironmentSpace)args.get("space");
					IDataView ret = (IDataView)((Class)MEnvSpaceInstance.getProperty(sourceview, "clazz")).newInstance();
					
					Map	props	= null;
					List lprops = (List)sourceview.get("properties");
					if(lprops!=null)
					{
						SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
						fetcher.setValues(args);
						props = MEnvSpaceInstance.convertProperties(lprops, fetcher);
					}
					
					ret.init(space, props);
					return ret;
				}
			}),
			}, null));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "perspective")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),		
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("opengl", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("invertxaxis", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE),
			new BeanAttributeInfo("invertyaxis", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.TRUE),
			new BeanAttributeInfo("objectplacement", null, null, BasicTypeConverter.STRING_CONVERTER, null, "", OBJECTPLACEMENT_BORDER),
			new BeanAttributeInfo("zoomlimit", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("bgcolor", null, null, colorconv, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IValueFetcher fetcher = (IValueFetcher)args.get("fetcher");
					args = (Map)args.get("object");
					
					IPerspective ret = (IPerspective)((Class)MEnvSpaceInstance.getProperty(args, "clazz")).newInstance();
					boolean opengl = MEnvSpaceInstance.getProperty(args, "opengl")!=null? 
						((Boolean)MEnvSpaceInstance.getProperty(args, "opengl")).booleanValue(): true;
					ret.setOpenGl(opengl);
//					String name = (String)MEnvSpaceInstance.getProperty(args, "name");
//					System.out.println("Perspective: "+name+" using opengl="+opengl);
					
					// Hack!!!
					if(ret instanceof Perspective2D)
					{
						Perspective2D pers = (Perspective2D)ret;
						Boolean invertx = (Boolean)MEnvSpaceInstance.getProperty(args, "invertxaxis");
						pers.setInvertYAxis(invertx.booleanValue());
						Boolean inverty = (Boolean)MEnvSpaceInstance.getProperty(args, "invertyaxis");
						pers.setInvertYAxis(inverty.booleanValue());
						
						pers.setBackground((Color)MEnvSpaceInstance.getProperty(args, "bgcolor"));
						
						String	placement	= (String)MEnvSpaceInstance.getProperty(args, "objectplacement");
						if(OBJECTPLACEMENT_CENTER.equals(placement))
							pers.setObjectShift(new Vector2Double(0.5));
						
						Double zoomlimit = (Double)MEnvSpaceInstance.getProperty(args, "zoomlimit");
						if(zoomlimit != null)
							pers.setZoomLimit(zoomlimit.doubleValue());
					}
					
					List drawables = (List)args.get("drawables");
					if(drawables!=null)
					{
						for(int k=0; k<drawables.size(); k++)
						{
							Map sourcedrawable = (Map)drawables.get(k);
							Map tmp = new HashMap();
							tmp.put("fetcher", fetcher);
							tmp.put("object", sourcedrawable);
							ret.addVisual(MEnvSpaceInstance.getProperty(sourcedrawable, "objecttype"), 
								((IObjectCreator)MEnvSpaceInstance.getProperty(sourcedrawable, "creator")).createObject(tmp));
						}
					}
					
					List prelayers = (List)args.get("prelayers");
					if(prelayers!=null)
					{
						List targetprelayers = new ArrayList();
						for(int k=0; k<prelayers.size(); k++)
						{
							Map layer = (Map)prelayers.get(k);
//							System.out.println("prelayer: "+layer);
							targetprelayers.add(((IObjectCreator)MEnvSpaceInstance.getProperty(layer, "creator")).createObject(layer));
						}
						((Perspective2D) ret).setPrelayers((ILayer[]) targetprelayers.toArray(new ILayer[0]));
					}
					
					List postlayers = (List)args.get("postlayers");
					if(postlayers!=null)
					{
						List targetpostlayers = new ArrayList();
						for(int k=0; k<postlayers.size(); k++)
						{
							Map layer = (Map)postlayers.get(k);
//							System.out.println("postlayer: "+layer);
							targetpostlayers.add(((IObjectCreator)MEnvSpaceInstance.getProperty(layer, "creator")).createObject(layer));
						}
						((Perspective2D) ret).setPostlayers((ILayer[]) targetpostlayers.toArray(new ILayer[0]));
					}
					
					return ret;
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawable"), "drawables", null, null, null, "")),
			new SubobjectInfo(new QName[]{new QName(uri, "prelayers")}, new BeanAttributeInfo(new QName(uri, "gridlayer"), "prelayers", null, null, null, "")),
			new SubobjectInfo(new QName[]{new QName(uri, "prelayers")}, new BeanAttributeInfo(new QName(uri, "tiledlayer"), "prelayers", null, null, null, "")),
			new SubobjectInfo(new QName[]{new QName(uri, "prelayers")}, new BeanAttributeInfo(new QName(uri, "colorlayer"), "prelayers", null, null, null, "")),
			new SubobjectInfo(new QName[]{new QName(uri, "postlayers")}, new BeanAttributeInfo(new QName(uri, "gridlayer"), "postlayers", null, null, null, "")),
			new SubobjectInfo(new QName[]{new QName(uri, "postlayers")}, new BeanAttributeInfo(new QName(uri, "tiledlayer"), "postlayers", null, null, null, "")),
			new SubobjectInfo(new QName[]{new QName(uri, "postlayers")}, new BeanAttributeInfo(new QName(uri, "colorlayer"), "postlayers", null, null, null, ""))
			}));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "drawable")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("objecttype", null, null, null, null, ""),
			new BeanAttributeInfo("x", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("y", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatex", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatey", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatez", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("position", null, null, null, null, ""),
			new BeanAttributeInfo("rotation", null, null, null, null, ""),
			new BeanAttributeInfo("size", null, null, null, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IValueFetcher fetcher = (IValueFetcher)args.get("fetcher");
					args = (Map)args.get("object");
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					DrawableCombiner ret = new DrawableCombiner(position, rotation, size);
					
					List parts = (List)args.get("parts");
					if(parts!=null)
					{
						for(int l=0; l<parts.size(); l++)
						{
							Map sourcepart = (Map)parts.get(l);
							int layer = MEnvSpaceInstance.getProperty(sourcepart, "layer")!=null? ((Integer)MEnvSpaceInstance.getProperty(sourcepart, "layer")).intValue(): 0;
							ret.addDrawable((IDrawable)((IObjectCreator)MEnvSpaceInstance.getProperty(sourcepart, "creator")).createObject(sourcepart), layer);
						}
					}
					
					List props = (List)args.get("properties");
					MEnvSpaceInstance.setProperties(ret, props, fetcher);
					
					return ret;
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "texturedrectangle"), "parts", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "triangle"), "parts", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "rectangle"), "parts", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "regularpolygon"), "parts", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "ellipse"), "parts", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "text"), "parts", null, null, null, "")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawcondition"), null, null, expconv, null, ""))
			}));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "texturedrectangle")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("y", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatex", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatey", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatez", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("position", null, null, null, null, ""),
			new BeanAttributeInfo("rotation", null, null, null, null, ""),
			new BeanAttributeInfo("size", null, null, null, null, ""),
			new BeanAttributeInfo("abspos", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("abssize", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("absrot", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("imagepath", null, null, null, null, ""),
			new BeanAttributeInfo("layer", null, null, tintconv, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? RotatingPrimitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? RotatingPrimitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? RotatingPrimitive.ABSOLUTE_ROTATION : 0;

					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new TexturedRectangle(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), (String)MEnvSpaceInstance.getProperty(args, "imagepath"), exp);
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawcondition"), null, null, expconv, null, ""))
			}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "triangle")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("y", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatex", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatey", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatez", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("position", null, null, null, null, ""),
			new BeanAttributeInfo("rotation", null, null, null, null, ""),
			new BeanAttributeInfo("size", null, null, null, null, ""),
			new BeanAttributeInfo("abspos", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("abssize", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("absrot", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("layer", null, null, tintconv, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? RotatingPrimitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? RotatingPrimitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? RotatingPrimitive.ABSOLUTE_ROTATION : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Triangle(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawcondition"), null, null, expconv, null, ""))
			}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "rectangle")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("y", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatex", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatey", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatez", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("position", null, null, null, null, ""),
			new BeanAttributeInfo("rotation", null, null, null, null, ""),
			new BeanAttributeInfo("size", null, null, null, null, ""),
			new BeanAttributeInfo("abspos", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("abssize", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("absrot", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("layer", null, null, tintconv, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? RotatingPrimitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? RotatingPrimitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? RotatingPrimitive.ABSOLUTE_ROTATION : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Rectangle(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawcondition"), null, null, expconv, null, ""))
			}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "regularpolygon")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("y", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatex", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatey", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatez", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("position", null, null, null, null, ""),
			new BeanAttributeInfo("rotation", null, null, null, null, ""),
			new BeanAttributeInfo("size", null, null, null, null, ""),
			new BeanAttributeInfo("abspos", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("abssize", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("absrot", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("vertices", null, null, BasicTypeConverter.INTEGER_CONVERTER, null, "", new Integer(3)),
			new BeanAttributeInfo("layer", null, null, tintconv, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? RotatingPrimitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? RotatingPrimitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? RotatingPrimitive.ABSOLUTE_ROTATION : 0;
					
					int vertices  = MEnvSpaceInstance.getProperty(args, "vertices")==null? 3: 
						((Integer)MEnvSpaceInstance.getProperty(args, "vertices")).intValue();
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new RegularPolygon(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), vertices, exp);
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawcondition"), null, null, expconv, null, ""))
			}));
	
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "ellipse")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("y", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatex", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatey", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("rotatez", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("position", null, null, null, null, ""),
			new BeanAttributeInfo("rotation", null, null, null, null, ""),
			new BeanAttributeInfo("size", null, null, null, null, ""),
			new BeanAttributeInfo("abspos", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("abssize", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("absrot", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("layer", null, null, tintconv, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}				
					Object rotation = MEnvSpaceInstance.getProperty(args, "rotation");
					if(rotation==null)
					{
						Double rx = (Double)MEnvSpaceInstance.getProperty(args, "rotatex");
						Double ry = (Double)MEnvSpaceInstance.getProperty(args, "rotatey");
						Double rz = (Double)MEnvSpaceInstance.getProperty(args, "rotatez");
						rotation = Vector3Double.getVector3(rx!=null? rx: new Double(0), ry!=null? ry: new Double(0), rz!=null? rz: new Double(0));
					}
					Object size = MEnvSpaceInstance.getProperty(args, "size");
					if(size==null)
					{
						size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					}
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? RotatingPrimitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? RotatingPrimitive.ABSOLUTE_SIZE : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "absrot"))? RotatingPrimitive.ABSOLUTE_ROTATION : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Ellipse(position, rotation, size, absFlags, MEnvSpaceInstance.getProperty(args, "color"), exp);
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawcondition"), null, null, expconv, null, ""))
			}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "text")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("y", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("position", null, null, null, null, ""),
			new BeanAttributeInfo("font", null, null, BasicTypeConverter.STRING_CONVERTER, null, ""),
			new BeanAttributeInfo("style", null, null, BasicTypeConverter.INTEGER_CONVERTER, null, ""),
			new BeanAttributeInfo("size", null, null, BasicTypeConverter.INTEGER_CONVERTER, null, ""),
			new BeanAttributeInfo("abspos", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("abssize", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, ""),
			new BeanAttributeInfo("color", null, null, colorconv, null, ""),
			new BeanAttributeInfo("layer", null, null, tintconv, null, ""),
			new BeanAttributeInfo("text", null, null, BasicTypeConverter.STRING_CONVERTER, null, ""),
			new BeanAttributeInfo("align", null, null, BasicTypeConverter.STRING_CONVERTER, null, ""),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					Object position = MEnvSpaceInstance.getProperty(args, "position");
					if(position==null)
					{
						position = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "x"),
							(Double)MEnvSpaceInstance.getProperty(args, "y"));
					}
					
					String fontname = (String) MEnvSpaceInstance.getProperty(args, "font");
					if(fontname==null)
					{
						fontname = "Default";
					}
					Integer fontstyle = (Integer) MEnvSpaceInstance.getProperty(args, "style");
					if (fontstyle==null)
					{
						fontstyle = new Integer(Font.PLAIN);
					}
					Integer fontsize = (Integer) MEnvSpaceInstance.getProperty(args, "size");
					if (fontsize==null)
					{
						fontsize = new Integer(12);
					}
					Font font = new Font(fontname, fontstyle.intValue(), fontsize.intValue());
					
					String text = (String) MEnvSpaceInstance.getProperty(args, "text");
					text = String.valueOf(text);
					
					// Hack! Upstream needs to provide _proper_ newlines not a \n literal
					// Attributes are probably not the right place for text...
					text = text.replaceAll("\\\\n", "\n").replaceAll("\\\\\\\\", "\\");
					
					String aligntxt = String.valueOf(MEnvSpaceInstance.getProperty(args, "align"));
					int align = Text.ALIGN_LEFT;
					if (aligntxt.equals("right"))
						align = Text.ALIGN_RIGHT;
					else if (aligntxt.equals("center"))
						align = Text.ALIGN_CENTER;
					
					int absFlags = Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abspos"))? RotatingPrimitive.ABSOLUTE_POSITION : 0;
					absFlags |= Boolean.TRUE.equals(MEnvSpaceInstance.getProperty(args, "abssize"))? RotatingPrimitive.ABSOLUTE_SIZE : 0;
					
					IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(args, "drawcondition");
					return new Text(position, font, (Color)MEnvSpaceInstance.getProperty(args, "color"), text, align, absFlags, exp);
				}
			})
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "drawcondition"), null, null, expconv, null, ""))
			}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "gridlayer")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("type", null, null, null, null, "", "gridlayer"),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IVector2 size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					return new GridLayer(size, MEnvSpaceInstance.getProperty(args, "color"));
				}
			})
			}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "tiledlayer")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("imagepath", null, null, null, null, ""),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, ""),
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("type", null, null, null, null, "", "tiledlayer"),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IVector2 size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
						(Double)MEnvSpaceInstance.getProperty(args, "height"));
					return new TiledLayer(size, MEnvSpaceInstance.getProperty(args, "color"), (String)MEnvSpaceInstance.getProperty(args, "imagepath"));
				}
			})
			}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "colorlayer")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("color", null, null, tcolorconv, null, ""),
			new BeanAttributeInfo("type", null, null, null, null, "", "colorlayer"),
			new BeanAttributeInfo("creator", null, null, null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					return new ColorLayer(MEnvSpaceInstance.getProperty(args, "color"));
				}
			})
			}, null));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "spaceexecutor")}, MultiCollection.class, null, 
			new BeanAttributeInfo("expression", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, "")
			}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "envspacetype"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)
			}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "envspace"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "processtype"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "tasktype"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "actiontype"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "percepttype"), new QName(uri, "agenttypes"), new QName(uri, "agenttype")}, HashMap.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, "")}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "perceptgenerator"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "perceptprocessor"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "view"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""), 
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "perspective"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "object"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "avatar"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),			
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "process"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "objecttype"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "drawable"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "spaceexecutor"), new QName(uri, "property")}, HashMap.class, null, new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("class", "clazz", null, typeconv, null, ""),
			new BeanAttributeInfo("dynamic", null, null, BasicTypeConverter.BOOLEAN_CONVERTER, null, "", Boolean.FALSE)}, null));
		
		// type instance declarations.
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "envspace")}, MEnvSpaceInstance.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("type", "typeName"),
			new BeanAttributeInfo("width", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, "property"),
			new BeanAttributeInfo("height", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, "property"),
			new BeanAttributeInfo("depth", null, null, BasicTypeConverter.DOUBLE_CONVERTER, null, "property"),
			new BeanAttributeInfo("border", null, null, null, null, "property"),
			new BeanAttributeInfo("neighborhood", null, null, null, null, "property")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "property"), "properties", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "object"), "objects", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "avatar"), "avatars", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "process"), "processes", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "spaceaction"), "spaceactions", null, null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "observer"), "observers", null, null, null, "property"))
			}));
		
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "object")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("type", null, null, null, null, ""),
			new BeanAttributeInfo("number", null, null, BasicTypeConverter.INTEGER_CONVERTER, null, "")
			}, null));
			
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "avatar")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("type", null, null, null, null, ""),
			new BeanAttributeInfo("owner", null, null, null, null, "")
			}, null));
			
		types.add(new TypeInfo(ti_po, new QName[]{new QName(uri, "process")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("type", null, null, null, null, ""),
			}, null));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "spaceaction")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("type", null, null, null, null, "")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo(new QName(uri, "parameter"), "parameters", null, null, null, ""))
			}));
		
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "spaceaction"), new QName(uri, "parameter")}, HashMap.class, null,
			new BeanAttributeInfo("value", null, null, expconv, null, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, "")}, null));

		types.add(new TypeInfo(null, new QName[]{new QName(uri, "observer")}, MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, null, null, ""),
			new BeanAttributeInfo("view", null, null, null, null, ""),
			new BeanAttributeInfo("perspective", null, null, null, null, ""),
			}, null));
		
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 * /
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();

		ITypeConverter expconv = new ExpressionConverter();
		ITypeConverter nameconv = new NameAttributeToObjectConverter();
		
		// applicationtype
		linkinfos.add(new LinkInfo("envspacetype", new BeanAttributeInfo("MSpaceType")));
		
		// application
		linkinfos.add(new LinkInfo("envspace", new BeanAttributeInfo("MSpaceInstance")));
	
		// spacetype
		linkinfos.add(new LinkInfo("objecttype", new BeanAttributeInfo("objecttypes", null, "property")));
		linkinfos.add(new LinkInfo("avatarmapping", new BeanAttributeInfo("avatarmappings", null, "property")));
		linkinfos.add(new LinkInfo("actiontype", new BeanAttributeInfo("actiontypes", null, "property")));
		linkinfos.add(new LinkInfo("processtype", new BeanAttributeInfo("processtypes", null, "property")));
		linkinfos.add(new LinkInfo("tasktype", new BeanAttributeInfo("tasktypes", null, "property")));
		linkinfos.add(new LinkInfo("perceptgenerator", new BeanAttributeInfo("perceptgenerators", null, "property")));
		linkinfos.add(new LinkInfo("perceptprocessor", new BeanAttributeInfo("perceptprocessors", null, "property")));
		linkinfos.add(new LinkInfo("view", new BeanAttributeInfo("views", null, "property")));
		linkinfos.add(new LinkInfo("spaceexecutor", new BeanAttributeInfo(null, null, "property")));
		linkinfos.add(new LinkInfo("perspective", new BeanAttributeInfo("perspectives", null, "property")));
		linkinfos.add(new LinkInfo("percepttype", new BeanAttributeInfo("percepttypes", null, "property")));
		
		// theme
		linkinfos.add(new LinkInfo("drawable", new BeanAttributeInfo("drawables", null, "")));
		linkinfos.add(new LinkInfo("prelayers/gridlayer", new BeanAttributeInfo("prelayers", null, "")));
		linkinfos.add(new LinkInfo("prelayers/tiledlayer", new BeanAttributeInfo("prelayers", null, "")));
		linkinfos.add(new LinkInfo("prelayers/colorlayer", new BeanAttributeInfo("prelayers", null, "")));
		linkinfos.add(new LinkInfo("postlayers/gridlayer", new BeanAttributeInfo("postlayers", null, "")));
		linkinfos.add(new LinkInfo("postlayers/tiledlayer", new BeanAttributeInfo("postlayers", null, "")));
		linkinfos.add(new LinkInfo("postlayers/colorlayer", new BeanAttributeInfo("postlayers", null, "")));
		
		// drawable
		linkinfos.add(new LinkInfo("texturedrectangle", new BeanAttributeInfo("parts", null, "")));		
		linkinfos.add(new LinkInfo("triangle", new BeanAttributeInfo("parts", null, "")));		
		linkinfos.add(new LinkInfo("rectangle", new BeanAttributeInfo("parts", null, "")));		
		linkinfos.add(new LinkInfo("regularpolygon", new BeanAttributeInfo("parts", null, "")));
		linkinfos.add(new LinkInfo("ellipse", new BeanAttributeInfo("parts", null, "")));
		linkinfos.add(new LinkInfo("text", new BeanAttributeInfo("parts", null, "")));
		
		// all drawable elems
		linkinfos.add(new LinkInfo("drawcondition", new BeanAttributeInfo("drawcondition", expconv, "")));		

		// space instance
		linkinfos.add(new LinkInfo("object", new BeanAttributeInfo("objects", null, "property")));
		linkinfos.add(new LinkInfo("avatar", new BeanAttributeInfo("avatars", null, "property")));
		linkinfos.add(new LinkInfo("process", new BeanAttributeInfo("processes", null, "property")));
		linkinfos.add(new LinkInfo("spaceaction", new BeanAttributeInfo("spaceactions", null, "property")));
		linkinfos.add(new LinkInfo("observer", new BeanAttributeInfo("observers", null, "property")));
		
		// space action 
		linkinfos.add(new LinkInfo("spaceaction/parameter", new BeanAttributeInfo("parameters", null, "")));
		
		// action, process, ...
		linkinfos.add(new LinkInfo("envspacetype/property", new BeanAttributeInfo("properties", null, "property")));
		linkinfos.add(new LinkInfo("envspace/property", new BeanAttributeInfo("properties", null, "property")));
		linkinfos.add(new LinkInfo("processtype/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("actiontype/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("object/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("avatar/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("process/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("view/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("perspective/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("objecttype/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("drawable/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("spaceexecutor/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("perceptgenerator/property", new BeanAttributeInfo("properties", null, "")));
		linkinfos.add(new LinkInfo("perceptprocessor/property", new BeanAttributeInfo("properties", null, "")));
		
		// percepts
		linkinfos.add(new LinkInfo("percepttype/objecttypes/objecttype", new BeanAttributeInfo("objecttypes", null, "")));
		linkinfos.add(new LinkInfo("percepttype/agenttypes/agenttype", new BeanAttributeInfo("agenttypes", new ITypeConverter()
		{
			public boolean acceptsInputType(Class inputtype)
			{
				return true;
			}
			public Object convertObject(Object val, Object root, ClassLoader classloader)
			{
				return ((Map)val).get("name");
			}
		}, "")));

		// perceptprocessors
		linkinfos.add(new LinkInfo("perceptprocessor/percepttype", new BeanAttributeInfo("percepttypes", nameconv, "")));
		
		
		return linkinfos;
	}*/
	
	/**
	 *  Parse class names.
	 */
	static class ExpressionConverter implements ITypeConverter
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			
			Object ret = null;
			
//			System.out.println("Found expression: "+val);
			try
			{
				ret = exp_parser.parseExpression((String)val, ((MApplicationType)root).getAllImports(), null, classloader);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 */
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}
	}
	
	/**
	 *  Parse class names.
	 */
	static class ClassConverter	implements ITypeConverter
	{
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
//			if(!(val instanceof String))
//				throw new RuntimeException("Source value must be string: "+val);
			
			Object ret = val;
			if(val instanceof String)
			{
				ret = SReflect.findClass0((String)val, ((MApplicationType)root).getAllImports(), classloader);
				if(ret==null)
					throw new RuntimeException("Could not parse class: "+val);
			}
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 * /
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}*/
	}
	
	/**
	 *  Parse class names.
	 */
	static class ColorConverter	implements ITypeConverter
	{
		protected StyleSheet ss = new StyleSheet();
		
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
			Object ret = val;
		
//			if(!(val instanceof String))
//				throw new RuntimeException("Source value must be string: "+val);
		
			if(val instanceof String)
			{
				String	str	= (String)val;
				String	alpha	= null;
				
				if((str.startsWith("#")) && (str.length()==9))
				{
					alpha	= str.substring(7);
					str	= str.substring(0, 7);
				}
				
				// Cannot use CSS.stringToColor() because they haven't made it public :-(
				Color	c	= ss.stringToColor((String)val);
				if(alpha!=null)
				{
					ret	= new Color(c.getRed(), c.getGreen(), c.getBlue(), Integer.parseInt(alpha, 16));
				}
				else if(c!=null)
				{
					ret = c;
				}
			}
			
//			System.out.println("tolerant conv: "+val+" "+ret+" "+(ret!=null? ""+ret.getClass(): ""));
			
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 * /
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}*/
	}
	
	/**
	 *  String -> Double/String converter.
	 *  Converts to a double if possible. Otherwise string will be kept.
	 */
	static class TolerantDoubleTypeConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
//			if(!(val instanceof String))
//				throw new RuntimeException("Source value must be string: "+val);
			
			Object ret = val;
			if(val instanceof String)
			{
				try{ret = new Double((String)val);}
				catch(Exception e){}
			}
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 * /
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}*/
	}
	
	/**
	 *  String -> Double/String converter.
	 *  Converts to a integer if possible. Otherwise string will be kept.
	 */
	static class TolerantIntegerTypeConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
//			if(!(val instanceof String))
//				throw new RuntimeException("Source value must be string: "+val);
			Object ret = val;
			if(val instanceof String)
			{
				try{ret = new Integer((String)val);}
				catch(Exception e){}
			}
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 * /
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}*/
	}
	
	/**
	 *  String -> Double/String converter.
	 *  Converts to a integer if possible. Otherwise string will be kept.
	 */
	static class TolerantColorConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
//			if(!(val instanceof String))
//				throw new RuntimeException("Source value must be string: "+val);
			
			Object ret = val;
			if(val instanceof String)
			{
				ret = new ColorConverter().convertObject(val, root, classloader, null);
			}
			
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 * /
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}*/
	}
	
	/**
	 *  Name attribute to object converter.
	 */
	static class NameAttributeToObjectConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 * /
		public boolean acceptsInputType(Class inputtype)
		{
			return true;
		}*/
		
		/**
		 *  Test if a converter accepts a specific input type.
		 * @param inputtype The input type.
		 *  @return True, if accepted.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
			Object ret = val;
			if(val instanceof Map)
			{
				ret = (String)((Map)val).get("name");
			}
			return ret;
		}
	}
}
