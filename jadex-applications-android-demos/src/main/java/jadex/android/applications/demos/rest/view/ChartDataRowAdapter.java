package jadex.android.applications.demos.rest.view;

import jadex.android.applications.demos.R;
import jadex.android.applications.demos.rest.ChartDataRow;
import jadex.android.applications.demos.rest.view.ColorPickerDialog.OnColorChangedListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class ChartDataRowAdapter extends ArrayAdapter<ChartDataRow>
{

	private Context context;

	public ChartDataRowAdapter(Context context)
	{
		super(context, R.layout.rest_chartdataitem);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final DataItemViewHolder viewHolder;
		final ChartDataRow item = getItem(position);

		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.rest_chartdataitem, parent, false);
			viewHolder = new DataItemViewHolder();
			viewHolder.item = item;
			viewHolder.btnColor = (Button) convertView.findViewById(R.id.rest_dataItem_color);
			viewHolder.editTextData = (EditText) convertView.findViewById(R.id.rest_dataItem_data);
			convertView.setTag(viewHolder);

			// ---------------- Input handling single data items
			// -------------------

			viewHolder.editTextData.addTextChangedListener(new TextWatcher()
			{

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count)
				{
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after)
				{
				}

				@Override
				public void afterTextChanged(Editable s)
				{
					viewHolder.item.setData(stringToDataArray(s.toString()));
				}
			});
			viewHolder.btnColor.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					new ColorPickerDialog(getContext(), new OnColorChangedListener()
					{
						@Override
						public void colorChanged(int color)
						{
							viewHolder.item.setColor(color);
							viewHolder.btnColor.setTextColor(color);
						}
					}, viewHolder.item.getColor()).show();
				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener()
			{

				@Override
				public boolean onLongClick(View v)
				{
					Builder builder = new AlertDialog.Builder(getContext());
					builder.setMessage("Delete this data row?").setTitle("Confirm delete");
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							ChartDataRowAdapter.this.remove(viewHolder.item);
						}

					});
					builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
						}

					});
					builder.create().show();
					return true;
				}
			});

		} else
		{
			viewHolder = (DataItemViewHolder) convertView.getTag();
		}

		viewHolder.item = item;

		viewHolder.btnColor.setTextColor(item.getColor());
		String dataString = dataArrayToString(item.getData());
		viewHolder.editTextData.setText(dataString);

		return convertView;
	}

	private static String dataArrayToString(double[] data)
	{
		StringBuilder sb = new StringBuilder();
		String spacer = "";
		for (double i : data)
		{
			sb.append(spacer);
			sb.append(i);
			spacer = ",";
		}
		return sb.toString();
	}

	private static double[] stringToDataArray(String s)
	{
		double[] result;
		if (s.length() > 0)
		{
			String dataString = s.toString();
			dataString = dataString.replaceAll("[^0-9,\\.]*", "");
			String[] dataArr = dataString.split(",");
			double[] data = new double[dataArr.length];
			for (int i = 0; i < data.length; i++)
			{
				if (dataArr[i].length() > 0)
				{
					data[i] = Double.parseDouble(dataArr[i]);
				} else
				{
					data[i] = 0;
				}
			}
			result = data;
		} else
		{
			result = new double[0];
		}

		return result;

	}

	/**
	 * ViewHolder for this Adapter
	 */
	static class DataItemViewHolder
	{
		ChartDataRow item;
		Button btnColor;
		EditText editTextData;
	}

}
