package clone.twitter.dto;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
public class User {
    private String id;

    private String username;

    private String email;

    private String passwordHash;

    private String profileName;

    private LocalDate birthdate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public User(String id, String username, String email, String passwordHash, String profileName, LocalDate birthdate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profileName = profileName;
        this.birthdate = birthdate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getProfileName() {
        return profileName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
