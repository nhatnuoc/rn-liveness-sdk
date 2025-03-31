package com.livenessrnexample.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ImageUtil {

	public static Bitmap base64ToBitmap(String b64Data) {
		try {
			byte[] decodedString = Base64.decode(b64Data,Base64.DEFAULT);
			InputStream inputStream  = new ByteArrayInputStream(decodedString);
			Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
			return  bitmap;
		} catch (Error e) {
			e.printStackTrace();
			return null;
		}
	}
}
