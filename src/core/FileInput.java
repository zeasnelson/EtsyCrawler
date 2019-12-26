package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class implements the buffer reader to provide the most useful features.
 *  -read lines
 *  -read any char
 *  -read only non whitespace chars
 *
 *  All exceptions are handled and all methods return a value, true or false null
 * @author Nelson Zeas
 */

public class FileInput{

    /**
     * Reads characters from a file
     */
    private BufferedReader reader;

    /**
     * Name of the file to open
     */
    String inputFileName;

        /**
     * Constructs a new FileWriter and FileReader
     * @param inputFileName The name of the file to be read
     */
    public FileInput(String inputFileName){
        try {
            reader = new BufferedReader(new FileReader(inputFileName));
        }catch ( IOException e ){
            System.out.println("Error opening input file!");
        }
        this.inputFileName = inputFileName;
    }

    public String readLine(){
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads a char from inputFile
     * @return Char read from input file
     * @throws IOException
     */
    public int getNextChar(){
        try {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Finds the next non-whitespace char in the stream
     * @return The next non-whitespace char
     */
    public int getNextNonWhiteSpace(){
        int charCode;
        try {
            charCode = reader.read();
            while (Character.isWhitespace(charCode)) {
                charCode = reader.read();
            }
        }catch ( IOException e ){
            return -1;
        }
        return charCode;
    }



    /**
     * @return Input File name
     */
    public String getInputFileName(){
        return this.inputFileName;
    }


    /**
     * Closes input file
     */
    public void close() {
        try {
            reader.close();
        }catch ( IOException e ){
            System.out.println("Error Closing input file!");
        }
    }
}
