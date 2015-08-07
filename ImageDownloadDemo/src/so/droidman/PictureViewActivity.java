package so.droidman;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureViewActivity extends Activity {

	private TextView tvStatus;
	private ImageView imgResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_view);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		imgResult = (ImageView) findViewById(R.id.imgResult);
		if (MainActivity.tmpResponseForUIDownload.length() > 0)
			tvStatus.setText("Network response: "
					+ MainActivity.tmpResponseForUIDownload);
		else
			tvStatus.setText("Network response: "
					+ getIntent().getStringExtra("status"));

		try {
			imgResult.setImageBitmap(MainActivity.image);
		} catch (Exception e) {
			e.printStackTrace();
			imgResult.setImageResource(R.drawable.ic_warning);
		}
	}
}
