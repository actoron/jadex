package jadex.bdi.examples.antworld.environment;

import jadex.adapter.base.ISimulationService;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.bdi.runtime.Plan;
import jadex.service.IServiceContainer;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This Plan is used to update the environment. Right now it is used as an observer to show observed values of agents / the application. 
 * 
 * 
 */
public class UpdateEnvironmentPlan extends Plan {

	private JFrame frame = new JFrame("Foraging World Oberserver");
	private JPanel rootPanel = new JPanel(new GridLayout(4, 1));
	private JPanel generalSettingsPanel = new JPanel();
	private JPanel dynamicSettingsPanel = new JPanel();

	private JLabel headlineLbl = new JLabel("Application Settings and Measured Values");
	private JLabel tmpLbl = new JLabel("  ");
	private Space2D env = null;
	boolean targetCondition = false;
	private Calendar cal = Calendar.getInstance();

	public UpdateEnvironmentPlan() {
//		System.out.println("\n\n\n\n\n\n\n\n Created: " + this);
		init();
	}

	public void body() {

		while (!targetCondition) {
//			System.out.println("\n\n\n\n\n\n\n\n Executing Udpate Environment Plan.");
			updateDynamicSettingsPnl();
			dynamicSettingsPanel.repaint();
			waitFor(2000);
			// counter++;
		}
	}

	private void init() {
		env = (Space2D) getBeliefbase().getBelief("env").getFact();
		generalSettingsPanel = initGeneralSettingsPnl();
		dynamicSettingsPanel = initDynamicSettingsPnl();
		rootPanel.add(headlineLbl);
		rootPanel.add(generalSettingsPanel);
		rootPanel.add(tmpLbl);
		rootPanel.add(dynamicSettingsPanel);
		rootPanel.setBackground(Color.white);

		frame.setSize(380, 400);
		// frame.setsetLocation(SGUI.calculateMiddlePosition(this));
		frame.setContentPane(rootPanel);
		frame.setVisible(true);
	}

	/**
	 * Update the Panel with the dynamic Application Settings which change
	 * during execution.
	 * 
	 * @return
	 */
	private void updateDynamicSettingsPnl() {
		dynamicSettingsPanel.invalidate();

		targetCondition = checkTargetCondition();
		if (targetCondition) {
			dynamicSettingsPanel.remove(1);
			dynamicSettingsPanel.add(new JLabel("Simulation End Time: " + String.valueOf(longToDateString(System.currentTimeMillis()))), 1);
			
			//Stop Siumlation when target condition true.
			IServiceContainer container = getExternalAccess().getServiceContainer();
			ISimulationService simServ = (ISimulationService)container.getService(ISimulationService.class);
			simServ.pause();
		}

		dynamicSettingsPanel.remove(2);
		dynamicSettingsPanel.add(new JLabel("Collected Pieces of Food: " + getObjectProperty("nest", "stock")), 2);

		int[] antInfo = getAntPopulationInfo("ant", "eval:walkedSteps");
		dynamicSettingsPanel.remove(3);
		dynamicSettingsPanel.add(new JLabel("Ant with Min Fields Walked: " + String.valueOf(antInfo[0])), 3);
		dynamicSettingsPanel.remove(4);
		dynamicSettingsPanel.add(new JLabel("Ant with Max Fields Walked: " + String.valueOf(antInfo[1])), 4);

		antInfo = getAntPopulationInfo("ant", "eval:carriedFood");
		dynamicSettingsPanel.remove(5);
		dynamicSettingsPanel.add(new JLabel("Ant with Min Carried Food: " + String.valueOf(antInfo[0])), 5);
		dynamicSettingsPanel.remove(6);
		dynamicSettingsPanel.add(new JLabel("Ant with Max Carried Food: " + String.valueOf(antInfo[1])), 6);

		dynamicSettingsPanel.revalidate();
		dynamicSettingsPanel.repaint();

	}

	/**
	 * Init the Panel with the dynamic Application Settings which change during
	 * execution.
	 * 
	 * @return
	 */
	private JPanel initDynamicSettingsPnl() {
		JPanel panel = new JPanel(new GridLayout(7, 1));
		panel.add(new JLabel("Simulation Start Time: " + String.valueOf(longToDateString(System.currentTimeMillis()))));
		panel.add(new JLabel("Simulation End Time: Simulation running..."));
		panel.add(new JLabel("Collected Pieces of Food: " + String.valueOf(-1)));
		panel.add(new JLabel("Ant with Min Fields Walked: " + String.valueOf(-1)));
		panel.add(new JLabel("Ant with Max Fields Walked: " + String.valueOf(-1)));
		panel.add(new JLabel("Ant with Min Carried Food: " + String.valueOf(-1)));
		panel.add(new JLabel("Ant with Min Carried Food: " + String.valueOf(-1)));
		return panel;
	}

	/**
	 * Init the Panel with the general Application Setting which do not change
	 * during execution.
	 * 
	 * @return
	 */
	private JPanel initGeneralSettingsPnl() {
		JPanel panel = new JPanel(new GridLayout(6, 1));
		panel.add(new JLabel("Finish Condition: All pieces of food are in the nest."));
		panel.add(new JLabel("Number of Foraging Agents: " + String.valueOf(getObjectCount("ant"))));
		panel.add(new JLabel("Vision Range of Foraging Agents: " + String.valueOf(getObjectProperty("ant", "vision_range"))));
		panel.add(new JLabel("Number of Initial Food Sources: " + String.valueOf(getObjectCount("foodSource"))));
		panel.add(new JLabel("Number of Food Pieces: " + String.valueOf(getObjectCount("food"))));
		panel.add(new JLabel("Number of Nests: " + String.valueOf(getObjectCount("nest"))));
		return panel;
	}

	/**
	 * Return number of ISpaceObjects in Space from specified type.
	 * 
	 * @param type
	 * @return
	 */
	private int getObjectCount(String type) {
		return env.getSpaceObjectsByType(type).length;
	}

	/**
	 * Return a property from a ISpaceObject
	 * 
	 * @param property
	 * @param type
	 * @return
	 */
	private String getObjectProperty(String type, String property) {
		ISpaceObject[] ants = env.getSpaceObjectsByType(type);
		return ants[0].getProperty(property).toString();
	}

	/*
	 * Checks the target condition of the application
	 */
	private boolean checkTargetCondition() {
		int numberOfAllFoodPieces = getObjectCount("food");
		int numberOfFoodInNest = Integer.valueOf(getObjectProperty("nest", "stock")).intValue();

		return numberOfAllFoodPieces == numberOfFoodInNest;
	}

	/**
	 * Computes the ant that has collected the most / less "type of thing",e g.
	 * food or walked steps. [0] = max, [1] min.
	 * 
	 * @return
	 */
	private int[] getAntPopulationInfo(String objectType, String property) {
		int minValue = 0;
		int maxValue = 0;
		ISpaceObject[] ants = env.getSpaceObjectsByType(objectType);

		for (int i = 0; i < ants.length; i++) {
			int value = Integer.valueOf(ants[i].getProperty(property).toString()).intValue();
			if (i == 0) {
				minValue = value;
				maxValue = value;
			} else {
				if (minValue > value) {
					minValue = value;
				}
				if (maxValue < value) {
					maxValue = value;
				}
			}
		}
		return new int[] { minValue, maxValue };
	}

	/**
	 * Used to generate DateString from a long-type
	 * 
	 * @param time
	 * @return
	 */
	private String longToDateString(long time) {
		cal.setTimeInMillis(time);
		DateFormat formater = DateFormat.getTimeInstance(DateFormat.DEFAULT);
		return formater.format(cal.getTime());
	}
}