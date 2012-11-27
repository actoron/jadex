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
		ChatEventViewHolder viewHolder;
		if (convertView == null)
		{
			viewHolder = new ChatEventViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.chatlistitem, parent, false);
			
			viewHolder.userTextView = (TextView) convertView.findViewById(R.id.chatlistitem_user);
			viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.chatlistitem_message);
			viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.chatlistitem_date);
			
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ChatEventViewHolder) convertView.getTag();
		}
		
		ChatEvent item = getItem(position);
		viewHolder.userTextView.setText(item.getNick());
		viewHolder.messageTextView.setText(item.getValue().toString());
		viewHolder.dateTextView.setText(timeFormatter.format(cal.getTime()));
		if (item.isPrivateMessage()) {
			viewHolder.userTextView.setTextColor(Color.RED);
		} else {
			viewHolder.userTextView.setTextColor(Color.WHITE);
		}
		
		return convertView;
	}
	
	private static class ChatEventViewHolder {
		TextView userTextView;
		TextView messageTextView;
		TextView dateTextView;
		
	}

}
