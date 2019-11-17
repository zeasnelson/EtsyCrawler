package manager;

import core.*;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public HashMap<String, String> getSearchHistory() {
        return searchHistory;
    }

    public void setIsGuestUser(Boolean isGuestUser){
        this.isGuestUser = isGuestUser;
    }

    public Boolean isGuestUser(){
        return this.isGuestUser;
    }

    public HashMap<String, ArrayList<Item>> getSearches() {
        return searches;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getCurrentSearch() {
        return currentSearch;
    }

    public Boolean isViewingEmailReport() {
        return isViewingEmailReport;
    }

    public ArrayList<Item> getAllSearches(){
        ArrayList<Item> allItems = new ArrayList<>();
        //remove it becuase the users might have searched for something new
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

    public ArrayList<Item> search(String searchQuery, String url){
        System.out.println(url);
        currentSearch = searchQuery;
        this.isViewingEmailReport = false;
        ArrayList<Item> results = null;

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
        return results;
    }

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

    public ArrayList<Item> searchOnEtsy(String searchQuery, String url){
        String localDir = "appdata/cache/results.html";
        //Download the html file
        WebpageReaderWithAgent downLoadHTML = new WebpageReaderWithAgent(url, localDir);
        Boolean downloadSuccess = downLoadHTML.writeToFile();
        if( !downloadSuccess ){
            return null;
        }
        //Parse and extract necessary data
        HMTLParser parser = new HMTLParser(localDir);
        parser.parse();
        ArrayList<Item> results = parser.getAllItems();
        if( results == null ){
            return null;
        }
        searches.put(searchQuery, results);
        this.searchHistory.put(searchQuery, url);
        return results;
    }

    public void storeUserActivity(){

        //separate file to save only search keys, not results
        FileOutput writer = new FileOutput(baseDir+userSearchKeyDir);
        for( String searchQuery: searchHistory.keySet() ){
            writer.println(searchQuery + "\",\"" + searchHistory.get(searchQuery));
        }
        writer.close();

        //write every search to a file

        for( String searchQuery: searches.keySet() ){
            writer = new FileOutput(baseDir + searchQuery+".txt");
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

    public boolean deleteSingleResult(int index){
        if( searches.get(currentSearch) != null ){
            if( index > -1 && index < searches.get(currentSearch).size() ){
                Integer imgId = searches.get(currentSearch).get(index).getImgId();

                searches.get(currentSearch).remove(index);
                searchHistory.remove(currentSearch);
                File localImg = new File("appdata/images/"+imgId+".jpg");
                if( localImg.exists() ){
                    localImg.delete();
                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteCurrentSearch(){
        if( currentSearch == null ){
            return false;
        }
        if( currentSearch.equals("absolutelyEverythingFromAllTime") ){
            Set<String> keys = searches.keySet();
            searches.keySet().removeAll(keys);
            //remove all search history items
            keys = searchHistory.keySet();
            searchHistory.keySet().removeAll(keys);
            return true;
        }
        else if( searchHistory.get(currentSearch) != null ){
            searchHistory.remove(currentSearch);
            searches.remove(currentSearch);
            return true;
        }
        return false;
    }

    public void loadHistory(){
        if( this.isGuestUser ){
            return;
        }
        String userLocalDir = baseDir+userSearchKeyDir;
        File file = new File(userLocalDir);
        if( file.exists() ){
            FileInput reader = new FileInput(userLocalDir);
            String search = reader.readLine();
            String [] searchSplit;
            while (search != null) {
                searchSplit = search.split("\",\"");
                searchHistory.put(searchSplit[0], searchSplit[1]);
                search = reader.readLine();
            }
            reader.close();
            file.delete();
        }
    }

    public void deleteEmailReport(){
        this.emailList = new ArrayList<>();
    }

    public String downloadItemImg(int itemIndex){

        Integer imgId = null;
        //return "etsy.png";
        if( searches.containsKey(currentSearch) ) {
            ArrayList<Item> list = searches.get(currentSearch);
            Item item = list.get(itemIndex);
            imgId = item.getImgId();
        }
        String imgDir;
        if( this.isGuestUser ){
            File guestDir = new File( "appdata/usersdata/1");
            if( !guestDir.exists() ) {
                guestDir.mkdir();
            }
            imgDir = "appdata/usersdata/1/" + imgId +".jpg";

        }
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
                return newImgId+".jpg";
            }else {
                return null;
            }
        }
    }

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

    private void incrementImgIdByOne(int currentId){
        String localDir = "appdata/usersdata/imgIds.txt";
        FileOutput writer = new FileOutput(localDir, false);
        writer.print(currentId + 1);
        writer.close();
    }

    public ArrayList<Item> getEmailList(){
        this.isViewingEmailReport = true;
        return this.emailList;
    }

    public boolean addItemToEmailReport(int index){
        if( searches.containsKey(currentSearch)){
            Item item = searches.get(currentSearch).get(index);
            emailList.add(item);
            return true;
        }
        return false;
    }

    public boolean deleteItemFromEmailReport(int index){
        if( this.emailList == null ){
            return false;
        }
        else if( index > -1 && index < this.emailList.size() ){
            this.emailList.remove(index);
            return true;
        }
        else
            return false;
    }

    public Boolean addCurrentSearchToEmailReport(){
        if( searches.containsKey(currentSearch)){
            ArrayList<Item> results = searches.get(currentSearch);
            emailList.addAll(results);
            return true;
        }
        else
            return false;
    }

    public void sortByPrice(String sortOrder){
        if( searches.containsKey(currentSearch)){
            ArrayList<Item> result = searches.get(currentSearch);
            if( sortOrder.equals("ASC")) {
                Collections.sort(result);
            }
            else if( sortOrder.equals("DESC"))
                Collections.sort(result, Collections.reverseOrder());
        }
    }

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
    }

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
    }

}
