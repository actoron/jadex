package jadex.editor.common.eclipse.ui;

/* 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */ 

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

/**
 * A selectable check box cell editor for tables that works with keyboard
 * traversal as well as mouse clicks. It renders as {@link Button} with
 * {@link SWT#CHECK} style, so the corresponding label provider should use
 * {@link CheckboxImages} to render boolean values.
 */
public final class SelectableCheckboxCellEditor extends CellEditor {
	/*
	 * Implementation notes: The flow of control in ColumnViewerEditor is ...
	 * peculiar. It mostly caters for the reality of CheckboxCellEditor that
	 * returns null as control and directly calls fireApplyEditorValue when
	 * activated. Doing the same if there is a control results in an NPE. To
	 * simulate the same behavior for mouse clicks, we use the activation
	 * listener below: if we detect an activation using direct mouse click, we
	 * arm fIsDirectToggle and return a null control, otherwise, we take the
	 * normal route using the check box control.
	 */
	private final ColumnViewerEditorActivationListener fListener= new ColumnViewerEditorActivationListener() {
		@Override
		public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
			if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION) {
				fIsDirectToggle= true;
			}
		}

		@Override
		public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
			fIsDirectToggle= false;
		}

		@Override
		public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
			fIsDirectToggle= false;
		}

		@Override
		public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
			fIsDirectToggle= false;
		}
	};
	private final TableViewer fViewer;
	private final int fColumnIndex;
    private boolean fValue = false;
    private boolean fIsDirectToggle= false;
    private Button fButton;

	/**
	 * Creates a new editor on the given viewer and column index.
	 * 
	 * @param viewer the targeted table viewer
	 * @param columnIndex the index of the column that this editor will be used
	 *        in
	 */
	public SelectableCheckboxCellEditor(TableViewer viewer, int columnIndex) {
		super(viewer.getTable());
//		Arguments.positive0(columnIndex);
		fViewer = viewer;
		fColumnIndex = columnIndex;
		viewer.getColumnViewerEditor().addEditorActivationListener(fListener);
	}
	
	@Override
	public void dispose() {
		fViewer.getColumnViewerEditor().removeEditorActivationListener(fListener);
		super.dispose();
	}
	
	@Override
	public Control getControl() {
		if (fIsDirectToggle)
			return null;
		return super.getControl();
	}

	@Override
	protected Control createControl(Composite parent) {
		Button button = new Button(parent, SWT.CHECK);
		button.setSelection(fValue);
		button.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Boolean old = (Boolean) doGetValue();
				doSetValue(Boolean.valueOf(!old.booleanValue()));
				fireEditorValueChanged(true, true);
			}
		});
		button.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				SelectableCheckboxCellEditor.this.focusLost();
			}
		});
        button.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });
        // allow arrow key navigation - stolen from TableTextCellEditor in JDT-UI.
        // adapted to also work with LEFT/RIGHT as these are not used for buttons
        button.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// support switching rows while editing:
				if (e.stateMask == SWT.MOD1 || e.stateMask == SWT.MOD2) {
					if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN) {
					    // allow starting multi-selection even if in edit mode
						deactivate();
						e.doit= false;
						return;
					}
				}
				
				if (e.stateMask != SWT.NONE)
					return;
				
				switch (e.keyCode) {
					case SWT.ARROW_DOWN :
						e.doit= false;
						int nextRow= fViewer.getTable().getSelectionIndex() + 1;
						if (nextRow >= fViewer.getTable().getItemCount())
							break;
						editRow(nextRow, fColumnIndex);
						break;

					case SWT.ARROW_UP :
						e.doit= false;
						int prevRow= fViewer.getTable().getSelectionIndex() - 1;
						if (prevRow < 0)
							break;
						editRow(prevRow, fColumnIndex);
						break;

					case SWT.ARROW_LEFT :
						e.doit= false;
						int prevColumn = fColumnIndex - 1;
						if (prevColumn < 0)
							break;
						editRow(fViewer.getTable().getSelectionIndex(), prevColumn);
						break;
						
					case SWT.ARROW_RIGHT :
						e.doit= false;
						int nextColumn = fColumnIndex + 1;
						if (nextColumn >= fViewer.getTable().getColumnCount())
							break;
						editRow(fViewer.getTable().getSelectionIndex(), nextColumn);
						break;
						
					case SWT.F2 :
						e.doit= false;
						deactivate();
						break;
				}
			}

			private void editRow(int row, int column) {
				fViewer.getTable().setSelection(row);
				IStructuredSelection newSelection= (IStructuredSelection) fViewer.getSelection();
				if (newSelection.size() == 1)
					fViewer.editElement(newSelection.getFirstElement(), column);
			}
		});
        button.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });
        fButton= button;
		return button;
	}
	
	@Override
	public LayoutData getLayoutData() {
		LayoutData data = super.getLayoutData();
		data.grabHorizontal= false;
		// make the button overlap the image button
		data.horizontalAlignment= SWT.RIGHT;
		data.minimumWidth= ((Table) fButton.getParent()).getColumn(fColumnIndex).getWidth() - 1;
		return data;
	}

	@Override
	protected void doSetFocus() {
		fButton.setFocus();
	}

	@Override
	protected Object doGetValue() {
        return fValue ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	protected void doSetValue(Object value) {
//      Arguments.require(value instanceof Boolean);
        fValue = ((Boolean) value).booleanValue();
        fButton.setSelection(fValue);
	}
	
	@Override
	public void activate(ColumnViewerEditorActivationEvent event) {
		if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION) {
			assert fIsDirectToggle;
			doSetValue(Boolean.valueOf(!fValue));
			fireApplyEditorValue();
		}
	}
}


