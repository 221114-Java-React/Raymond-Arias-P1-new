package com.revature.erts.handlers;

import com.revature.erts.dtos.requests.NewUserRequest;
import com.revature.erts.dtos.requests.NewLoginRequest;
import com.revature.erts.dtos.responses.Principal;
import com.revature.erts.models.UserRole;
import com.revature.erts.models.User;
import com.revature.erts.services.TokenService;
import com.revature.erts.services.UserService;
import com.revature.erts.utils.custom_exceptions.InvalidAuthException;
import com.revature.erts.utils.custom_exceptions.InvalidUserException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/* purpose of this UserHandler class is to handle http verbs and endpoints */
/* hierarchy dependency injection -> UserHandler -> UserService -> UserDAO */
public class UserHandler {
    private final UserService userService;
    private final TokenService tokenService;
    private final ObjectMapper mapper;
    private final static Logger logger = LoggerFactory.getLogger(User.class);

    public UserHandler(UserService userService, TokenService tokenService, ObjectMapper mapper) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    public void signup(Context ctx) throws IOException {
        NewUserRequest req = mapper.readValue(ctx.req.getInputStream(), NewUserRequest.class);

        try {
            logger.info("Attempting to signup...");

            User createdUser;

            if (userService.isValidUsername(req.getUsername())) {
                if (!userService.isDuplicateUsername(req.getUsername())) {
                    if (userService.isValidPassword(req.getPassword1())) {
                        if (userService.isSamePassword(req.getPassword1(), req.getPassword2())) {
                            createdUser = userService.signup(req);
                        } else throw new InvalidUserException("Passwords do not match.");
                    } else throw new InvalidUserException("Password needs to be minimum 8 characters long, " +
                            "and have at least one number");
                } else throw new InvalidUserException("Username is already taken.");
            } else throw new InvalidUserException("Username needs to be 8 - 20 characters long.");

            ctx.status(201); // CREATED
            ctx.json(createdUser.toString());
            logger.info("Signup attempt successful...");
        } catch (InvalidUserException e) {
            ctx.status(403); // FORBIDDEN
            ctx.json(e);
            logger.info("Signup attempt unsuccessful...");
        }
    }

    public void login(Context ctx) throws IOException {
        NewLoginRequest req = mapper.readValue(ctx.req.getInputStream(), NewLoginRequest.class);

        try {
            logger.info("Attempting to login...");

            Principal principal;

            if (this.userService.isValidUser(req)) {
                principal = this.userService.login(req);
            }
            else throw new InvalidUserException("Bad Username or Password.");

            ctx.status(202); // ACCEPTED
            ctx.json(principal.getUserUUID());
            logger.info("Login attempt successful...");
        } catch (InvalidUserException e) {
            ctx.status(403); // FORBIDDEN
            ctx.json(e);
            logger.info("Login attempt unsuccessful...");
        }
    }

    public void getAllUsers(Context ctx) {
        try {
            String token = ctx.req.getHeader("authorization");
            if (token == null || token.isEmpty()) throw new InvalidAuthException("You are not signed in");

            Principal principal = tokenService.extractRequesterDetails(token);
            if (principal == null) throw new InvalidAuthException("Invalid token");
            if (!principal.getRole().equals(UserRole.ADMIN)) throw new InvalidAuthException("You are not authorized to do this");

            List<User> users = userService.getAllUsers();
            ctx.json(users);
        } catch (InvalidAuthException e) {
            ctx.status(401);
            ctx.json(e);
        }
    }

    public void getAllUsersByUsername(Context ctx) {
        try {
            String token = ctx.req.getHeader("authorization");
            if (token == null || token.isEmpty()) throw new InvalidAuthException("You are not signed in");

            Principal principal = tokenService.extractRequesterDetails(token);
            if (principal == null) throw new InvalidAuthException("Invalid token");
            if (!principal.getRole().equals(UserRole.ADMIN)) throw new InvalidAuthException("You are not authorized to do this");

            String username = ctx.req.getParameter("username");
            List<User> users = userService.getAllUsersByUsername(username);
            ctx.json(users);
        } catch (InvalidAuthException e) {
            ctx.status(401);
            ctx.json(e);
        }
    }
}
