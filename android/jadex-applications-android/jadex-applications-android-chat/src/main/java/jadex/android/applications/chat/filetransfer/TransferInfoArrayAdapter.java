package jadex.android.applications.chat.filetransfer;

import jadex.android.applications.chat.R;
import jadex.android.applications.chat.filetransfer.TransferInfoItemWidget.TransferInfoViewHolder;
import jadex.bridge.service.types.chat.TransferInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TransferInfoArrayAdapter extends ArrayAdapter<TransferInfo>
{

	private Context context;
	private static final int sizeDisplayFactor = 1024;

	public TransferInfoArrayAdapter(Context context)
	{
		super(context, R.layout.transferinfoitem);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TransferInfoViewHolder viewHolder;
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.transferinfoitem, parent, false);
			viewHolder = new TransferInfoViewHolder();
			viewHolder.txtEta = (TextView) convertView.findViewById(R.id.transferinfo_eta);
			viewHolder.txtFileName = (TextView) convertView.findViewById(R.id.transferinfo_filename);
			viewHolder.txtPeer = (TextView) convertView.findViewById(R.id.transferinfo_peer);
			viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.transferinfo_progress);
			viewHolder.txtSize = (TextView) convertView.findViewById(R.id.transferinfo_size);
			viewHolder.txtSpeed = (TextView) convertView.findViewById(R.id.transferinfo_speed);
			viewHolder.txtState = (TextView) convertView.findViewById(R.id.transferinfo_state);
			viewHolder.txtUpDown = (TextView) convertView.findViewById(R.id.transferinfo_updown);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (TransferInfoViewHolder) convertView.getTag();
		}

		TransferInfo ti = getItem(position);

		viewHolder.updateFrom(ti);

		return convertView;
	}

}
