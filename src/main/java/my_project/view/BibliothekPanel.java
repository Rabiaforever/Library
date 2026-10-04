package my_project.view;

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

    public JPanel getMainPanel() {
        return mainPanel;
    }

}
