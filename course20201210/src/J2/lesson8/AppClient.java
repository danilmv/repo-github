package J2.lesson8;

import javax.swing.*;

public class AppClient {
    public static void main(String[] args) {
//        new Client();
//        new ClientGUI();

        SwingUtilities.invokeLater(ClientGUI::new);
    }
}
