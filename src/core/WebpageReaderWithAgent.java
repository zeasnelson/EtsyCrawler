package core;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class to read web pages from a provided url
 * The user agent for the latest google chrome on Windows is used
 * Files are not modified and are directly stored locally
 */
public class WebpageReaderWithAgent {

    /**
     * File writer, saves html received from url
     */
    private FileOutput fileWriter;

    /**
     * url of website to be scrapped, Etsy.com in this project
     */
    private String URL;

    /**
     * User Agent to send to browser
     */
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36";

    /**
     * Initializes file writer to read from the provided url
     * Results are written to a html file
     * @param URL search query url
     * @param outputFileName   name of the file to save results
     */
    public WebpageReaderWithAgent(String URL, String outputFileName) {
        fileWriter = new FileOutput(outputFileName);
        this.URL = URL;
    }


    /**
     * Method provided in class by professor
     * @param sURL The user from which the web page will be downloaded
     * @return a connection object
     */
    public InputStream getURLInputStream(String sURL)  {
        try {
            URLConnection oConnection = (new URL(sURL)).openConnection();
            oConnection.setRequestProperty("User-Agent", USER_AGENT);
            return oConnection.getInputStream();
        }catch (Exception e){
            return null;
        }
     } // getURLInputStream

    /**
     * Read the data from the provided url
     * Method provided in class by professor
     * @param url The user from which the web page will be downloaded
     * @return A BufferReader with input stream initialized
     */
    public  BufferedReader read(String url) {
        try {
            InputStream content = getURLInputStream(url);
            if( content != null ) {
                return new BufferedReader(new InputStreamReader(content));
            }
            return null;
        }catch (Exception e){
            return null;
        }
    } // read


    /**
     * Reads lines from the the input stream and writes results to a html file
     * Method provided in class by professor
     */
    public boolean writeToFile(){
        try {
            BufferedReader reader = read(URL);
            if( reader == null ){
                return false;
            }
            String line = reader.readLine();

            while (line != null) {
                fileWriter.println(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error while downloading file");
            return false;
        }
        return true;
    }//writeToFile

    /**
     * close the fie when done writing it
     */
    public  void close(){
        fileWriter.close();
    }//close

} // Core.WebpageReaderWithAgent
