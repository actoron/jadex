package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.SelectionController;
import jadex.bpmn.editor.gui.propertypanels.SPropertyPanelFactory;
import jadex.bpmn.editor.model.visual.BpmnVisualModelReader;
import jadex.bpmn.editor.model.visual.BpmnVisualModelWriter;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.io.SBpmnModelReader;
import jadex.bpmn.model.io.SBpmnModelWriter;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxStylesheet;

public class BpmnMenuBar extends JMenuBar
{
	/** The model container */
	protected ModelContainer modelcontainer;
	
	public BpmnMenuBar(ModelContainer container)
	{
		this.modelcontainer = container;
		
		JMenu filemenu = new JMenu("File");
		JMenuItem newitem = new JMenuItem("");
		JMenuItem openitem = new JMenuItem(new AbstractAction("Open...")
		{
			public void actionPerformed(ActionEvent e)
			{
				BetterFileChooser fc = new BetterFileChooser();
				FileFilter filter = new FileNameExtensionFilter("BPMN model file", "bpmn2");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				int result = fc.showOpenDialog(getParent());
				if (JFileChooser.APPROVE_OPTION == result)
				{
					try
					{
						BpmnGraph graph = new BpmnGraph(modelcontainer.getGraph().getStylesheet());
						BpmnVisualModelReader vreader = new BpmnVisualModelReader(graph);
						
						File file = fc.getSelectedFile();
						if (!file.getName().endsWith(".bpmn2"))
						{
							file = new File(file.getAbsolutePath() + ".bpmn2");
						}
						MBpmnModel mmodel = SBpmnModelReader.readModel(file, vreader);
						graph.getSelectionModel().addListener(mxEvent.CHANGE, new SelectionController(modelcontainer));
						
						modelcontainer.setBpmnModel(mmodel);
						modelcontainer.setGraph(graph);
						modelcontainer.getGraphComponent().setGraph(graph);
						modelcontainer.setDirty(false);
						
						
						modelcontainer.getGraphComponent().refresh();
						modelcontainer.setFile(file);
						modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
						
						new DeletionController(modelcontainer);
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		JMenuItem saveitem = new JMenuItem();
		JMenuItem saveasitem = new JMenuItem();
		saveasitem.setAction(new AbstractAction("Save As...")
		{
			public void actionPerformed(ActionEvent e)
			{
				BetterFileChooser fc = new BetterFileChooser();
				FileFilter filter = new FileNameExtensionFilter("BPMN model file (*.bpmn2)", "bpmn2");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				fc.setAcceptAllFileFilterUsed(false);
				
				if (modelcontainer.getFile() != null)
				{
					fc.setSelectedFile(modelcontainer.getFile());
				}
				
				int result = fc.showSaveDialog(getParent());
				if (JFileChooser.APPROVE_OPTION == result)
				{
					try
					{
						FileNameExtensionFilter ef = (FileNameExtensionFilter) fc.getFileFilter();
						String ext = "." + ef.getExtensions()[0];
						File file = fc.getSelectedFile();
						if (!file.getName().endsWith(ext))
						{
							file = new File(file.getAbsolutePath() + ext);
						}
						SBpmnModelWriter.writeModel(file, modelcontainer.getBpmnModel(), new BpmnVisualModelWriter(modelcontainer.getGraph()));
						modelcontainer.setDirty(false);
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		JMenuItem exportitem = new JMenuItem();
		JMenuItem exititem = new JMenuItem();
		filemenu.add(newitem);
		filemenu.add(openitem);
		filemenu.addSeparator();
		filemenu.add(saveitem);
		filemenu.add(saveasitem);
		filemenu.add(exportitem);
		filemenu.addSeparator();
		filemenu.add(exititem);
		add(filemenu);
		
		
		JMenu viewmenu = new JMenu("View");
		add(viewmenu);
		
		/* Styles */
		JMenu stylemenu = new JMenu("Styles");
		ButtonGroup stylegroup = new ButtonGroup();
		Action styleaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JRadioButtonMenuItem button = (JRadioButtonMenuItem) e.getSource();
				modelcontainer.getGraph().setStylesheet((mxStylesheet) button.getClientProperty("sheet"));
			}
		};
		JRadioButtonMenuItem colorview = new JRadioButtonMenuItem(styleaction);
		
		// Assumes the default style sheet is color and set in the graph.
		colorview.putClientProperty("sheet", modelcontainer.getGraph().getStylesheet());
		
		colorview.setSelected(true);
		colorview.setText("Color");
		stylegroup.add(colorview);
		stylemenu.add(colorview);
		/*final JRadioButtonMenuItem grayview = new JRadioButtonMenuItem(styleaction);
		grayview.putClientProperty("sheet", IViewAccess.GS_STYLESHEET);
		grayview.setText("Grayscale");
		stylegroup.add(grayview);
		stylemenu.add(grayview);*/
		viewmenu.add(stylemenu);
		
		/** Icon sizes */
		JMenu iconmenu = new JMenu("Icon Size");
		ButtonGroup icongroup = new ButtonGroup();
		Action iconaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JRadioButtonMenuItem button = (JRadioButtonMenuItem) e.getSource();
				int iconsize = (Integer) button.getClientProperty("size");
				((BpmnToolbar) modelcontainer.getEditingToolbar()).setIconSize(iconsize);
			}
		};
		
		for (int i = 0; i < GuiConstants.ICON_SIZES.length; ++i)
		{
			JRadioButtonMenuItem isbutton = new JRadioButtonMenuItem(iconaction);
			isbutton.putClientProperty("size", GuiConstants.ICON_SIZES[i]);
			if (GuiConstants.ICON_SIZES[i] == GuiConstants.DEFAULT_ICON_SIZE)
			{
				isbutton.setSelected(true);
			}
			isbutton.setText("" + GuiConstants.ICON_SIZES[i] + "x" + GuiConstants.ICON_SIZES[i]);
			icongroup.add(isbutton);
			iconmenu.add(isbutton);
		}
		viewmenu.add(iconmenu);
	}
}
