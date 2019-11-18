package core;

import java.io.File;
import java.util.ArrayList;

public class Admin {

    private final String usersLocalDir;

    private ArrayList<User> users;

    public Admin(){
        users = new ArrayList<>(10);
        this.usersLocalDir = "appdata/usersdata";
    }

    private ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();


        return users;
    }


    private ArrayList<Integer> getUsersIds(){
        File localDir = new File(this.usersLocalDir);
        int size = localDir.listFiles().length;
        ArrayList<Integer> ids = new ArrayList<>(size);

        if( localDir.exists() ){
            for( File fileName : localDir.listFiles() ){
                if( fileName.getName().trim().matches("[0-9]+?")){
                    String id = fileName.getName().trim();
                    ids.add(Integer.parseInt(id));
                }
            }
        }

        return ids;
    }

    public static void main(String[] args) {
        Admin admin = new Admin();

        admin.getUsersIds();
    }

}
