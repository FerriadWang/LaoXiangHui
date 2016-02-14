package com.SamePlacesCommunity.Main;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Downloadbitmap {
	public static Bitmap getImage(String Url) throws Exception {

		try {

			URL url = new URL(Url);

			String responseCode = url.openConnection().getHeaderField(0);

			if (responseCode.indexOf("200") < 0)

				throw new Exception("图片文件不存在或路径错误，错误代码：" + responseCode);

			return BitmapFactory.decodeStream(url.openStream());

		} catch (IOException e) {

			// TODO Auto-generated catch block

			throw new Exception(e.getMessage());

		}

	}
}
