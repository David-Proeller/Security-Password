import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
@Slf4j
public class UserPersistence {
    @Inject
    EntityManager entityManager;
    @Transactional
    public void saveUsers(final User user) {
        try{
            Pattern pattern = Pattern.compile("^\\+?[0-9\\s\\-().]{8,20}$");
            Matcher matcherTelephoneNumber = pattern.matcher(user.getTelephoneNumber());
            pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher matcherUserName = pattern.matcher(user.getUsername());
            pattern = Pattern.compile("^([a-zA-Z]|[0-9]){6,}$");
            Matcher matcherPassword = pattern.matcher(user.getPassword());
            if(matcherTelephoneNumber.matches() && matcherUserName.matches() && matcherPassword.matches()){
                String salt = PasswordHasher.generateSalt();
                String hashedPassword = PasswordHasher.hashPassword(user.getPassword(), salt);
                user.setPassword(hashedPassword);
                user.setSalt(salt);
                entityManager.persist(user);
            }
        }catch(Exception ex){
            log.warn(ex.getMessage());
        }
    }

    @Transactional
    public User getUser(final String username) {
        return entityManager.find(User.class, username);
    }

    @Transactional
    public User updateUser(final User user, final String password){
        try{
            Pattern pattern = Pattern.compile("^([a-zA-Z]|[0-9]){6,}$");
            Matcher matcherPassword = pattern.matcher(password);
            if(matcherPassword.matches()){
                user.setPassword(password);
                String hashedPassword = PasswordHasher.hashPassword(user.getPassword(), user.getSalt());
                user.setPassword(hashedPassword);
                final String username = user.getUsername();
                User persistUser = entityManager.find(User.class, username);
                if(persistUser != null){
                    return entityManager.merge(user);
                }
                throw new RuntimeException("User with username" + username + "not found");
            }
        }catch (Exception ex){
            log.warn(ex.getMessage());
        }
        return null;
    }
}
