package jadex.android.applications.chat;

import java.text.DateFormat;

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
		
		int totalSize = (int) (ti.getSize()/sizeDisplayFactor);
		int doneSize = (int) (ti.getDone()/sizeDisplayFactor);
		int speed = (int) (ti.getSpeed()/sizeDisplayFactor);
		
		viewHolder.txtEta.setText(getETA(ti));
		viewHolder.progressBar.setMax(totalSize);
		viewHolder.progressBar.setProgress(doneSize);
		viewHolder.txtFileName.setText(ti.getFileName());
		viewHolder.txtPeer.setText(ti.getOther().getLocalName());
		viewHolder.txtSize.setText(""+totalSize);
		viewHolder.txtSpeed.setText(""+speed);
		viewHolder.txtState.setText(ti.getState());
		viewHolder.txtUpDown.setText(ti.isDownload() ? "v" : "^");
		
		return convertView;
	}
	
	private String getETA(TransferInfo ti) {
		String ret;
		if(TransferInfo.STATE_TRANSFERRING.equals(ti.getState()))
		{
			long	time	= (long)((ti.getSize()-ti.getDone())/ti.getSpeed());
			long	hrs	= time / 3600;
			long	min	= time % 3600 / 60;
			long	sec	= time % 60;
			ret	= hrs + ":" + (min<10 ? "0"+min : min) + ":" + (sec<10 ? "0"+sec : sec);
		}
		else if(ti.getTimeout()>0)
		{
			long	time	= (ti.getTimeout()-System.currentTimeMillis())/1000;
			ret	= time>0 ? Long.toString(time) : "0";
		}
		else
		{
			ret	= "";
		}
		return ret;
	}
	
	private static class TransferInfoViewHolder {

		public TextView txtUpDown;
		public TextView txtState;
		public TextView txtSpeed;
		public TextView txtSize;
		public ProgressBar progressBar;
		public TextView txtPeer;
		public TextView txtFileName;
		public TextView txtEta;
		
	}
	
	

}
