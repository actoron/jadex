package jadex.android.controlcenter.componentViewer.properties;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class PropertyItemAdapter extends ArrayAdapter<PropertyItem>
{

	public PropertyItemAdapter(Context context, PropertyItem[] items)
	{
		super(context, 0, items);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		PropertyItemView itemView = (PropertyItemView) convertView;
		if (itemView == null) {
			itemView = new PropertyItemView(parent.getContext());
		}
		PropertyItem item = getItem(position);
		itemView.txtName.setText(item.name);
		itemView.txtValue.setText(item.value != null ? item.value.toString() : "N/A");
		return itemView;
	}
	
	private static class PropertyItemView extends LinearLayout
	{
		
		private TextView txtName;
		private TextView txtValue;

		public PropertyItemView(Context context)
		{
			super(context);
			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.VERTICAL);
			txtName = new TextView(context);
			txtName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
			txtName.setTextColor(ColorStateList.valueOf(Color.WHITE));
			ll.addView(txtName);
			txtValue = new TextView(context);
			txtName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			txtName.setTextColor(ColorStateList.valueOf(Color.GRAY));
			ll.addView(txtValue);
			
			LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(5, 10, 5, 10);
			ll.setLayoutParams(lp);
			this.addView(ll);
		}
	}

}
