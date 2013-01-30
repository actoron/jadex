package jadex.agentkeeper.view;

import jadex.agentkeeper.game.state.creatures.SimpleCreatureState;
import jadex.agentkeeper.game.state.player.SimplePlayerState;
import jadex.agentkeeper.game.userinput.UserEingabenManager;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.agentkeeper.util.ISpaceStrings;
import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.extension.envsupport.math.Vector3Int;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;
import jadex.extension.envsupport.observer.graphics.jmonkey.appstate.gui.DefaultGuiController;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;


/**
 * The Methods from all GUI Input are implemented here
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class KeeperGuiController extends DefaultGuiController
{


	private Node				rootNode;

	private boolean				toggleStats	= true;

	private SpaceObject			selected;

	private ISpaceController	spaceController;

	private UserEingabenManager	usermanager;

	private MonkeyApp			app;

	private SimpleCreatureState	creatureState;

	private SimplePlayerState	playerState;
	
	private TextRenderer manaStatusRenderer;
	
	private TextRenderer claimedTilesRender;

	public KeeperGuiController(SimpleApplication app, ISpaceController spacecontroller)
	{
		this.app = (MonkeyApp)app;
		rootNode = this.app.getRootNode();

		this.spaceController = spacecontroller;
		this.usermanager = (UserEingabenManager)spacecontroller.getProperty("uem");
		this.creatureState = (SimpleCreatureState)spaceController.getProperty(ISpaceStrings.CREATURE_STATE);
		this.playerState = (SimplePlayerState)spaceController.getProperty(ISpaceStrings.PLAYER_STATE);


	}


	/** Nifty GUI ScreenControl methods */
	public void bind(Nifty nifty, Screen screen)
	{
		this.nifty = nifty;
		this.screen = screen;
	}


	public void fireFullscreen()
	{
		app.fireFullscreen();

	}

	public void guiActive()
	{
		app.setGuiActive(true);
	}

	public void options()
	{
		// spaceController.getSpaceObjectsByGridPosition(new Vector2Int(10, 10),
		// null);


	}

	public void setPerform()
	{

		app.getStateManager().getState(StatsAppState.class).setDisplayStatView(toggleStats);
		app.getStateManager().getState(StatsAppState.class).setDisplayFps(toggleStats);
		toggleStats = !toggleStats;

	}

	public void setGrid()
	{
		if(rootNode.getChild("gridNode") != null)
		{
			rootNode.detachChild(((MonkeyApp)app).getGridNode());

		}
		else
		{
			rootNode.attachChild(((MonkeyApp)app).getGridNode());
		}
	}


	public void quitGame()
	{
		app.stop();
	}


    public void update(float tpf) {
		
    	
		String manatext = ""+(int)playerState.getMana();
		while(manatext.length()<=7)
		{
			manatext = "0".concat(manatext);
		}
		manaStatusRenderer.setText(manatext);
		claimedTilesRender.setText("+"+playerState.getClaimedSectors());

    }
    

	public void onStartScreen()
	{
		Element manaT = this.app.getNiftyDisplay().getNifty().getCurrentScreen().findElementByName("manastatus");
		this.manaStatusRenderer = manaT.getRenderer(TextRenderer.class);
		
		Element claimedT = this.app.getNiftyDisplay().getNifty().getCurrentScreen().findElementByName("claimedtiles");
		this.claimedTilesRender = claimedT.getRenderer(TextRenderer.class);
		
		

		
		Element impText = this.app.getNiftyDisplay().getNifty().getCurrentScreen().findElementByName("imp_total");
		TextRenderer impRender = impText.getRenderer(TextRenderer.class);
		impRender.setText("" + creatureState.getCreatureCount(InitMapProcess.IMP));

		Element goblinT = this.app.getNiftyDisplay().getNifty().getCurrentScreen().findElementByName("goblin_total");
		TextRenderer goblinR = goblinT.getRenderer(TextRenderer.class);
		goblinR.setText("" + creatureState.getCreatureCount(InitMapProcess.GOBLIN));

		Element warlockT = this.app.getNiftyDisplay().getNifty().getCurrentScreen().findElementByName("warlock_total");
		TextRenderer warlockR = warlockT.getRenderer(TextRenderer.class);
		warlockR.setText("" + creatureState.getCreatureCount(InitMapProcess.WARLOCK));

		Element orcT = this.app.getNiftyDisplay().getNifty().getCurrentScreen().findElementByName("troll_total");
		TextRenderer orcR = orcT.getRenderer(TextRenderer.class);
		orcR.setText("" + creatureState.getCreatureCount(InitMapProcess.TROLL));

	}

	public void onEndScreen()
	{
	}


}