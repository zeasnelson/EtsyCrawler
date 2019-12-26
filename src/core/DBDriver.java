package core;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is the driver to connect to the Derby Embedded database.
 * It is only used to store user's information such as username and password.
 * It is capable of performing all basic operations such as insert, delete, update
 *
 * @Author Nelson Zeas
 * @Date 11/20/2019
 */

public class DBDriver {
    private static final String url = "jdbc:derby:cs370db";//;shutdown=true

    /**
     * Crates a user table
     * Cols:
     *      user_id PK
     *      user_name varchar
     *      password  varchar
     *      deleted   int
     */
//    private static void createTable(){
//        try{
//            Connection conn = DriverManager.getConnection(url);
//            Statement statement = conn.createStatement();
//            String sql = "CREATE TABLE Users (" +
//                "user_id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
//                "user_name varchar(15), " +
//                "password varchar(30)," +
//                    "deleted int default 0)";
//                statement.execute(sql);
//        }catch ( SQLException e ){
//            e.printStackTrace();
//        }
//    }


    /**
     * Update a column by user id.
     * @param userId The user id
     * @param colName The name of the column to be updated
     * @param value The new value that will be put in the specified column
     * @return True if column was updated, false otherwise
     */
    public static Boolean updateColByUserId(String userId, String colName, String value){
        Connection conn     = null;
        PreparedStatement statement = null;
        ResultSet res       = null;
        String query        ="update Users SET "+colName+"=? where user_id=?";
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.prepareStatement(query);
            statement.setString(1, value);
            statement.setString(2, userId);
           int rowsUpdated = statement.executeUpdate();
           if( rowsUpdated == 1 ){
               return true;
           }
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Check if a user exists in the database by user name
     * @param userName the user name to be checked if it exists in the database
     * @return
     */
    public static Boolean userExists(String userName) {
        Connection conn     = null;
        PreparedStatement statement = null;
        ResultSet res       = null;
        String query        = "SELECT user_name FROM Users WHERE user_name=?";
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.prepareStatement(query);
            statement.setString(1, userName);
            res = statement.executeQuery();

            if( res.next() ) {
                if( res.getString("user_name").equals(userName) ){
                    return true;
                }
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * Find all users in Users table by ids
     * @param ids An ArrayList of ids
     * @return An ArrayList of User type
     */
    public static ArrayList<User> getAllUsersById(ArrayList<String> ids){
        if( ids.isEmpty() ){
            return null;
        }

        ArrayList<User> users = new ArrayList<>();
        Connection conn     = null;
        PreparedStatement statement = null;
        ResultSet res       = null;

        String userId = "("+ids.get(0);
        for (int i = 1; i < ids.size(); i++ ) {
            if (i < ids.size())
                userId += ","+ids.get(i);
        }
        userId += ")";

        String query = "SELECT * FROM Users WHERE user_id IN " + userId;
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.prepareStatement(query);
            res = statement.executeQuery();
            User user = null;
            while (res.next() ){
                String id = res.getString("user_id");
                String userName = res.getString("user_name");
                String userPassword = res.getString("password");
                users.add(new User(id, userName, userPassword));
            }
            return users;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Check if user exists by user id and user name
     * @param userName The user name
     * @param password The password
     * @return A User object if it the user was found, null otherwise
     */
    public static User userExists(String userName, String password) {

        Connection conn     = null;
        PreparedStatement statement = null;
        ResultSet res       = null;
        String query        = "SELECT * FROM Users WHERE user_name=? AND password=?";
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, password);
            res = statement.executeQuery();
            if( res.next() ) {
                if( res.getString("user_name").equals(userName)
                        && res.getString("password").equals(password)){
                    String userId = res.getString("user_id");
                    String name = res.getString("user_name");
                    String pass = res.getString("password");
                    return new User(userId, name, pass);
                }
            }
            else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Find all users
     * @param tableName The name of the table to be queried
     * @return An ArrayList containing all users in the table
     */
    public static ArrayList<User> getAllUsers(String tableName){
        ArrayList<User> users = new ArrayList<>();
        Connection conn     = null;
        Statement statement = null;
        ResultSet res       = null;
        String query        = "SELECT * FROM " + tableName + " WHERE user_id <> 1";
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            res = statement.executeQuery(query);
            while (res.next() ){
                String userId = res.getString("user_id");
                String userName = res.getString("user_name");
                String password = res.getString("password");
                users.add(new User(userId, userName, password));
            }

        } catch (SQLException e) {
            return null;
            //e.printStackTrace();
        }
        return users;
    }

    /**
     * Print all rows of a table
     * @param tableName The name of the table to be printed
     */
    public static void printAllTableRows(String tableName){
        Connection conn     = null;
        Statement statement = null;
        ResultSet res       = null;
        String query        = "SELECT * FROM " + tableName ;
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            res = statement.executeQuery(query);

            while (res.next() ){
                System.out.print("User id: " + res.getString("user_id"));
                System.out.print("    Username: " + res.getString("user_name"));
                System.out.print("    pass: " + res.getString("password"));
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete users by user id
     * @param userId The user id to be deleted from the database
     */
    public static void deleteUserByUserId(String userId){
        Connection conn     = null;
        Statement statement = null;
        String query        = "DELETE FROM Users WHERE user_id =" + userId ;
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            statement.execute(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new user to the table
     * @param userName The user's username
     * @param password The user's password
     * @return true if the user was added, false otherwise
     */
    public static Boolean addNewUser(String userName, String password){
        Connection conn     = null;
        PreparedStatement statement = null;
        String query        = "INSERT INTO Users (user_name, password) VALUES ( ?, ? )";
        try {
            conn = DriverManager.getConnection(url);
            statement = conn.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, password);
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
