
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final String PEPPER = "FlorianDavid";

    public static String hashPassword(String plainPassword, String salt) {
        String combinedData = plainPassword + salt + PEPPER;
        int logRounds = 10;
        return BCrypt.hashpw(combinedData, BCrypt.gensalt(logRounds));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword, String salt) {
        String combinedData =  plainPassword + salt + PEPPER;
        return BCrypt.checkpw(combinedData, hashedPassword);
    }

    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
