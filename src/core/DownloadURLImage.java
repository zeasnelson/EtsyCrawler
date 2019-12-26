package core;

import java.io.*;
import java.net.URL;

/**
 * A class  to download images from a provided url
 * It downloads a stores images at the specified location
 *
 * NOTE: This is not the code provided in class
 *
 * @Author Nelson Zeas
 * @Date 11/20/2019
 */
public class DownloadURLImage {


	/**
	 * Donwload an image from a given url
	 * @param imgUrl The image url from which to download the image
	 * @param destinationFile The location, including the image name and format, where image will be saved to
	 * @return true if image was downloaded, false otherwise
	 */
	public static Boolean downloadImage(String imgUrl, String destinationFile) {
		try {
			//create the url
			URL url = new URL(imgUrl);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(destinationFile);

			byte[] b = new byte[2048];
			int length;

			//read the image
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


} // Core.DownloadUrlImage
