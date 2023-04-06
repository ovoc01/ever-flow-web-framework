package testFramework;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.annotation.Tables;
import com.ovoc01.dao.java.BddObject;
import etu2074.framework.controller.Model_view;
import etu2074.framework.url.Link;
import jakarta.servlet.http.HttpServletRequest;
import testFramework.connection.Connnection;

import java.util.LinkedList;
@Tables(name = "patient")
public class Client extends BddObject {
    @PrimaryKey(isSerial = false,prefix = "PATIENT",seqComp = "patientSeq")
    String id;
    @Column
    String nom;
    @Column
    String date_naissance;
    @Column
    String entrance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(String date_naissance) {
        this.date_naissance = date_naissance;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public Client(){

    }

    @Link(url="listClient")
    public Model_view clientList() throws Exception {
        Model_view model_view = new Model_view("Hello.jsp");
        LinkedList<Client> listClient = select(Connnection.pgCon());
        model_view.addItem("listClient",listClient);
        return model_view;
    }

    @Link(url="insertClient",method = "POST")
    public Model_view insert(HttpServletRequest request) throws Exception{
        System.out.println(request);
        return new Model_view("Test.jsp");
    }

}
