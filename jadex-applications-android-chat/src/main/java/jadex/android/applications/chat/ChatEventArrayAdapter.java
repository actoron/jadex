package jadex.android.applications.chat;

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

public class ChatEventArrayAdapter extends ArrayAdapter<ChatEvent>
{

	private Context context;
	private DateFormat timeFormatter;
	private Calendar cal;

	public ChatEventArrayAdapter(Context context)
	{
		super(context, R.layout.chatlistitem);
		this.context = context;
		timeFormatter = DateFormat.getTimeInstance(java.text.DateFormat.MEDIUM);
		cal = Calendar.getInstance();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View itemView;
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = inflater.inflate(R.layout.chatlistitem, parent, false);
		} else
		{
			itemView = convertView;
		}
		TextView userTextView = (TextView) itemView.findViewById(R.id.chatlistitem_user);
		TextView messageTextView = (TextView) itemView.findViewById(R.id.chatlistitem_message);
		TextView dateTextView = (TextView) itemView.findViewById(R.id.chatlistitem_date);
		ChatEvent item = getItem(position);
		userTextView.setText(item.getNick());
		messageTextView.setText(item.getValue().toString());
		dateTextView.setText(timeFormatter.format(cal.getTime()));
		if (item.isPrivateMessage()) {
			userTextView.setTextColor(Color.RED);
		} else {
			userTextView.setTextColor(Color.WHITE);
		}
		
		return itemView;
	}

}
