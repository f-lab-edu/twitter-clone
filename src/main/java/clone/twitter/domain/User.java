package clone.twitter.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * - auto-generated fields: id, createdAt, updatedAt
 * - reference: ERD_V01.02_C
 */
public class User {
    private Long id;

    private String username;
    private String email;
    private String passwordHash;
    private String profileName;
    private LocalDate birthdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String username,
            String email,
            String passwordHash,
            String profileName,
            LocalDate birthdate) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profileName = profileName;
        this.birthdate = birthdate;
    }

    public Long getId() {return id;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getPasswordHash() {return passwordHash;}
    public String getProfileName() {return profileName;}
    public LocalDate getBirthdate() {return birthdate;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public void setUsername(String username) {this.username = username;}
    public void setEmail(String email) {this.email = email;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}
    public void setProfileName(String profileName) {this.profileName = profileName;}
    public void setBirthdate(LocalDate birthdate) {this.birthdate = birthdate;}
}
