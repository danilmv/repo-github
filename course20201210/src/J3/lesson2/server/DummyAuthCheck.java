package J3.lesson2.server;

import java.util.Map;
import java.util.TreeMap;

public class DummyAuthCheck implements AuthorizationCheck {
    Map<String, String> auth = new TreeMap<>();

    public DummyAuthCheck() {
        auth.put("Test", "test");
    }

    @Override
    public String checkAuthorization(String login, String password) {
        String pass = auth.get(login);
        if (pass != null && pass.equals(password))
            return login;

        return null;
    }

    @Override
    public void close() {
    }
}
