package jadex.commons.gui;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

public class TreeCombo extends JComboBox {
    static final int OFFSET = 16;

    public TreeCombo(JTree tree) {
        super();
        setModel(new TreeToListModel(tree));
        setRenderer(new ListEntryRenderer(tree));
    }

    class TreeToListModel extends AbstractListModel implements 
ComboBoxModel,TreeModelListener {
        TreeModel source;
        boolean invalid = true;
        Object currentValue;
        Vector cache = new Vector();

        public TreeToListModel(JTree tree) {
            source = tree.getModel();
            source.addTreeModelListener(this);
            setRenderer(new ListEntryRenderer(tree));
        }

        public void setSelectedItem(Object anObject) {
            if ( (currentValue != null && !currentValue.equals( anObject )) ||
                 currentValue == null && anObject != null ) {
                currentValue = anObject;

                Enumeration en = cache.elements();
                while(en.hasMoreElements()) {
                    ListEntry le = (ListEntry)en.nextElement();
                    if(le.object().equals(anObject)) {
                        currentValue = le;
                        break;
                    }         
                }
            }

            fireContentsChanged(this, -1, -1);
        }

        public Object getSelectedItem() {
            return currentValue;
        }

        public int getSize() {
            validate();
            return cache.size();
        }

        public Object getElementAt(int index) {
            return cache.elementAt(index);
        }

        public void treeNodesChanged(TreeModelEvent e) {
            invalid = true;
        }

        public void treeNodesInserted(TreeModelEvent e) {
            invalid = true;
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            invalid = true;
        }

        public void treeStructureChanged(TreeModelEvent e) {
            invalid = true;
        }

        void validate() {
            if(invalid) {
                cache = new Vector();
                cacheTree(source.getRoot(),0);
                if(cache.size() > 0)
                    currentValue = cache.elementAt(0);
                invalid = false;             
                fireContentsChanged(this, 0, 0);
            }
        }

        void cacheTree(Object anObject,int level) {
            if(source.isLeaf(anObject))
                addListEntry(anObject,level,false);
            else {
                int c = source.getChildCount(anObject);
                int i;
                Object child;

                addListEntry(anObject,level,true);
                level++;

                for(i=0;i<c;i++) {
                    child = source.getChild(anObject,i);
                    cacheTree(child,level);
                }

                level--;
            }
        }

        void addListEntry(Object anObject,int level,boolean isNode) {
            cache.addElement(new ListEntry(anObject,level,isNode));
        }
    }

    class ListEntry {
        Object object;
        int    level;
        boolean isNode;

        public ListEntry(Object anObject,int aLevel,boolean isNode) {
            object = anObject;
            level = aLevel;
            this.isNode = isNode;
        }

        public Object object() {
            return object;
        }

        public int level() {
            return level;
        }

        public boolean isNode() {
            return isNode;
        }
    }

    static Border emptyBorder = new EmptyBorder(0,0,0,0);

    class ListEntryRenderer extends JLabel implements ListCellRenderer  {
        Icon leafIcon = (Icon)UIManager.get( "Tree.expandedIcon" 
);//SwingSet.sharedInstance().loadImageIcon("images/document.gif","Document");
        Icon nodeIcon = (Icon)UIManager.get( "Tree.collapsedIcon" 
);//SwingSet.sharedInstance().loadImageIcon("images/folder.gif","Folder");
        JTree	tree;

        public ListEntryRenderer(JTree tree) {
            this.setOpaque(true);
            this.tree	= tree;
        }

        public Component getListCellRendererComponent(
            JList listbox, 
            Object value, 
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
        	JComponent	ret;
            ListEntry listEntry = (ListEntry)value;
            if(listEntry != null) {
            	ret	= (JComponent)tree.getCellRenderer().getTreeCellRendererComponent(tree, listEntry.object(), isSelected, true, !listEntry.isNode(), index, cellHasFocus);
                Border border;
//                setText(listEntry.object().toString());
//                setIcon( listEntry.isNode() ? nodeIcon : leafIcon );
                if(index != -1)
                    border = new EmptyBorder(0, OFFSET * listEntry.level(), 0, 0);
                else 
                    border = emptyBorder;
//
//                if(UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
//                    if(index == -1 )
//                        this.setOpaque(false);
//                    else
//                        this.setOpaque(true);
//                } else 
//                    this.setOpaque(true);
//                
                ret.setBorder(border); 
//                if (isSelected) {
//                    
//this.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
//                    
//this.setForeground(UIManager.getColor("ComboBox.selectionForeground"));
//                } else {
//                    this.setBackground(UIManager.getColor("ComboBox.background"));
//                    this.setForeground(UIManager.getColor("ComboBox.foreground"));
//                }
            } else {
                setText("");
                ret	= this;
            }
            return ret;
        }
    }
    
//    public static void main(String[] args)
//	{
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				TreeCombo	tc	= new TreeCombo(new TreeModel()
//				{
//					// a
//					// +-b
//					// | +-c
//					// | | +-f
//					// | +-d
//					// +-e
//					public void addTreeModelListener(TreeModelListener l)
//					{
//						// TODO Auto-generated method stub
//						
//					}
//					public Object getChild(Object parent, int index)
//					{
//						Object ret	= null;
//						if(parent.equals("a") && index==0)
//						{
//							ret	= "b";
//						}
//						else if(parent.equals("b") && index==0)
//						{
//							ret	= "c";
//						}
//						else if(parent.equals("b") && index==1)
//						{
//							ret	= "d";
//						}
//						else if(parent.equals("a") && index==1)
//						{
//							ret	= "e";
//						}
//						else if(parent.equals("c") && index==0)
//						{
//							ret	= "f";
//						}
//						return ret;
//					}
//					public int getChildCount(Object parent)
//					{
//						int ret	= 0;
//						if(parent.equals("a"))
//						{
//							ret	= 2;
//						}
//						else if(parent.equals("b"))
//						{
//							ret	= 2;
//						}
//						else if(parent.equals("c"))
//						{
//							ret	= 1;
//						}
//						return ret;
//					}
//					public int getIndexOfChild(Object parent, Object child)
//					{
//						int ret	= -1;
//						if(parent.equals("a") && child.equals("b"))
//						{
//							ret	= 0;
//						}
//						else if(parent.equals("b") && child.equals("c"))
//						{
//							ret	= 0;
//						}
//						else if(parent.equals("b") && child.equals("d"))
//						{
//							ret	= 1;
//						}
//						else if(parent.equals("a") && child.equals("e"))
//						{
//							ret	= 1;
//						}
//						else if(parent.equals("c") && child.equals("f"))
//						{
//							ret	= 0;
//						}
//						return ret;
//					}
//					public Object getRoot()
//					{
//						return "a";
//					}
//					public boolean isLeaf(Object node)
//					{
//						return node.equals("f") || node.equals("d") || node.equals("e");
//					}
//					public void removeTreeModelListener(TreeModelListener l)
//					{
//						// TODO Auto-generated method stub
//						
//					}
//					public void valueForPathChanged(TreePath path,
//							Object newValue)
//					{
//						// TODO Auto-generated method stub
//						
//					}
//				});
//				
//				tc.setPreferredSize(new Dimension(300, tc.getPreferredSize().height));
//				
//				JFrame	f	= new JFrame("Tree Combo test");
//				f.getContentPane().setLayout(new FlowLayout());
//				f.getContentPane().add(tc, BorderLayout.CENTER);
//				f.setSize(400, 300);
//				f.setLocation(SGUI.calculateMiddlePosition(f));
//				f.setVisible(true);
//			}
//		});
//	}
}
