package jadex.extension.envsupport.observer.graphics.jmonkey;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.jme3.animation.AnimChannel;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Caps;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.water.SimpleWaterProcessor;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.AbstractViewport3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.IJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.ArrowJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.BoxJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.CylinderJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.DomeJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.Object3dJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.QuadJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.SphereJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.Text3dJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry.TorusJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.EffectRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.PointLightRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.SkyJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.SoundJMonkeyPlayer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.TerrainJMonkeyRenderer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;
import jadex.extension.envsupport.observer.perspective.Perspective3D;


/**
 * This class manages the 3d Visualization and all user interaction.
 * 
 * @author Flipflop
 */

/**
 * @author 7willuwe
 */
public class ViewportJMonkey extends AbstractViewport3d
{
	MonkeyApp								_app;

	private JmeCanvasContext				_context;

	int										_selectedId;

	DrawableCombiner3d						_marker;

	private ClassLoader						_classloader;


	// the graphic card capabilities
	Collection<Caps>						_capabilities;


	private SpaceObject						_selectedObj;

	private Set<Object>						_drawObjects;

	// Objects called in this refresh
	private Set<Object>						_drawObjectsRefresh;

	// Objects has been createt during the last refresh
	private Set<Object>						_drawObjectsLast;

	private Node							_geometryNode	= new Node("Geometry");

	private BatchNode						tmpVisualsBatch	= new BatchNode("StaticVisuals");
	
	private Node							dVisuals	= new Node("DynamicVisuals");
	
//	private BatchNode						sVisualsBatch	= new BatchNode("StaticVisuals");

	private Node							_staticNode		= new Node("Static Geometry");

	private boolean							_firstrun		= true;

	// The Node to be updated
	private Node							_tmpNode;

	private Node							marker;

	private int								_lastselect		= -1;

//	private Callable<Object>				renderFrameAction_;

	private float							_scale			= 1;


	/** The overal scale for the Application */
	private final static int				_scaleApp		= 500;
	
	private Dimension canvasSize;

	private boolean batchchanges = false;
	private boolean deletechanges = false;
	
	/**
	 * BatchLists
	 */
	ArrayList<String> toRemoveList = new ArrayList<String>();
	ArrayList<Spatial> toAddList = new ArrayList<Spatial>();

	/**
	 * Animation Stuff
	 */
	private HashMap<String, AnimChannel>	_animChannels;
	
	/**
	 * Effect Stuff
	 */
	private HashMap<String, ParticleEmitter> _particleEmiters;
	
	private ArrayList<Light> lights = new ArrayList<Light>();
	
	private boolean shader;
	
	private String camera;
	
	private SimpleWaterProcessor waterProcessor;
	
	private AmbientLight lightmarker = new AmbientLight();
	
	public HashMap<String, Material> materials = new HashMap<String, Material>();
	public HashMap<String, Spatial> complexobjects = new HashMap<String, Spatial>();


	/** The 3d renderers. */
	private static final IJMonkeyRenderer[]	RENDERERS		= new IJMonkeyRenderer[15];
	static
	{
		RENDERERS[0] = new SphereJMonkeyRenderer();
		RENDERERS[1] = new BoxJMonkeyRenderer();
		RENDERERS[2] = new CylinderJMonkeyRenderer();
		RENDERERS[3] = new ArrowJMonkeyRenderer();
		RENDERERS[4] = new DomeJMonkeyRenderer();
		RENDERERS[5] = new TorusJMonkeyRenderer();
		RENDERERS[6] = new Object3dJMonkeyRenderer();
		RENDERERS[7] = new Text3dJMonkeyRenderer();
		RENDERERS[8] = new SkyJMonkeyRenderer();
		RENDERERS[9] = new TerrainJMonkeyRenderer();
		RENDERERS[10] = new SoundJMonkeyPlayer();
		RENDERERS[11] = new PointLightRenderer();
		RENDERERS[12] = new PointLightRenderer();
		RENDERERS[13] = new EffectRenderer();
		RENDERERS[14] = new QuadJMonkeyRenderer();
	}

	/**
	 * Creates a new ViewportJMonkey
	 * 
	 * @param perspective the selected Perspective
	 * @param ClassLoader the Classloader
	 */
	public ViewportJMonkey(IPerspective perspective, ClassLoader classloader, IVector3 spacesize, boolean isGrid, boolean shader, String camera, String guiCreatorPath, ISpaceController spaceController)
	{
		super(perspective, spacesize, isGrid, shader, camera, spaceController);

		// Context ClassLoader for Assets
		Thread.currentThread().setContextClassLoader(classloader);
		_classloader = classloader;

		// Animation Channels
		_animChannels = new HashMap<String, AnimChannel>();
		this.shader = shader;
		this.camera = camera;
		

		// TODO: scaling komplett
		_scale = _scaleApp / areaSize_.getXAsFloat();
		
		_geometryNode.attachChild(dVisuals);
//		_geometryNode.attachChild(sVisualsBatch);
		_staticNode.setLocalScale(_scale);
		_geometryNode.setLocalScale(_scale);
		
		_app = new MonkeyApp(_scaleApp, _scale, areaSize_.getXAsFloat(), isGrid, this.shader, this.camera, guiCreatorPath,  spaceController);
		AppSettings settings = new AppSettings(true);
		settings.setFrameRate(30);
//		settings.setBitsPerPixel(2);
//		settings.setResolution(523, 488);settings
		settings.setResolution(1280, 760);
		_app.setPauseOnLostFocus(false);

		_app.setSettings(settings);
		_app.createCanvas();
		_app.startCanvas();
		
		_app.getRootNode().attachChild(_geometryNode);

		_context = (JmeCanvasContext)_app.getContext();
		canvas_ = _context.getCanvas();
		canvas_.setSize(settings.getWidth(), settings.getHeight());
		canvasSize = canvas_.getSize();
		
		

		// Drawstuff
		_drawObjects = Collections.synchronizedSet(new HashSet<Object>());
		_drawObjectsRefresh = Collections.synchronizedSet(new HashSet<Object>());
		_drawObjectsLast = Collections.synchronizedSet(new HashSet<Object>());
		
//		lightmarker.setPosition(new Vector3f(0,_scaleApp,0));
		lightmarker.setColor(ColorRGBA.Red.mult(2f));
	}

	public void refresh(List<Object[]>  objectList, Collection<DrawableCombiner3d> staticvisuals)
	{
		if(!rendering)
		{
			rendering = true;
			_app.enqueue(new MyCallAction(objectList, staticvisuals));
		}
	}


	/**
	 * This starts the jMonkey Application
	 */
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


	/**
	 * the Static visuals are created here
	 */
	private Node createStatics(Collection<DrawableCombiner3d> staticvisuals)
	{
		for(DrawableCombiner3d combiner3d : staticvisuals)
		{
			Node objectNode = new Node("one static");
			Vector3Double sizeDrawableD = (Vector3Double)combiner3d.getSize();

			Vector3f sizeDrawable = new Vector3f(sizeDrawableD.getXAsFloat(), sizeDrawableD.getYAsFloat(), sizeDrawableD.getZAsFloat());

			objectNode.setLocalScale(sizeDrawable);

			// ACHTUNG? sobj ist NULL
			createObjects(objectNode, combiner3d, null);

			_staticNode.attachChild(objectNode);
		}
		return _staticNode;
	}

	/**
	 * the Dynamic Visuals are created and updated here
	 */
	private void updateMonkey(List<Object[]> objectList)
	{
		toRemoveList.clear();
		toAddList.clear();
		this.batchchanges = false;
		this.deletechanges = false;
		
		// Clear the Refresh "listener"
		// This list holds every object that has been drawn in the last
		// Draw-"round". The list is necessary to make it possible to check
		// for objects that has to be removed from the 3d Szene
		_drawObjectsRefresh = new HashSet<Object>();

		// Step 2 : Create and/or update all visible Visuals
		createAndUpdateVisuals(objectList);
		
		

		// Step 3 : Set and Create the Visualiszation of the Selected Object
//		int selected = getSelected();
//		createAndUpdateVisualSelection(selected);


		// Step 4 : Update deleted Objects (Remove them from gemetryNode)
		for(Iterator<Object> itr = _drawObjectsLast.iterator(); itr.hasNext();)
		{
			Object id = itr.next();
			if(!_drawObjectsRefresh.contains(id))
			{
				Spatial delspatial = dVisuals.getChild(id.toString());
				if(delspatial !=null)
				{
					delspatial.removeFromParent();
				}
				else
				{
					toRemoveList.add(id.toString());
				}
				
			}

		}


		_drawObjectsLast = new HashSet<Object>(_drawObjectsRefresh);

		// Step 5 Return the freshest Version of the geometryNode
		if(batchchanges)
		{

			_app.setToDelete(toRemoveList);
			_app.setToAdd(toAddList);
		}

	}


	/**
	 * the Visuals for each Object are created and updated here
	 */
	private void createAndUpdateVisuals(List<Object[]> objectList)
	{
		for(Iterator<Object[]> it = objectList.iterator(); it.hasNext();)
		{
			
			Object[] o = it.next();
			DrawableCombiner3d combiner3d = (DrawableCombiner3d)o[1];
			SpaceObject sobj = (SpaceObject)o[0];
			Object identifier = SObjectInspector.getId(sobj);

			boolean rotation3d = combiner3d.isRotation3d();

			_drawObjectsRefresh.add(identifier);

			// Handle Selection
			_selectedObj = null;
			int selected = getSelected();
			int iteration = ((Long)sobj.getId()).intValue();
			if(iteration == selected)
			{
				_selectedObj = sobj;
			}

			Object posObj = SObjectInspector.getProperty(sobj, "position");
			Vector3f position = handleHeightValue(posObj);

			Vector3Double sizeDrawableD = (Vector3Double)combiner3d.getSize();
			Vector3f sizeDrawable = new Vector3f(sizeDrawableD.getXAsFloat(), sizeDrawableD.getYAsFloat(), sizeDrawableD.getZAsFloat());
			// TODO: good solution?
			sizeDrawable = sizeDrawable.divide(2);

			if(!_drawObjects.contains(identifier))
			{

				
				_drawObjects.add(identifier);
				Node objectNode = new Node(identifier.toString());

				objectNode.setLocalScale(sizeDrawable);
				objectNode.setLocalTranslation(position);
				
				
				
				createObjects(objectNode, combiner3d, sobj);
				if(combiner3d.isDynamic())
				{
					dVisuals.attachChild(objectNode);
				}
				else
				{
					
//					sVisualsBatch.attachChild(objectNode);
					toAddList.add(objectNode);
//					sVisualsBatch.batch();
					batchchanges = true;
				}
				
				
				

				
			}
			

			/** Only make updates if the Drawable3d is dynamic */
			else if(combiner3d.isDynamic())
			{

				Node dynamicnode = (Node)_geometryNode.getChild("DynamicVisuals");
				Spatial node = dynamicnode.getChild(identifier.toString());
				_tmpNode = (Node)node;

				// Calculate the Direction
				Quaternion quat = calculateRotation(position, _tmpNode.getLocalTranslation(), rotation3d, sobj, combiner3d.isAutoRotation());

				if(quat != null)
				{
					_tmpNode.setLocalRotation(quat);
				}

				_tmpNode.setLocalScale(sizeDrawable);

				_tmpNode.setLocalTranslation(position);


				List<Primitive3d> drawList = combiner3d.getPrimitives3d();
				if(drawList != null)
				{
					for(Iterator<Primitive3d> itx = drawList.iterator(); itx.hasNext();)
					{
						Primitive3d p = (Primitive3d)itx.next();

						//TODO: waaaaaaaa? identifier
						identifier = "Type: "+ p.getType()+ " HCode " +p.hashCode() + " sobjid " + sobj.hashCode();

						Spatial sp = _tmpNode.getChild((String)identifier);

						if(!(sp == null))
						{
							updatePrimitive3d(combiner3d, p, sobj, sp);

						}
						else
						{
							Spatial spatial = createPrimitive3d(combiner3d, p, sobj);
							if(spatial != null)
							{
								_tmpNode.attachChild(spatial);
							}
						}
					}
				}
			}
		}


	}
	


	/**
	 * the Dynamic Visuals for the Visualization of the Selection are created
	 * and updated here
	 */
	private void createAndUpdateVisualSelection(int selected)
	{
		Perspective3D perp = (Perspective3D)perspective;

		if(selected != -1)
		{
			if(_lastselect != -1 && _lastselect != selected)
			{
				perp.leftClicked("" + selected);
				
				
				
				_geometryNode.getChild("Marker").removeFromParent();
				
				
//				Node node = (Node)_geometryNode.getChild("" + _lastselect);
//				if(node != null && node.getChild("Marker") != null)
//				{
//					_geometryNode.getChild("Marker").removeFromParent();
////					if(node.getParent() instanceof BatchNode)
////					{
////					}
////					else
////					{
////						node.getChild("Marker").removeFromParent();
////						node.removeLight(lightmarker);
////					}
//					
//				}
			}

			if(_lastselect != selected)
			{
				perp.leftClicked("" + selected);
				Node node = (Node)_geometryNode.getChild("" + selected);
				marker = new Node("Marker");
				createObjects(marker, getMarker(), _selectedObj);

				if(_geometryNode.getChild("Marker") == null)
				{
					
					_geometryNode.attachChild(marker);		
					marker.setLocalTranslation(node.getLocalTranslation());
					
					
//					if(node.getParent() instanceof BatchNode)
//					{
////						BatchNode staticnode = (BatchNode)node.getParent();
////						staticnode.batch();
//					}
//					else
//					{
//		
//					}
				}
			}
		}
		else if(selected == -1 && _lastselect != -1)
		{
			perp.leftClicked("" + selected);
			Node node = (Node)_geometryNode.getChild("" + _lastselect);
			if(_geometryNode.getChild("Marker") != null)
			{
				_geometryNode.getChild("Marker").removeFromParent();
//				if(node.getParent() instanceof BatchNode)
//				{
//				}
//				else
//				{
//					node.getChild("Marker").removeFromParent();
//					node.removeLight(lightmarker);
//				}
			}
		}


		_lastselect = selected;
	}

	/**
	 * Handle the Height Value. Check if its set or not
	 */
	public Vector3f handleHeightValue(Object posObj)
	{
		Vector3f position = Vector3f.ZERO;
		if(posObj instanceof Vector2Double)
		{
			Vector2Double pos2d = (Vector2Double)posObj;
			Vector2f pos2df = new Vector2f(pos2d.getXAsFloat(), pos2d.getYAsFloat());
			position = new Vector3f(pos2df.x, _app.getHeightAt(pos2df), pos2df.y);
		}
		else if(posObj instanceof Vector3Double)
		{
			Vector3Double pos3d = (Vector3Double)posObj;
			Vector3f pos3f = new Vector3f(pos3d.getXAsFloat(), pos3d.getYAsFloat(), pos3d.getZAsFloat());
			position = pos3f;
		}
		return position;
	}


	/**
	 * Calculate the current Rotation of the Object in Up/Down and Left/Right
	 */
	private Quaternion calculateRotation(Vector3f newp, Vector3f oldp, boolean rotation3d, ISpaceObject sobj, boolean calculateRotation)
	{
		Vector3f newpos;
		Vector3f oldpos;
		if(rotation3d)
		{
			newpos = newp;
			oldpos = oldp;
		}
		else
		{
			newpos = newp.clone().setY(0);
			oldpos = oldp.clone().setY(0);
		}

		Quaternion quat = null;
		Vector3f direction = newpos.subtract(oldpos);
		if(calculateRotation && !direction.equals(Vector3f.ZERO))
		{
			quat = new Quaternion();
			quat.lookAt(direction, Vector3f.UNIT_Y);

		}
		else if(!calculateRotation && sobj.hasProperty("rotation"))
		{

			float height = 0f;
			Object ob = _tmpNode.getUserData("height");
			if(ob == null)
			{
				_tmpNode.setUserData("height", height);
			}


			Object rot = sobj.getProperty("rotation");

			if(rot instanceof IVector2)
			{
				if(!direction.equals(Vector3f.ZERO))
				{
					height = direction.clone().getY() * 100;
					_tmpNode.setUserData("height", height);
				}

				IVector2 vector2 = (IVector2)rot;

				Vector3f vector3 = new Vector3f(vector2.getXAsFloat(), (Float)_tmpNode.getUserData("height"), vector2.getYAsFloat());

				quat = new Quaternion();
				quat.lookAt(vector3, Vector3f.UNIT_Y);
			}
			
			else if(rot instanceof IVector3)
			{
				//TODO: calc rot for 3d
			}
			
		}
		return quat;


	}


	/**
	 * Create every 3d-Primitive of a Drawable3D
	 */
	private void createObjects(Node objectNode, DrawableCombiner3d combiner3d, SpaceObject sobj)
	{
		List<Primitive3d> drawList = combiner3d.getPrimitives3d();
		if(drawList == null)
			return;

		objectNode.setUserData("hasEffect", false);
		
		for(Iterator<Primitive3d> itp = drawList.iterator(); itp.hasNext();)
		{
			List <Spatial> spatials =null;
			
			Primitive3d p = (Primitive3d)itp.next();
			Spatial spatial = createPrimitive3d(combiner3d, p, sobj);
			if(spatial != null)
			{
				
				
				
				if((p.getType()==Primitive3d.PRIMITIVE_TYPE_EFFECT))
				{
					objectNode.setUserData("hasEffect", true);
					//now the Effects are in the correct Size, we emit the Particles if there is an effect
//					if(spatial instanceof Node)
//					{
//						Node effectNode = ((Node) spatial);
//						
//						if(effectNode!=null && effectNode.getName().equals("effectNode for : "+"Type: "+ p.getType()+ " HCode " +p.hashCode() + " sobjid " + sobj.hashCode()))
//						{
//							spatials = effectNode.getChildren();
//							
//							if(!spatials.isEmpty())
//							{
//								
//								for(Spatial effect : spatials)
//								{
//									
//									if(effect != null && effect instanceof ParticleEmitter)
//									{
//										
//										ParticleEmitter tmpeffect = ((ParticleEmitter)effect);
//										tmpeffect.emitAllParticles();
//									}
//								}
//								
//
//							}
//						}
//					}

				}
				
				
				objectNode.attachChild(spatial);


			}
		}
	}


	/**
	 * @return the Classloader
	 */
	public ClassLoader getClassloader()
	{
		return _classloader;
	}


	boolean first = true;
	public void stopApp()
	{
		_app.enqueue(new Callable<Void>()
		{
			public Void call() throws Exception
			{	
				_app.stop(false);
				return null;
			}
		});
	}


	public void pauseApp()
	{

		_app.loseFocus();
	}


	/**
	 * @return return the graphic card capabilities
	 */
	public Collection<Caps> getCapabilities()
	{
		return _capabilities;
	}


	/**
	 * @param capabilities the graphic card capabilities
	 */
	public void setCapabilities(Collection<Caps> capabilities)
	{
		this._capabilities = capabilities;
	}


	/**
	 * @return the _animChannels
	 */
	public HashMap<String, AnimChannel> getAnimChannels()
	{
		return _animChannels;
	}


	/**
	 * @param _animChannels the _animChannels to set
	 */
	public void setAnimChannels(HashMap<String, AnimChannel> _animChannels)
	{
		this._animChannels = _animChannels;
	}

	public class MyCallAction implements Callable<Void>
	{
		List<Object[]>  objectList;
		Collection<DrawableCombiner3d> staticvisuals;
		
		public MyCallAction(List<Object[]>  objectList, Collection<DrawableCombiner3d> staticvisuals)
		{
			this.objectList = objectList;
			this.staticvisuals = staticvisuals;
		}
		
		
		
		public Void call()
		{
			if(_firstrun)
			{
				_staticNode = createStatics(staticvisuals);
				_app.setStaticGeometry(_staticNode);
				_app.setChannels(_animChannels);
				_app.setParticleEmiters(_particleEmiters);
				_app.setCanvassize(canvas_.getSize());
				_firstrun = false;
			}
			else
			{
				updateMonkey(objectList);
			}

				
			if(!canvas_.getSize().equals(canvasSize))
			{
				_app.setCleanupPostFilter(true);
				canvasSize = canvas_.getSize();
				_app.setCanvassize(canvas_.getSize());
			}

			

			rendering = false;

			

			return null;
		}
	}

	/**
	 * @return the lights
	 */
	public ArrayList<Light> getLights() {
		return lights;
	}

	/**
	 * @param lights the lights to set
	 */
	public void setLights(ArrayList<Light> lights) {
		this.lights = lights;
	}
	
	public void addLight(Light light)
	{
		lights.add(light);
	}
	
	public void remveLight(Light light)
	{
		lights.remove(light);
	}

	/**
	 * @return the waterProcessor
	 */
	public SimpleWaterProcessor getWaterProcessor() {
		return waterProcessor;
	}

	/**
	 * @param waterProcessor the waterProcessor to set
	 */
	public void setWaterProcessor(SimpleWaterProcessor waterProcessor) {
		this.waterProcessor = waterProcessor;
	}
	
	/**
	 * set the Selected Visual by intId
	 */
	public void setSelected(int selected, DrawableCombiner3d marker)
	{
		_selectedId = selected;
		_marker = marker;
	}

	/**
	 * get the Selected Visual
	 */
	public int getSelected()
	{
		_selectedId = _app.getSelectedTarget();
		return _selectedId;
	}


	public DrawableCombiner3d getMarker()
	{
		return _marker;
	}
	
	public AssetManager getAssetManager()
	{
		return _app.getAssetManager();
	}

	/**
	 * Create a 3d Object
	 * 
	 * @param drawableCombiner3d The 3d combiner.
	 * @param p The primitive3d.
	 * @param obj The object being drawn.
	 */
	public Spatial createPrimitive3d(DrawableCombiner3d drawableCombiner3d, Primitive3d p, SpaceObject sobj)
	{
		return RENDERERS[p.getType()].prepareAndExecuteDraw(drawableCombiner3d, p, sobj, this);
	}

	/**
	 * Update a 3d Object
	 * 
	 * @param drawableCombiner3d The 3d combiner.
	 * @param p The primitive3d.
	 * @param obj The object being drawn.
	 * @param sp The spatial where the object is saved.
	 */
	public void updatePrimitive3d(DrawableCombiner3d drawableCombiner3d, Primitive3d p, SpaceObject sobj, Spatial sp)
	{
		RENDERERS[p.getType()].prepareAndExecuteUpdate(drawableCombiner3d, p, sobj, this, sp);
	}
	

}
