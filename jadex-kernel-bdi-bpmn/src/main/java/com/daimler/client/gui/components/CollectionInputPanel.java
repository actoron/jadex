// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.daimler.util.swing.SwingUtils;

/**
 *
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class CollectionInputPanel extends AbstractInputPanel implements ActionListener{

	public static final int TYPE_LIST = 0;
	public static final int TYPE_SET = 1;
	
    public static final NumberFormat DOUBLE_FORMAT = NumberFormat
        .getNumberInstance(Locale.GERMANY);

    public static final NumberFormat INTEGER_FORMAT = NumberFormat
        .getIntegerInstance(Locale.GERMANY);

    public static final DateFormat DATE_FORMAT = DateFormat
        .getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);

    public static final DateFormat DATETIME_FORMAT = DateFormat
        .getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
            Locale.GERMANY);

    public static final DateFormat TIME_FORMAT = DateFormat
        .getTimeInstance(DateFormat.MEDIUM, Locale.GERMANY);


    private String labelText;

    private JPanel mainPanel;

    private IInputComponent inputField;

    private JList panellist;

    private JButton btAdd;

    private JButton btDelete;

    private JButton btUp;

    private JButton btDown;

    private TitledBorder border;
    
    private int type;
    
    public CollectionInputPanel(String name, String labelText, String helpText, Color bgColor, boolean isRequired, IInputComponent inputField, int type)
    {
        super(name, helpText, bgColor, isRequired);
        this.type = type;
        this.labelText = labelText;
        this.inputField = inputField;
        initComponents();
        super.init();
    }

    private void initComponents() {

        mainPanel = new JPanel();
        mainPanel.setBackground(getBackgroundColor());
        mainPanel.setLayout(new BorderLayout());
        
        JPanel pTop = new JPanel(new BorderLayout());
        pTop.setBackground(getBackgroundColor());
        JLabel lbElement = new JLabel("Wert:");
        JPanel pTop2 = new JPanel();
        pTop2.setLayout(new BoxLayout(pTop2, BoxLayout.LINE_AXIS));
        pTop2.setBackground(getBackgroundColor());
        if (inputField != null)
        {
            pTop.add(SwingUtils.getPanelWithComponent(lbElement,
                    getBackgroundColor(), Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT, getElementBorder()), BorderLayout.WEST);
            pTop2.add(SwingUtils.getPanelWithComponentAndSize(inputField.getComponent(),
                    getBackgroundColor(), Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT, new Dimension(inputField.getWidth(), inputField.getHeight()), true, getElementBorder()));
        }
        btAdd = new JButton();
        btAdd.addActionListener(this);
        btAdd.setPreferredSize(new Dimension(60, 22));
        btAdd.setText("Add");
        btDelete = new JButton("Delete");
        btDelete.addActionListener(this);
        btDelete.setText("Delete");
        if (type == TYPE_LIST)
        {
            btUp = new JButton("Up");
            btUp.addActionListener(this);
            btUp.setText("Up");
            btUp.setPreferredSize(new Dimension(80, 22));
            btDown = new JButton("Down");
            btDown.addActionListener(this);
            btDown.setText("Down");
            btDown.setPreferredSize(new Dimension(80, 22));
        }
        pTop2.add(SwingUtils.getPanelWithComponentAndSize(btAdd,
                getBackgroundColor(), Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT, new Dimension(60, 22) , true, getElementBorder()));
        pTop2.add(Box.createVerticalGlue());
        if (inputField != null)
        {
            pTop.add(pTop2, BorderLayout.CENTER);
        }
        mainPanel.add(pTop, BorderLayout.NORTH);

        JPanel pCenter = new JPanel();
        pCenter.setLayout(new BoxLayout(pCenter, BoxLayout.LINE_AXIS));
        pCenter.setBackground(getBackgroundColor());
        panellist = new JList();
        panellist.setBackground(Color.WHITE);
        panellist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panellist.setModel(new DefaultListModel());
        JScrollPane scp = new JScrollPane(panellist);
        pCenter.add(pTop2.add(SwingUtils.getPanelWithComponentAndSize(scp,
                getBackgroundColor(), Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT, new Dimension(300, 120) , true, getElementBorder())));
        JPanel pCenter2 = new JPanel();
        pCenter2.setLayout(new BorderLayout());
        pCenter2.setBackground(getBackgroundColor());
        JPanel pCenter3 = new JPanel();
        pCenter3.setLayout(new GridLayout(2, 1));
        pCenter3.setBackground(getBackgroundColor());
        if (type == TYPE_LIST)
        {
            pCenter3.add(SwingUtils.getPanelWithComponentAndSize(btUp, getBackgroundColor(), Component.CENTER_ALIGNMENT, Component.BOTTOM_ALIGNMENT, new Dimension(80, 22), true, getElementBorder()));
            pCenter3.add(SwingUtils.getPanelWithComponentAndSize(btDown, getBackgroundColor(), Component.CENTER_ALIGNMENT, Component.TOP_ALIGNMENT, new Dimension(80, 22), true, getElementBorder()));
        }
        else
        {
            JLabel lbPlaceholder = new JLabel();
            lbPlaceholder.setBackground(getBackgroundColor());
            pCenter3.add(lbPlaceholder);
            pCenter3.add(lbPlaceholder);
        }
        if (inputField != null)
        {
            pCenter2.add(pCenter3  , BorderLayout.CENTER);
            pCenter2.add(SwingUtils.getPanelWithComponentAndSize(btDelete,
                    getBackgroundColor(), Component.LEFT_ALIGNMENT, Component.BOTTOM_ALIGNMENT, new Dimension(100, 22) , true, getElementBorder()), BorderLayout.SOUTH);
        }
        pCenter.add(pTop2.add(SwingUtils.getPanelWithComponentAndSize(pCenter2,
                getBackgroundColor(), Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT, new Dimension(300, 100) , true, getElementBorder())));
        pCenter.add(Box.createVerticalGlue());
        mainPanel.add(pCenter, BorderLayout.CENTER);
        String sLabText = labelText;
        if (isInputRequired()) {
            sLabText += "*";
        }
        border = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), sLabText);
        mainPanel.setBorder(border);
        /*if (getTheAccessible().getTheValue() != null) {
            setTheCurrentValue(getTheAccessible().getTheValue());
        }*/

    }

    /*private IInputComponent getInputFieldFromClass(Class clazz)
    {
        if (clazz.equals(String.class))
        {
            if (domain.isInstanceOf(DomainFactory.DOM_STRING)
                    && ((SimpleContextVariableDomain) domain).getThePattern() != null) {
                return new RegExprTextField((SimpleContextVariableDomain) domain);
            } else {
                return new ComboChoiceField((SimpleContextVariableDomain) domain);
            }
        }
        if (domain instanceof DateContextVariableDomain) {
            if (domain.isInstanceOf(DomainFactory.DOM_DATE)) {
                return new StringTextField(domain, THE_DATE_FORMAT);
            }
            if (domain.isInstanceOf(DomainFactory.DOM_DATETIME)) {
                return new StringTextField(domain, THE_DATETIME_FORMAT);
            }
            if (domain.isInstanceOf(DomainFactory.DOM_TIME)) {
                return new StringTextField(domain, THE_TIME_FORMAT);
            }
            return null;
        }
        if (domain instanceof PrimitiveContextVariableDomain) {
            if (domain.isInstanceOf(DomainFactory.DOM_INTEGER)) {
                return new StringTextField(domain, THE_INTEGER_FORMAT);
            }
            if (domain.isInstanceOf(DomainFactory.DOM_DOUBLE)) {
                return new StringTextField(domain, THE_DOUBLE_FORMAT);
            }
            if (domain.isInstanceOf(DomainFactory.DOM_LONG)) {
                return new StringTextField(domain, THE_INTEGER_FORMAT);
            }
            if (domain.isInstanceOf(DomainFactory.DOM_STRING)) {
                return new StringTextField(domain);
            }
            if (domain.isInstanceOf(DomainFactory.DOM_TEXT)) {
                return new TextTextField();
            }
            if (domain.isInstanceOf(DomainFactory.DOM_BOOLEAN)) {
                System.err.println(">>>>>>>>>>>>>>Input fields for complex variables in lists are not supported yet!");
            }
            return null;
        }
        if (domain instanceof ComplexContextVariableDomain) {
            System.err.println(">>>>>>>>>>>>>>Input fields for complex variables in lists are not supported yet!");
            return null;
        }
        return null;
    }

    public static Format getFormat(IContextVariableDomain dom) {
        if (dom.isInstanceOf(DomainFactory.DOM_DATE)) {
            return THE_DATE_FORMAT;
        }
        if (dom.isInstanceOf(DomainFactory.DOM_DATETIME)) {
            return THE_DATETIME_FORMAT;
        }
        if (dom.isInstanceOf(DomainFactory.DOM_TIME)) {
            return THE_TIME_FORMAT;
        }
        if (dom.isInstanceOf(DomainFactory.DOM_DOUBLE)) {
            return THE_DOUBLE_FORMAT;
        }
        if (dom.isInstanceOf(DomainFactory.DOM_INTEGER)) {
            return THE_INTEGER_FORMAT;
        }
        if (dom.isInstanceOf(DomainFactory.DOM_LONG)) {
            return THE_INTEGER_FORMAT;
        }
        return null;
    }*/

    public String getLabel() {
        return labelText;
    }

    
    public int getWeight() {
        return -10;
    }

    
    public Border getBorder() {
        return null;
    }

    
    JPanel getMainPanel() {
        return mainPanel;
    }

    
    public boolean isValueFilled() {
        if (getCurrentValue() != null) {
           return true;
        }
        return false;
    }

    
    public void markError() {
        border.setTitleColor(Color.RED);
        mainPanel.repaint();
    }

    
    public void markOK() {
        border.setTitleColor(Color.BLACK);
        mainPanel.repaint();
    }

    
    public void setMainPanelEnabled(boolean b) {
        if (inputField != null) {
            inputField.setEnabled(b);
        }
        btAdd.setEnabled(b);
        if (btUp != null) {
            btUp.setEnabled(b);
            btDown.setEnabled(b);
        }
        btDelete.setEnabled(b);
        mainPanel.setEnabled(b);
    }

    
    public int getFieldCategory() {
        return AbstractInputPanel.CAT_COLLECTION;
    }

    
    public JPanel getLabelPanel() {
        return null;
    }

    
    public Object getCurrentValue() {
        if (panellist.getModel().getSize() == 0) {
            return null;
        }
        if (type == TYPE_LIST)
        {
        	ArrayList ret = new ArrayList(panellist.getModel().getSize());
        	for (int i = 0; i < panellist.getModel().getSize(); ++i)
        	{
        		ret.add(panellist.getModel().getElementAt(i));
        	}
        	return ret;
        }
        else if (type == TYPE_SET)
        {
        	HashSet ret = new HashSet(panellist.getModel().getSize());
            for (int i = 0; i < panellist.getModel().getSize(); ++i)
            {
                ret.add(panellist.getModel().getElementAt(i));
            }
            return ret;
        }
        return null;
    }

    
    public void setCurrentValue(Object currentValue)
    {
        ((DefaultListModel) panellist.getModel()).clear();
        if (currentValue != null && currentValue instanceof Collection)
        {
            Iterator it = ((Collection) currentValue).iterator();
            while (it.hasNext())
            {
                ((DefaultListModel) panellist.getModel()).addElement(it.next());
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btAdd) {
            boolean bAdd = true;
            Object o = inputField.getValue();
            if (o == null) {
                bAdd = false;
            }
            //o = ((AbstractCollectionContextVariableDomain) getTheAccessible().getTheDomain()).getTheElementsDomain().transformToValidValue(o);
            //Check for duplicates
            if (type == TYPE_SET) {
                if (((DefaultListModel)panellist.getModel()).contains(o)) {
                    bAdd = false;
                }
            }
            if (bAdd) {
                //add it as an IAccessible to profit from its toString
                //method that uses the default formatter (otherwise we
                //would get awful entries for e.g. DateVariables)
                ((DefaultListModel) panellist.getModel()).addElement(o);
            }
        } else if (e.getSource() == btDelete) {
            int iIndex = panellist.getSelectedIndex();
            if (iIndex >= 0 && iIndex < panellist.getModel().getSize()) {
                ((DefaultListModel) panellist.getModel()).remove(panellist.getSelectedIndex());
            }
        } else if (e.getSource() == btUp) {
            int iIndex = panellist.getSelectedIndex();
            if (iIndex > 0) {
                Object o = panellist.getModel().getElementAt(iIndex);
                ((DefaultListModel) panellist.getModel()).remove(iIndex);
                ((DefaultListModel) panellist.getModel()).add(iIndex - 1, o);
                panellist.getSelectionModel().clearSelection();
                panellist.getSelectionModel().setSelectionInterval(iIndex - 1, iIndex - 1);
            }
        } else if (e.getSource() == btDown) {
            int iIndex = panellist.getSelectedIndex();
            if (iIndex > -1 && iIndex < panellist.getModel().getSize() - 1) {
                Object o = panellist.getModel().getElementAt(iIndex);
                ((DefaultListModel) panellist.getModel()).remove(iIndex);
                ((DefaultListModel) panellist.getModel()).add(iIndex + 1, o);
                panellist.getSelectionModel().clearSelection();
                panellist.getSelectionModel().setSelectionInterval(iIndex + 1, iIndex + 1);
            }
        }
    }
}
