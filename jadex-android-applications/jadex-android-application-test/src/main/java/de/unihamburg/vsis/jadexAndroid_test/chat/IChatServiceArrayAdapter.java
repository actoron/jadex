package de.unihamburg.vsis.jadexAndroid_test.chat;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.unihamburg.vsis.jadexAndroid_test.R;

public class IChatServiceArrayAdapter extends ArrayAdapter<IChatService> {

	public IChatServiceArrayAdapter(Context context, List<IChatService> svcs) {
		super(context, R.layout.iservice_list_item, svcs);
	}

	public IChatServiceArrayAdapter(Context context) {
		super(context, R.layout.iservice_list_item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.iservice_list_item, null);
		}

		TextView name = (TextView) v.findViewById(R.id.iservice_list_item_name);
		IChatService chatService = getItem(position);

		String identification = chatService.getIdentification();
		
		if (name != null) {
			name.setText(identification);
//			name.setTextColor(msg.color);
		}
		
		return v;
	}
}
