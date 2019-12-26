package manager;

import core.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to create an admin session
 * Enables the admin to perform different operation such as:
 *      - restoring all deleted data for all users
 *      - restore specific deleted results for specific users
 *      - view all searched data from all users
 *      - display all registered users
 *      - display all users that have search history
 *      - delelete user accounts
 */

public class Admin {
    /**
     * constant to store a user's local directory, where all data is saved
     */
    private final String usersLocalDir;

    /**
     * to store data deleted by all/specific users
     */
    ArrayList<Item> deletedData;
    /**
     * to store user's saved data
     */
    ArrayList<Item> userSavedData;


    /**
     * Initialize
     */
    public Admin(){
        this.usersLocalDir = "appdata/usersdata/";
        this.deletedData = new ArrayList<Item>();
        this.userSavedData = new ArrayList<>();
    }

    /**
     * get deleted data array list
     * @return ArrayList that contains deleted data from users
     */
    public ArrayList<Item> getDeletedData() {
        return deletedData;
    }

    /**
     * Get all users that have search history
     * @return
     */
    public ArrayList<User> getActiveUsers(){
        ArrayList<User> users = DBDriver.getAllUsersById(getUsersIds());
        return users;
    }

    /**
     * Get all registered users from the db
     * @return An ArrayList that contains all registered users if any
     */
    public ArrayList<User> getAllRegisteredUsers() {
        return DBDriver.getAllUsers("Users");
    }

    /**
     * Read from log file all deleted data from all users
     * @return
     */
    public ArrayList<Item> getAllDeletedHistory(){
        saveDeletedHistory();
        deletedData = new ArrayList<>();
        FileOutput writer = new FileOutput("appdata/log/tempdatalog.txt");
        FileInput reader = new FileInput(FileOutput.DATA_LOG);
        File dataLog = new File(FileOutput.DATA_LOG);
        if( dataLog.exists() ){
            String line = reader.readLine();
            while ( line != null ){
                String [] data = line.split("\",\"");
                if( data[0].equals("DELETE") ) {
                    deletedData.add(extractItem(data));
                }
                else{
                    writer.println(line);
                }
                //read the next item
                line = reader.readLine();
            }
        }
        reader.close();
        writer.close();
        dataLog.delete();

        //rename the temporary file to datalog.txt
        File tempDataLog = new File("appdata/log/tempdatalog.txt");
        File newFileName = new File(FileOutput.DATA_LOG);
        tempDataLog.renameTo(newFileName);

        return deletedData;
    }

    /**
     * Ability to add search key to a users search history
     * The adds recoveredData to a users search history,
     * when the admin recovers data, it is not merged the user's other data.
     * Instead it saved under a new search history key "recoveredData"
     * @param userId a search query
     */
    private void addSearchKeyToUser(String userId){

        String parentDir = "appdata/usersdata";
        FileOutput.createDir(parentDir, userId);
        FileOutput.createFile(parentDir+"/"+userId, "searchKey.txt");

        boolean containsSearchQuery = false;
        FileInput reader = new FileInput(parentDir+"/"+userId+"/searchKey.txt");
        String line = reader.readLine();
        while (line != null){
            String [] data = line.split("\",\"");
            if( data[0].equals("recoveredData")){
                containsSearchQuery = true;
                line = null;
            }
            else
                line = reader.readLine();
        }
        reader.close();

        if( !containsSearchQuery ) {
            FileOutput writer = new FileOutput("appdata/usersdata/" + userId + "/searchKey.txt", true);
            writer.appendToFile("recoveredData\",\"nolink");
            writer.close();
        }
    }

    /**
     * Helper method.
     *
     */
    public void saveDeletedHistory(){
        saveDeletedHistory(this.deletedData);
    }

    /**
     * When the admin requests all deleted data from users,
     * this data must be written back to a file if it was not recovered
     * @param list
     */
    private void saveDeletedHistory(ArrayList<Item> list){
        if (list != null ){
            FileOutput writer = new FileOutput("appdata/log/datalog.txt", true);
            for (Item item : list ){
                writer.print("DELETE\",\"");
                writer.print(item.getSearchQuery() + "\",\"");
                writer.print(item.getUserId() + "\",\"");
                writer.print(item.getDescription() + "\",\"");
                writer.print(item.getCategory() + "\",\"");
                writer.print(item.getPrice() + "\",\"");
                writer.print(item.getImgScr() + "\",\"");
                writer.print(item.getImgId() + "\",\"");
                writer.print(item.getTimeStamp() + "\",\"");
                writer.println();
            }
            writer.close();
        }
        logActivity("admin savedDeletedHistory " + list.size() + " items");
    }

    /**
     * Copy data from ArrayList to HashMap
     * Key are user ids and value, search results
     * Facilitates recovering data when all deleted searches are being recovered
     * @param items An ArrayList with items
     * @return new HashMap
     */
    public HashMap<String, ArrayList<Item>> convertToHashMap(ArrayList<Item> items){
        if( items == null ){
            return null;
        }
        HashMap<String, ArrayList<Item>> byUserId = new HashMap<>();
        for( Item item : deletedData ){
            String userId = item.getUserId();
            if( byUserId.containsKey(userId) ){
                byUserId.get(userId).add(item);
            }
            else{
                ArrayList<Item> newList = new ArrayList<>();
                newList.add(item);
                byUserId.put(userId, newList);
            }
        }
        return byUserId;
    }

    /**
     * Enable ability to recover all deleted history by all users
     * Data is saved under "recoveredData" under search history
     */
    public void restoreAllDeletedHistory(){
        if( deletedData ==  null ){
            return;
        }
        String parentDir = "appdata/usersdata";
        HashMap<String, ArrayList<Item>> byUserId = convertToHashMap(deletedData);
        for( String key : byUserId.keySet() ){
            //ensure user's local dir exists before writing file
            FileOutput.createDir(parentDir, key);  //user's main folder, key represents the users id
            FileOutput.createFile(parentDir+"/"+key, "recoveredData.txt");
            FileOutput writer = new FileOutput(parentDir+"/"+key+"/recoveredData.txt");
            for(Item item : byUserId.get(key)){
                writer.print(item.getUserId() + "\",\"");
                writer.print(item.getDescription() + "\",\"");
                writer.print(item.getCategory() + "\",\"");
                writer.print(item.getPrice() + "\",\"");
                writer.print(item.getTimeStamp() + "\",\"");
                writer.print((item.getImgId() == null ? "null" : item.getImgId()) + "\",\"");
                writer.print(item.getImgScr() + "\n");
            }
            writer.close();
            addSearchKeyToUser(key);
        }
        logActivity("admin restore allDeletedData " + " " + deletedData.size() + " items");
        deletedData = null;
    }

    /**
     * Allows  the user to restore a specific selected result from the table
     * @param index The index of the selected item
     * @return true if item was deleted, false otherwise
     */
    public boolean restoreSelectedResult(int index){
        if( deletedData == null ) {
            return false;
        }

        if( index > -1 && index < deletedData.size() ) {
            Item item = deletedData.remove(index);
            File userSearchKeys = new File("appdata/usersdata/" + item.getUserId() + "/searchKey.txt");
            if (!userSearchKeys.exists()) {
                userSearchKeys.mkdir();
            }

            FileOutput writer = new FileOutput("appdata/usersdata/" + item.getUserId() + "/recoveredData.txt", true);
            writer.print(item.getUserId() + "\",\"");
            writer.print(item.getDescription() + "\",\"");
            writer.print(item.getCategory() + "\",\"");
            writer.print(item.getPrice() + "\",\"");
            writer.print(item.getTimeStamp() + "\",\"");
            writer.print(item.getImgId() + "\",\"");
            writer.print(item.getImgScr() + "\n");
            writer.close();
            addSearchKeyToUser(item.getUserId());
            logActivity("admin restore item " + item);
            return true;
        }
        return false;
    }

    /**
     * Allow admin to view all stored data by a specific user
     * @param userId user id
     * @return An ArrayList with all saved data
     */
    public ArrayList<Item> getSelectedUserData(String userId){
        //clear the list if it has data already, may be from different user
        userSavedData = new ArrayList<>();
        if( userId.isEmpty() ){
            return null;
        }
        File userDir = new File("appdata/usersdata/" + userId);
        if( userDir.exists() ){
            for( File file : userDir.listFiles() ){
                if( !file.getName().equals("absolutelyEverythingFromAllTime.txt")
                        && !file.getName().equals("searchKey.txt")
                        && !file.getName().equals("recoveredData.txt") ){
                    userSavedData.addAll(readFileItems(file.getName(), userId));
                }
            }
        }
        logActivity("admin get userSavedData " + userSavedData.size() + " items");
        return userSavedData;
    }

    /**
     * Read saved user data. Search history is stored under a unique directory, which is named after the user's id
     * @param fileName file to be read
     * @param userId The user's id
     * @return An ArrayList with all data
     */
    public ArrayList<Item> readFileItems(String fileName, String userId){
        ArrayList<Item> items = new ArrayList<>();
        File userDir = new File("appdata/usersdata/" + userId + "/" + fileName);

        if( userDir.exists() ){
            FileInput reader = new FileInput("appdata/usersdata/" + userId + "/" + fileName);
            if( reader != null ){
                String line = reader.readLine();
                while (line != null ){
                    String [] data = line.split("\",\"");
                    String description = data[1];
                    String category    = data[2];
                    String price       = data[3];
                    String timeStamp   = data[4];
                    String imgId       = data[5];
                    String imgSrc      = data[6];
                    Item itemObj = new Item(description, category, price, imgSrc, timeStamp);
                    if( imgId.equals("null")){
                        itemObj.setImgId(null);
                    }
                    itemObj.setUserId(data[0]);
                    items.add(itemObj);
                    line = reader.readLine();
                }
                reader.close();
                return items;
            }
        }
        logActivity("admin get userSavedData " + items.size() + " items");
        return items;
    }

    /**
     * Creates an ArrayList with all deleted item by user id
     * @param userId the user's id
     */
    public void loadUserDeletedDataById(String userId){
        //if the deletedData array is not empty, save that first
        saveDeletedHistory();
        deletedData = new ArrayList<>();
        String regex = "\",\"";
        String tempDir = "appdata/log/tempdatalog.txt";
        FileOutput writer = new FileOutput(tempDir);
        FileInput reader = new FileInput(FileOutput.DATA_LOG);

        //read data
        if( reader != null ){
            String line = reader.readLine();
            while( line != null ){
                String [] data = line.split(regex);
                if( data[0].equals("DELETE") && data[2].equals(userId)){
                    deletedData.add(extractItem(data));
                }
                else {
                    writer.println(line);
                }
                line = reader.readLine();
            }
            reader.close();
            writer.close();;
            //rename the temporary file to datalog.txt
            File newFileName = new File(FileOutput.DATA_LOG);
            newFileName.delete();
            File tempDataLog = new File(tempDir);
            tempDataLog.renameTo(newFileName);
        }
        logActivity("admin get deletedItems " + deletedData.size() + " items");
    }

    /**
     * Create and fill an item object
     * @param data An array with item details: searchQuery, userId, desciption, category, price, imgSrc, timeStamp
     * @return an Item object
     */
    private Item extractItem(String [] data){
        if( data.length < 9 )
            return null;

        Item item;

        String searchQuery = data[1];
        String userId      = data[2];
        String description = data[3];
        String category    = data[4];
        String price       = data[5];
        String imgSrc      = data[6];
        String timeStamp   = data[8];

        //create the actual Item object
        item = new Item(description, category, price, imgSrc, timeStamp);
        item.setUserId(userId);
        item.setSearchQuery(searchQuery);

        if (!data[7].equals("null")) {
            item.setImgId(Integer.parseInt(data[7].trim()));
        }
        return item;
    }

    /**
     * Restores one item from an ArrayList if it is not empty
     * Each item in the array list has a user id, and the result being recovered is stored under that user's directory
     * @param userId the user's id
     */
    public void recoverSelectedUserDeletedData(String userId){

        String parentDir = "appdata/usersdata";
        FileOutput.createDir(parentDir, userId);
        FileOutput.createFile(parentDir+"/"+userId, "recoveredData.txt");

        String userDir = parentDir+"/"+userId+"/recoveredData.txt";
        FileOutput writer = new FileOutput(userDir, true);
        if( writer != null ){for( Item item : deletedData){
                writer.print(item.getUserId() + "\",\"");
                writer.print(item.getDescription() + "\",\"");
                writer.print(item.getCategory() + "\",\"");
                writer.print(item.getPrice() + "\",\"");
                writer.print(item.getTimeStamp() + "\",\"");
                writer.print(item.getImgId() + "\",\"");
                writer.print(item.getImgScr() + "\n");
            }
            writer.close();
        }

        addSearchKeyToUser(userId);
        deletedData = null;
    }

    /**
     * When a user searches from on Etsy.com,
     * there is a folder automatically generated and named after the user's id
     * These folder names are scanned as user ids
     * @return An arrayList with all user ids
     */
    private ArrayList<String> getUsersIds(){
        File localDir = new File(this.usersLocalDir);
        int size = localDir.listFiles().length;
        ArrayList<String> ids = new ArrayList<>(size);

        if( localDir.exists() ){
            for( File fileName : localDir.listFiles() ){
                if( fileName.getName().trim().matches("[0-9]+?")){
                    String id = fileName.getName().trim();
                    if( !id.equals("1") )
                        ids.add(id);
                }
            }
        }
        return ids;
    }

    /**
     * To log all user activity with in the application
     *  - restoring data
     *  - deleting user accounts
     *  - viewing user data
     * This logged data is more for humans to read that to be processed by a program
     * @param data data to be logged
     */
    public void logActivity(String data){
        //log activity
        FileOutput activityLog = new FileOutput(FileOutput.ACTIVITY_LOG, true);
        activityLog.println(data);
        activityLog.close();
    }
}
