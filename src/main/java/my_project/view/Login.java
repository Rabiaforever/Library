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
    private JTextField passwort;
    private JButton LOGINButton;
    private JLabel loginError;

    private JTextField vorname;
    private JTextField email;
    private JTextField geburtsdatum;
    private JTextField signUpPasswort;
    private JTextField signUpPasswort2;
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
        frame.setVisible(true);

        showCard("welcome");
    }

    private void showCard(String cardName) {
        CardLayout cl = (CardLayout) manager.getLayout();
        cl.show(manager, cardName);
    }

    /**
     * Anmelden.
     */
    private void login(){
        if(name.getText() != null && passwort.getText() != null){
            if(!programController.login(name.getText(), passwort.getText())){
                loginError.setText("Name oder Passwort falsch");
            }else{
                frame.setVisible(false);
                //TODO: Switch to UserInterface
            }
        }else{
            loginError.setText("Bitte Name und Passwort eingeben");
        }
    }

    /**
     * Neuer Account wird erstellt.
     */
    private void signUp(){
        if(vorname.getText() != null && nachname.getText() != null && email.getText() != null && geburtsdatum.getText() != null
           && signUpPasswort.getText() != null && signUpPasswort2.getText() != null){
            if(!signUpPasswort.getText().equals(signUpPasswort2.getText())){
                signUpError.setText("Passwort nicht identisch");
            }else{
              if(!programController.signUp(vorname.getText(), nachname.getText(), email.getText(), geburtsdatum.getText(), signUpPasswort.getText())){
                  signUpError.setText("Anmeldung fehlgeschlagen");
              }else{
                  frame.setVisible(false);
                //TODO: Switch to UserInterface
              }
            }
        }else{
            signUpError.setText("Bitte alles ausfüllen");
        }
    }
}
