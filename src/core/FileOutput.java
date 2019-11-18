package core;

import java.io.*;

/**
 * Writes text to a character-output stream, buffering characters so as to provide for the efficient writing
 * of single characters, arrays, and strings.
 * @author Nelson Zeas
 */
public class FileOutput{


    public final static String DATA_LOG= "appdata/log/datalog.txt";
    public final static String ACTIVITY_LOG= "appdata/log/activitylog";
    public final static String INSERT = "INSERT";
    public final static String DELETE = "DELETE";

    /**
     * Name of the file to open
     */
    private String outputFileName;

    private PrintWriter printWriter;


    /**
     * Writes text to a character-output stream
     */
    private  BufferedWriter writer;


    Boolean append;
        /**
     * Constructs a new FileWriter and FileReader
     * @param outputFileName The name of the file to be writte
     * @param append  append to existing file instead of creating new file
     */
    public FileOutput(String outputFileName, Boolean append) {
        try {
            this.writer = new BufferedWriter(new FileWriter(outputFileName, append));
            if( append ){
                this.printWriter =  new PrintWriter(writer);
            }
        }catch ( IOException e ){
            System.out.println("Error opening output file!");
        }
        this.outputFileName = outputFileName;
        this.append = append;
    }


    /**
     * Constructs a new FileWriter and FileReader
     * @param outputFileName The name of the file to be writte
     */
    public FileOutput(String outputFileName) {
        try {
            this.writer = new BufferedWriter(new FileWriter(outputFileName, false));
        }catch ( IOException e ){
            System.out.println("Error opening output file!");
        }
        this.outputFileName = outputFileName;
        this.append = false;
    }


    /**
     * @return Output File name
     */
    public String getOutputFileName() {
        return this.outputFileName;
    }


    /**
     * Appends an object's toString method to outputFile
     * @param o The object to be printed
     */
    public void printObj(Object o) {
        try {
            writer.append(o.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints strings to outputFile but does not advance to the next line
     * @param data
     */
    public void print(String data) {
        try {
            writer.append(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Prints chars but does not advance to the next line in outputFile
     * @param data
     */
    public void print(char data) {
        try {
            this.writer.append(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean appendToFile(String data){
        try {
            printWriter.println(data + "\n");
            return true;
        }catch ( Exception e ){
            return false;
        }
    }


    /**
     * Prints strings and advances to next line in outputFile
     * @param data
     */
    public void println(String data) {
        print(data + "\n");
    }


    /**
     * Advances to next line in outputFIle
     */
    public void println() {
        print("\n");
    }


    /**
     * Appends an integer to output file
     */
    public void print( int i ) {
        print( Integer.toString(i) );
    }



    /**
     * Closes file
     */
    public void close() {
        try {
            writer.flush();
            writer.close();
        }catch ( IOException e ){
            System.out.println("Error Closing files!");
        }
    }

}
