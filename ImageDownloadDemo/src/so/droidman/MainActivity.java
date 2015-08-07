package so.droidman;

import so.droidman.AsyncImageLoader.onImageLoaderListener;
import so.droidman.AsyncImageLoader.onProgressUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		onProgressUpdateListener {

	private Button btnDownloadUI, btnDownloadAsync, btnDownloadDM, btnSave;
	private EditText etURL, etFileName;
	private CheckBox chkUseCookie, chkIgnoreSSL;
	private RelativeLayout rlProgress;
	private TextView tvProgress;
	private ProgressBar pbProgress;
	private String url = "", fname = "";
	private final int SDK = Build.VERSION.SDK_INT;
	/* don't do it in a real project :) */
	public static String tmpResponseForUIDownload = "";
	public static Bitmap image;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (SDK >= 11) {
			setTheme(android.R.style.Theme_Holo_NoActionBar);
		}
		setContentView(R.layout.activity_main);
		init();
	}

	/**
	 * initializes Activity's layout components for the initial launch
	 */
	private void init() {
		btnDownloadAsync = (Button) findViewById(R.id.btnDLAsync);
		btnDownloadDM = (Button) findViewById(R.id.btnDLWithDM);
		btnDownloadUI = (Button) findViewById(R.id.btnDLOnUi);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnDownloadAsync.setOnClickListener(this);
		btnDownloadDM.setOnClickListener(this);
		btnDownloadUI.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		chkIgnoreSSL = (CheckBox) findViewById(R.id.chkIgnoreSSL);
		chkUseCookie = (CheckBox) findViewById(R.id.chkUseCookie);
		rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
		pbProgress = (ProgressBar) findViewById(R.id.pbDLProgerss);
		tvProgress = (TextView) findViewById(R.id.tvProgress);
		etURL = (EditText) findViewById(R.id.etImgUrl);
		etFileName = (EditText) findViewById(R.id.etFileName);
	}

	/**
	 * Toggles download progress' visibility
	 * 
	 * @param visible
	 *            pass <i>true</i> to show or <i>false</i> to hide
	 */
	private void toggleProgressVisibility(boolean visible) {
		if (visible)
			rlProgress.setVisibility(View.VISIBLE);
		else
			rlProgress.setVisibility(View.GONE);
	}

	/**
	 * Gets the url and desired file name from input fields
	 */
	private boolean getNameAndUrl() {
		/* you can add checks for invalid values here */

		if (etURL.getText().toString().length() > 0
				&& etFileName.getText().toString().length() > 0) {
			url = etURL.getText().toString();
			fname = etFileName.getText().toString();
			return true;
		} else
			Toast.makeText(this, "input incomplete", Toast.LENGTH_SHORT).show();
		return false;
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		if (getNameAndUrl()) {

			switch (v.getId()) {

			case R.id.btnDLAsync:
				toggleProgressVisibility(true);
				AsyncImageLoader loader = new AsyncImageLoader(
						new onImageLoaderListener() {

							@Override
							public void onImageLoaded(Bitmap image,
									String response) {
								btnSave.setEnabled(true);
								Intent i = new Intent(MainActivity.this,
										PictureViewActivity.class);
								i.putExtra("status", response);
								startActivity(i);
								MainActivity.image = image;

							}

						}, chkIgnoreSSL.isChecked(), chkUseCookie.isChecked(),
						this);
				if (SDK >= 11)
					loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							url);
				else
					loader.execute(url);

				break;

			case R.id.btnDLOnUi:
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
				image = AsyncImageLoader.downloadImage(url);

				break;

			case R.id.btnDLWithDM:
				DLManager.useDownloadManager(url, fname, MainActivity.this);
				break;

			case R.id.btnSave:
				Utils.saveImage(image, fname, MainActivity.this);
				break;
			}
		}

	}

	@Override
	public void doUpdateProgress(int progress) {
		tvProgress.setText(progress + "%");
		pbProgress.setProgress(progress);
	}
}
