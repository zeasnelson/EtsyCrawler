package core;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class WebpageReaderWithAgent {

    /**
     * To create to files, saves html received from url
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


    public InputStream getURLInputStream(String sURL)  {
        try {
            URLConnection oConnection = (new URL(sURL)).openConnection();
            oConnection.setRequestProperty("User-Agent", USER_AGENT);
            return oConnection.getInputStream();
        }catch (Exception e){
            return null;
        }
     } // getURLInputStream

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
            e.printStackTrace();
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

//   public static void main (String[] args){
//        String searchQuery = "banana";
//        String outFileName = "results.html";
//        WebpageReaderWithAgent htmlReader = new WebpageReaderWithAgent(searchQuery, outFileName);
//        htmlReader.writeToFile();
//        htmlReader.close();
//
//
//   } // main
} // Core.WebpageReaderWithAgent
