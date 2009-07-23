package com.daimler.util.swing.fileautocompleter;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

//@author Santhosh Kumar T - santhosh@in.fiorano.com
//@author Christian Wiech (extension for Strings with pathseperator and Support for truezip by Thomas Schlichterle)
public class TruezipFileAutoCompleter extends AutoCompleter {
	private String thePathSeperator = null;

	public TruezipFileAutoCompleter(JTextComponent comp, String pathSeperator) {
		super(comp);
		thePathSeperator = pathSeperator;
	}

	public TruezipFileAutoCompleter(JTextComponent comp) {
		this(comp, null);
	}

	protected boolean updateListData() {
		String value = textComp.getText();
		int index1 = value.lastIndexOf('\\');
		int index2 = value.lastIndexOf('/');
		int index = Math.max(index1, index2);
		if (index == -1)
			return false;
		int index3 = 0;
		if (thePathSeperator != null)
		{
			index3 = value.lastIndexOf(thePathSeperator);
			if (index3 == -1)
				index3 = 0;
			else
				index3 += thePathSeperator.length();
			if (index3 > index)
				return false;
		}
		String dir = value.substring(index3, index + 1);
		final String prefix = index == value.length() - 1 ? null : value
				.substring(index + 1).toLowerCase();
		String[] files = new File(dir).list(new FilenameFilter() {
			public boolean accept(java.io.File dir, String name) {
				return prefix != null ? name.toLowerCase().startsWith(prefix)
						: true;
			}
		});
		if (files == null) {
			list.setListData(new String[0]);
			return true;
		} else {
			if (files.length == 1 && files[0].equalsIgnoreCase(prefix))
				list.setListData(new String[0]);
			else
				list.setListData(files);
			return true;
		}
	}

	protected void acceptedListItem(String selected) {
		if (selected == null)
			return;

		String value = textComp.getText();
		int index1 = value.lastIndexOf('\\');
		int index2 = value.lastIndexOf('/');
		int index = Math.max(index1, index2);
		if (index == -1)
			return;
		int prefixlen = textComp.getDocument().getLength() - index - 1;
		try {
			textComp.getDocument().insertString(textComp.getCaretPosition(),
					selected.substring(prefixlen), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}