package so.droidman;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class DLManager {

	@SuppressLint("NewApi")
	public static void useDownloadManager(String url, String name, Context c) {
		DownloadManager dm = (DownloadManager) c
				.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request dlrequest = new DownloadManager.Request(
				Uri.parse(url));
		dlrequest
				.setAllowedNetworkTypes(
						DownloadManager.Request.NETWORK_WIFI
								| DownloadManager.Request.NETWORK_MOBILE)
				.setAllowedOverRoaming(false)
				.setTitle(name)
				.setDescription("Download in progress")
				.setDestinationInExternalPublicDir("my dl test", name + ".jpg")
				.allowScanningByMediaScanner();

		dm.enqueue(dlrequest);

	}
}
