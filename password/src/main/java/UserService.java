import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {
    @Inject
    private UserPersistence userPersistence;

    public User getUser(final String username) {
        return userPersistence.getUser(username);
    }

    public void saveUser(final User user){
        userPersistence.createUser(user);
    }

    public void changePassword(User user, String password) {
        userPersistence.updateUser(user, password);
    }
}
