package my_project.control;

public class Mitglied {

    private int id;
    private String vorname;
    private String nachname;
    private String rolle;
    private boolean istAdmin;

    public Mitglied(int id, String vorname, String nachname, String rolle){
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.rolle = rolle;
        if(rolle.equals("Admin")){
            istAdmin = true;
        }else{
            istAdmin = false;
        }
    }

    public int getId(){
        return id;
    }
    public String getVorname(){
        return vorname;
    }
    public String getNachname(){
        return nachname;
    }
    public boolean istAdmin(){
        return istAdmin;
    }
}
