package com.daimler.client.gui.event;

import jadex.bpmn.runtime.ITaskContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import com.daimler.client.connector.ClientConnector;
import com.daimler.client.connector.UserNotification;
import com.daimler.client.gui.GuiClient;
import com.daimler.client.gui.components.GuiDataScrollPanel;

public class FetchDataTaskSelectAction extends AbstractTaskSelectAction{

    private GuiDataScrollPanel theDataPanel;
    
    public FetchDataTaskSelectAction(GuiClient client, UserNotification notification) {
    	super(client, notification.getContext().getModelElement().getName(), notification);
        initAction();
        initPanel(notification.getContext().getModelElement().getParameters());
    }
    
    private void initAction() {
    	//TODO: Different default name?
        String sTitle = "<HTML>" + "" + getTitle() + " </HTML>";
        putValue(Action.SMALL_ICON, ICON_TASK);
        putValue(Action.NAME, sTitle);
    }
    
    private void initPanel(List parameters)
    {
        theDataPanel = new GuiDataScrollPanel(parameters, this);
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
            client.getHelpBrowser().setVisible(false);
            ClientConnector.getInstance().commitNotification(getNotification(), data);
            
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
