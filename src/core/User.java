package core;

public class User {
    String userName;
    String userId;
    String userPass;

    public User(String userId, String userName, String userPass) {
        this.userName = userName;
        this.userId = userId;
        this.userPass = userPass;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", userPass='" + userPass + '\'' +
                '}';
    }

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
