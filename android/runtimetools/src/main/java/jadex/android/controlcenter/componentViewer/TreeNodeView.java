package jadex.android.controlcenter.componentViewer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TreeNodeView extends LinearLayout
{

	private TextView txtName;

	public TreeNodeView(Context context)
	{
		super(context);
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		txtName = new TextView(context);
		txtName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
		txtName.setTextColor(ColorStateList.valueOf(Color.WHITE));
		ll.addView(txtName);
		LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(5, 10, 5, 10);
		ll.setLayoutParams(lp);
		this.addView(ll);
	}

	public void setName(String name)
	{
		txtName.setText(name);
	}

}
