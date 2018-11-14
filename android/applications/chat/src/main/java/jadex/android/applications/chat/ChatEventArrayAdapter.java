package jadex.android.applications.chat;

import jadex.bridge.service.types.chat.ChatEvent;

import java.text.DateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
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
	private String myNick;

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
		viewHolder.dateTextView.setText(timeFormatter.format(cal.getTime()));
		if (myNick != null && item.getNick().equals(myNick)) {
//			viewHolder.userTextView.setTextColor(0xFF222e3b);
			viewHolder.userTextView.setTextColor(0xFF0099CC);
		} else {
			if (item.isPrivateMessage()) {
//				viewHolder.userTextView.setTextColor(0xFFa82f2f);
				viewHolder.userTextView.setTextColor(0xFFCC0000);
			} else {
				viewHolder.userTextView.setTextColor(Color.BLACK);
			}
		}
		
		if (item.getType().equals(ChatEvent.TYPE_MESSAGE)) {
			viewHolder.userTextView.setVisibility(View.VISIBLE);
			viewHolder.userTextView.setText(item.getNick());
			viewHolder.messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			viewHolder.messageTextView.setText(item.getValue().toString());
		} else if (item.getType().equals(ChatEvent.TYPE_STATECHANGE)) {
			viewHolder.userTextView.setVisibility(View.GONE);
			viewHolder.messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			if (item.getValue() == null) {
				viewHolder.messageTextView.setText(item.getNick() + " changed his nick. ");
			} else {
				viewHolder.messageTextView.setText(item.getNick() + "'s status is now: " + item.getValue().toString());
			}
		}
		
		return convertView;
	}
	
	private static class ChatEventViewHolder {
		TextView userTextView;
		TextView messageTextView;
		TextView dateTextView;
	}

	public void setOwnNick(String nick) {
		this.myNick = nick;
	}

}
