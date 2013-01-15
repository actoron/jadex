package jadex.extension.envsupport.observer.graphics.jmonkey.appstate;

import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;
import jadex.extension.envsupport.observer.graphics.jmonkey.camera.IsoCamera;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;


public class CameraState extends AbstractAppState
{

	private MonkeyApp		app;

	private Node			rootNode;

	private AssetManager	assetManager;

	private AppStateManager	stateManager;

	private InputManager	inputManager;

	private ViewPort		viewPort;

	private Camera			cam;

	private float			appSize;

	private float			appScaled;

	private String			cameraSelection;

	private Node			camNode;


	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (MonkeyApp)app;
		this.rootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.stateManager = this.app.getStateManager();
		this.inputManager = this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.cam = this.app.getCamera();
		this.appSize = this.app.getAppSize();
		this.appScaled = this.app.getAppScaled();
		this.cameraSelection = this.app.getCameraSelection();

		camNode = new Node("camNode");


		initCam();

	}


	public void initCam()
	{


		/** Configure cam to look at scene */
		cam.setLocation(new Vector3f(appSize / 2, 0, appSize / 2));
		cam.lookAt(new Vector3f(appSize / 2, 0, appSize / 2), Vector3f.UNIT_Y);
		cam.setFrustumNear(1f);
		cam.setFrustumFar(appSize * 5);


		app.getFlyByCamera().setEnabled(false);


		if(cameraSelection.equals("Default"))
		{
			cam.setLocation(new Vector3f(appSize / 2, appSize / 4, appSize / 2));
			// The Fly Camera

			app.getFlyByCamera().setUpVector(Vector3f.UNIT_Y);
			app.getFlyByCamera().registerWithInput(inputManager);
			app.getFlyByCamera().setMoveSpeed(appScaled);
			app.getFlyByCamera().setDragToRotate(true);
			app.getFlyByCamera().setEnabled(true);


			app.getFlyByCamera().setEnabled(true);
		}
		else if(cameraSelection.equals("Iso"))
		{

			IsoCamera dungeonCam = new IsoCamera(cam, inputManager, app);

			camNode = dungeonCam.getCamNode();
			rootNode.attachChild(camNode);
		}
		else if(cameraSelection.equals("Focus"))
		{

			// // The Focus Camera
			// focusCam = new FocusCamera(cam, staticNode, inputManager);
			// focusCam.setUpVector(Vector3f.UNIT_Y);
			// focusCam.setSmoothMotion(true);
			// focusCam.setDragToRotate(false);
			// focusCam.setDefaultDistance(1000f);
			// focusCam.setMaxDistance(2000f);
			// focusCam.setMinDistance(50f);
			// focusCam.setZoomSpeed(20f);
			// focusCam.setEnabled(true);
		}


		// cam.

		// Change the mappings we don't want

		//
		// Camera cam_n = cam.clone();
		// cam.setViewPort( 0.0f , 1.0f , 0.0f , 1.0f );
		// cam_n.setViewPort( 0.0f , 0.1f , 0.85f , 1.0f );
		// cam_n.setLocation(new Vector3f(appSize/2, appSize/2,
		// appSize/2));
		// cam_n.lookAt(new Vector3f(appSize/2, 0.001f, appSize/2),
		// Vector3f.UNIT_Y);
		//
		//
		//
		//
		// ViewPort view = renderManager.createMainView("View of camera #1",
		// cam);
		// view.setEnabled(true);
		// view.setClearFlags(true, true, true);
		// view.attachScene(rootNode);
		// view.setBackgroundColor(ColorRGBA.Black);
		// view.addProcessor(fpp);
		// //
		// ViewPort view_n = renderManager.createMainView("View of camera #2",
		// cam_n);
		// view_n.setEnabled(true);
		// view_n.setClearFlags(true, true, true);
		// view_n.attachScene(rootNode);
		// view_n.setBackgroundColor(ColorRGBA.Black);

	}
}