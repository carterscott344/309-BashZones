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

    @Transient
    private final String IMAGE_DIRECTORY = "/home/jsheets1/ZoneZoneImages/";

    @Transient
    private final String PROFILE_DIRECTORY = "/home/jsheets1/ZoneZoneImages/profilePictures/";

    // ✅ Primary Key - Auto Generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountID;

    // ✅ Login Details
    @Column(nullable = false, unique = true)
    private String accountUsername;

    @Column(nullable = false)
    private String accountPassword;

    @Column(nullable = false, unique = true)
    private String accountEmail;

    // ✅ Private Details
    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String userBirthday; // YYYY-MM-DD format only
    private int userAge;

    // ✅ Account Permissions
    private String accountType; // e.g., "Standard", "Admin", "Limited"
    private Boolean isBanned;

    // ✅ Profile Picture Path
    @Column(name = "profile_picture", nullable = true)
    private String profilePicturePath; // Stores the filename, NOT the full path

    // ✅ User Status
    private Boolean isOnline;  // ✅ Tracks if user is online
    private Boolean isPlaying; // ✅ Tracks if user is in a game
    private Boolean isInQueue;   // ✅ Tracks if user is in matchmaking queue

    // ✅ Game Details
    private Integer userLevel = 1;
    private Long currentLevelXP;
    private Integer gemBalance = 0;

    private Integer numDeaths;
    private Integer numKills;
    private Double killDeathRatio;

    // ✅ Social Information
    @ElementCollection
    @CollectionTable(name = "account_friends_list", joinColumns = @JoinColumn(name = "accountid"))
    @Column(name = "friended_user")
    private List<Long> friendsList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "account_blocked_list", joinColumns = @JoinColumn(name = "accountid"))
    @Column(name = "blocked_user")
    private List<Long> blockedList = new ArrayList<>();

    @Transient
    private AccountPlayTime userSessionPlayTime = new AccountPlayTime();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "play_time_id", referencedColumnName = "playTimeID", nullable = false)
    private AccountPlayTime totalUserPlayTime;

    @OneToMany(mappedBy = "belongToAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserItemModel> ownedPlayerItems = new ArrayList<>();

    // ✅ Default Constructor
    public AccountModel() {}

    /** ✅ Ensures default values before saving to database */
    @PrePersist
    protected void onCreate() {
        if (accountType == null || accountType.isEmpty()) {
            accountType = "Standard";
        }
        if (accountUsername == null || accountUsername.isEmpty()) {
            accountUsername = "defaultUsername";
        }
        if (accountPassword == null || accountPassword.isEmpty()) {
            accountPassword = "defaultPassword";
        }
        if (firstName == null || firstName.isEmpty()) {
            firstName = "defaultFirstName";
        }
        if (lastName == null || lastName.isEmpty()) {
            lastName = "defaultLastName";
        }
        if (accountEmail == null || accountEmail.isEmpty()) {
            accountEmail = "defaultEmail@gmail.com";
        }
        if (userBirthday == null || userBirthday.isEmpty()) {
            userBirthday = "2000-01-01";
        }

        // ALWAYS DEFAULT ON CREATION

        profilePicturePath = PROFILE_DIRECTORY + "default";
        userLevel = 1;
        currentLevelXP = 1L;
        gemBalance = 0;
        numDeaths = 0;
        numKills = 0;
        killDeathRatio = 0.0;

        // ✅ Ensure collections are not null\
        friendsList = new ArrayList<>();
        blockedList = new ArrayList<>();
        ownedPlayerItems = new ArrayList<>();
        totalUserPlayTime = new AccountPlayTime();

        // ✅ Ensure status fields are always initialized

        isBanned = false;
        isOnline = false;
        isPlaying = false;
        isInQueue = false;

        // ✅ Set age based on birthday
        setUserAge();
    }

    // ✅ GETTER & SETTER METHODS

    public Long getAccountID() {
        return accountID;
    }
    public void setAccountID(Long accountID) {
        this.accountID = accountID;
    } // SHOULD NEVER ACTUALLY BE USED

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
            if (!online) { // If user goes offline, reset these to false
                this.isPlaying = false;
                this.isInQueue = false;
                this.userSessionPlayTime = new AccountPlayTime();
            }

        }
    }

    public Boolean getIsPlaying() {
        return isPlaying;
    }
    public void setIsPlaying(Boolean playing) {
        if (playing != null) { // Can only play if online
            this.isPlaying = playing;
            if (playing) {
                this.isInQueue = false; // ✅ Ensure user is NOT in queue while playing
            }
        }
    }

    public Boolean getIsInQueue() {
        return isInQueue;
    }
    public void setIsInQueue(Boolean queueStatus) {
        if (queueStatus != null) { // Can only queue if online
            this.isInQueue = queueStatus;
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

    public Integer getUserAge() {
        return userAge;
    }
    private void setUserAge() {
        this.userAge = Period.between(LocalDate.parse(this.userBirthday), LocalDate.now()).getYears();
        if (this.userAge < 18 && this.accountType.compareTo("Admin") != 0) {
            this.accountType = "Limited"; // ✅ Auto-set "Limited" if under 18
        }
        else {
            this.accountType = "Standard";
        }

    }

    public Integer getUserLevel() {
        return userLevel;
    }
    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Long getCurrentLevelXP() {
        return currentLevelXP;
    }
    public void setCurrentLevelXP(Long currentLevelXP) {
        this.currentLevelXP = currentLevelXP;
    }

    public Integer getGemBalance() {
        return gemBalance;
    }
    public void setGemBalance(Integer gemBalance) {
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

    public Integer getNumKills() {
        return numKills;
    }
    public void setNumKills(Integer numKills) {
        this.numKills = numKills;
    }

    public Integer getNumDeaths() {
        return numDeaths;
    }
    public void setNumDeaths(Integer numDeaths) {
        this.numDeaths = numDeaths;
    }

    public Double getKillDeathRatio() {
        return killDeathRatio;
    }
    public void setKillDeathRatio(Double killDeathRatio) {
        this.killDeathRatio = killDeathRatio;
    }
    public void setKillDeathRatio() {
        if(numDeaths == 0) {
            killDeathRatio = 0.0;
        }
        else {
            killDeathRatio = ((double)numKills / (double)numDeaths);
        }
    }

    public List<UserItemModel> getOwnedPlayerItems() {
        return ownedPlayerItems;
    }
    public void setOwnedPlayerItems(List<UserItemModel> ownedPlayerItems) {
        this.ownedPlayerItems = ownedPlayerItems;
    }

    public AccountPlayTime getUserSessionPlayTime() {
        return userSessionPlayTime;
    }
    public void setUserSessionPlayTime(AccountPlayTime userSessionPlayTime) {
        this.userSessionPlayTime = userSessionPlayTime;
    }

    public AccountPlayTime getTotalUserPlayTime() {
        return totalUserPlayTime;
    }
    public void setTotalUserPlayTime(AccountPlayTime totalUserPlayTime) {
        this.totalUserPlayTime = totalUserPlayTime;
    }

    public void addPlayTime(int days, int hours, int minutes, int seconds) {
        if (this.totalUserPlayTime == null) {
            this.totalUserPlayTime = new AccountPlayTime();
        }
        if (this.userSessionPlayTime == null) {
            this.userSessionPlayTime = new AccountPlayTime();
        }

        // ✅ Add playtime to both total and session
        this.totalUserPlayTime.addTime(days, hours, minutes, seconds);
        this.userSessionPlayTime.addTime(days, hours, minutes, seconds);
    }
    public void addPlayTimeSeconds(int seconds) {
        if (this.totalUserPlayTime == null) {
            this.totalUserPlayTime = new AccountPlayTime();
        }
        if (this.userSessionPlayTime == null) {
            this.userSessionPlayTime = new AccountPlayTime();
        }
        System.out.println("Before update - Total Playtime: " + this.totalUserPlayTime);
        System.out.println("Before update - Session Playtime: " + this.userSessionPlayTime);

        // ✅ Add playtime to both total and session
        this.totalUserPlayTime.addSeconds(seconds);
        this.userSessionPlayTime.addSeconds(seconds);

        System.out.println("After update - Total Playtime: " + this.totalUserPlayTime);
        System.out.println("After update - Session Playtime: " + this.userSessionPlayTime);
    }

}