package J3.lesson2.server;

public interface AuthorizationCheck {
    public String checkAuthorization(String login, String password);
    public void close();
    public default void changeNickname(String login, String nickname){};
}
