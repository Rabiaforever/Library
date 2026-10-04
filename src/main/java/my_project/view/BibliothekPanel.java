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

        // Prüfen, ob der Benutzer Admin ist
        boolean admin = programController.istAdmin();

        // Diese Buttons sieht nur der Admin
        addBookButton.setVisible(admin);
        editBookButton.setVisible(admin);
        deleteBookButton.setVisible(admin);

        // Normale Mitglieder sollen die beiden Admin-Tabs nicht sehen
        if (!admin) {
            mainTabbedPanel.removeTabAt(3);
            mainTabbedPanel.removeTabAt(2);
        }

        ladeBuecher(programController);
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

        DefaultTableModel model = new DefaultTableModel(daten, spalten);


        booksTable.setModel(model);
    }

    private ProgramController controller;

    public BibliothekPanel(ProgramController controller) {
        this.controller = controller;

        suchenBookButton.addActionListener(e -> {
            String suchtext = bookSearchField.getText();

            controller.buecherSuchen(suchtext);
        });
        searchMemberButton.addActionListener(e -> {
            String suchtext = memberSearchField.getText();

            controller.mitgliederSuchen(suchtext); });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

}
