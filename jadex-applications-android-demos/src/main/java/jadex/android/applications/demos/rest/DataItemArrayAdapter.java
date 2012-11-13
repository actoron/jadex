package jadex.android.applications.demos.rest;

import jadex.android.applications.demos.R;
import jadex.bridge.service.types.chat.ChatEvent;

import java.text.DateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DataItemArrayAdapter extends ArrayAdapter<DataItem>
{

	private Context context;
	private DateFormat timeFormatter;
	private Calendar cal;

	public DataItemArrayAdapter(Context context)
	{
		super(context, R.layout.rest_chartdataitem);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View itemView;
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = inflater.inflate(R.layout.rest_chartdataitem, parent, false);
		} else
		{
			itemView = convertView;
		}
//		TextView userTextView = (TextView) itemView.findViewById(R.id.chatlistitem_user);
//		TextView messageTextView = (TextView) itemView.findViewById(R.id.chatlistitem_message);
//		TextView dateTextView = (TextView) itemView.findViewById(R.id.chatlistitem_date);
//		ChatEvent item = getItem(position);
//		userTextView.setText(item.getNick());
//		messageTextView.setText(item.getValue().toString());
//		dateTextView.setText(timeFormatter.format(cal.getTime()));
//		if (item.isPrivateMessage()) {
//			userTextView.setTextColor(Color.RED);
//		} else {
//			userTextView.setTextColor(Color.WHITE);
//		}
//		
		return itemView;
	}

}
