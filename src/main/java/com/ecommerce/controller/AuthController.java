package com.ecommerce.controller;

import com.ecommerce.bo.UserBO;
import com.ecommerce.dto.CreateUserDTO;
import com.ecommerce.dto.LoginDTO;
import com.ecommerce.dto.UserDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    
    @Inject
    UserBO userBO;
    
    @POST
    @Path("/login")
    public Response login(LoginDTO loginDTO) {
        try {
            String token = userBO.authenticate(loginDTO);
            return Response.ok(Map.of("token", token)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    @POST
    @Path("/register")
    public Response register(CreateUserDTO createUserDTO) {
        try {
            UserDTO user = userBO.createUser(createUserDTO);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        }
    }
}