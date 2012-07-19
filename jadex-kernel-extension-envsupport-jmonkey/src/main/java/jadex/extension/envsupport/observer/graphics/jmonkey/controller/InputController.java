//package jadex.extension.envsupport.observer.graphics.jmonkey.controller;
//
//import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;
//
//import com.jme3.app.state.AbstractAppState;
//import com.jme3.input.InputManager;
//import com.jme3.input.KeyInput;
//import com.jme3.input.MouseInput;
//import com.jme3.input.controls.ActionListener;
//import com.jme3.input.controls.KeyTrigger;
//import com.jme3.input.controls.MouseAxisTrigger;
//import com.jme3.input.controls.MouseButtonTrigger;
//
//public class InputController extends AbstractAppState 
//{
//	
//	private MonkeyApp app;
//	private InputManager inputManager;
//
//    public InputController(MonkeyApp app) {
//        this.app = app;
//        this.inputManager = app.getInputManager();
//        initKeys();
//     }
//
//    /** Custom Keybinding: Map named actions to inputs. */
//	private void initKeys() {
//
//		inputManager.addMapping("Select", new MouseButtonTrigger(
//				MouseInput.BUTTON_RIGHT));
//		// You can map one or several inputs to one named action
//		inputManager.addMapping("Random", new KeyTrigger(KeyInput.KEY_SPACE));
//
//		inputManager.addMapping("Hud", new KeyTrigger(KeyInput.KEY_F1));
//		inputManager.addMapping("ChaseCam", new KeyTrigger(KeyInput.KEY_F3));
//		inputManager.addMapping("FollowCam", new KeyTrigger(KeyInput.KEY_F4));
//		inputManager.addMapping("ChangeCam", new KeyTrigger(KeyInput.KEY_F6));
//		inputManager.addMapping("Grid", new KeyTrigger(KeyInput.KEY_F8));
//		inputManager.addMapping("Fullscreen", new KeyTrigger(KeyInput.KEY_F11),new KeyTrigger(KeyInput.KEY_F));
//		inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
//		inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
//
//		ActionListener actionListener = new ActionListener() {
//			public void onAction(String name, boolean keyPressed, float tpf) {
//
//				if (keyPressed && name.equals("Hud")) {
//					if (app.isHudactive()) {
//						app.niftyDisplay.getNifty().gotoScreen("hud");
//					} else {
//						app.niftyDisplay.getNifty().gotoScreen("default");
//					}
//
//					app.setHudactive(!app.isHudactive()) ;
//				}
//
//				if (keyPressed && name.equals("Fullscreen")) {
//					app.fireFullscreen();
//
//				}
//
//				if (keyPressed && name.equals("Random")) {
//					app.setHeight();
//
//				}
//				if (keyPressed && name.equals("Grid")) {
//					app.showHideGrid();
//				}
//
//				if (name.equals("Select") && keyPressed) {
//					app.fireSelection();
//				}
//
//				else if (!_chaseCamera&&name.equals("ZoomIn")) {
//					moveCamera(6, false);
//				} else if (!_chaseCamera &&name.equals("ZoomOut")) {
//					moveCamera(-6, false);
//				} else if (!_chaseCamera && keyPressed && name.equals("ChangeCam")) {
//					_walkCam = !_walkCam;
//
//				}
//
//				else if (keyPressed && name.equals("ChaseCam")) {
//
//					_chaseCamera = !_chaseCamera;
//					
//					if (_chaseCamera) {
//						if (_selectedSpatial != null)
//						{
//							_focusCam.setSpatial(_selectedSpatial);
//						}
//						else
//						{
//							_focusCam.setSpatial(_staticNode);
//						}
//						_focusCam.setEnabled(true);
//
//
//					} else 
//					{
//						_focusCam.setEnabled(false);
//						flyCamera.setEnabled(true);
//					}
//
//
//
//				}
//			}
//
//		};
//
//		inputManager.addListener(actionListener, new String[] { "Hud" });
//		inputManager.addListener(actionListener, new String[] { "Random" });
//		inputManager.addListener(actionListener, new String[] { "Grid" });
//		inputManager.addListener(actionListener, new String[] { "ChaseCam" });
//		inputManager.addListener(actionListener, new String[] { "ChangeCam" });
//		inputManager.addListener(actionListener, new String[] { "ZoomIn" });
//		inputManager.addListener(actionListener, new String[] { "ZoomOut" });
//		inputManager.addListener(actionListener, new String[] { "Select" });
//		inputManager.addListener(actionListener, new String[] { "FollowCam" });
//		inputManager.addListener(actionListener, new String[] { "Fullscreen" });
//
//	}
//}
