package ZoneZone.com.accountHandler;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import ZoneZone.com.itemsHandler.UserItemModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts") // ✅ Explicit table name
public class AccountModel {

    private final String IMAGE_DIRECTORY = "/home/jsheets1/ZoneZoneImages/";
    private final String PROFILE_DIRECTORY = "/home/jsheets1/ZoneZoneImages/profilePictures/";

    // ✅ Primary Key - Auto Generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountID;

    // ✅ Account Permissions
    private String accountType; // e.g., "Standard", "Admin", "Limited"
    private Boolean isBanned;

    // ✅ Profile Picture Path
    @Column(name = "profile_picture", nullable = true)
    private String profilePicturePath; // Stores the filename, NOT the full path

    // ✅ User Status
    private Boolean isOnline;  // ✅ Tracks if user is online
    private Boolean isPlaying; // ✅ Tracks if user is in a game
    private Boolean inQueue;   // ✅ Tracks if user is in matchmaking queue

    // ✅ Login Details
    @Column(nullable = false, unique = true)
    private String accountUsername;

    @Column(nullable = false)
    private String accountPassword;

    // ✅ Private Details
    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String accountEmail;

    @Column(nullable = false)
    private String userBirthday; // YYYY-MM-DD format only

    private int userAge;

    // ✅ Game Details
    private int userLevel = 1;
    private long currentLevelXP;
    private int gemBalance = 0;

    // ✅ Social Information
    @ElementCollection
    @CollectionTable(name = "account_friends_list", joinColumns = @JoinColumn(name = "accountid"))
    @Column(name = "friended_user")
    private List<Long> friendsList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "account_blocked_list", joinColumns = @JoinColumn(name = "accountid"))
    @Column(name = "blocked_user")
    private List<Long> blockedList = new ArrayList<>();

    @OneToMany(mappedBy = "belongToAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserItemModel> ownedPlayerItems = new ArrayList<>();

    // ✅ Default Constructor
    public AccountModel() {}

    /** ✅ Ensures default values before saving to database */
    @PrePersist
    protected void onCreate() {
        if (accountType == null || accountType.isEmpty()) accountType = "Standard";
        if (profilePicturePath == null || profilePicturePath.isEmpty()) profilePicturePath = PROFILE_DIRECTORY + "default";
        if (accountUsername == null || accountUsername.isEmpty()) accountUsername = "defaultUsername";
        if (accountPassword == null || accountPassword.isEmpty()) accountPassword = "defaultPassword";
        if (firstName == null || firstName.isEmpty()) firstName = "defaultFirstName";
        if (lastName == null || lastName.isEmpty()) lastName = "defaultLastName";
        if (accountEmail == null || accountEmail.isEmpty()) accountEmail = "defaultEmail@gmail.com";
        if (userBirthday == null || userBirthday.isEmpty()) userBirthday = "2000-01-01";

        // ✅ Ensure collections are not null
        if (friendsList == null) friendsList = new ArrayList<>();
        if (blockedList == null) blockedList = new ArrayList<>();
        if (ownedPlayerItems == null) ownedPlayerItems = new ArrayList<>();

        // ✅ Ensure status fields are always initialized
        if (isOnline == null) isOnline = false;
        if (isPlaying == null) isPlaying = false;
        if (inQueue == null) inQueue = false;

        // ✅ Set age based on birthday
        setUserAge();
    }

    // ✅ GETTER & SETTER METHODS

    public Long getAccountID() {
        return accountID;
    }
    public void setAccountID(Long accountID) {
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
    public void setIsBanned(Boolean banned) {
        this.isBanned = banned;
    }

    public String getProfilePicturePath() {
        return profilePicturePath != null ? profilePicturePath : "default";
    }

    public void setProfilePicturePath(String profilePicturePath) {
        if (profilePicturePath == null || profilePicturePath.isEmpty()) {
            this.profilePicturePath = "default.png"; // ✅ Ensure default is always used
        } else {
            this.profilePicturePath = profilePicturePath;
        }
    }

    public Boolean getIsOnline() {
        return isOnline;
    }
    public void setIsOnline(Boolean online) {
        if (online != null) {
            this.isOnline = online;
        }
    }

    public Boolean getIsPlaying() {
        return isPlaying;
    }
    public void setIsPlaying(Boolean playing) {
        if (playing != null) {
            this.isPlaying = playing;
            if (playing) {
                this.inQueue = false; // ✅ Ensure user is NOT in queue while playing
            }
        }
    }

    public Boolean getIsInQueue() {
        return inQueue;
    }
    public void setIsInQueue(Boolean queueStatus) {
        if (queueStatus != null) {
            this.inQueue = queueStatus;
            if (queueStatus) {
                this.isPlaying = false; // ✅ Ensure user is NOT playing while in queue
            }
        }
    }

    public String getAccountUsername() {
        return accountUsername;
    }
    public void setAccountUsername(String accountUsername) {
        if (accountUsername != null && !accountUsername.trim().isEmpty()) {
            this.accountUsername = accountUsername;
        }
    }

    public String getAccountPassword() {
        return accountPassword;
    }
    public void setAccountPassword(String accountPassword) {
        if (accountPassword != null && !accountPassword.trim().isEmpty()) {
            this.accountPassword = accountPassword;
        }
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName;
        }
    }

    public String getAccountEmail() {
        return accountEmail;
    }
    public void setAccountEmail(String accountEmail) {
        if (accountEmail != null && !accountEmail.trim().isEmpty()) {
            this.accountEmail = accountEmail;
        }
    }

    public String getUserBirthday() {
        return userBirthday;
    }
    public void setUserBirthday(String userBirthday) {
        if (userBirthday == null || userBirthday.isEmpty()) return;

        if (userBirthday.matches("\\d{4}-\\d{2}-\\d{2}")) {
            this.userBirthday = userBirthday;
        }
        else {
            // Convert MM-DD-YYYY to YYYY-MM-DD
            String myYears = userBirthday.substring(6, 10);
            String myMonth = userBirthday.substring(0, 2);
            String myDays = userBirthday.substring(3, 5);
            this.userBirthday = myYears + "-" + myMonth + "-" + myDays;
        }

        // ✅ Automatically update userAge
        setUserAge();
    }

    public int getUserAge() {
        return userAge;
    }
    private void setUserAge() {
        this.userAge = Period.between(LocalDate.parse(this.userBirthday), LocalDate.now()).getYears();
        if (this.userAge < 18) {
            this.accountType = "Limited"; // ✅ Auto-set "Limited" if under 18
        }
        else {
            this.accountType = "Standard";
        }

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
        this.gemBalance = Math.max(0, gemBalance); // ✅ Prevent negative gem balance
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

    public List<UserItemModel> getOwnedPlayerItems() {
        return ownedPlayerItems;
    }

}