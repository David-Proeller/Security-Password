import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;

@Path("/api/v1/user")
@Slf4j
public class UserResource {

    @Inject
    private UserService userService;
    @GET
    @Path("/{username}")
    public Response getUser(@PathParam("username") final String username) {
        try{
            final User user = userService.getUser(username);
            if(user != null) {
                return Response.ok(user).build();
            }
            return Response.noContent().build();
        } catch(final Exception ex){
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    @POST
    public Response createUser(final User user) throws URISyntaxException {
        try{
            userService.createUser(user);
            return Response.created(new URI("/api/v1/users/" + user.getUsername())).build();
        }catch (final Exception ex){
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    @PUT
    @Path("/{username}")
    public Response changePassword(@PathParam("username") final String username,final String password) throws URISyntaxException{
        try{
            User user = userService.getUser(username);
            userService.changePassword(user, password);
            return Response.ok().build();
        }catch (final Exception ex){
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
}
