package clone.twitter.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryptor {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean isMatch(String password, String passwordHash) {
        return BCrypt.checkpw(password, passwordHash);
    }
}
