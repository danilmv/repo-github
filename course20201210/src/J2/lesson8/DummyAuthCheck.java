package J2.lesson8;

import java.util.Map;
import java.util.TreeMap;

public class DummyAuthCheck implements AuthorizationCheck {
    Map<String, String> auth = new TreeMap<>();

    public DummyAuthCheck() {
        auth.put("Test", "test");
    }

    @Override
    public boolean checkAuthorization(String login, String password) {
        String pass = auth.get(login);
        if (pass != null)
            return pass.equals(password);

        return false;
    }
}
