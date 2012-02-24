package jadex.extension.envsupport.observer.graphics.jmonkey;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.AbstractViewport3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.ArrowJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.BoxJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.CylinderJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.DomeJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.IJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.Object3dJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.SphereJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.TorusJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.SkyJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.TerrainJMonkeyRenderer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.lwjgl.Sys;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;



public class ViewportJMonkey extends AbstractViewport3d 
{
	MonkeyApp					_app;

	private JmeCanvasContext	_context;

	int							_selectedId;

	DrawableCombiner3d			_marker;
	
	private ClassLoader	_classloader;
	
	//
	// Drawstuff
	//
	

	private SpaceObject selectedObj;
	private Set<Object> _drawObjects;
	//Objects called in this refresh
	private Set<Object> _drawObjectsRefresh;
	//Objects has been createt during the last refresh
	private Set<Object> _drawObjectsLast;
	private Node _geometryNode = new Node("Geometry");
	private Node _staticNode = new Node("Static Geometry");
	
	private boolean _firstrun = true;
	
	// The Node to be updated
	private Node _tmpNode;
	
	private Node marker;
	
	private int _lastselect = -1;

	
	private Callable<Object>	renderFrameAction_;

	
	/** The overal scale for the Application */
	private final static int _scaleApp; 

	/** The 3d renderers. */
	private static final IJMonkeyRenderer[] RENDERERS = new IJMonkeyRenderer[10];
	static
	{
		RENDERERS[0] = new SphereJMonkeyRenderer();
		RENDERERS[1] = new BoxJMonkeyRenderer();
		RENDERERS[2] = new CylinderJMonkeyRenderer();
		RENDERERS[3] = new ArrowJMonkeyRenderer();
		RENDERERS[4] = new DomeJMonkeyRenderer();
		RENDERERS[5] = new TorusJMonkeyRenderer();
		RENDERERS[6] = new Object3dJMonkeyRenderer();
//		RENDERERS[7] = new Text3dJMonkeyRenderer();
		RENDERERS[8] = new SkyJMonkeyRenderer();
		RENDERERS[9] = new TerrainJMonkeyRenderer();
		
		// Don´t change this
		_scaleApp = 100;
	}
	public ViewportJMonkey(IPerspective perspective, ClassLoader classloader)
	{
		super(perspective);
		
		//Context ClassLoader for Assets
		Thread.currentThread().setContextClassLoader(classloader);
		
		_classloader = classloader;
		_app = new MonkeyApp(_scaleApp);

		AppSettings settings = new AppSettings(true);

		_app.setPauseOnLostFocus(false);
		_app.setSettings(settings);
		_app.createCanvas();
		_app.startCanvas();

		
		_context = (JmeCanvasContext)_app.getContext();
		System.out.println("ViewportJMonkey Konstruktur: bis hier");
		canvas_ = _context.getCanvas();
		canvas_.setSize(settings.getWidth(), settings.getHeight());
		
		// Drawstuff
		_drawObjects = Collections.synchronizedSet(new HashSet<Object>());
		_drawObjectsRefresh = Collections.synchronizedSet(new HashSet<Object>());
		_drawObjectsLast = Collections.synchronizedSet(new HashSet<Object>());
				
		renderFrameAction_ = new Callable<Object>()
		{
			public Object call()
			{
				
				rendering = false;

				if(_firstrun)
				{
					_staticNode = updateMonkey(_staticvisuals);
					_staticNode.setLocalScale(_scaleApp / (areaSize_.getXAsFloat()));
					_app.setStaticGeometry(_staticNode);
					_firstrun = false;
				}
				
				_geometryNode = updateMonkey(objectList_);
				
				_geometryNode.setLocalScale(_scaleApp / (areaSize_.getXAsFloat()));
				
				_app.setGeometry(_geometryNode);
				
				return null;
			};
		};
		 
	}



	public void setSelected(int selected, DrawableCombiner3d marker)
	{
		_selectedId = selected;
		_marker = marker;
	}

	public int getSelected()
	{
		return _selectedId;
	}


	public DrawableCombiner3d getMarker()
	{
		return _marker;
	}

	public void refresh()
	{
		if(!rendering)
		{
			rendering = true;
			_app.enqueue(renderFrameAction_);
		}
	}

	public AssetManager getAssetManager()
	{
		return _app.getAssetManager();
	}


	public Spatial createPrimitive3d(DrawableCombiner3d drawableCombiner3d,
			Primitive3d p, Object obj)
	{
		return RENDERERS[p.getType()].prepareAndExecuteDraw(drawableCombiner3d, p, obj, this);
	}


	public void updatePrimitive3d(DrawableCombiner3d drawableCombiner3d,
			Primitive3d p, Object obj, Spatial sp)
	{
		RENDERERS[p.getType()].prepareAndExecuteUpdate(drawableCombiner3d, p, obj, this, sp);

	}


	public void startApp()
	{
		_app.startCanvas();
		_app.enqueue(new Callable<Void>()
		{
			public Void call()
			{
				if(_app instanceof SimpleApplication)
				{
					SimpleApplication simpleApp = (SimpleApplication)_app;
					simpleApp.getFlyByCamera().setDragToRotate(true);
				}
				return null;
			}
		});
	}


	
	protected Node updateMonkey(ArrayList<DrawableCombiner3d> staticvisuals)
	{
		for(DrawableCombiner3d combiner : _staticvisuals)
		{
			Node objectNode = new Node("one static");
			Vector3Double sizeDrawableD = (Vector3Double) combiner.getSize();
				
			Vector3f sizeDrawable =   new Vector3f(sizeDrawableD.getXAsFloat(), sizeDrawableD.getYAsFloat(), sizeDrawableD.getZAsFloat());	
			
			objectNode.setLocalScale(sizeDrawable);
//			Vector3f vector = new Vector3f(areaSize_.getXAsFloat(), 0 , areaSize_.getYAsFloat());
//			objectNode.setLocalTranslation(vector.divide(2));
			
//			TODO: ACHTUNG?
			List<Primitive3d> drawList = combiner.getPrimitives3d();
			if(drawList == null)
				return objectNode;

			for(Iterator<Primitive3d> it = drawList.iterator(); it.hasNext();)
			{
				Primitive3d p = (Primitive3d)it.next();
				Spatial spatial = createPrimitive3d(combiner, p, null);
				if(spatial!=null)
				{
					objectNode.attachChild(spatial);
				}
			}

			_staticNode.attachChild(objectNode);
		}
		return _staticNode;
	}
	
	public Node updateMonkey(List<Object> objectList) {
		synchronized(objectList)
		{
			// Clear the Refresh "listener"
			_drawObjectsRefresh = new HashSet<Object>();
			

			selectedObj = null;
				for (Iterator<Object> it = objectList.iterator(); it.hasNext(); )
				{
					Object[] o = (Object[]) it.next();
					DrawableCombiner3d d = (DrawableCombiner3d) o[1];
					SpaceObject sobj = (SpaceObject) o[0];
					
					Object identifier = SObjectInspector.getId(sobj);
					
					_drawObjectsRefresh.add(identifier);
					
					
					// Handle Selection
					int selected = getSelected();
					int iteration =  ((Long) sobj.getId()).intValue();
					if(iteration == selected)
					{
						selectedObj = sobj;
					}
					

					
					if (!_drawObjects.contains(identifier))
					{
						_drawObjects.add(identifier);
						Node objectNode = new Node(identifier.toString());

						Vector2Double pos2d = ((Vector2Double) SObjectInspector.getProperty(sobj, "position"));
						Vector2f pos2df = new Vector2f(pos2d.getXAsFloat(),pos2d.getYAsFloat());
						
						Vector3f position = new Vector3f(pos2df.x,_app.getHeightAt(pos2df),pos2df.y);
						// OLD POS
//						Vector3f position = ((Vector2Double) SObjectInspector.getProperty(sobj, "position")).getVector3DoubleValueNoHight().createAsFloat();
						Vector3Double sizeDrawableD = (Vector3Double) d.getSize();
						
						Vector3f sizeDrawable =   new Vector3f(sizeDrawableD.getXAsFloat(), sizeDrawableD.getYAsFloat(), sizeDrawableD.getZAsFloat());	
						
						
		

						
						// Calculate the Direction
						Vector3f degreecalc = (position.subtract(objectNode.getLocalTranslation())).normalizeLocal();
						if(!degreecalc.equals(Vector3f.ZERO))
								{
								float grad = degreecalc.clone().angleBetween(Vector3f.UNIT_X.normalizeLocal());
								Quaternion quat = new Quaternion();
								if(degreecalc.getX() <= 0.0f && degreecalc.getZ() >= 0.0f )
								{
								quat.fromAngleAxis((360*FastMath.DEG_TO_RAD)-grad,Vector3f.UNIT_Y);
								}
								else if(degreecalc.getX() >= 0.0f && degreecalc.getZ() >= 0.0f )
								{
								quat.fromAngleAxis((360*FastMath.DEG_TO_RAD)-grad,Vector3f.UNIT_Y);
								}
								else
								{
									quat.fromAngleAxis(grad,Vector3f.UNIT_Y);	
								}
								objectNode.setLocalRotation(quat);
								}
						
						
						
						objectNode.setLocalScale(sizeDrawable);
						objectNode.setLocalTranslation(position);
						
						
//						NEW NEW NEW
//						TODO: ACHTUNG?
						List<Primitive3d> drawList = d.getPrimitives3d();
						if(drawList == null)
							return objectNode;

						for(Iterator<Primitive3d> itp = drawList.iterator(); itp.hasNext();)
						{
							Primitive3d p = (Primitive3d)itp.next();
							Spatial spatial = createPrimitive3d(d, p, sobj);
							if(spatial!=null)
							{
								objectNode.attachChild(spatial);
							}
						}


						_geometryNode.attachChild(objectNode);

					}
					else
					{
						Vector2Double pos2d = ((Vector2Double) SObjectInspector.getProperty(sobj, "position"));
						Vector2f pos2df = new Vector2f(pos2d.getXAsFloat(),pos2d.getYAsFloat());
						
						Vector3f position = new Vector3f(pos2df.x,_app.getHeightAt(pos2df),pos2df.y);
						// OLD POS
//						Vector3f position = ((Vector2Double) SObjectInspector.getProperty(sobj, "position")).getVector3DoubleValueNoHight().createAsFloat();
						Vector3Double sizeDrawableD = (Vector3Double) d.getSize();

						Vector3f sizeDrawable =   new Vector3f(sizeDrawableD.getXAsFloat(), sizeDrawableD.getYAsFloat(), sizeDrawableD.getZAsFloat());	
						
						Spatial node = _geometryNode.getChild(identifier.toString());
						_tmpNode = (Node) node;
						

						
						
						// Calculate the Direction
						try
						{
						Vector3f degreecalc = (position.clone().subtract(_tmpNode.getLocalTranslation().clone())).normalizeLocal();
	
						if(!degreecalc.equals(Vector3f.ZERO))
								{
								float grad = degreecalc.clone().angleBetween(Vector3f.UNIT_X.normalizeLocal());
								Quaternion quat = new Quaternion();
								if(degreecalc.getX() <= 0.0f && degreecalc.getZ() >= 0.0f )
								{
								quat.fromAngleAxis((360*FastMath.DEG_TO_RAD)-grad,Vector3f.UNIT_Y);
								}
								else if(degreecalc.getX() >= 0.0f && degreecalc.getZ() >= 0.0f )
								{
								quat.fromAngleAxis((360*FastMath.DEG_TO_RAD)-grad,Vector3f.UNIT_Y);
								}
								else
								{
									quat.fromAngleAxis(grad,Vector3f.UNIT_Y);	
								}
								_tmpNode.setLocalRotation(quat);
								}
						
						_tmpNode.setLocalScale(sizeDrawable);
						
						_tmpNode.setLocalTranslation(position);
						
						
								List<Primitive3d> drawList = d.getPrimitives3d();
								if(drawList == null)
								{
									System.out.println("Viewport Monkey Zeile 407ca: null?!");
								}
								else
								{
									for(Iterator<Primitive3d> itx = drawList.iterator(); itx.hasNext();)
									{
										Primitive3d p = (Primitive3d)itx.next();
										
										identifier = "Type: "+ p.getType()+ " HCode " +p.hashCode();
								
										Spatial sp = _tmpNode.getChild((String)identifier);
										
										if(!(sp == null))
										{
											updatePrimitive3d(d, p, sobj, sp);
											
										}
										else
											{
												
												Spatial spatial = createPrimitive3d(d, p, sobj);
												if(spatial!=null)
												{
													_tmpNode.attachChild(spatial);
												}
											}
								}
									
					

									
								}

						

						
						
						} catch(NullPointerException e)
						{
							
						}
					}


				}
				
				// Update Selection (The Visualisation) // Big HACK would like to have an easier solution
				int selected = getSelected();

				if(selected != -1)
				{
					if(_lastselect!=-1 && _lastselect!=selected)
					{
						Node node = (Node) _geometryNode.getChild(_lastselect);
						marker = new Node("Marker");
//						marker = getMarker().modify(selectedObj, this, marker);
						if(node.getChild("Marker")!=null)
						{		
							node.getChild("Marker").removeFromParent();
						}
					}
					_lastselect = selected;
					Node node = (Node) _geometryNode.getChild(selected);
					marker = new Node("Marker");
//					marker = getMarker().modify(selectedObj, this, marker);
					if(node.getChild("Marker")==null)
					{		
						node.attachChild(marker);
					}
				}
				else if (selected == -1 && _lastselect != -1)
				{
					Node node = (Node) _geometryNode.getChild(_lastselect);
					marker = new Node("Marker");
//					marker = getMarker().modify(selectedObj, this, marker);
					if(node.getChild("Marker")!=null)
					{		
						node.getChild("Marker").removeFromParent();
					}
				}
				
				// End of Selection Updates
		
				
				// Delete all deleted Objects by removing them from the Geometry Node
				for (Iterator<Object> it = _drawObjectsLast.iterator(); it.hasNext(); )
				{
					Object identifier = it.next();
					if(!_drawObjectsRefresh.contains(identifier))
					{
						_geometryNode.getChild(identifier.toString()).removeFromParent();
						System.out.println("delete!");
					}
				
				}
				_drawObjectsLast = new HashSet<Object>(_drawObjectsRefresh);
		}
		return _geometryNode;
		
	}
	
	public ClassLoader getClassloader()
	{
		return _classloader;
	}

	
	// HACK TODO: good?
	public void stopApp()
	{

		

		_app.stop();

			System.out.println("ViewportJmonkey: STOP STOP STOP");

	}

	// TODO Auto-generated method stub
	public void pauseApp()
	{
		
		_app.loseFocus();
	}



}
