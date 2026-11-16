package edu.ifrs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import edu.ifrs.model.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@PermitAll
public class UserResource {

    private static final String ISSUER = "http://localhost:9081";

    private Map<Long, User> users = new ConcurrentHashMap<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        Long id = idGenerator.getAndIncrement();
        User newUser = new User(id, user.name(), user.email(), user.password());
        users.put(id, newUser);
        return Response.status(Response.Status.CREATED).entity(newUser).build();
    }

    @POST
    @Path("/auth/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(User credentials) {
        for (User user : users.values()) {
            if (user.email().equals(credentials.email()) && user.password().equals(credentials.password())) {
                String token = Jwt.issuer(ISSUER)
                        .upn(user.email())
                        .groups("User")
                        .sign();
                return Response.ok(token).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
