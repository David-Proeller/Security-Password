import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.common.constraint.Assert;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jdk.jfr.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertThrows;


@QuarkusTest
@Slf4j
public class UserPersistenceTest {
    @Inject
    private UserPersistence userPersistence;

    @Test
    @TestTransaction
    public void testInsertUser() {
        User user = User.builder()
                .username("test@gmail.com")
                .telephoneNumber("+43 557 1234567")
                .password("Hagi11")
                .build();
        User secondUser = User.builder()
                .username("test123@gmail.com")
                .telephoneNumber("+44 783 1234567")
                .password("Hallo123")
                .build();
        userPersistence.createUser(user);
        userPersistence.createUser(secondUser);
        User userFromDB = userPersistence.getUser(user.getUsername());
        Assert.assertNotNull(userFromDB);
        Assert.assertTrue(user.getUsername() == userFromDB.getUsername());
    }

    @Test
    @TestTransaction
    public void testGetUsers() {
        testInsertUser();
        List<User> users = userPersistence.getUsers();
        Assert.assertTrue(users.size() == 2);
    }

    @Test
    @TestTransaction
    public void testUpdateUser() {
        testInsertUser();
        User user = userPersistence.getUser("test@gmail.com");
        userPersistence.updateUser(user, "ChangedPassword");
        User userFromDB = userPersistence.getUser(user.getUsername());
        Assert.assertTrue(userFromDB.getPassword() == user.getPassword());
    }

    @Test
    @TestTransaction
    public void testRegexForValidations_Correct() {
        String telephoneNumber = "+43 557 1234567";
        Pattern pattern = Pattern.compile("^\\+?[0-9\\s\\-().]{8,20}$");
        Matcher matcherTelephoneNumber = pattern.matcher(telephoneNumber);

        String username = "DavidUndFlorian@gmail.com";
        pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcherUserName = pattern.matcher(username);

        String password = "FuerFortnite123";
        pattern = Pattern.compile("^([a-zA-Z]|[0-9]){6,}$");
        Matcher matcherPassword = pattern.matcher(password);

        Assert.assertTrue(matcherTelephoneNumber.matches());
        Assert.assertTrue(matcherUserName.matches());
        Assert.assertTrue(matcherPassword.matches());
    }

    @Test
    @TestTransaction
    public void testRegexForValidations_Incorrect() {
        String telephoneNumber = "+4S 575 4KLI67";
        Pattern pattern = Pattern.compile("^\\+?[0-9\\s\\-().]{8,20}$");
        Matcher matcherTelephoneNumber = pattern.matcher(telephoneNumber);

        String username = "testinggmail.com";
        pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcherUserName = pattern.matcher(username);

        String password = "#BestPassword!";
        pattern = Pattern.compile("^([a-zA-Z]|[0-9]){6,}$");
        Matcher matcherPassword = pattern.matcher(password);

        Assert.assertFalse(matcherTelephoneNumber.matches());
        Assert.assertFalse(matcherUserName.matches());
        Assert.assertFalse(matcherPassword.matches());
    }
}
