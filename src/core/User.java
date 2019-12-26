package core;

public class User {

    /**
     * To store this user's username
     */
    private String userName;

    /**
     * To store this user's id
     */
    private String userId;

    /**
     * To store this user's password
     */
    private String userPass;

    public User(String userId, String userName, String userPass) {
        this.userName = userName;
        this.userId = userId;
        this.userPass = userPass;
    }


    //getter
    public String getUserName() {
        return userName;
    }

    //setter
    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", userPass='" + userPass + '\'' +
                '}';
    }

    /**
     * Method to provide the ability to change the user's password
     * @param newPassword the new password
     * @return 2 if if the user does not exist in the database, 1 if password was changed, -1 otherwise
     */
    public int changePassword(String newPassword){
        User exists = DBDriver.userExists(this.userName, newPassword);
        if( exists != null ){
            return 2;
        }else {
            Boolean changed = DBDriver.updateColByUserId(userId, "password", newPassword);
            if( changed ){
                return 1;
            }else
                return -1;
        }
    }
}
