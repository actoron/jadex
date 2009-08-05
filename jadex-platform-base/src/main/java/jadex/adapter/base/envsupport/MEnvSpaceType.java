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
import jadex.commons.SUtil;
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

/**
 *  Java representation of environemnt space type for xml description.
 */
public class MEnvSpaceType	extends MSpaceType
{
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
		
		TypeInfo ti_po = new TypeInfo(null, "abstract_propertyobject", MultiCollection.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("property", "properties", null, ""))
			}
		);
		
		types.add(new TypeInfo(ti_po, "objecttype", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("name", "name", null, "")}, null));
		
		types.add(new TypeInfo(null, "envspacetype", MEnvSpaceType.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, "property"),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, "property"),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, "property"),
			new BeanAttributeInfo("depth", null, BasicTypeConverter.DOUBLE_CONVERTER, "property"),
			new BeanAttributeInfo("border", null, null, "property"),
			new BeanAttributeInfo("neighborhood", null, null, "property")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("property", "properties", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("objecttype", "objecttypes", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("avatarmapping", "avatarmappings", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("actiontype", "actiontypes", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("processtype", "processtypes", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("tasktype", "tasktypes", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("perceptgenerator", "perceptgenerators", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("perceptprocessor", "perceptprocessors", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("view", "views", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("spaceexecutor", null, null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("perspective", "perspectives", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("percepttype", "percepttypes", null, "property"))
		}));
		
		types.add(new TypeInfo(null, "avatarmapping", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("agenttype", null, null, ""),
			new BeanAttributeInfo("objecttype", null, null, ""),
			new BeanAttributeInfo("createavatar", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("createagent", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("killavatar", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("killagent", null, BasicTypeConverter.BOOLEAN_CONVERTER, "")
			}, null));
		
		types.add(new TypeInfo(null, "percepttype", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("objecttype", "objecttypes", null, ""),
			new BeanAttributeInfo("agenttype", "agenttypes", null, "")
			}, null, null,
			new SubobjectInfo[]{
//			new SubobjectInfo("percepttype/objecttypes/objecttype", new BeanAttributeInfo("objecttype", "objecttypes", null, "")),
//			new SubobjectInfo("percepttype/agenttypes/agenttype", new BeanAttributeInfo("percepttype", "agenttypes", new ITypeConverter()
			new SubobjectInfo(new BeanAttributeInfo("objecttypes/objecttype", "objecttypes", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("agenttypes/agenttype", "agenttypes", new ITypeConverter()
			{
				public boolean acceptsInputType(Class inputtype)
				{
					return true;
				}
				public Object convertObject(Object val, Object root, ClassLoader classloader)
				{
					return ((Map)val).get("name");
				}
			}, ""))
			}));
		
		types.add(new TypeInfo(ti_po, "actiontype", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("name", null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, "processtype", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("name", null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, "tasktype", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("name", null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, "perceptgenerator", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("name", null, null, "")
			}, null));

		types.add(new TypeInfo(ti_po, "perceptprocessor", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{	
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("agenttype", null, null, "")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("percepttype", "percepttypes", nameconv, "")),
			}));
		
		types.add(new TypeInfo(null, "perceptprocessor/percepttype", HashMap.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, "")
			}, null));
		
		types.add(new TypeInfo(ti_po, "view", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("objecttype", null, null, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
		
		types.add(new TypeInfo(ti_po, "perspective", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, ""),		
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("opengl", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("invertxaxis", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE),
			new BeanAttributeInfo("invertyaxis", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.TRUE),
			new BeanAttributeInfo("objectshiftx", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("objectshifty", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("zoomlimit", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("bgcolor", null, colorconv, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IValueFetcher fetcher = (IValueFetcher)args.get("fetcher");
					args = (Map)args.get("object");
					
					IPerspective ret = (IPerspective)((Class)MEnvSpaceInstance.getProperty(args, "clazz")).newInstance();
					boolean opengl = MEnvSpaceInstance.getProperty(args, "opengl")!=null? 
						((Boolean)MEnvSpaceInstance.getProperty(args, "opengl")).booleanValue(): true;
					ret.setOpenGl(opengl);
					String name = (String)MEnvSpaceInstance.getProperty(args, "name");
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
						
						Double xshift = (Double)MEnvSpaceInstance.getProperty(args, "objectshiftx");
						Double yshift = (Double)MEnvSpaceInstance.getProperty(args, "objectshifty");
						if(xshift!=null && yshift!=null)
							pers.setObjectShift(Vector2Double.getVector2(xshift, yshift));
//						else if(ret instanceof Grid2D)
//							pers.setObjectShift(new Vector2Double(0.5));
						
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
			new SubobjectInfo(new BeanAttributeInfo("drawable", "drawables", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("prelayers/gridlayer", "prelayers", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("prelayers/tiledlayer", "prelayers", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("prelayers/colorlayer", "prelayers", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("postlayers/gridlayer", "postlayers", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("postlayers/tiledlayer", "postlayers", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("postlayers/colorlayer", "postlayers", null, ""))
			}));
		
		types.add(new TypeInfo(ti_po, "drawable", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("objecttype", null, null, ""),
			new BeanAttributeInfo("x", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("y", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatex", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatey", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatez", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("position", null, null, ""),
			new BeanAttributeInfo("rotation", null, null, ""),
			new BeanAttributeInfo("size", null, null, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
			new SubobjectInfo(new BeanAttributeInfo("texturedrectangle", "parts", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("triangle", "parts", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("rectangle", "parts", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("regularpolygon", "parts", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("ellipse", "parts", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("text", "parts", null, "")),
			new SubobjectInfo(new BeanAttributeInfo("drawcondition", null, expconv, ""))
			}));

		types.add(new TypeInfo(null, "texturedrectangle", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("y", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatex", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatey", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatez", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("position", null, null, ""),
			new BeanAttributeInfo("rotation", null, null, ""),
			new BeanAttributeInfo("size", null, null, ""),
			new BeanAttributeInfo("abspos", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("abssize", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("absrot", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("imagepath", null, null, ""),
			new BeanAttributeInfo("layer", null, tintconv, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
			new SubobjectInfo(new BeanAttributeInfo("drawcondition", null, expconv, ""))
			}));
		
		types.add(new TypeInfo(null, "triangle", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("y", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatex", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatey", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatez", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("position", null, null, ""),
			new BeanAttributeInfo("rotation", null, null, ""),
			new BeanAttributeInfo("size", null, null, ""),
			new BeanAttributeInfo("abspos", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("abssize", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("absrot", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("layer", null, tintconv, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
			new SubobjectInfo(new BeanAttributeInfo("drawcondition", null, expconv, ""))
			}));
		
		types.add(new TypeInfo(null, "rectangle", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("y", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatex", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatey", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatez", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("position", null, null, ""),
			new BeanAttributeInfo("rotation", null, null, ""),
			new BeanAttributeInfo("size", null, null, ""),
			new BeanAttributeInfo("abspos", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("abssize", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("absrot", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("layer", null, tintconv, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
			new SubobjectInfo(new BeanAttributeInfo("drawcondition", null, expconv, ""))
			}));
		
		types.add(new TypeInfo(null, "regularpolygon", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("y", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatex", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatey", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatez", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("position", null, null, ""),
			new BeanAttributeInfo("rotation", null, null, ""),
			new BeanAttributeInfo("size", null, null, ""),
			new BeanAttributeInfo("abspos", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("abssize", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("absrot", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("vertices", null, BasicTypeConverter.INTEGER_CONVERTER, "", new Integer(3)),
			new BeanAttributeInfo("layer", null, tintconv, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
			new SubobjectInfo(new BeanAttributeInfo("drawcondition", null, expconv, ""))
			}));
	
		types.add(new TypeInfo(null, "ellipse", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("y", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatex", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatey", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("rotatez", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("position", null, null, ""),
			new BeanAttributeInfo("rotation", null, null, ""),
			new BeanAttributeInfo("size", null, null, ""),
			new BeanAttributeInfo("abspos", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("abssize", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("absrot", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("layer", null, tintconv, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
			new SubobjectInfo(new BeanAttributeInfo("drawcondition", null, expconv, ""))
			}));
		
		types.add(new TypeInfo(null, "text", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("x", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("y", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("position", null, null, ""),
			new BeanAttributeInfo("font", null, BasicTypeConverter.STRING_CONVERTER, ""),
			new BeanAttributeInfo("style", null, BasicTypeConverter.INTEGER_CONVERTER, ""),
			new BeanAttributeInfo("basesize", null, BasicTypeConverter.INTEGER_CONVERTER, ""),
			new BeanAttributeInfo("abspos", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("abssize", null, BasicTypeConverter.BOOLEAN_CONVERTER, ""),
			new BeanAttributeInfo("color", null, colorconv, ""),
			new BeanAttributeInfo("layer", null, tintconv, ""),
			new BeanAttributeInfo("text", null, BasicTypeConverter.STRING_CONVERTER, ""),
			new BeanAttributeInfo("align", null, BasicTypeConverter.STRING_CONVERTER, ""),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
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
					Integer fontsize = (Integer) MEnvSpaceInstance.getProperty(args, "basesize");
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
			new SubobjectInfo(new BeanAttributeInfo("drawcondition", null, expconv, ""))
			}));
		
		types.add(new TypeInfo(null, "gridlayer", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("type", null, null, "", "gridlayer"),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IVector2 size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
							(Double)MEnvSpaceInstance.getProperty(args, "height"));
					return new GridLayer(size, MEnvSpaceInstance.getProperty(args, "color"));
				}
			})
			}, null));

		types.add(new TypeInfo(null, "tiledlayer", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("imagepath", null, null, ""),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("type", null, null, "", "tiledlayer"),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					IVector2 size = Vector2Double.getVector2((Double)MEnvSpaceInstance.getProperty(args, "width"),
						(Double)MEnvSpaceInstance.getProperty(args, "height"));
					return new TiledLayer(size, MEnvSpaceInstance.getProperty(args, "color"), (String)MEnvSpaceInstance.getProperty(args, "imagepath"));
				}
			})
			}, null));
		
		types.add(new TypeInfo(null, "colorlayer", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("color", null, tcolorconv, ""),
			new BeanAttributeInfo("type", null, null, "", "colorlayer"),
			new BeanAttributeInfo("creator", null, null, "", new IObjectCreator()
			{
				public Object createObject(Map args) throws Exception
				{
					return new ColorLayer(MEnvSpaceInstance.getProperty(args, "color"));
				}
			})
			}, null));
		
		types.add(new TypeInfo(ti_po, "spaceexecutor", MultiCollection.class, null, 
			new BeanAttributeInfo("expression", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("class", "clazz", typeconv, "")
			}, null));
		
		types.add(new TypeInfo(null, "envspacetype/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)
			}, null));
		
		types.add(new TypeInfo(null, "envspace/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, "processtype/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, "tasktype/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, "actiontype/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, "percepttype/agenttypes/agenttype", HashMap.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, "")}, null));
		
		types.add(new TypeInfo(null, "perceptgenerator/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, "perceptprocessor/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, "view/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""), 
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, "perspective/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));
		
		types.add(new TypeInfo(null, "object/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, "avatar/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),			
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, "process/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, "objecttype/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, "drawable/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));

		types.add(new TypeInfo(null, "spaceexecutor/property", HashMap.class, null, new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("class", "clazz", typeconv, ""),
			new BeanAttributeInfo("dynamic", null, BasicTypeConverter.BOOLEAN_CONVERTER, "", Boolean.FALSE)}, null));
		
		// type instance declarations.
		
		types.add(new TypeInfo(null, "envspace", MEnvSpaceInstance.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("type", "typeName"),
			new BeanAttributeInfo("width", null, BasicTypeConverter.DOUBLE_CONVERTER, "property"),
			new BeanAttributeInfo("height", null, BasicTypeConverter.DOUBLE_CONVERTER, "property"),
			new BeanAttributeInfo("depth", null, BasicTypeConverter.DOUBLE_CONVERTER, "property"),
			new BeanAttributeInfo("border", null, null, "property"),
			new BeanAttributeInfo("neighborhood", null, null, "property")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("property", "properties", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("object", "objects", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("avatar", "avatars", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("process", "processes", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("spaceaction", "spaceactions", null, "property")),
			new SubobjectInfo(new BeanAttributeInfo("observer", "observers", null, "property"))
			}));
		
		types.add(new TypeInfo(ti_po, "object", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("type", null, null, ""),
			new BeanAttributeInfo("number", null, BasicTypeConverter.INTEGER_CONVERTER, "")
			}, null));
			
		types.add(new TypeInfo(ti_po, "avatar", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("type", null, null, ""),
			new BeanAttributeInfo("owner", null, null, "")
			}, null));
			
		types.add(new TypeInfo(ti_po, "process", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("type", null, null, ""),
			}, null));
		
		types.add(new TypeInfo(null, "spaceaction", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("type", null, null, "")
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("parameter", "parameters", null, ""))
			}));
		
		types.add(new TypeInfo(null, "spaceaction/parameter", HashMap.class, null,
			new BeanAttributeInfo("value", null, expconv, ""),
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, "")}, null));

		types.add(new TypeInfo(null, "observer", MultiCollection.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("name", null, null, ""),
			new BeanAttributeInfo("view", null, null, ""),
			new BeanAttributeInfo("perspective", null, null, ""),
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
		public Object convertObject(Object val, Object root, ClassLoader classloader)
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
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			Class ret = SReflect.findClass0((String)val, ((MApplicationType)root).getAllImports(), classloader);
			if(ret==null)
				throw new RuntimeException("Could not parse class: "+val);
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
	static class ColorConverter	implements ITypeConverter
	{
		protected StyleSheet ss = new StyleSheet();
		
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			
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
				c	= new Color(c.getRed(), c.getGreen(), c.getBlue(), Integer.parseInt(alpha, 16));
			}
			return c;
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
	 *  String -> Double/String converter.
	 *  Converts to a double if possible. Otherwise string will be kept.
	 */
	static class TolerantDoubleTypeConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			Object ret = val;
			try{ret = new Double((String)val);}
			catch(Exception e){}
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
	 *  String -> Double/String converter.
	 *  Converts to a integer if possible. Otherwise string will be kept.
	 */
	static class TolerantIntegerTypeConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			Object ret = val;
			try{ret = new Integer((String)val);}
			catch(Exception e){}
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
	 *  String -> Double/String converter.
	 *  Converts to a integer if possible. Otherwise string will be kept.
	 */
	static class TolerantColorConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			
			Object ret = (new ColorConverter()).convertObject(val, root, classloader);
			if (ret == null)
				ret = val;
			
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
	 *  Name attribute to object converter.
	 */
	static class NameAttributeToObjectConverter implements ITypeConverter
	{
		/**
		 *  Convert a string value to another type.
		 *  @param val The string value to convert.
		 */
		public boolean acceptsInputType(Class inputtype)
		{
			return true;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			return (String)((Map)val).get("name");
		}
	}
}
