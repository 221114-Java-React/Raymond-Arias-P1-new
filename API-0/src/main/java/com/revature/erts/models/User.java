package models;

import com.revature.erts.models.UserRole;
import models.DatatypeCrossRef;

public class User {
    private String id;
    private String username;
    private String email;
    private String givenName;
    private String surname;
    private String password;
    private UserRole role;

    public User() {
        super();
    }

    public User(String id, String username, String email, String givenName, String surname, String password,
                UserRole role) {
        this.id        = id;
        this.username  = username;
        this.email     = email;
        this.givenName = givenName;
        this.surname   = surname;
        this.password  = password;
        this.role      = role;
    }

    public User(String id, String username, String email, String givenName, String surname, String password,
                String roleUUID) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.givenName = givenName;
        this.surname = surname;
        this.password = password;
        this.role = DatatypeCrossRef.userRoleUUID2Enum(roleUUID);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() { return this.email; }

    public void setEmail(String email) { this.email = email; }

    public String getGivenName() { return this.givenName; }

    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getSurname() { return this.surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getPassword() { return this.password; }

    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() {
        return this.role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getRoleUUID() { return DatatypeCrossRef.userRoleEnum2UUID(this.role); }

    public void setRoleByUUID(String roleUUID) { this.role = DatatypeCrossRef.userRoleUUID2Enum(roleUUID); }

    public String getRoleText() { DatatypeCrossRef.userRoleEnum2Text(this.role); }

    public void setRoleByText(String roleText) { this.role = DatatypeCrossRef.userRoleText2Enum(roleText); }

    @Override
    public String toString() {
        return "User{" +
                "id='" + this.id + '\'' +
                ", username='" + this.username + '\'' +
                ", email='" + this.email + '\'' +
                ", givenName='" + this.givenName + '\'' +
                ", surname='" + this.surname + '\'' +
                ", roleUUID=" + getRoleUUID() + '\'' +
                ", roleText=" + getRoleText() + '\'' +
                '}';
    }
}