package my_project.view;

import my_project.control.ProgramController;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
        import java.awt.*;

public class BibliothekPanel extends Container {

    private JPanel headerPanel;
    private JLabel titleLabel;
    private JLabel userLabel;
    private JButton logoutButton;
    private JTabbedPane mainTabbedPanel;
    private JPanel booksPanle;

    private JTextField bookSearchField;
    private JButton suchenBookButton;
    private JButton showAllBooksButton;

    private JButton borrowBookButton;
    private JButton addBookButton;
    private JButton editBookButton;
    private JButton deleteBookButton;

    private JTable myLoansTable;
    private JButton returnBookButton;
    private JButton refreshMyLoansButton;

    private JTextField memberSearchField;
    private JButton searchMemberButton;
    private JButton editMemberButton;
    private JButton deleteMemberButton;
    private JButton changeRoleButton;

    private JButton adminReturnBookButton;
    private JCheckBox openLoansOnlyCheckBox;
    private JButton refreshAllLoansButton;

    private JTable booksTable;
    private JTable memebrsTable;
    private JTable allLoansTable;
    private JPanel mainPanel;


    public BibliothekPanel(ProgramController programController) {

        userLabel.setText(programController.getCurrentUserName());

        boolean admin = programController.istAdmin();

        addBookButton.setVisible(admin);
        editBookButton.setVisible(admin);
        deleteBookButton.setVisible(admin);

        if (!admin) {
            mainTabbedPanel.removeTabAt(3);
            mainTabbedPanel.removeTabAt(2);
        }


        ladeBuecher(programController);


        suchenBookButton.addActionListener(e -> {
            sucheBuecher(programController); });


        showAllBooksButton.addActionListener(e -> {
            ladeBuecher(programController);
        });

        searchMemberButton.addActionListener(e -> { sucheMitglieder(programController); });
    }


    private void ladeBuecher(ProgramController programController) {

        String[][] daten = programController.getBuecherDaten();

        String[] spalten = {
                "ID",
                "Titel",
                "ISBN",
                "Genre",
                "Standort"
        };

        DefaultTableModel model =
                new DefaultTableModel(daten, spalten);

        booksTable.setModel(model);
    }


    private void sucheBuecher(ProgramController programController) {

        String suchtext = bookSearchField.getText();

        String[][] daten =
                programController.getBuecherDaten(suchtext);

        String[] spalten = {
                "ID",
                "Titel",
                "ISBN",
                "Genre",
                "Standort"
        };

        DefaultTableModel model =
                new DefaultTableModel(daten, spalten);

        booksTable.setModel(model);
    }

    private void ladeMitglieder(ProgramController programController) {
        String[][] daten = programController.getMitgliederDaten();
        String[] spalten = {
                "ID", "Vorname", "Nachname", "E-Mail", "Geburtsdatum"
        };
        DefaultTableModel model = new DefaultTableModel(daten, spalten);
        memebrsTable.setModel(model);
    }
    private void sucheMitglieder(ProgramController programController) {
        String suchtext = memberSearchField.getText();
        String[][] daten = programController.getMitgliederDaten(suchtext);
        String[] spalten = {
                "ID", "Vorname", "Nachname", "E-Mail", "Geburtsdatum"
        };
        DefaultTableModel model = new DefaultTableModel(daten, spalten);
        memebrsTable.setModel(model);
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }
}

