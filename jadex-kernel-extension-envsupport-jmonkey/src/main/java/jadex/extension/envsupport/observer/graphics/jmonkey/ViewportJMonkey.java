package jadex.extension.envsupport.observer.graphics.jmonkey;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.IVector3;
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
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.Text3dJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.TorusJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.SkyJMonkeyRenderer;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.TerrainJMonkeyRenderer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;
import jadex.extension.envsupport.observer.perspective.Perspective3D;

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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Caps;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;


/**
 * This class manages the 3d Visualization and all user interaction.
 * 
 * @author Flipflop
 */
/**
 * @author 7willuwe
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

	private Node							_staticNode		= new Node("Static Geometry");

	private boolean							_firstrun		= true;

	// The Node to be updated
	private Node							_tmpNode;

	private Node							marker;

	private int								_lastselect		= -1;


	private Callable<Object>				renderFrameAction_;

	private float							_scale			= 1;


	/** The overal scale for the Application */
	private final static int				_scaleApp		= 500;


	
	/**
	 *  Animation Stuff
	 */
	private HashMap<String, AnimChannel> _animChannels; 

	/** The 3d renderers. */
	private static final IJMonkeyRenderer[]	RENDERERS		= new IJMonkeyRenderer[10];
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
	}

	/**
	 * Creates a new ViewportJMonkey
	 * 
	 * @param perspective the selected Perspective
	 * @param ClassLoader the Classloader
	 */
	public ViewportJMonkey(IPerspective perspective, ClassLoader classloader, IVector3 spacesize, boolean isGrid)
	{
		super(perspective, spacesize, isGrid);

		// Context ClassLoader for Assets
		Thread.currentThread().setContextClassLoader(classloader);
		_classloader = classloader;
		
		// Animation Channels
		_animChannels = new HashMap<String, AnimChannel>();

		//TODO: scaling komplett 
		_scale = _scaleApp / areaSize_.getXAsFloat();
		
		_app = new MonkeyApp(_scaleApp, areaSize_.getXAsFloat(), isGrid);
		AppSettings settings = new AppSettings(true);

		
		
		_app.setPauseOnLostFocus(false);
		_app.setSettings(settings);
		_app.createCanvas();
		_app.startCanvas();


		_context = (JmeCanvasContext)_app.getContext();
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
				_geometryNode = updateMonkey(objectList_);
				

				if(_firstrun)
				{
					_capabilities = _app.getCaps();
					System.out.println("capabilities: \n" + _capabilities);
					_staticNode = createStatics(_staticvisuals);
					_app.setStaticGeometry(_staticNode);
					_app.setChannels(_animChannels);
					_firstrun = false;
					_staticNode.setLocalScale(_scale);
					_geometryNode.setLocalScale(_scale);
				}
				_app.setGeometry(_geometryNode);

				rendering = false;
				return null;
			};
		};

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

	/**
	 * Create a 3d Object
	 * 
	 * @param drawableCombiner3d The 3d combiner.
	 * @param p The primitive3d.
	 * @param obj The object being drawn.
	 */
	public Spatial createPrimitive3d(DrawableCombiner3d drawableCombiner3d, Primitive3d p, Object obj)
	{
		return RENDERERS[p.getType()].prepareAndExecuteDraw(drawableCombiner3d, p, obj, this);
	}

	/**
	 * Update a 3d Object
	 * 
	 * @param drawableCombiner3d The 3d combiner.
	 * @param p The primitive3d.
	 * @param obj The object being drawn.
	 * @param sp The spatial where the object is saved.
	 */
	public void updatePrimitive3d(DrawableCombiner3d drawableCombiner3d, Primitive3d p, Object obj, Spatial sp)
	{
		RENDERERS[p.getType()].prepareAndExecuteUpdate(drawableCombiner3d, p, obj, this, sp);

	}

	/**
	 * This start´s the jMonkey Application
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
	private Node createStatics(ArrayList<DrawableCombiner3d> staticvisuals)
	{
		for(DrawableCombiner3d combiner3d : _staticvisuals)
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
	private Node updateMonkey(List<Object> objectList)
	{
		synchronized(objectList)
		{
			// Clear the Refresh "listener"
			// This List hold´s every object that has been drawn in the last
			// Draw-"round". The List is necessary to make it possible to check
			// for Object´s that has to be removed from the 3d Szene
			_drawObjectsRefresh = new HashSet<Object>();

			// Step 2 : Create and/or update all visible Visuals
			createAndUpdateVisuals(objectList);

			// Step 3 : Set and Create the Visualiszation of the Selected Object
			int selected = getSelected();
			createAndUpdateVisualSelection(selected);

			// Step 4 : Update deleted Objects (Remove them from gemetryNode)
			for(Iterator<Object> itr = _drawObjectsLast.iterator(); itr.hasNext();)
			{
				Object id = itr.next();
				if(!_drawObjectsRefresh.contains(id))
				{
					_geometryNode.getChild(id.toString()).removeFromParent();
				}

			}
			_drawObjectsLast = new HashSet<Object>(_drawObjectsRefresh);

		}
		// Step 5 Return the freshest Version of the geometryNode
		return _geometryNode;

	}


	/**
	 * the Visuals for each Object are created and updated here
	 */
	private void createAndUpdateVisuals(List<Object> objectList)
	{
		for(Iterator<Object> it = objectList.iterator(); it.hasNext();)
		{

			Object[] o = (Object[])it.next();
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
			//TODO: good solution?
			sizeDrawable = sizeDrawable.divide(2);

			if(!_drawObjects.contains(identifier))
			{
				_drawObjects.add(identifier);
				Node objectNode = new Node(identifier.toString());
				// Use this distance Vector to calculate the Rotation of the
				// Object
//				Quaternion quat = calculateRotation(position, objectNode.getLocalTranslation(), rotation3d);
//
//				if(quat != null)
//					objectNode.setLocalRotation(quat);
				objectNode.setLocalScale(sizeDrawable);
				objectNode.setLocalTranslation(position);

				// The visuals for each DrawableCombiner3d (stored in a the
				// objectNode, influenced by it´s holder the SpaceObject
				// (sobj)...
				createObjects(objectNode, combiner3d, sobj);

				// ...and attached to the gemetryNode, holder of all dyn.
				// visuals in the Scene
				_geometryNode.attachChild(objectNode);

			}
			else
			{
				Spatial node = _geometryNode.getChild(identifier.toString());
				_tmpNode = (Node)node;

				// Calculate the Direction
				Quaternion quat = calculateRotation(position, _tmpNode.getLocalTranslation(), rotation3d);

				if(quat != null)
					_tmpNode.setLocalRotation(quat);

				_tmpNode.setLocalScale(sizeDrawable);

				_tmpNode.setLocalTranslation(position);


				List<Primitive3d> drawList = combiner3d.getPrimitives3d();
				if(drawList != null)
				{
					for(Iterator<Primitive3d> itx = drawList.iterator(); itx.hasNext();)
					{
						Primitive3d p = (Primitive3d)itx.next();

						identifier = "Type: " + p.getType() + " HCode " + p.hashCode();

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
	 * the Dynamic Visuals für the Visualization of the Selection are created
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
				Node node = (Node)_geometryNode.getChild("" + _lastselect);
				if(node.getChild("Marker") != null)
				{
					node.getChild("Marker").removeFromParent();
				}
			}

			if(_lastselect != selected)
			{
				perp.leftClicked("" + selected);
				Node node = (Node)_geometryNode.getChild("" + selected);
				marker = new Node("Marker");
				createObjects(marker, getMarker(), _selectedObj);

				if(node.getChild("Marker") == null)
				{
					node.attachChild(marker);
				}
			}
		}
		else if(selected == -1 && _lastselect != -1)
		{
			perp.leftClicked("" + selected);
			Node node = (Node)_geometryNode.getChild("" + _lastselect);
			if(node.getChild("Marker") != null)
			{
				node.getChild("Marker").removeFromParent();
			}
		}


		_lastselect = selected;
	}

	/**
	 * Handle the Height Value. Check if it´s set or not
	 */
	private Vector3f handleHeightValue(Object posObj)
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
	private Quaternion calculateRotation(Vector3f newp, Vector3f oldp, boolean rotation3d)
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

		Vector3f direction = newpos.subtract(oldpos);
		if(!direction.equals(Vector3f.ZERO))
		{
			Quaternion quat = new Quaternion();
			quat.lookAt(direction, Vector3f.UNIT_Y);
			return quat;
		}

		return null;

	}


	/**
	 * Create every 3d-Primitive of a Drawable3D
	 */
	private void createObjects(Node objectNode, DrawableCombiner3d combiner3d, SpaceObject sobj)
	{
		List<Primitive3d> drawList = combiner3d.getPrimitives3d();
		if(drawList == null)
			return;

		for(Iterator<Primitive3d> itp = drawList.iterator(); itp.hasNext();)
		{
			Primitive3d p = (Primitive3d)itp.next();
			Spatial spatial = createPrimitive3d(combiner3d, p, sobj);
			if(spatial != null)
			{
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


	public void stopApp()
	{


		_app.stop();

//		System.out.println("ViewportJmonkey: STOP STOP STOP");

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




}
