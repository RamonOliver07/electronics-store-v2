package com.ecommerce.controller;

import com.ecommerce.bo.UserBO;
import com.ecommerce.dto.UserDTO;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    
    @Inject
    UserBO userBO;
    
    @GET
    @RolesAllowed({"ADMIN"})
    public List<UserDTO> getAllUsers() {
        return userBO.getAllUsers();
    }
    
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response getUserById(@PathParam("id") Long id) {
        Optional<UserDTO> user = userBO.getUserById(id);
        if (user.isPresent()) {
            return Response.ok(user.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "User not found")).build();
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response deactivateUser(@PathParam("id") Long id) {
        userBO.deactivateUser(id);
        return Response.ok(Map.of("message", "User deactivated successfully")).build();
    }
}