package jadex.android.controlcenter.preference;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public class JadexIntegerPreference extends JadexStringPreference {

	public JadexIntegerPreference(Context context) {
		super(context);
	}
	
	@Override
	protected View onCreateDialogView() {
		View onCreateDialogView = super.onCreateDialogView();
		EditText text = getEditText();
		text.setInputType(InputType.TYPE_CLASS_NUMBER);
		return onCreateDialogView;
	}
	
	@Override
	public void setValue(Object value) {
//		Integer intValue = (Integer) value;
		super.setValue("" + value);
	}
	
	public int getInt() {
		return Integer.parseInt(getText());
	}

	@Override
	protected boolean callChangeListener(Object newValue) {
		return super.callChangeListener(Integer.parseInt((String) newValue));
	}
	
	

}
