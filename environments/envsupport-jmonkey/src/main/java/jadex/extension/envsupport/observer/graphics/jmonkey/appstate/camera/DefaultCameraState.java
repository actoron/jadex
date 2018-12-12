package jadex.extension.envsupport.observer.graphics.jmonkey.appstate.camera;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;


public class DefaultCameraState extends AbstractAppState
{

	private MonkeyApp		app;

	private Camera			cam;

	private float			appSize;

	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (MonkeyApp)app;
		this.cam = this.app.getCamera();
		this.appSize = this.app.getAppSize();

		initCam();

	}


	public void initCam()
	{

		/** Configure cam to look at scene */
		cam.setLocation(new Vector3f(appSize / 2, appSize / 2, appSize / 2));
		cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
		cam.setFrustumNear(1f);
		cam.setFrustumFar(appSize * 5);

		this.app.getFlyByCamera().setEnabled(true);
		
		DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -1.0f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(ColorRGBA.White));
        this.app.getRootNode().addLight(dl);    

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        this.app.getRootNode().addLight(al);


	}
}