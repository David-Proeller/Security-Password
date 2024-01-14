import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
@Transactional
@Slf4j
public class UserPersistence {
    @Inject
    EntityManager entityManager;
    @Transactional
    public void createUser(final User user) {
        try{
            Pattern pattern = Pattern.compile("^\\+?[0-9\\s\\-().]{8,20}$");
            Matcher matcherTelephoneNumber = pattern.matcher(user.getTelephoneNumber());
            pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher matcherUserName = pattern.matcher(user.getUsername());
            pattern = Pattern.compile("^([a-zA-Z]|[0-9]){6,}$");
            Matcher matcherPassword = pattern.matcher(user.getPassword());
            if(matcherTelephoneNumber.matches() && matcherUserName.matches() && matcherPassword.matches()){
                String salt = generateSalt();
                String hashedPassword = hashPassword(user.getPassword(), salt);
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
    public List<User> getUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Transactional
    public User updateUser(final User user, final String password){
        try{
            Pattern pattern = Pattern.compile("^([a-zA-Z]|[0-9]){6,}$");
            Matcher matcherPassword = pattern.matcher(password);
            if(matcherPassword.matches()){
                user.setPassword(password);
                String hashedPassword = hashPassword(user.getPassword(), user.getSalt());
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

    public static String hashPassword(String plainPassword, String salt) {
        String combinedData = plainPassword + salt + "FlorianDavid";
        int logRounds = 10;
        return BCrypt.hashpw(combinedData, BCrypt.gensalt(logRounds));
    }

    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
