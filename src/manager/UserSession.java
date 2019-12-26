package manager;

import core.*;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Allows to create a new user session
 * This class provides different operations to manage all user activity while logged in
 *      - Search
 *      - Advanced search with filters
 *      - Send email report
 *      - Sort data by price
 *      - Sort data by date
 *      - Delete entire searches
 *      - Delete specific results
 *      - Create an email report
 *      - Delete the email report
 *      - Account management: changing password
 *      - View images for specific results
 *      - View search history
 */
public class UserSession {



    /**
     * ArrayList to store all items selected by the user to be emailed
     */

    private ArrayList<Item> emailList;
    /**
     * Stores the file name of the current user
     * This file store all search keys and url
     */

    private String userSearchKeyDir;
    /**
     * Store the local directory for the current user
     * This directory will be a file with the user's unique id in which all search data will be stored
     */

    private String baseDir;
    /**
     * Store the search key for the most recent search performed by the user
     */

    private String currentSearch;
    /**
     * To store the information of the user currently logged in
     */

    private User user;
    /**
     * HasMap to store all user searches
     * This hashmap is written to the user's local directory when the user logs out from the account
     */

    private HashMap<String, ArrayList<Item>> searches;

    /**
     * Flag to know when the user is viewing the email report to enable/disable specific functions
     */
    private Boolean isViewingEmailReport;

    /**
     * Flag to know when the current user is logged is as guest or member
     * This flag allows to disable/enable specific functions
     */
    private Boolean isGuestUser;

    /**
     * HashMap contains all search keys, each search key is associated with its own unique url
     * This HashMap is always initialized when the user logs in to enable viewing history
     */
    private HashMap<String, String> searchHistory;


    /**
     * Int user's default vales
     * @param user A User with Id and password
     */
    public UserSession(User user) {
        this.user = user;
        this.searches = new HashMap<>();
        this.searchHistory = new HashMap<>(10);
        this.emailList = new ArrayList<>();
        this.isViewingEmailReport = false;
        this.baseDir = "appdata/usersdata/"+user.getUserId()+"/";
        this.userSearchKeyDir = "searchKey.txt";
        new File(baseDir).mkdir();
        this.isGuestUser = false;
    }

    /**
     * @return searchHistory HashMap
     */
    public HashMap<String, String> getSearchHistory() {
        return searchHistory;
    }

    /**
     * set isGuestUser to enable/disable specific GUI functions
     * @param isGuestUser true or false
     */
    public void setIsGuestUser(Boolean isGuestUser){
        this.isGuestUser = isGuestUser;
    }

    /**
     * @return true if current user is guest, false otherwise
     */
    public Boolean isGuestUser(){
        return this.isGuestUser;
    }

    /**
     * @return HashMap
     */
    public HashMap<String, ArrayList<Item>> getSearches() {
        return searches;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the currently logged in user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the last searchQuery performed by user
     */
    public String getCurrentSearch() {
        return currentSearch;
    }

    /**
     * @return true if the current user is viewing the email report, false otherwise
     */
    public Boolean isViewingEmailReport() {
        return isViewingEmailReport;
    }

    /**
     * Read all saved searches from user and store it in an ArrayList
     * @return an ArrayList that contains all saved history
     */
    public ArrayList<Item> getAllSearches(){
        ArrayList<Item> allItems = new ArrayList<>();
        //remove it becuase users might have searched for something new
        if( searches.containsKey("absolutelyEverythingFromAllTime")){
            searches.remove("absolutelyEverythingFromAllTime");
        }
        for( String searchQuery : searchHistory.keySet() ){
            ArrayList<Item> results = search(searchQuery, searchHistory.get(searchQuery));
            if( results != null )
                allItems.addAll(results);
        }
        if( allItems == null ){
            return null;
        }
        searches.put("absolutelyEverythingFromAllTime", allItems);
        currentSearch = "absolutelyEverythingFromAllTime";
        return allItems;
    }

    /**
     * Request data on Etsy.com and store results in an ArrayList
     * @param searchQuery The search query
     * @param url a formatted Etsy.com url
     * @return An ArrayList with all extracted data
     */
    public ArrayList<Item> search(String searchQuery, String url){
        System.out.println(url);
        currentSearch = searchQuery;
        this.isViewingEmailReport = false;
        ArrayList<Item> results = null;

        //first check if it already is in the history, don't download
        String historyUrl = searchHistory.get(searchQuery);
        if( searchHistory != null &&  historyUrl != null && historyUrl.equals(url) ) {
            //check if it already is stored in the HashMap
            if (searches.containsKey(searchQuery)) {
                results = searches.get(searchQuery);
            }
            //check if it is stored in a local file
            if (results == null && !isGuestUser) {
                results = getResultsFromHistory(searchQuery);
            }
        }

        //request results on Etsy.com
        if(results == null){
            results = searchOnEtsy(searchQuery, url);
        }

        //log activity
        logActivity("search " + currentSearch + " " + results.size() + " items");
        return results;
    }

    /**
     * Log a specific item
     * @param operation either DELETE or INSERT
     * @param item Item to be logged
     * @param searchQuery the search query for this Item
     */
    public void logData(String operation, Item item, String searchQuery){
        if( item == null ){
            return;
        }
        ArrayList<Item> tempList = new ArrayList<>(1);
        tempList.add(item);
        logData(operation, tempList, searchQuery);
    }

    /**
     * Write to log file all item from an ArrayList
     * @param operation either DELETE or INSERT
     * @param items Items to be logged
     * @param searchQuery the search query for these Items
     */
    public void logData(String operation, ArrayList<Item> items, String searchQuery){
        FileOutput log = new FileOutput(FileOutput.DATA_LOG, true);
        for( Item item : items ){
            log.print(operation + "\",\"");
            log.print(searchQuery + "\",\"");
            log.print(this.user.getUserId() + "\",\"");
            log.print(item.getDescription() + "\",\"");
            log.print(item.getCategory() + "\",\"");
            log.print(item.getPrice() + "\",\"");
            log.print(item.getImgScr() + "\",\"");
            log.print(item.getImgId() + "\",\"");
            log.print(item.getTimeStamp() + "\n");
        }
        log.close();
    }

    /**
     * Load items from search history
     * @param searchQuery the search query, which represents the file name in the local dir
     * @return all items as an ArrayList
     */
    public ArrayList<Item> getResultsFromHistory(String searchQuery){
        ArrayList<Item> results = null;
        File localFile = new File(baseDir+searchQuery+".txt");
        if( localFile.exists() ){
            results = new ArrayList<>(64);
            FileInput reader = new FileInput(baseDir+searchQuery+".txt");
            String item = reader.readLine();
            while ( item != null ){
                String [] details = item.split("\",\"");
                String description = details[1];
                String category    = details[2];
                String price       = details[3];
                String timeStamp   = details[4];
                String imgId       = details[5];
                String imgSrc      = details[6];
                Item itemObj = new Item(description, category, price, imgSrc, timeStamp);
                if( imgId.equals("null")){
                    itemObj.setImgId(null);
                }
                else
                    itemObj.setImgId(Integer.parseInt(imgId.trim()));
                results.add(itemObj);
                item = reader.readLine();
            }
            reader.close();
            localFile.delete();
            searches.put(searchQuery, results);
        }

        return results;
    }

    /**
     * Request data on Etsy.com
     * @param searchQuery a search query
     * @param url a formatted Etsy.com url
     * @return An ArrayList that contains all items extracted
     */
    public ArrayList<Item> searchOnEtsy(String searchQuery, String url){
        String localDir = "appdata/cache/results.html";
        //Download the html file
        WebpageReaderWithAgent downLoadHTML = new WebpageReaderWithAgent(url, localDir);
        Boolean downloadSuccess = downLoadHTML.writeToFile();
        if( !downloadSuccess ){
            return null;
        }
        //Parse and extract necessary data //localDir
        HTMLParser parser = new HTMLParser(localDir);
        parser.parse();
        ArrayList<Item> results = parser.getAllItems();
        if( results == null ){
            return null;
        }
        searches.put(searchQuery, results);
        this.searchHistory.put(searchQuery, url);
        return results;
    }

    /**
     * Writes both the searches HashMap and searchHistory HashMap
     * to the currently logged in user' history directory
     * @param append true if data should be appended, false otherwise
     */
    public void storeUserActivity(Boolean append){

        //separate file to save only search keys, not results
        FileOutput writer = new FileOutput(baseDir+userSearchKeyDir);
        for( String searchQuery: searchHistory.keySet() ){
            writer.println(searchQuery + "\",\"" + searchHistory.get(searchQuery));
        }
        writer.close();

        //write every search to a file
        for( String searchQuery: searches.keySet() ){
            writer = new FileOutput(baseDir + searchQuery+".txt", append);
            for( Item item : searches.get(searchQuery) ){
                writer.print(user.getUserId() + "\",\"");
                writer.print(item.getDescription() + "\",\"");
                writer.print(item.getCategory() + "\",\"");
                writer.print(item.getPrice() + "\",\"");
                writer.print(item.getTimeStamp() + "\",\"");
                writer.print(item.getImgId() + "\",\"");
                writer.print(item.getImgScr() + "\n");
            }
            writer.close();

        }
    }

    /**
     * Delete a single item from the searches HashMap
     * @param index The index of the item to be deleted
     * @return true if it was deleted, false otherwise
     */
    public boolean deleteSingleResult(int index){
        if( searches.get(currentSearch) != null ){
            if( index > -1 && index < searches.get(currentSearch).size() ){
                Integer imgId = searches.get(currentSearch).get(index).getImgId();
                Item item = searches.get(currentSearch).remove(index);
                searchHistory.remove(currentSearch);

                //log data
                logData(FileOutput.DELETE, item, currentSearch);
                logActivity("delete item " + currentSearch + " " + item );
                //delete local image if it was downloaded
                File localImg = new File("appdata/images/"+imgId+".jpg");
                if( localImg.exists() ){
                    localImg.delete();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Remove from searches HashMap the current search.
     * the current searchQuery is the key, and the value is removed
     * @return
     */
    public boolean deleteCurrentSearch(){
        if( currentSearch == null ){
            return false;
        }
        //delete all search history
        if( currentSearch.equals("absolutelyEverythingFromAllTime") ){
            Set<String> keys = searches.keySet();
            searches.keySet().removeAll(keys);
            //remove all search history items
            keys = searchHistory.keySet();
            searchHistory.keySet().removeAll(keys);
            return true;
        }
        else if( searchHistory.get(currentSearch) != null ){
            //log data before delete
            logData(FileOutput.DELETE, searches.get(currentSearch), currentSearch);
            logActivity("delete " + currentSearch + " " + searches.get(currentSearch) + " items" );
            //delete from user's profile
            searchHistory.remove(currentSearch);
            searches.remove(currentSearch);
            return true;
        }
        return false;
    }

    /**
     * Read all searchQueries from the user's local history
     */
    public void loadHistory(){
        if( this.isGuestUser ){
            return;
        }
        String userLocalDir = baseDir+userSearchKeyDir;
        File file = new File(userLocalDir);
        if( file.exists() ){
            logActivity("get historyItems");
            FileInput reader = new FileInput(userLocalDir);
            String search = reader.readLine();
            String [] searchSplit;
            while (search != null && !search.isEmpty()) {
                searchSplit = search.split("\",\"");
                searchHistory.put(searchSplit[0], searchSplit[1]);
                search = reader.readLine();
            }
            reader.close();
            file.delete();
        }
    }

    /**
     * Delete the emailList ArrayList
     */
    public void deleteEmailReport(){
        logActivity("delete emailReport " + emailList.size() + " items");
        this.emailList = new ArrayList<>();
    }

    /**
     * Downloads the image for the Item at specified index
     * There is a local file that keeps the last used id for an image.
     * A unique image id is generated to avoid re-downloading images.
     * If an image has already been downloaded, the local image is used instead.
     * The Item class, which represents a single result,  also defines a field for the image id.
     * @param itemIndex the index of the item
     * @return The downloaded image id
     */
    public String downloadItemImg(int itemIndex){

        Integer imgId = null;
        //return "etsy.png";
        if( searches.containsKey(currentSearch) ) {
            ArrayList<Item> list = searches.get(currentSearch);
            Item item = list.get(itemIndex);
            imgId = item.getImgId();
        }
        String imgDir;
        //guest user images are not saved
        if( this.isGuestUser ){
            File guestDir = new File( "appdata/usersdata/1");
            if( !guestDir.exists() ) {
                guestDir.mkdir();
            }
            imgDir = "appdata/usersdata/1/" + imgId +".jpg";

        }
        //save image with unique id
        else {
            imgDir = "appdata/images/" + imgId +".jpg";
        }


        File tempFile = new File(imgDir);
        if( imgId != null && tempFile.exists() ){
            return imgId+".jpg";
        }
        else{
            Integer newImgId = getNextImgId();
            String imgUrl = searches.get(currentSearch).get(itemIndex).getImgScr();
            String saveDir = (isGuestUser ? "appdata/usersdata/1/"+ newImgId + ".jpg" : "appdata/images/" + newImgId + ".jpg");
            Boolean downloaded = DownloadURLImage.downloadImage(imgUrl, saveDir);
            if( downloaded ){
                incrementImgIdByOne(newImgId);
                searches.get(currentSearch).get(itemIndex).setImgId(newImgId);
                logActivity("download img id " + newImgId+".jpg" );
                return newImgId+".jpg";
            }else {
                return null;
            }
        }

    }

    /**
     * Reads from the local file in appdata/usersdata/imgIds.txt
     * the last Id which the new image will be saved as
     * @return the download image id
     */
    private Integer getNextImgId(){
        String localDir = "appdata/usersdata/imgIds.txt";
        FileInput reader = new FileInput(localDir);
        String lastImgId = reader.readLine().trim();
        reader.close();
        try {
            return Integer.parseInt(lastImgId);
        }catch (NumberFormatException e){
            return null;
        }
    }

    /**
     * Increments the last used image id by 1 and writes to file to be used in the next call
     * @param currentId
     */
    private void incrementImgIdByOne(int currentId){
        String localDir = "appdata/usersdata/imgIds.txt";
        FileOutput writer = new FileOutput(localDir, false);
        writer.print(currentId + 1);
        writer.close();
    }

    /**
     * @return set the user as viewingEmailReport and return the emailList Array
     */
    public ArrayList<Item> getEmailList(){
        this.isViewingEmailReport = true;
        //log activity
        logActivity("get emailReport " + emailList.size() + " items");
        return this.emailList;
    }

    /**
     * Add the item at specified index to emailList from searches HashMap
     * @param index The index of the item to be searched in searches.HashMap
     * @return true if it was added to email list, false otherwise
     */
    public boolean addItemToEmailReport(int index){
        if( searches.containsKey(currentSearch)){
            Item item = searches.get(currentSearch).get(index);
            emailList.add(item);
            //log activity
            logActivity("insert emailReport " + item);
            return true;
        }
        return false;
    }

    /**
     * Delete item at specified index
     * @param index the index to be removed
     * @return true if it was deleted, false otherwise
     */
    public boolean deleteItemFromEmailReport(int index){
        if( this.emailList == null ){
            return false;
        }
        else if( index > -1 && index < this.emailList.size() ){
            Item removedItem = this.emailList.remove(index);
            //log activity
            logActivity("delete emailReportItem " + removedItem);
            return true;
        }
        else
            return false;
    }

    /**
     * Add the value from the HashMap with key currentSearch to emailList.
     * @return true if it was added, false otherwise
     */
    public Boolean addCurrentSearchToEmailReport(){
        if( searches.containsKey(currentSearch)){
            ArrayList<Item> results = searches.get(currentSearch);
            emailList.addAll(results);
            logActivity("insert emailReport " + results.size() + " items");
            return true;
        }
        else
            return false;
    }

    /**
     * sort items by price in either ASC or DESC order
     * @param sortOrder ASC for ascending or DESC for descending
     */
    public void sortByPrice(String sortOrder){
        if( searches.containsKey(currentSearch)){
            ArrayList<Item> result = searches.get(currentSearch);
            if( sortOrder.equals("ASC")) {
                Collections.sort(result);
            }
            else if( sortOrder.equals("DESC"))
                Collections.sort(result, Collections.reverseOrder());
        }
        //log activity
        logActivity("sort " + sortOrder);
    }

    /**
     * Sort item by date in ascending order
     */
    public void sortByDateASC(){
        ArrayList<Item> results = searches.get(currentSearch);
        if( results == null ){
            return;
        }
        Collections.sort(results, (item1, item2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(item1.getTimeStamp());
                Timestamp item1Time = new Timestamp(parsedDate.getTime());
                parsedDate = dateFormat.parse(item2.getTimeStamp());
                Timestamp item2Time = new Timestamp(parsedDate.getTime());
                return  item1Time.compareTo(item2Time);
            } catch(Exception e) {
                return 0;
            }
        });
        //log activity
        logActivity("sort ASC");
    }

    /**
     * sort items by date in descending order
     */
    public void sortByDateDESC(){
        ArrayList<Item> results = searches.get(currentSearch);
        if( results == null ){
            return;
        }
        Collections.sort(results, (item1, item2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(item1.getTimeStamp());
                Timestamp item1Time = new Timestamp(parsedDate.getTime());
                parsedDate = dateFormat.parse(item2.getTimeStamp());
                Timestamp item2Time = new Timestamp(parsedDate.getTime());
                return  item2Time.compareTo(item1Time);
            } catch(Exception e) {
                return 0;
            }
        });
        //log activity
        logActivity("sort DESC");
    }

    /**
     * Stringify the user id an the and all searches performed by the currently logged in user
     * @return a string representation of the user id and searches HashMap
     */
    public String toString(){
        String object = "";
        if( searches.isEmpty() ){
            return "";
        }
        for (String key :  searches.keySet() ) {
            for( Item item : searches.get(key) ){
                object += user.getUserId() + "  " + key + " " + item.toString() + "\n";
            }
        }
        return object;
    }

    /**
     * To log all user activity with in the application
     *  - deleting
     *  - sorting
     *  - searching
     *  - image downlands
     * @param data data to be logged
     */
    public void logActivity(String data){
        //log activity
        FileOutput activityLog = new FileOutput(FileOutput.ACTIVITY_LOG, true);
        activityLog.println(data);
        activityLog.close();
    }

}
