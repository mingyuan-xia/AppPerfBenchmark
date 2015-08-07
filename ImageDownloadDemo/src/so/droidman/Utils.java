package so.droidman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

public class Utils {
	private static FileOutputStream fos;
	/**
	 * This is an empty Cookie object for demo purposes. In a real project, you
	 * would use the cookie that you have received from your backend when
	 * authorizing. Below in this class you can find a demo method showing you
	 * how to retrieve Cookies from the HttpClient after server response (in
	 * case they exist).
	 */
	public static Cookie sessionCookie = new Cookie() {

		@Override
		public boolean isSecure() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isPersistent() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isExpired(Date date) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getVersion() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int[] getPorts() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getExpiryDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDomain() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCommentURL() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getComment() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * A demo method showing how to get the cookies from a HttpClient
	 * 
	 * @param httpclient
	 *            the HttpClient to get the cookies from
	 * @return all cookies or an empty List in case there are none of them
	 */
	public List<Cookie> getCookies(DefaultHttpClient httpclient) {
		return httpclient.getCookieStore().getCookies();

	}

	public static void saveImage(Bitmap bmp, String name, Context c) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + name + ".jpg");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			fos.write(bytes.toByteArray());
			fos.close();
			Toast.makeText(c, "Image saved", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
