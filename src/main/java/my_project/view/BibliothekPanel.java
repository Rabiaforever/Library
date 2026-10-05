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

        // Nur beim normalen Mitglied Admin-Tabs entfernen
        if (!admin) {
            mainTabbedPanel.removeTabAt(3);
            mainTabbedPanel.removeTabAt(2);
        }

        // Alles, was nur der Admin benutzen darf
        if (admin) {

            ladeAlleAusleihen(programController);
            ladeMitglieder(programController);

            editMemberButton.addActionListener(e -> {

                int zeile = memebrsTable.getSelectedRow();

                if (zeile == -1) {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "Bitte zuerst ein Mitglied auswählen."
                    );
                    return;
                }

                int id = Integer.parseInt(
                        memebrsTable.getValueAt(zeile, 0).toString()
                );

                String alterVorname =
                        wertAusTabelle(memebrsTable, zeile, 1);

                String alterNachname =
                        wertAusTabelle(memebrsTable, zeile, 2);

                String alteEmail =
                        wertAusTabelle(memebrsTable, zeile, 3);

                String altesGeburtsdatum =
                        wertAusTabelle(memebrsTable, zeile, 4);


                String vorname = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "Vorname:",
                        "Mitglied bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        alterVorname
                );

                if (vorname == null) {
                    return;
                }


                String nachname = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "Nachname:",
                        "Mitglied bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        alterNachname
                );

                if (nachname == null) {
                    return;
                }


                String email = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "E-Mail:",
                        "Mitglied bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        alteEmail
                );

                if (email == null) {
                    return;
                }


                String geburtsdatum = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "Geburtsdatum (JJJJ-MM-TT):",
                        "Mitglied bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        altesGeburtsdatum
                );

                if (geburtsdatum == null) {
                    return;
                }

                programController.mitgliedBearbeiten(
                        id,
                        vorname,
                        nachname,
                        email,
                        geburtsdatum
                );

                ladeMitglieder(programController);
            });

            deleteMemberButton.addActionListener(e -> {

                int zeile = memebrsTable.getSelectedRow();

                if (zeile == -1) {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "Bitte zuerst ein Mitglied auswählen."
                    );
                    return;
                }

                int id = Integer.parseInt(
                        memebrsTable.getValueAt(zeile, 0).toString()
                );

                String name =
                        wertAusTabelle(memebrsTable, zeile, 1)
                                + " "
                                + wertAusTabelle(memebrsTable, zeile, 2);

                int antwort = JOptionPane.showConfirmDialog(
                        mainPanel,
                        "Soll \"" + name + "\" wirklich gelöscht werden?",
                        "Mitglied löschen",
                        JOptionPane.YES_NO_OPTION
                );

                if (antwort != JOptionPane.YES_OPTION) {
                    return;
                }

                programController.mitgliedLoeschen(id);

                ladeMitglieder(programController);
            });

            changeRoleButton.addActionListener(e -> {

                int zeile = memebrsTable.getSelectedRow();

                if (zeile == -1) {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "Bitte zuerst ein Mitglied auswählen."
                    );
                    return;
                }

                int id = Integer.parseInt(
                        memebrsTable.getValueAt(zeile, 0).toString()
                );

                String aktuelleRolle =
                        wertAusTabelle(memebrsTable, zeile, 5);

                String[] rollen = {
                        "Mitglied",
                        "Admin"
                };

                Object auswahl = JOptionPane.showInputDialog(
                        mainPanel,
                        "Neue Rolle:",
                        "Rolle ändern",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        rollen,
                        aktuelleRolle
                );

                if (auswahl == null) {
                    return;
                }

                programController.rolleAendern(
                        id,
                        auswahl.toString()
                );

                ladeMitglieder(programController);
            });



            refreshAllLoansButton.addActionListener(e ->
                    ladeAlleAusleihen(programController)
            );

            openLoansOnlyCheckBox.addActionListener(e ->
                    ladeAlleAusleihen(programController)
            );

            adminReturnBookButton.addActionListener(e -> {

                int zeile = allLoansTable.getSelectedRow();

                if (zeile == -1) {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "Bitte zuerst eine Ausleihe auswählen."
                    );
                    return;
                }

                int ausleiheID = Integer.parseInt(
                        allLoansTable.getValueAt(zeile, 0).toString()
                );

                programController.buchZurueckgeben(ausleiheID);

                ladeAlleAusleihen(programController);
                ladeBuecher(programController);
                ladeMeineAusleihen(programController);
            });


            editBookButton.addActionListener(e -> {

                int zeile = booksTable.getSelectedRow();

                if (zeile == -1) {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "Bitte zuerst ein Buch auswählen."
                    );
                    return;
                }

                int id = Integer.parseInt(
                        booksTable.getValueAt(zeile, 0).toString()
                );

                String alterTitel =
                        wertAusTabelle(booksTable, zeile, 1);

                String alteISBN =
                        wertAusTabelle(booksTable, zeile, 2);

                String altesGenre =
                        wertAusTabelle(booksTable, zeile, 3);

                String alterStandort =
                        wertAusTabelle(booksTable, zeile, 4);


                String titel = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "Titel:",
                        "Buch bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        alterTitel
                );

                if (titel == null) {
                    return;
                }

                String isbn = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "ISBN:",
                        "Buch bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        alteISBN
                );

                if (isbn == null) {
                    return;
                }

                String genre = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "Genre:",
                        "Buch bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        altesGenre
                );

                if (genre == null) {
                    return;
                }

                String standort = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "Standort:",
                        "Buch bearbeiten",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        alterStandort
                );

                if (standort == null) {
                    return;
                }

                programController.buchBearbeiten(
                        id,
                        titel,
                        isbn,
                        genre,
                        standort
                );

                ladeBuecher(programController);
            });

            //KI
            addBookButton.addActionListener(e -> {

                String titel = JOptionPane.showInputDialog(
                        mainPanel,
                        "Titel:"
                );

                if (titel == null) {
                    return;
                }

                String isbn = JOptionPane.showInputDialog(
                        mainPanel,
                        "ISBN:"
                );

                if (isbn == null) {
                    return;
                }

                String genre = JOptionPane.showInputDialog(
                        mainPanel,
                        "Genre:"
                );

                if (genre == null) {
                    return;
                }

                String standort = JOptionPane.showInputDialog(
                        mainPanel,
                        "Standort:"
                );

                if (standort == null) {
                    return;
                }

                programController.buchAnlegen(
                        titel,
                        isbn,
                        genre,
                        standort
                );

                ladeBuecher(programController);
            });

            //KI für JOption
            deleteBookButton.addActionListener(e -> {

                int zeile = booksTable.getSelectedRow();

                if (zeile == -1) {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "Bitte zuerst ein Buch auswählen."
                    );
                    return;
                }

                int id = Integer.parseInt(
                        booksTable.getValueAt(zeile, 0).toString()
                );

                String titel =
                        wertAusTabelle(booksTable, zeile, 1);

                int antwort = JOptionPane.showConfirmDialog(
                        mainPanel,
                        "Soll \"" + titel + "\" wirklich gelöscht werden?",
                        "Buch löschen",
                        JOptionPane.YES_NO_OPTION
                );

                if (antwort != JOptionPane.YES_OPTION) {
                    return;
                }

                programController.buchLoeschen(id);

                ladeBuecher(programController);
            });
        }


        // Ab hier wieder Funktionen für ALLE Benutzer

        // Funktionen für ALLE Benutzer

        ladeBuecher(programController);
        ladeMeineAusleihen(programController);


// AUSLEIHEN
        borrowBookButton.addActionListener(e -> {

            int zeile = booksTable.getSelectedRow();

            if (zeile == -1) {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "Bitte zuerst ein Buch auswählen."
                );
                return;
            }

            int buchID = Integer.parseInt(
                    booksTable.getValueAt(zeile, 0).toString()
            );

            programController.buchAusleihen(buchID);

            ladeBuecher(programController);
            ladeMeineAusleihen(programController);

            if (admin) {
                ladeAlleAusleihen(programController);
            }
        });


// ZURÜCKGEBEN
        returnBookButton.addActionListener(e -> {

            int zeile = myLoansTable.getSelectedRow();

            if (zeile == -1) {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "Bitte zuerst eine Ausleihe auswählen."
                );
                return;
            }

            int ausleiheID = Integer.parseInt(
                    myLoansTable.getValueAt(zeile, 0).toString()
            );

            programController.buchZurueckgeben(ausleiheID);

            ladeBuecher(programController);
            ladeMeineAusleihen(programController);

            if (admin) {
                ladeAlleAusleihen(programController);
            }
        });


// AKTUALISIEREN
        refreshMyLoansButton.addActionListener(e -> {

            ladeMeineAusleihen(programController);
            ladeBuecher(programController);

            if (admin) {
                ladeAlleAusleihen(programController);
            }
        });


// LOGOUT
        logoutButton.addActionListener(e -> {

            programController.logout();

            Window fenster =
                    SwingUtilities.getWindowAncestor(mainPanel);

            if (fenster != null) {
                fenster.dispose();
            }

            new Login(programController);
        });


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

    private void ladeMeineAusleihen(ProgramController programController) {

        String[][] daten = programController.getMeineAusleihenDaten();

        String[] spalten = {
                "ID",
                "Buch",
                "Ausgeliehen am",
                "Fällig am",
                "Rückgabe"
        };

        DefaultTableModel model =
                new DefaultTableModel(daten, spalten);

        myLoansTable.setModel(model);
    }

    private void ladeAlleAusleihen(
            ProgramController programController) {

        String[][] daten =
                programController.getAlleAusleihenDaten(
                        openLoansOnlyCheckBox.isSelected()
                );

        String[] spalten = {
                "ID",
                "Vorname",
                "Nachname",
                "Buch",
                "Ausgeliehen am",
                "Fällig am",
                "Rückgabe"
        };

        DefaultTableModel model =
                new DefaultTableModel(daten, spalten);

        allLoansTable.setModel(model);
    }

    private void ladeMitglieder(ProgramController programController) {

        String[][] daten =
                programController.getMitgliederDaten();

        String[] spalten = {
                "ID",
                "Vorname",
                "Nachname",
                "E-Mail",
                "Geburtsdatum",
                "Rolle"
        };

        DefaultTableModel model =
                new DefaultTableModel(daten, spalten);

        memebrsTable.setModel(model);
    }

    private void sucheMitglieder(ProgramController programController) {
        String suchtext = memberSearchField.getText();
        String[][] daten = programController.getMitgliederDaten(suchtext);
        String[] spalten = {
                "ID", "Vorname", "Nachname", "E-Mail", "Geburtsdatum", "Rolle"
        };
        DefaultTableModel model = new DefaultTableModel(daten, spalten);
        memebrsTable.setModel(model);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private String wertAusTabelle(
            JTable tabelle,
            int zeile,
            int spalte) {

        Object wert = tabelle.getValueAt(zeile, spalte);

        if (wert == null) {
            return "";
        }

        return wert.toString();
    }
}