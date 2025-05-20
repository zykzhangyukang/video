import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author ：zhangyukang
 * @date ：2025/05/20 14:48
 */
public class PasswordTest {

    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encode);
    }
}
