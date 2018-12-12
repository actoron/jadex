package jadex.extension.envsupport.observer.graphics.jmonkey.appstate.userinteraction;

import java.util.Iterator;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.math.Vector3Int;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;


public class SelectionLogic
{
	private MonkeyApp		app;

	private InputManager	inputManager;

	private Camera			cam;

	public SelectionLogic(AppStateManager stateManager, Application app)
	{
		this.app = (MonkeyApp)app;
		this.inputManager = this.app.getInputManager();
		this.cam = this.app.getCamera();
	}

	private CollisionResults fireRaytrace(Node collidenode)
	{
		// Reset results list.
		CollisionResults results = new CollisionResults();
		// Convert screen click
		// to 3d position
		Vector2f click2d = inputManager.getCursorPosition();

		Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
		Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalize();

		// Aim the ray from the clicked spot forwards.
		Ray ray = new Ray(click3d, dir);

		// rootNode.collideWith(ray, results);
		collidenode.collideWith(ray, results);

		return results;
	}

	public Vector3Int getSelectedVector3Int(Node collidenode)
	{
		Vector3Int ret = null;
		CollisionResults results = fireRaytrace(collidenode);

		if(results.size() > 0)
		{

			Vector3f contact = results.getClosestCollision().getContactPoint();

			Vector3f worldcontact = contact.divideLocal(this.app.getAppScaled());

			Vector3Int intworld = new Vector3Int(Math.round(worldcontact.getX()), Math.round(worldcontact.getY()), Math.round(worldcontact.getZ()));

			ret = intworld;

		}
		return ret;
	}


	// TODO: still big hack
	public Object computeSelectedId(Node collidenode)
	{
		Object ret = -1;

		CollisionResults results = fireRaytrace(collidenode);
		if(results.size() > 0)
		{

			Geometry contact = results.getClosestCollision().getGeometry();

//			int count = 0;
//			for(Iterator<CollisionResult> i = results.iterator(); i.hasNext();)
//			{
//				CollisionResult s = i.next();
//				count++;
//				System.out.println("result: " + count + " " + s.getGeometry().getName());
//			}

			Spatial parent = contact;
//			System.out.println("parent name: " + parent.getName());

			if(parent.getName().startsWith("Static"))
			{
				Iterator<CollisionResult> iterator = results.iterator();
				if(iterator.hasNext())
				{
					iterator.next();
					if(iterator.hasNext())
					{
						parent = iterator.next().getGeometry();
					}
				}
			}
			while(parent.getParent() != null && parent.getName() != null && !Character.isDigit(parent.getName().charAt(0)))
			{

				parent = parent.getParent();
			}

			ret = parent.getName();

//			System.out.println("ret name: " + parent.getName());

		}
		return ret;
	}

	public IVector3 getMouseContactPoint(Node collidenode)
	{
		IVector3 ret = null;
		CollisionResults results = fireRaytrace(collidenode);

		if(results.size() > 0)
		{

			Vector3f contact = results.getClosestCollision().getContactPoint().divideLocal(this.app.getAppScaled());


			ret = new Vector3Double(contact.getX(), contact.getY(), contact.getZ());

			// System.out.println("world contact point " + ret);


		}
		return ret;
	}

	// public void fireSelection(Node collidenode)
	// {
	// CollisionResults results = fireRaytrace(collidenodes);
	//
	// int selection = -1;
	// Spatial selectedspatial = null;
	// if(results.size() > 0)
	// {
	//
	//
	// System.out.println("app scaled " + appScaled + " contact point " +
	// results.getClosestCollision().getContactPoint());
	//
	// Vector3f contact = results.getClosestCollision().getContactPoint();
	//
	// Geometry target = results.getClosestCollision().getGeometry();
	//
	// System.out.println("target parent " + target.getParent().getName());
	//
	// System.out.println("target parent parent  " +
	// target.getParent().getParent().getName());
	//
	// Vector3f worldcontact = contact.divideLocal(appScaled);
	//
	// selectedworldcoord = new Vector3Int((int)worldcontact.getX(),
	// (int)worldcontact.getY(), (int)worldcontact.getZ());
	//
	// // for(Iterator<CollisionResult> itp = results.iterator();
	// // itp.hasNext();)
	// // {
	// // CollisionResult result = itp.next();
	// //
	// // Geometry geo = result.getGeometry();
	// // System.out.println("geo: " + geo.getName());
	// // }
	//
	// }
	//
	// // Geometry target = results.getClosestCollision().getGeometry();
	// //
	// // System.out.println("target: " + target.getName());
	// // // Here comes the
	// // // action:
	// // Spatial selectedsp = target;
	// //
	// // try {
	// //
	// //
	// // // we look for the SpaceObject-Parent
	// // if (selectedsp != null) {
	// // while (!Character.isDigit(selectedsp.getName().charAt(0))) {
	// // selectedsp = selectedsp.getParent();
	// // }
	// //
	// // selection = Integer.parseInt(selectedsp.getName());
	// // selectedspatial = selectedsp;
	// // System.out.println("selected!");
	// // }
	// // }
	// // catch (NullPointerException e) {
	// // System.out.println("AMonkeyFunctions: Selection NULL");
	// // }
	// // }
	// // setSelectedTarget(selection);
	// // setSelectedSpatial(selectedspatial);
	// // if(focusCamActive)
	// // {
	// // if(selectedspatial!=null)
	// // focusCam.setSpatial(selectedspatial);
	// // else
	// // focusCam.setSpatial(staticNode);
	// // }
	//
	// }

}
