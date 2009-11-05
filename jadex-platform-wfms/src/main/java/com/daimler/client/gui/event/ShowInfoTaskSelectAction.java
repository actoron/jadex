package com.daimler.client.gui.event;

import jadex.wfms.client.GuiClient;
import jadex.wfms.client.IWorkitem;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.html.HTMLEditorKit;

public class ShowInfoTaskSelectAction extends AbstractTaskSelectAction{

    public ShowInfoTaskSelectAction(GuiClient client, IWorkitem workitem)
    {
    	super(client, workitem.getName(), workitem);
        initAction();
        String infoText = (String) workitem.getParameterValue("info_test");
        if (infoText == null)
        	infoText = "<html><center><h1>" + workitem.getName() + "</h1></center></html>";
        initPanel(infoText);
    }
    
    private void initAction()
    {
    	//TODO: Different default name?
        String sTitle = "<HTML>" + "" + getTitle() + " </HTML>";
        putValue(Action.SMALL_ICON, ICON_INFO);
        putValue(Action.NAME, sTitle);
    }
    
    private void initPanel(String text) {
        /*URL url = null;
        try {
            String sRelPath = textOrURL;
            if (sRelPath.startsWith("/"))
                sRelPath = sRelPath.substring(1);
            url = ClassLoader.getSystemClassLoader().getResource(sRelPath);
            if (url == null)
                url = new URL(textOrURL);

        } catch (MalformedURLException err) {
        }*/
        JEditorPane ep = new JEditorPane();
        ep.setEditable(false);
        //if (url == null) {
            ep.setContentType("text/html");
            ep.setText(text);
        /*} else {
            try {
                HTMLEditorKit kit = new HTMLEditorKit();
                ep.setEditorKit(kit);
                // ep.setPage(url);
                // Thread.sleep(3000);
                ep.getDocument().remove(0, ep.getDocument().getLength());
                InputStream in = url.openStream();
                try {
                    ep.getEditorKit().read(GuiEnvConnUtils.convertStream(in, getThePlan()), ep.getDocument(),
                            0);
                } catch (ChangedCharSetException e1) {
                    String charSetSpec = e1.getCharSetSpec();
                    if (e1.keyEqualsCharSet()) {
                        ep.putClientProperty("charset", charSetSpec);
                    }*/ /*
                         * else {
                         * ep.setCharsetFromContentTypeParameters(charSetSpec); }
                         */
                    /*in.close();
                    URLConnection conn = url.openConnection();
                    in = GuiEnvConnUtils.convertStream(conn.getInputStream(), getThePlan());
                    try {
                        ep.getDocument()
                                .remove(0, ep.getDocument().getLength());
                    } catch (BadLocationException e) {
                    }
                    ep.getDocument().putProperty("IgnoreCharsetDirective",
                            Boolean.valueOf(true));
                    ep.getEditorKit().read(GuiEnvConnUtils.convertStream(in, getThePlan()), ep.getDocument(),
                            0);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }*/
        setTheContent(ep);
    }
    
    public void okButtonPressed()
    {
    	client.finishActivity(getWorkitem());
        dispose();
    }

}
