package core;

import manager.UserSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Admin {



    private final String usersLocalDir;

    ArrayList<Item> deletedData;
    ArrayList<Item> userSavedData;

    private ArrayList<User> users;

    public Admin(){
        users = new ArrayList<>(10);
        this.usersLocalDir = "appdata/usersdata/";
        this.deletedData = new ArrayList<Item>();
        this.userSavedData = new ArrayList<>();
    }

    public ArrayList<Item> getDeletedData() {
        return deletedData;
    }

    public ArrayList<User> getActiveUsers(){
        ArrayList<User> users = DBDriver.getAllUsersById(getUsersIds());
        return users;
    }

    public ArrayList<User> getAllRegisteredUsers() {
        return DBDriver.getAllUsers("Users");
    }

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

    public void saveDeletedHistory(){
        saveDeletedHistory(this.deletedData);
    }

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
    }

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
        deletedData = null;
    }

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
            return true;
        }
        return false;
    }

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
        return userSavedData;
    }

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
        return items;
    }

    public void loadUserDeletedDataById(String userId){
        saveDeletedHistory();
        deletedData = new ArrayList<>();
        String regex = "\",\"";
        String tempDir = "appdata/log/tempdatalog.txt";
        FileOutput writer = new FileOutput(tempDir);
        FileInput reader = new FileInput(FileOutput.DATA_LOG);

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
    }

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

    public static void main(String[] args) {
        Admin admin = new Admin();

    }

}
