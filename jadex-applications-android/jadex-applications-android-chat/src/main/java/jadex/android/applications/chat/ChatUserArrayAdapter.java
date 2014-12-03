package jadex.android.applications.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatUserArrayAdapter extends ArrayAdapter<ChatUser>
{

	private Context context;

	public ChatUserArrayAdapter(Context context)
	{
		super(context, R.layout.chatuseritem);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		UserViewHolder viewHolder;
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.chatuseritem, parent, false);
			viewHolder = new UserViewHolder();
			viewHolder.txtNickName = (TextView) convertView.findViewById(R.id.chatuseritem_txtnickname);
			viewHolder.txtPlatformName = (TextView) convertView.findViewById(R.id.chatuseritem_txtplatform);
			viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.chatuseritem_txtstatus);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (UserViewHolder) convertView.getTag();
		}

		ChatUser user = getItem(position);
		viewHolder.txtNickName.setText(user.getNickName());
		viewHolder.txtPlatformName.setText(user.getCid().getPlatformName());
		viewHolder.txtStatus.setText(user.getStatus());

		return convertView;
	}

	private static class UserViewHolder
	{
		TextView txtNickName;
		TextView txtStatus;
		TextView txtPlatformName;
	}

}
