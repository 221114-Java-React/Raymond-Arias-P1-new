package com.revature.erts.services;

import com.revature.erts.daos.UserDAO;
import com.revature.erts.dtos.requests.NewLoginRequest;
import com.revature.erts.dtos.requests.NewUserRequest;
import com.revature.erts.dtos.responses.Principal;
import com.revature.erts.models.UserRole;
import com.revature.erts.models.User;
import com.revature.erts.models.DatatypeCrossRef;
import com.revature.erts.utils.custom_exceptions.InvalidAuthException;
import com.revature.erts.utils.custom_exceptions.InvalidUserException;

import java.util.List;
import java.util.UUID;

/* purpose of UserService is to validate and retrieve data from the DAO (DATA ACCESS OBJECT) */
/* Service class is essentially an api */
public class UserService {
    /* dependency injection = when a class is dependent on another class */
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {

        this.userDAO = userDAO;
    }

    public User signup(NewUserRequest req) {
        User createdUser = new User(UUID.randomUUID().toString(), req.getUsername(), req.getEmail(),
                req.getGivenName(), req.getSurname(), req.getPassword1(), UserRole.EMPLOYEE);
        if (createdUser.getUsername() == null) {
            if (!isValidUsername(req.getUsername()))
                throw new InvalidUserException("Username must be 8 - 20 characters.");
            else if (isDuplicateUsername(req.getUsername())) throw new InvalidUserException("This username is taken.");
            else if (!isSamePassword(req.getPassword1(), req.getPassword2()))
                throw new InvalidUserException("Both passwords must be identical.");
        }
        userDAO.save(createdUser);
        return createdUser;
    }

    public Principal login(NewLoginRequest req) {
        User validUser = userDAO.getUserByUsernameAndPassword(req.getUsername(), req.getPassword());
        if (validUser == null) throw new InvalidAuthException("Invalid username or password.");
        return new Principal(validUser.getId(), validUser.getUsername(),  validUser.getRole(), validUser.getAuthID());
    }

    // Change this later
    public boolean isValidUser(NewLoginRequest req) {
        return true;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public List<User> getAllUsersByUsername(String username) {
        return userDAO.getAllUsersByUsername(username);
    }

    public List<User> getAllUsersByRoleUUID(String roleUUID) {
        return userDAO.getAllUsersByRoleUUID(roleUUID);
    }

    public List<User> getAllUsersByRole(UserRole role) {
        return userDAO.getAllUsersByRoleUUID(DatatypeCrossRef.userRoleEnum2UUID(role));
    }

    public boolean isValidUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    public boolean isDuplicateUsername(String username) {
        List<String> usernames = userDAO.findAllUsernames();
        return usernames.contains(username);
    }

    public boolean isValidPassword(String password) {
        return true; // password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    public boolean isSamePassword(String password1, String password2) {
        return password1.equals(password2);
    }

}

