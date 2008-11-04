package jadex.tools.common;

import jadex.commons.SGUI;
import jadex.commons.SUtil;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *
 */
public class GuiProperties
{
	//-------- constants --------

	/**
	 * The image  for (m/r) elements.
	 */
	private static UIDefaults icons = new UIDefaults(new Object[]
	{
		"RMAAction.LoggerActionIcon", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/logger.png"),
		"RMAAction.JadexIntrospectorActionIcon", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/jdxintro.png"),
		"RMAAction.TracerActionIcon", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/tracer.png"),

		// Runtime elements.
		"RCapability", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_capability_small.png"),
		"RBDIAgent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent.png"),
		"RBeliefbase", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/beliefbase2.png"),
		"RGoalbase", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/goalbase2.png"),
		"RPlanbase", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/planbase2.png"),
		"RBelief", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bulb2.png"),
		"RBeliefSet", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bulb2.png"),
		"RBeliefSetContainer", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bulbs2.png"),
		"RBeliefReference", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bulb2ref.png"),
		"RBeliefSetReference", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bulb2ref.png"),
		"RBeliefSetReferenceContainer", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bulbs2ref.png"),
//		"RAbstractGoal",	SGUI.makeIcon(""),
		"RGoal", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2.png"), // Used for goalbase panel (hack???)
		"RMetaGoal", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2meta.png"),
		"RMetaGoalReference", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2refmeta.png"),
		"RAchieveGoal", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2a.png"),
		"RMaintainGoal", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2m.png"),
		"RPerformGoal", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2p.png"),
		"RQueryGoal", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2q.png"),
		"RAchieveGoalReference", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2refa.png"),
		"RMaintainGoalReference", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2refm.png"),
		"RPerformGoalReference", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2refp.png"),
		"RQueryGoalReference", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/cloud2refq.png"),
		"RProcessGoal", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/gearwheel2.png"),
		"RPlan", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/plan2.png"),
		"RMessageEvent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_message_small.png"),
		"RInternalEvent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/internal.png"),
		"RGoalEvent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/goalevent.png"),

		// Model elements.
		"MCapability", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_capability_small.png"),
		"MBDIAgent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent.png"),
		"Capability", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_capability_small.png"),
		"Agent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent.png"),
		"MCapability_broken", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_capability_broken.png"),
		"MBDIAgent_broken", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent_broken.png"),
		"Capability_broken", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_capability_broken.png"),
		"Agent_broken", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent_broken.png"),

		// APL candidates.
		"PlanInstanceInfo", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/plan2.png"),
		"WaitqueueInfo", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/wplan.png"),
		"PlanInfo", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/pplan.png"),

		// Arrows
		"right", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/arrowright.png"),
		"top", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/arrowtop.png"),
		"up", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/arrowup.png"),
		"down", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/arrowdown.png"),
		"bottom", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/arrowbottom.png"),
		"delete", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/delete.png"),
		"empty", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/empty.png"),
		"bug_icon", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bug_small.png"),

		// Agenda actions.
		"ProcessEventAction", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_message_small.png"),
		"FindApplicableCandidatesAction", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_message_small.png"),
		"SelectCandidatesAction", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_message_small.png"),
		"ScheduleCandidatesAction", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_message_small.png"),
		"ExecutePlanStepAction", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/plan2.png")
	});

	/** The jadex help set. */
	protected static HelpBroker jadex_hb;

	/** tells if a help set has been searched for */
	protected static boolean    searched_for_help_set;
	
	/**
	 *  Get an icon for an element.
	 *  @param classname	The class name of the element (e.g. RBelief).
	 *  @return	The icon for the element.
	 */
	public static Icon	getElementIcon(Object classname)
	{
		return icons.getIcon(classname);
	}

	/**
	 *  @param cmp
     *  @param helpID
     *  @return helpbroker
     */
    public static HelpBroker setupHelp(Component cmp, String helpID)
    {
        HelpBroker hb = getJadexHelpBroker();
        if(hb != null)
        {
            CSH.setHelpIDString(cmp, helpID);
            hb.enableHelpKey(cmp, helpID, hb.getHelpSet());
        }

        return hb;
    }

	/*
 	 *  Get the Jadex help broker.
	 */
	public static HelpBroker getJadexHelpBroker()
	{
		if(!searched_for_help_set)
		{
			searched_for_help_set=true;
			// Create main help set and broker
			try
			{
				ClassLoader cl = SUtil.class.getClassLoader();
				URL url = HelpSet.findHelpSet(cl, "jadex/help/jhelpset");
				ExtendedHelpSet hs =  new ExtendedHelpSet(cl, url);
				Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
				int w = (int)(sd.getWidth()*0.8);
				int h = (int)(sd.getHeight()*0.8);
				int xs = (int)(sd.getWidth()*0.1);
				int ys = (int)(sd.getHeight()*0.1);
				HelpSet.Presentation pr = new HelpSet.Presentation("def", true, true, new Dimension(w, h), new Point(xs, ys), "Jadex Help", null, false, null);
				hs.addPresentation(pr, true);
				jadex_hb = hs.createHelpBroker();
			}
			catch(Exception e)
			{
				System.out.println("Help Set JadexHelp not found.");
			}
			catch(ExceptionInInitializerError ex)
			{
				System.out.println("Help set initialization error.");
				//ex.getException().printStackTrace();
			}
		}
		return jadex_hb;
	}


}
