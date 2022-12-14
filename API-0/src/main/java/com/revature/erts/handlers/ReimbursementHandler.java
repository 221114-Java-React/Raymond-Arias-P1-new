package com.revature.erts.handlers;


import com.revature.erts.dtos.requests.NewReimbursementRequest;
import com.revature.erts.dtos.responses.Principal;
import com.revature.erts.models.*;
import com.revature.erts.services.TokenService;
import com.revature.erts.services.ReimbursementService;
import com.revature.erts.utils.custom_exceptions.InvalidAuthException;
import com.revature.erts.utils.custom_exceptions.InvalidReimbursementTicketException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/* purpose of this ReimbursementHandler class is to handle http verbs and endpoints */
/* hierarchy dependency injection -> ReimbursementHandler -> ReimbursementService -> ReimbursementDAO */
public class ReimbursementHandler {
    private final ReimbursementService reimbursementService;
    private final TokenService tokenService;
    private final ObjectMapper mapper;
    private final static Logger logger = LoggerFactory.getLogger(Reimbursement.class);

    public ReimbursementHandler() {
        super();
        this.reimbursementService = null;
        this.tokenService = null;
        this.mapper = null;
    }

    public ReimbursementHandler(ReimbursementService reimbursementService, TokenService tokenService,
                                ObjectMapper mapper) {
        this.reimbursementService = reimbursementService;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    public void newReimbursement(Context ctx) throws IOException {
        NewReimbursementRequest req = mapper.readValue(ctx.req.getInputStream(), NewReimbursementRequest.class);

        try {
            logger.info("Attempting to place new Reimbursement ticket...");

            Reimbursement createdReimbursement = null;

            ctx.status(201); // CREATED
            ctx.json(createdReimbursement.toString());
            logger.info("Attempt to place new Reimbursement ticket successful...");
        } catch (InvalidReimbursementTicketException e) {
            ctx.status(403); // FORBIDDEN
            ctx.json(e);
            logger.info("Attempt to place new Reimbursement ticket unsuccessful...");
        }
    }

    public void getAllReimbursements(Context ctx) {
        try {
            String token = ctx.req.getHeader("authorization");
            if (token == null || token.isEmpty()) throw new InvalidAuthException("You are not signed in.");

            Principal principal = tokenService.extractRequesterDetails(token);
            if (principal == null) throw new InvalidAuthException("Invalid token");
            if ((!principal.getRole().equals(UserRole.MANAGER)) && (!principal.getRole().equals(UserRole.ADMIN)))
                throw new InvalidAuthException("You are not authorized to do this");

            List<Reimbursement> reimbursements = reimbursementService.getAllReimbursements();
            ctx.json(reimbursements);
        } catch (InvalidAuthException e) {
            ctx.status(401); // UNAUTHORIZED
            ctx.json(e);
        }
    }

    public void getAllReimbursementsByUserUUID(Context ctx) {
        try {
            String token = ctx.req.getHeader("authorization");
            if (token == null || token.isEmpty()) throw new InvalidAuthException("You are not signed in.");

            Principal principal = tokenService.extractRequesterDetails(token);
            String userUUID = ctx.req.getParameter("user_id");
            if (principal == null) throw new InvalidAuthException("Invalid token.");
            if (!principal.getUserUUID().equals(userUUID)) {
                if (!principal.getRole().equals(UserRole.ADMIN) || !principal.getRole().equals(UserRole.MANAGER))
                    throw new InvalidAuthException("You are not authorized to do this.");
            }
            List<Reimbursement> reimbursements = reimbursementService.getAllReimbursementsByUserUUID(userUUID);
            ctx.json(reimbursements);
        } catch (InvalidAuthException e) {
            ctx.status(401); // UNAUTHORIZED
            ctx.json(e);
        }
    }

    public void getAllReimbursementsByUserUUIDAndStatusUUID(Context ctx) {
        try {
            String token = ctx.req.getHeader("authorization");
            if (token == null || token.isEmpty()) throw new InvalidAuthException("You are not signed in.");

            Principal principal = tokenService.extractRequesterDetails(token);
            String userUUID = ctx.req.getParameter("user_id");
            if (principal == null) throw new InvalidAuthException("Invalid token.");
            if (!principal.getUserUUID().equals(userUUID)) {
                if (!principal.getRole().equals(UserRole.ADMIN) || !principal.getRole().equals(UserRole.MANAGER))
                    throw new InvalidAuthException("You are not authorized to do this.");
            }
            String statusUUID = ctx.req.getParameter("status_id");
            List<Reimbursement> reimbursements =
                    reimbursementService.getAllReimbursementsByUserUUIDAndStatusUUID(userUUID, statusUUID);
            ctx.json(reimbursements);
        } catch (InvalidAuthException e) {
            ctx.status(401); // UNAUTHORIZED
            ctx.json(e);
        }
    }

}

