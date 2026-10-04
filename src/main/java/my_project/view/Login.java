package my_project.view;

import my_project.control.ProgramController;

import javax.swing.*;
import java.awt.*;

public class Login {

    private ProgramController programController;

    private JFrame frame;
    private JPanel manager;
    private JPanel signUp;
    private JPanel login;
    private JPanel welcome;

    private JButton loginButton;
    private JButton signUpButton;

    private JTextField name;
    private JPasswordField passwort;
    private JButton LOGINButton;
    private JLabel loginError;

    private JTextField vorname;
    private JTextField email;
    private JTextField geburtsdatum;
    private JPasswordField signUpPasswort;
    private JPasswordField signUpPasswort2;
    private JButton SIGNUPButton;
    private JTextField nachname;
    private JLabel signUpError;

    public Login(ProgramController programController) {
        this.programController = programController;

        frame = new JFrame("Library");
        frame.setContentPane(manager);

        loginButton.addActionListener(e -> showCard("login"));
        signUpButton.addActionListener(e -> showCard("signUp"));
        LOGINButton.addActionListener(e -> login());
        SIGNUPButton.addActionListener(e -> signUp());

        frame.setMinimumSize(new Dimension(400, 400));
        frame.pack();
        frame.setLocationRelativeTo(null);

        showCard("welcome");

        frame.setVisible(true);
    }

    private void showCard(String cardName) {
        CardLayout cl = (CardLayout) manager.getLayout();
        cl.show(manager, cardName);
    }

    private void login() {
        if (name.getText().trim().isEmpty() ||
                passwort.getPassword().length == 0) {
            loginError.setText("Bitte Name und Passwort eingeben");
            return;
        }

        boolean erfolgreich = programController.login(
                name.getText(),
                new String(passwort.getPassword())
        );

        if (!erfolgreich) {
            loginError.setText("Name oder Passwort falsch");
        } else {
            frame.setVisible(false);
            // TODO: Bibliotheksoberfläche öffnen.
        }
    }

    private void signUp() {
        if (vorname.getText().trim().isEmpty() ||
                nachname.getText().trim().isEmpty() ||
                signUpPasswort.getPassword().length == 0 ||
                signUpPasswort2.getPassword().length == 0) {
            signUpError.setText("Bitte Namen und beide Passwörter eingeben");
            return;
        }

        String erstesPasswort = new String(signUpPasswort.getPassword());
        String zweitesPasswort = new String(signUpPasswort2.getPassword());

        if (!erstesPasswort.equals(zweitesPasswort)) {
            signUpError.setText("Passwörter sind nicht identisch");
            return;
        }

        boolean erfolgreich = programController.signUp(
                vorname.getText(),
                nachname.getText(),
                email.getText(),
                geburtsdatum.getText(),
                erstesPasswort
        );

        if (!erfolgreich) {
            signUpError.setText(
                    "Registrierung fehlgeschlagen. Angaben und Konsole prüfen."
            );
        } else {
            frame.setVisible(false);
            // TODO: Bibliotheksoberfläche öffnen.
        }
    }
}