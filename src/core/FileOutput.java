package core;

import java.io.*;

/**
 * Writes text to a character-output stream, buffering characters so as to provide for the efficient writing
 * of single characters, arrays, and strings.
 * @author Nelson Zeas
 */
public class FileOutput{


    /**
     * Global static final variables to where log files are stored with the application
     */
    public final static String DATA_LOG= "appdata/log/datalog.txt";
    public final static String ACTIVITY_LOG= "appdata/log/activitylog.txt";

    /**
     * Operations for logged data
     */
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
     * Check if privided directory exists, if it does not attempts to create it
     * @param parentDir The parent dir where the subfolder will be created
     * @param subFolder The child folder of the parent dir
     * @return true if folder was created or already existed, false otherwise
     */
    public static Boolean createDir(String parentDir, String subFolder){
        File parent = new File(parentDir);
        if( parent.exists() ){
            File subDir = new File(parent, subFolder);
            if( !subDir.exists() ){
                return subDir.mkdir();
            }
        }
        return false;
    }

    /**
     * Create a file as a child of @parentDir
     * @param parentDir The parent dir where the child file will be created
     * @param fileName The file name that will be created as a child of parent dir
     * @return true if file was created or already existed, false otherwise
     */
    public static Boolean createFile(String parentDir, String fileName){
        File parent = new File(parentDir);
        if( parent.exists() ){
            File subDir = new File(parent, fileName);
            if( !subDir.exists() ){
                try {
                    return subDir.createNewFile();
                } catch (IOException e) {
                    return false;
                }

            }
        }
        return false;
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
