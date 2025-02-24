package ZoneZone.com.accountHandler;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "userAccounts") // Optional: Specify a table name
public class AccountModel {

    // Primary Key - Auto Generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Account Permission Details
    private long accountID; // Used To Recognize Account By Computer
    private boolean isAdult; // Determines If A Player Can See Or Use Certain Features
    private boolean isAdmin; // If A Player Has Admin Status Or Not

    // Login Details
    private String accountUsername;
    private String accountPassword;

    // Private Details
    private String firstName;
    private String lastName;
    private String accountEmail;
    private String userBirthday;
    private int userAge;

    // Game Details
    private int userLevel;
    private long currentLevelXP;

    //  Social Information
    @ElementCollection
    @CollectionTable(name = "accountFriendsList", joinColumns = @JoinColumn(name = "accountID"))
    @Column(name = "friendedUser")
    private List<String> friendsList;

    @ElementCollection
    @CollectionTable(name = "accountBlockedList", joinColumns = @JoinColumn(name = "accountID"))
    @Column(name = "blockedUser")
    private List<String> blockedList;

    // Default Constructor
    public AccountModel() {
        friendsList = new ArrayList<>();
        blockedList = new ArrayList<>();
    }

    // GETTER & SETTER METHODS
    public long getAccountID() {
        return accountID;
    }
    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public boolean getIsAdult() {
        return isAdult;
    }
    public void setIsAdult(boolean isAdult) {
        this.isAdult = isAdult;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getAccountUsername() {
        return accountUsername;
    }
    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public String getAccountPassword() {
        return accountPassword;
    }
    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }
    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getUserBirthday() {
        return userBirthday;
    }
    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public int getUserAge() {
        return userAge;
    }
    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getUserLevel() {
        return userLevel;
    }
    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public long getCurrentLevelXP() {
        return currentLevelXP;
    }
    public void setCurrentLevelXP(long currentLevelXP) {
        this.currentLevelXP = currentLevelXP;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }
    public void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
    }

    public List<String> getBlockedList() {
        return blockedList;
    }
    public void setBlockedList(List<String> blockedList) {
        this.blockedList = blockedList;
    }
}
