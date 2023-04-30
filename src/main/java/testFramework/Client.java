package testFramework;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.annotation.Tables;
import com.ovoc01.dao.java.BddObject;
import etu2074.framework.annotations.RequestParameter;
import etu2074.framework.controller.ModelView;
import etu2074.framework.annotations.Link;
import testFramework.connection.Connnection;

import java.sql.Connection;
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
        super();
    }

    @Link(url="listClient")
    public ModelView clientList() throws Exception {
        ModelView model_view = new ModelView("Hello.jsp");
        LinkedList<Client> listClient = select(Connnection.pgCon());
        model_view.addItem("listClient",listClient);
        return model_view;
    }

    @Link(url="testMethod")
    public ModelView andrana(String bogos, String sipa){
        System.out.println(bogos+" sy "+sipa);
        return new ModelView("Test.jsp");
    }

    @Link(url = "insert-client")
    public ModelView trying() throws Exception {
        Connection c = Connnection.pgCon();
        insert(c);
        c.commit();
        String message = "insert successfully";
        ModelView modelView = new ModelView("Test.jsp");
        modelView.addItem("message",message);
        return modelView;
    }

    @Link(url = "sprint8")
    public ModelView sprint_8(@RequestParameter(name="bonjour") String bonjour){
        System.out.println(bonjour);
        String message = "insert successfully";
        ModelView modelView = new ModelView("Test.jsp");
        modelView.addItem("message",message);
        return modelView;
    }



}
