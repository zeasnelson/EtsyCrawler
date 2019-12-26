package manager;


import core.*;
import gui.GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class to manage execute command line flags and start up the GUI
 * Available flags: print, email or print and email
 *      flag explanation
 *          -i: specify the input file name or null as value
 *              -i inputFileName  | null
 *          -o: specify the output file name or null as value
 *              -o outputFileName | null
 *          -p: can send only email or only print or email and print
 *              -p ( email )( emailAddress ) | null
 *  *           -p ( both )(emailAddress )   | null
 *  *           -p print  | null
 *          -yes will start the GUI after executing flags
 *          -no  will not start GUI after executing flags
 *
 * NOTE: invalid flags will halt the program
 */
public class App {

    /**
     * If a file with an initial set of queries is specified,
     * all data downloaded will be stored in this HashMap
     */
    public static HashMap<String, ArrayList<Item>> results;

    /**
     * flag to detect command line arguments error
     */
    public static boolean flagError     = false;

    /**
     * flag to send of or not send email
     */
    public static boolean sendEmail     = false;

    /**
     * flag to print or not print
     */
    public static boolean print         = false;

    /**
     * flag to start GUI after executing flags or not
     */
    public static boolean startGui      = false;

    /**
     * flag to email and print if input file was specified
     */
    public static boolean emailAndPrint = false;

    /**
     * Store the input file name
     */
    public static String inputFilename  = "";

    /**
     * store the output file name
     */
    public static String outputFilename = "";

    /**
     * To store recipient's email address
     */
    public static String emailAddress   = "";

    /**
     * Email object to be able to send email
     */
    public static Email email;

    /**
     * Validate file name
     * @param value the file name
     * @return true if valid, false otherwise
     */
    public static Boolean isFileName(String value){
        return value.matches("([a-zA-Z0-9_\\\\.\\-\\(\\):])+(.txt)$");
    }

    /**
     * Validate entered email address
     * @param emailAddress the entered email address
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmailAddress(String emailAddress){
        String validEmail = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return emailAddress.matches(validEmail);
    }

    /**
     * Parse all flags specified
     * @param flags an array containing all flags
     */
    public static void parseFlags(String [] flags){
        //check if inputFile name was provided
        if( !flags[1].equals("null") ){
            if( isFileName(flags[1] )){
                inputFilename = flags[1];
            }
        }

        //check if output file name was provided
        if( !flags[2].equals("null") ){
            if( isFileName(flags[3]) ){
                outputFilename = flags[3];
            }
        }

        //check if sending email and creating a file with all results
        if( flags[5].equals("both") ){
            if( isValidEmailAddress(flags[6]) ){
                emailAddress = flags[6];
                emailAndPrint = true;
            }
        }
        //check if only sending email with results
        else if( flags[5].equals("email") ){
            if( isValidEmailAddress(flags[6]) ){
                emailAddress = flags[6];
                sendEmail = true;
            }
        }
        //check if only creating a file with all results
        else if( flags[5].equals("print") ){
            print = true;
        }

        //check if GUI has to be started
        if (flags[flags.length-1].equals("yes")) {
            startGui = true;
        }
        else if(flags[flags.length-1].equals("no")){
            startGui = false;
        }
        else {
            flagError = true;
            System.out.println("Invalid flag: " + flags[flags.length-1]);

        }

    }

    /**
     * Display to user which flags will be executed
     */
    public static void displayToUser(){
        if( inputFilename.isEmpty() && outputFilename.isEmpty() ){
            System.out.println("Error: no input or output file name specified");
            return;
        }

        if( flagError ){
            System.out.println("Invalid flags provided, flags should be provided as specified in the instructions");
            return;
        }

        if( sendEmail ){
            System.out.println("Will send email to: " + emailAddress );
        }
        else if( print ){
            System.out.println("Will send print results to: " + outputFilename );
        }
        else if( emailAndPrint ){
            System.out.println("Will email results to: " + emailAddress + " and print results to: " + outputFilename );
        }

        if( sendEmail || print || emailAndPrint ){
            System.out.println("Search queries will be read from: " + inputFilename);
            System.out.print("Search queries results will be written to: ");
            System.out.println( (outputFilename.isEmpty() ? "output.txt" : outputFilename) );
        }

        if( startGui ){
            System.out.println("Gui will start after specified flags are executed");
        }
        else
            System.out.println("Gui will not start after specified flags are executed");
    }

    /**
     * Read all search queries from the provided input file
     * @return an ArrayList with all search queries
     */
    public static ArrayList<String> readSearchQueries(){
        //check if file exists first
        File inputFile = new File(inputFilename);
        if( !inputFile.exists() ){
            return null;
        }

        //read search queries from file
        ArrayList<String> searchQueries = new ArrayList<>();
        FileInput reader = new FileInput(inputFilename);
        String searchQuery = reader.readLine();
        while( searchQuery != null ){
            searchQueries.add(searchQuery.trim().replace(" ", "%20"));
            searchQuery = reader.readLine();
        }
        reader.close();
        return searchQueries;
    }

    /**
     * Download data from Etsy.com
     * @param searchQuery the search query
     * @return An ArrayList with all items extracted
     */
    public static ArrayList<Item> requestDataFromEtsy(String searchQuery){
        EtsyUrlFormatter url = new EtsyUrlFormatter();
        url.setSearchQuery(searchQuery);
        String localDir = "appdata/cache/flags.html";

        //Download the html file
        System.out.println("Downloading from Etsy.com");
        WebpageReaderWithAgent downLoadHTML = new WebpageReaderWithAgent(url.getFormatedUrl(), localDir);
        Boolean downloadSuccess = downLoadHTML.writeToFile();
        if( !downloadSuccess ){
            System.out.println("Error downloading html file from Etsy.com");
            return null;
        }
        System.out.println("Parsing data");
        //Parse and extract necessary data //localDir
        HTMLParser parser = new HTMLParser(localDir);
        parser.parse();

        System.out.println("Extracted " + parser.getAllItems().size() + " items!\n");
        return parser.getAllItems();
    }


    /**
     * Create a HashMap with all data downloaded from Etsy.com
     * @param searchQuery ArrayList with searchQueries
     */
    public static void requestDataFromEtsy(ArrayList<String> searchQuery){
        results = new HashMap<>();
        for( String query : searchQuery ){
            System.out.println("searching "+query);
            results.put(query, requestDataFromEtsy(query) );
        }
    }

    /**
     * If print flag is activated, all results are written to a file
     */
    public static void printToFile(){
        FileOutput writer = new FileOutput(outputFilename);
        for(String query : results.keySet() ) {
            writer.println("Search query: " + query.replace("%20", " ") + "   Number of results: " + results.get(query).size());
            for( Item item : results.get(query) ){
                writer.println("Description: " + item.getDescription());
                writer.println("Category:    " + item.getCategory());
                writer.println("Price:       $ " + item.getPrice());
                writer.println("Timestamp:   " + item.getTimeStamp());
                writer.println("Image url:   " + item.getImgScr());
                writer.println();
            }
            writer.println("\n\n\n");
        }
        writer.close();
    }

    /**
     * If email flag is activated, create an email report
     */
    public static void createEmailReport(){
        if( results != null ){
            email.setHeader("Email Report from EtsyCrawler");
            for( String query : results.keySet() ){
                email.addLineToBody("Search query: " + query.replaceAll("%20", " ") + "   Number of results: " + results.get(query).size());
                email.setEmailBody(results.get(query), "");
            }
        }
    }

    /**
     * Execute all flags
     */
    public static void executeFlags(){
        if( sendEmail || print || emailAndPrint && ( !inputFilename.isEmpty() && !outputFilename.isEmpty() ) ){
            ArrayList<String> searchQueries = readSearchQueries();
            if( searchQueries == null ){
                System.out.println("No results received from Etsy.com");
                return;
            }

            System.out.println(searchQueries.size() + " found");

            //search all items in the input file
            requestDataFromEtsy(searchQueries);
            System.out.println(results.size());

            //write to file if requested
            if( print ){
                printToFile();
                System.out.println("All results printed to: " + outputFilename);
            }

            //send email if requested
            else if( sendEmail ){
                email = new Email();
                createEmailReport();
                email.setRecipient(emailAddress);
                boolean wasSent = email.sendEmail();
                if( wasSent ) {
                    System.out.println("All results emailed to: " + emailAddress);
                }
                else {
                    System.out.println("Email could not be sent to " + emailAddress + ". Check if email is valid");
                }
            }
            else
                //send email and write to file if requested
                if( emailAndPrint ){
                    //send email
                    email = new Email();
                    createEmailReport();
                    email.setRecipient(emailAddress);
                    boolean wasSent = email.sendEmail();
                    if( wasSent ) {
                        System.out.println("All results emailed to: " + emailAddress);
                    }
                    else {
                        System.out.println("Email could not be sent to " + emailAddress + ". Check if email is valid");
                    }

                    //write to output file
                    printToFile();
                    System.out.println("All results printed to: " + outputFilename);
                }
        }

        //start gui after all flags executed if set to "yes"
        if( startGui ){
            System.out.println("Starting GUI");
            GUI gui = new GUI();
        }
    }


    public static void main(String[] args) {
        parseFlags(args);
        displayToUser();
        executeFlags();

        System.out.println("Done!");
    }
}
