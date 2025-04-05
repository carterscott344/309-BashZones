package ZoneZone.com.accountHandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "play_times")
public class AccountPlayTime {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playTimeID;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0") // ✅ Default DB values
    private int days;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int hours;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int minutes;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int seconds;

    // ✅ Default Constructor
    public AccountPlayTime() {
        this.days = 0;
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
    }

    // ✅ Ensure values are set before persisting
    @PrePersist
    protected void onCreate() {
        if (this.days < 0) this.days = 0;
        if (this.hours < 0) this.hours = 0;
        if (this.minutes < 0) this.minutes = 0;
        if (this.seconds < 0) this.seconds = 0;
    }

    public void setPlayTimeID(Long playTimeID) {
        this.playTimeID = playTimeID;
    }

    public Long getPlayTimeID() {
        return playTimeID;
    }

    /** ✅ Adds time and normalizes values */
    public void addTime(int days, int hours, int minutes, int seconds) {
        this.seconds += seconds;
        this.minutes += minutes;
        this.hours += hours;
        this.days += days;
        normalizeTime();
    }

    /* ✅ Sets value of time manually */
    public void setTime(int days, int hours, int minutes, int seconds) {
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
        normalizeTime();
    }

    /** ✅ Adds seconds and normalizes */
    public void addSeconds(int seconds) {
        this.seconds += seconds;
        normalizeTime();
    }

    /** ✅ Ensures values roll over properly */
    private void normalizeTime() {
        if (this.seconds >= 60) {
            this.minutes += this.seconds / 60;
            this.seconds %= 60;
        }
        if (this.minutes >= 60) {
            this.hours += this.minutes / 60;
            this.minutes %= 60;
        }
        if (this.hours >= 24) {
            this.days += this.hours / 24;
            this.hours %= 24;
        }
    }

    // ✅ GETTERS & SETTERS
    public int getDays() { return days; }
    public int getHours() { return hours; }
    public int getMinutes() { return minutes; }
    public int getSeconds() { return seconds; }

    // ✅ To String Representation
    @Override
    public String toString() {
        return String.format("%d days, %02d:%02d:%02d", days, hours, minutes, seconds);
    }
}
