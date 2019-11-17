package core;

import java.io.*;
import java.net.URL;

public class DownloadURLImage {

	public static Boolean downloadImage(String imgUrl, String destinationFile) {
		try {
			URL url = new URL(imgUrl);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(destinationFile);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();
		}catch (IOException e ){
			return false;
		}
		return true;
	}

//	public static void main(String[] args) throws Exception {
//		String imageUrl = "https://i.etsystatic.com/8328926/d/il/a72f4b/477683526/il_340x270.477683526_qh7t.jpg?version=0";
//		String destinationFile = "image.jpg";
//
//		saveImage(imageUrl, destinationFile);
//	}


} // Core.GetURLImage
