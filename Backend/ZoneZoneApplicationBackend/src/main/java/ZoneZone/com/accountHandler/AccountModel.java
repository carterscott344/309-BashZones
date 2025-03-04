package ZoneZone.com.accountHandler;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import ZoneZone.com.itemsHandler.ServerItemModel;
import ZoneZone.com.itemsHandler.UserItemModel;
import jakarta.persistence.*;
import org.apache.catalina.User;

@Entity
@Table(name = "user_accounts") // Optional: Specify a table name
public class AccountModel {

    // Primary Key - Auto Generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Account Permission Details
    private long accountID; // Used To Recognize Account By Computer
    private String accountType; // If A Player Has Admin Status Or Not
    private Boolean isBanned;

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
    long currentLevelXP;
    private int gemBalance;

    //  Social Information
    @ElementCollection
    @CollectionTable(name = "account_friends_list", joinColumns = @JoinColumn(name = "accountid"))
    @Column(name = "friended_user")
    private List<Long> friendsList;

    @ElementCollection
    @CollectionTable(name = "account_blocked_list", joinColumns = @JoinColumn(name = "accountid"))
    @Column(name = "blocked_user")
    private List<Long> blockedList;

    @Transient // Prevents storing the list in the database
    private List<UserItemModel> playerItems;  // This will hold objects retrieved from UserItemRepository

    // Default Constructor
    public AccountModel() {
        this.friendsList = new ArrayList<>();
        this.blockedList = new ArrayList<>();
        this.playerItems = new ArrayList<>();
    }

    // On Create Method
    @PrePersist
    protected void onCreate() {
        if (accountType == null) {
            accountType = "Standard";
        }
        if (isBanned == null) {
            isBanned = false;
        }
        if (accountUsername == null) {
            accountUsername = "defaultUsername";
        }
        if (accountPassword == null) {
            accountPassword = "defaultPassword";
        }
        if (firstName == null) {
            firstName = "defaultFirstName";
        }
        if (lastName == null) {
            lastName = "defaultLastName";
        }
        if (accountEmail == null) {
            accountEmail = "defaultEmail@gmail.com";
        }
        if (userBirthday == null) {
            userBirthday = "2000-01-01";
        }
        if (friendsList == null) {
            friendsList = new ArrayList<>();
        }
        if (blockedList == null) {
            blockedList = new ArrayList<>();
        }
        if (playerItems == null) {
            playerItems = new ArrayList<>();
        }
        if (userLevel == 0) {
            userLevel = 1;
        }
    }

    // GETTER & SETTER METHODS
    public long getAccountID() {
        return accountID;
    }
    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Boolean getIsBanned() {
        return isBanned;
    }
    public void setIsBanned(Boolean isbanned) {
        this.isBanned = isbanned;
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
        if (userBirthday == null || userBirthday.isEmpty()) {
            System.out.println("❌ Invalid birthday format: " + userBirthday);
            return;
        }

        // ✅ If already in YYYY-MM-DD format, use it directly
        if (userBirthday.matches("\\d{4}-\\d{2}-\\d{2}")) {
            this.userBirthday = userBirthday;
        }
        else {
            // ✅ Convert MM-DD-YYYY to YYYY-MM-DD
            String myYears = userBirthday.substring(6, 10);
            String myMonth = userBirthday.substring(0, 2);
            String myDays = userBirthday.substring(3, 5);
            this.userBirthday = myYears + "-" + myMonth + "-" + myDays;
        }

        // ✅ Automatically update userAge
        this.setUserAge();
        System.out.println("✅ Birthday updated to: " + this.userBirthday + ", Age: " + this.userAge);
    }

    public int getUserAge() {
        return userAge;
    }
    public void setUserAge() {
        LocalDate localDateBirthday = LocalDate.parse(this.userBirthday);
        int myReturnAge = Period.between(localDateBirthday, LocalDate.now()).getYears();
        if(myReturnAge < 18) {
            this.accountType = "Limited";
        }
        this.userAge = Period.between(localDateBirthday, LocalDate.now()).getYears();
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

    public int getGemBalance() {
        return gemBalance;
    }
    public void setGemBalance(int gemBalance) {
        this.gemBalance = gemBalance;
    }

    public List<Long> getFriendsList() {
        return friendsList;
    }
    public void setFriendsList(List<Long> friendsList) {
        this.friendsList = friendsList;
    }

    public List<Long> getBlockedList() {
        return blockedList;
    }
    public void setBlockedList(List<Long> blockedList) {
        this.blockedList = blockedList;
    }

    public List<UserItemModel> getPlayerItems() {
        return playerItems;
    }
    public void setPlayerItems(List<UserItemModel> playerItems) {
        this.playerItems = playerItems;
    }
}
