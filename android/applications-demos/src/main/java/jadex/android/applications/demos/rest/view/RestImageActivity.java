package jadex.android.applications.demos.rest.view;

import jadex.android.applications.demos.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

public class RestImageActivity extends Activity
{
	public static final String EXTRA_IMAGE = "extra_image";

	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rest_imageactivity);

		imageView = (ImageView) findViewById(R.id.rest_imageView);

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		byte[] imageArr = getIntent().getByteArrayExtra(EXTRA_IMAGE);

		if (imageArr != null)
		{
			final Bitmap image = BitmapFactory.decodeByteArray(imageArr, 0, imageArr.length);

			imageView.setImageBitmap(image);
		} else
		{
			finish();
		}
	}
}
