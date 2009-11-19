package com.daimler.client.gui.event;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.wfms.client.IWorkitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import com.daimler.client.GuiClient;
import com.daimler.client.gui.components.GuiDataScrollPanel;

public class FetchDataTaskSelectAction extends AbstractTaskSelectAction{

    private GuiDataScrollPanel theDataPanel;
    
    public FetchDataTaskSelectAction(GuiClient client, IWorkitem workitem) {
    	super(client, workitem.getName(), workitem);
        initAction();
        initPanel(workitem);
    }
    
    private void initAction() {
    	//TODO: Different default name?
        String sTitle = "<HTML>" + "" + getTitle() + " </HTML>";
        putValue(Action.SMALL_ICON, ICON_TASK);
        putValue(Action.NAME, sTitle);
    }
    
    private void initPanel(IWorkitem workitem)
    {
        theDataPanel = new GuiDataScrollPanel(workitem, this);
        setTheContent(theDataPanel);
    }
    
    public void setTheExplenationText(String explanantionText) {
        if (explanantionText != null && explanantionText.trim().length() > 0) {
            theDataPanel.setExplanantionText(explanantionText);
        }
    }
    
    public void setInputEnabled(boolean bEnabled) {
        theDataPanel.setEnabled(bEnabled);
    }

    public void okButtonPressed()
    {
        if (theDataPanel != null)
        {
            if (!theDataPanel.isFilled())
            {
                return;
            }
            
            Map data = theDataPanel.getTheData();
            client.getCurrentActivity().setParameterValues(data);
            client.finishActivity(getWorkitem());
            
            /*ArrayList<IdentifierValueTuple> fetchedData = theDataPanel.getTheData();
            if (fetchedData != null && fetchedData.size() > 0 && getTheGuiConnector().getTheConfig().getBooleanProperty("Record_Process", false, "GUI_Options")) {
                //System.out.println("Writing out fetched data: " + fetchedData.size());
            }
            //getTheHelpBrowser().setVisible(false);
            getTheGuiConnector().setReturnedData(getTheTicket(), fetchedData);
            getTheGuiConnector().removeTask(getTheTicket());
            theDataPanel = null;
            dispose();*/
        }
        
    }

}
