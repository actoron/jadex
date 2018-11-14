package jadex.gpmn.editor.gui.propertypanels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;

import jadex.gpmn.editor.gui.BetterFileChooser;
import jadex.gpmn.editor.gui.DocumentAdapter;
import jadex.gpmn.editor.gui.ImageProvider;
import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;

public class GpmnPropertyPanel extends BasePropertyPanel
{
	/**
	 *  Creates a new property panel.
	 *  @param graph The graph.
	 */
	public GpmnPropertyPanel(ModelContainer container)
	{
		super(container);
		setLayout(new BorderLayout());
		
		ImageProvider imgprovider = container.getImageProvider();
		
		JTabbedPane tabpane = new JTabbedPane();
		add(tabpane);
		
		int y = 0;
		JPanel column = new JPanel();
		column.setLayout(new GridBagLayout());
		tabpane.addTab("Model", column);
		
		JLabel label = new JLabel("Description");
		JTextArea textarea = new JTextArea(container.getGpmnModel().getDescription());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getModel().setDescription(SGuiHelper.getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Package");
		textarea = new JTextArea(container.getGpmnModel().getDescription());
		textarea.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				getModel().setPackage(SGuiHelper.getText(((JTextArea) e.getSource()).getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(column, y);
		
		final ContextTableModel model = new ContextTableModel(container);
		final JTable table = new JTable(model);
		JScrollPane tablepane = new JScrollPane(table);
		
		JPanel buttonpanel = new JPanel(new GridLayout(2, 1));
		JButton addbutton = new JButton(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				model.addParameter();
			}
		});
		int iconsize = 32;
//		addbutton.setIcon(imgprovider.getImageIcon("plus_unpressed.png"));
//		addbutton.setPressedIcon(imgprovider.getImageIcon("plus_pressed.png"));
//		addbutton.setRolloverIcon(imgprovider.getImageIcon("plus_high.png"));
		Icon[] icons = imgprovider.generateGenericFlatImageIconSet(iconsize, "add_+");
		addbutton.setIcon(icons[0]);
		addbutton.setPressedIcon(icons[1]);
		addbutton.setRolloverIcon(icons[2]);
		addbutton.setContentAreaFilled(false);
		addbutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		addbutton.setMargin(new Insets(0, 0, 0, 0));
		addbutton.setToolTipText("Add Parameter");
		buttonpanel.add(addbutton);
		JButton removebutton = new JButton(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] rows = table.getSelectedRows();
				if (rows.length == 1)
				{
					model.removeParameter(rows[0]);
				}
				else
				{
					model.removeParameters(rows);
				}
			}
		});
		icons = imgprovider.generateGenericFlatImageIconSet(iconsize, "remove_-");
		removebutton.setIcon(icons[0]);
		removebutton.setPressedIcon(icons[1]);
		removebutton.setRolloverIcon(icons[2]);
		removebutton.setContentAreaFilled(false);
		removebutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		removebutton.setMargin(new Insets(0, 0, 0, 0));
		removebutton.setToolTipText("Remove Parameter");
		buttonpanel.add(removebutton);
		
		JPanel contextpanel = new JPanel(new GridBagLayout());
		tabpane.addTab("Context", contextpanel);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		contextpanel.add(tablepane, gc);
		
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		contextpanel.add(buttonpanel, gc);
		
		// TODO: Optional with Eclipse?
		y = 0;
		column = new JPanel();
		column.setLayout(new GridBagLayout());
		tabpane.addTab("Settings", column);
		
		label = new JLabel("Project Root");
		textarea = new JTextArea(container.getProjectRoot() != null? container.getProjectRoot().getAbsolutePath() : "");
		textarea.setEditable(false);
		textarea.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (MouseEvent.BUTTON1 == e.getButton())
				{
					BetterFileChooser fc = new BetterFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setAcceptAllFileFilterUsed(false);
					
					int result = fc.showOpenDialog(GpmnPropertyPanel.this);
					
					if (JFileChooser.APPROVE_OPTION == result)
					{
						File root = fc.getSelectedFile();
						String path = root.getAbsolutePath();
						JTextArea textarea = (JTextArea) e.getSource();
						textarea.setText(path);
						modelcontainer.setProjectRoot(root);
					}
				}
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(column, y);
	}
}
