package jadex.bpmn.editor.gui;

import jadex.bridge.ClassInfo;
import jadex.commons.SReflect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

public class SettingsPanel extends JPanel
{
	/** The library entry table. */
	protected JTable libentrytable;
	
	/** The new library entries. */
	protected List<File> libentries;
	
	/** Smooth zoom box. */
	protected JCheckBox szbox;
	
	/** Sequence edge enabled box. */
	protected JCheckBox sebox;
	
	/** Direct sequence edge auto box. */
	protected JCheckBox dsbox;
	
	/** Data edge enabled box. */
	protected JCheckBox debox;
	
	/** Name/Type data edge box. */
	protected JCheckBox ntbox;
	
	/** The global cache. */
	protected GlobalCache globalcache;
	
	/** The settings */
	protected Settings settings;
	
	public SettingsPanel(GlobalCache globalcache, Settings settings)
	{
		super(new GridBagLayout());
		this.globalcache = globalcache;
		this.libentries = new ArrayList<File>();
		if (settings.getLibraryEntries() != null)
		{
			libentries.addAll(Arrays.asList(settings.getLibraryEntries()));
		}
		
		JTabbedPane tabpane = new JTabbedPane();
		tabpane.setBorder(new EmptyBorder(10, 5, 10, 5));
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		add(tabpane, g);
		
		JPanel generalpanel = new JPanel(new GridBagLayout());
		generalpanel.setBorder(new TitledBorder("General Settings"));
		tabpane.addTab("General", generalpanel);
		
		this.settings = settings;
		
		final AbstractAction changeaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				SettingsPanel.this.firePropertyChange(OptionDialog.OPTIONS_CHANGED_PROPERTY, null, null);
			}
		};
		
		libentrytable = new JTable(new ClassPathTableModel());
		
		JButton pathbutton = new JButton(new AbstractAction("Add Path")
		{
			public void actionPerformed(ActionEvent e)
			{
				File last = libentries.size() > 0? libentries.get(libentries.size() - 1) : null;
				BetterFileChooser fc = new BetterFileChooser(last);
				fc.setDialogType(BetterFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(BetterFileChooser.FILES_AND_DIRECTORIES);
				FileFilter filter = new FileNameExtensionFilter("BPMN model file", "bpmn2");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				
				int state = fc.showOpenDialog(SettingsPanel.this);
				
				if (state == BetterFileChooser.APPROVE_OPTION)
				{
					File path = fc.getSelectedFile();
					if (!libentries.contains(path))
					{
						int pos = libentries.size();
						libentries.add(path);
						((ClassPathTableModel) libentrytable.getModel()).fireTableRowsInserted(pos, pos);
					}
					changeaction.actionPerformed(e);
				}
			}
		});
		
		JButton projectbutton = new JButton(new AbstractAction("Add Project")
		{
			public void actionPerformed(ActionEvent e)
			{
				File last = libentries.size() > 0? libentries.get(libentries.size() - 1) : null;
				BetterFileChooser fc = new BetterFileChooser(last);
				fc.setDialogType(BetterFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(BetterFileChooser.DIRECTORIES_ONLY);
				
				int state = fc.showOpenDialog(SettingsPanel.this);
				
				if (state == BetterFileChooser.APPROVE_OPTION)
				{
					File libhome = fc.getSelectedFile();
					
					File libdir = new File(libhome.getAbsolutePath() + File.separator + "lib");
					if (!libdir.exists() || !libdir.isDirectory())
					{
						libdir = libhome;
					}
					
					Set<File> entries = new HashSet<File>();
					File[] files = libdir.listFiles();
					if (files != null)
					{
						for (File file : files)
						{
							if (file.getAbsolutePath().endsWith(".jar"))
							{
								entries.add(file);
							}
						}
					}
					
					if (entries.isEmpty())
					{
						// Attempt developer-mode search.
						File[] dirs = libdir.listFiles();
						for (File dir : dirs)
						{
							if (dir.isDirectory())
							{
								File targetdir = new File(dir.getAbsolutePath() + File.separator + "target" + File.separator + "classes");
								if (targetdir.exists() && targetdir.isDirectory())
								{
									entries.add(targetdir);
								}
							}
						}
					}
					
					entries.removeAll(libentries);
					int start = libentries.size();
					libentries.addAll(entries);
					if (start < libentries.size())
					{
						((ClassPathTableModel) libentrytable.getModel()).fireTableRowsInserted(start, libentries.size() - 1);
						changeaction.actionPerformed(e);
					}
				}
			}
		});
		
		JButton removebutton = new JButton(new AbstractAction("Remove Paths")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = libentrytable.getSelectedRows();
				Arrays.sort(ind);
				
				for (int i = ind.length - 1; i >= 0; --i)
				{
					libentries.remove(ind[i]);
					((ClassPathTableModel) libentrytable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
				
				if (ind.length > 0)
				{
					changeaction.actionPerformed(e);
				}
			}
		});
		
		JButton clearbutton = new JButton(new AbstractAction("Clear")
		{
			public void actionPerformed(ActionEvent e)
			{
				libentries.clear();
				((ClassPathTableModel) libentrytable.getModel()).fireTableStructureChanged();
				changeaction.actionPerformed(e);
			}
		});
		
//		JButton button = new JButton(new AbstractAction("...")
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				File oldhome = new File(libpathfield.getText());
//				BetterFileChooser fc = new BetterFileChooser(oldhome);
//				fc.setDialogType(BetterFileChooser.OPEN_DIALOG);
//				fc.setFileSelectionMode(BetterFileChooser.DIRECTORIES_ONLY);
//				
//				int state = fc.showOpenDialog(SettingsPanel.this);
//				
//				if (state == BetterFileChooser.APPROVE_OPTION)
//				{
//					libpathfield.setText(fc.getSelectedFile().getPath());
//				}
//			}
//		});
		
//		g = new GridBagConstraints();
//		g.insets = new Insets(0, 5, 0, 10);
//		generalpanel.add(label, g);
//		
//		g = new GridBagConstraints();
//		g.gridx = 1;
//		g.weightx = 1.0;
//		g.fill = GridBagConstraints.HORIZONTAL;
//		generalpanel.add(libpathfield, g);
//		
//		g = new GridBagConstraints();
//		g.gridx = 2;
//		g.insets = new Insets(0, 10, 0, 5);
//		generalpanel.add(button, g);
		
		JPanel cppanel = new JPanel(new GridBagLayout());
		cppanel.setBorder(new TitledBorder("Class Path Settings"));
		tabpane.addTab("Class Path", cppanel);
		
		g = new GridBagConstraints();
		g.gridheight = 5;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		cppanel.add(new JScrollPane(libentrytable), g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(pathbutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(projectbutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 2;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(removebutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 3;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(clearbutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 4;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.VERTICAL;
		cppanel.add(new JPanel(), g);
		
		szbox = new JCheckBox(changeaction);
		szbox.setText("Smooth Zoom");
		szbox.setSelected(settings.isSmoothZoom());
		g = new GridBagConstraints();
		g.anchor = GridBagConstraints.WEST;
		generalpanel.add(szbox, g);
		
		g = new GridBagConstraints();
		g.gridwidth = 2;
		g.gridy = 1;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		generalpanel.add(new JPanel(), g);
		
//		g = new GridBagConstraints();
//		g.gridy = 5;
//		g.gridwidth = GridBagConstraints.REMAINDER;
//		g.weightx = 1.0;
//		g.weighty = 1.0;
//		g.fill = GridBagConstraints.BOTH;
//		generalpanel.add(new JPanel(), g);
		
		JPanel edgepanel = new JPanel(new GridBagLayout());
		edgepanel.setBorder(new TitledBorder("Edge Settings"));
		tabpane.addTab("Data Edge Settings", edgepanel);
		
		sebox = new JCheckBox(changeaction);
		sebox.setText("Enable sequence edges");
		sebox.setToolTipText("Enable sequence edges.");
		sebox.setSelected(settings.isSequenceEdges());
		g = new GridBagConstraints();
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(sebox, g);
		
		dsbox = new JCheckBox(changeaction);
		dsbox.setText("Generate missing sequence edge on data connections");
		dsbox.setToolTipText("Generate missing sequence edge on data connections.");
		dsbox.setSelected(settings.isDirectSequenceAutoConnect());
		g = new GridBagConstraints();
		g.gridy = 1;
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(dsbox, g);
		
		debox = new JCheckBox(changeaction);
		debox.setText("Enable data edges");
		debox.setToolTipText("Enable data edges.");
		debox.setSelected(settings.isDataEdges());
		g = new GridBagConstraints();
		g.gridy = 2;
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(debox, g);
		
		ntbox = new JCheckBox(changeaction);
		ntbox.setText("Generate data edge for matching name and type");
		ntbox.setToolTipText("Generate data edge if following task has parameter of matching name and type.");
		ntbox.setSelected(settings.isNameTypeDataAutoConnect());
		g = new GridBagConstraints();
		g.gridy = 3;
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(ntbox, g);
		
		g = new GridBagConstraints();
		g.gridy = 4;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		edgepanel.add(new JPanel(), g);
	}
	
	/**
	 *  Applies the settings.
	 */
	public void applySettings()
	{
//		String pf = libpathfield.getText();
//		pf = pf != null && pf.length() == 0 ? null : pf;
//		if ((pf != null && settings.getLibraryHome() == null) ||
//			(pf == null && settings.getLibraryHome() != null) ||
//			(pf != null && !libpathfield.getText().equals(settings.getLibraryHome().getPath())))
		Set<File> orig = new HashSet<File>();
		Set<File> cand = new HashSet<File>(libentries);
		if (settings.getLibraryEntries() != null)
		{
			orig.addAll(Arrays.asList(settings.getLibraryEntries()));
		}
		
		if (!cand.equals(orig))
		{
			settings.setLibraryEntries(libentries);
			
			Comparator<ClassInfo> comp = new Comparator<ClassInfo>()
			{
				public int compare(ClassInfo o1, ClassInfo o2)
				{
					String str1 = SReflect.getUnqualifiedTypeName(o1.toString());
					String str2 = SReflect.getUnqualifiedTypeName(o2.toString());
					return str1.compareTo(str2);
				}
			};
			Set<ClassInfo>[] tmp = GlobalCache.scanForClasses(settings.getLibraryClassLoader());
			globalcache.getGlobalTaskClasses().addAll(tmp[0]);
			globalcache.getGlobalInterfaces().addAll(tmp[1]);
			Collections.sort(globalcache.getGlobalTaskClasses(), comp);
			Collections.sort(globalcache.getGlobalInterfaces(), comp);
			settings.setGlobalTaskClasses(globalcache.getGlobalTaskClasses());
			settings.setGlobalInterfaces(globalcache.getGlobalInterfaces());
			settings.setGlobalAllClasses(globalcache.getGlobalAllClasses());
		}
		settings.setSmoothZoom(szbox.isSelected());
		settings.setDirectSequenceAutoConnect(dsbox.isSelected());
		settings.setSequenceEdges(sebox.isSelected());
		settings.setNameTypeDataAutoConnect(ntbox.isSelected());
		settings.setDataEdges(debox.isSelected());
	}
	
	/**
	 *
	 */
	protected class ClassPathTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return "Class Path";
		}
		
		public int getRowCount()
		{
			return libentries.size();
		}
		
		public int getColumnCount()
		{
			return 1;
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}
		
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return libentries.get(rowIndex).getPath();
		}
	}
}
