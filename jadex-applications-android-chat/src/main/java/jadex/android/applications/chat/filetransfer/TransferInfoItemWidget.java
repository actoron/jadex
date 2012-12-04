package jadex.android.applications.chat.filetransfer;

import jadex.android.applications.chat.R;
import jadex.android.applications.chat.R.id;
import jadex.android.applications.chat.R.layout;
import jadex.bridge.service.types.chat.TransferInfo;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TransferInfoItemWidget extends LinearLayout
{

	private TransferInfoViewHolder viewHolder;


	public TransferInfoItemWidget(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		viewHolder = new TransferInfoViewHolder();
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.transferinfoitem, this);
		viewHolder.initFromView(this);
	}

	public void updateFrom(TransferInfo ti)
	{
		viewHolder.updateFrom(ti);
	}
	
	public static class TransferInfoViewHolder {

		public TextView txtUpDown;
		public TextView txtState;
		public TextView txtSpeed;
		public TextView txtSize;
		public ProgressBar progressBar;
		public TextView txtPeer;
		public TextView txtFileName;
		public TextView txtEta;
		
		private static final int sizeDisplayFactor = 1024;
		
		public void updateFrom(TransferInfo ti)
		{
			int totalSize = (int) (ti.getSize()/sizeDisplayFactor);
			int doneSize = (int) (ti.getDone()/sizeDisplayFactor);
			int speed = (int) (ti.getSpeed()/sizeDisplayFactor);
			
			txtEta.setText(getETA(ti));
			progressBar.setMax(totalSize);
			progressBar.setProgress(doneSize);
			txtFileName.setText(ti.getFileName());
			txtPeer.setText(ti.getOther().getLocalName());
			txtSize.setText(""+totalSize);
			txtSpeed.setText(""+speed);
			txtState.setText(ti.getState());
			txtUpDown.setText(ti.isDownload() ? "v" : "^");
		}
		
		public void initFromView(View view)
		{
			txtEta = (TextView) view.findViewById(R.id.transferinfo_eta);
			txtFileName = (TextView) view.findViewById(R.id.transferinfo_filename);
			txtPeer = (TextView) view.findViewById(R.id.transferinfo_peer);
			progressBar = (ProgressBar) view.findViewById(R.id.transferinfo_progress);
			txtSize = (TextView) view.findViewById(R.id.transferinfo_size);
			txtSpeed = (TextView) view.findViewById(R.id.transferinfo_speed);
			txtState = (TextView) view.findViewById(R.id.transferinfo_state);
			txtUpDown = (TextView) view.findViewById(R.id.transferinfo_updown);
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
	}

}
