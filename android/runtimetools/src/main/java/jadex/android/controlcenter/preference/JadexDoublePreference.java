package jadex.android.controlcenter.preference;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public class JadexDoublePreference extends JadexStringPreference {

	public JadexDoublePreference(Context context) {
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
//		Double doubleValue = (Double) value;
		super.setValue("" + value);
	}
	
	public double getInt() {
		return Double.parseDouble(getText());
	}

	@Override
	protected boolean callChangeListener(Object newValue) {
		return super.callChangeListener(Double.parseDouble((String) newValue));
	}
	
	

}
