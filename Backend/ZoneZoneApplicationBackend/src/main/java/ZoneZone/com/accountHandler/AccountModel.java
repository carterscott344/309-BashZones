package ZoneZone.com.accountHandler;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
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
    private boolean isAdmin; // If A Player Has Admin Status Or Not
    private boolean isBanned;

    // Login Details
    private String accountUsername;
    private String accountPassword;

    // Private Details
    private String firstName;
    private String lastName;
    private String accountEmail;
    private String userBirthday = "2000-01-01";
    private int userAge;

    // Game Details
    private int userLevel;
    long currentLevelXP = 0;
    private int gemBalance;

    //  Social Information
    @ElementCollection
    @CollectionTable(name = "accountFriendsList", joinColumns = @JoinColumn(name = "accountID"))
    @Column(name = "friendedUser")
    private List<String> friendsList;

    @ElementCollection
    @CollectionTable(name = "accountBlockedList", joinColumns = @JoinColumn(name = "accountID"))
    @Column(name = "blockedUser")
    private List<String> blockedList;

    @ElementCollection
    @CollectionTable(name = "itemsList", joinColumns = @JoinColumn(name = "accountID"))
    @Column(name = "itemName")
    private List<String> itemsList;

    // Default Constructor
    public AccountModel() {
        this.friendsList = new ArrayList<>();
        this.blockedList = new ArrayList<>();
        this.itemsList = new ArrayList<>();
        this.setUserAge();
    }

    // GETTER & SETTER METHODS
    public long getAccountID() {
        return accountID;
    }
    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsBanned() {
        return isBanned;
    }
    public void setIsBanned(boolean isbanned) {
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

    public List<String> getItemsList() {
        return itemsList;
    }
    public void setItemsList(List<String> itemsList) {
        this.itemsList = itemsList;
    }
}
