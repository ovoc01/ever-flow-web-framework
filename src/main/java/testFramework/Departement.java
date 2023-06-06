package testFramework;

import com.ovoc01.dao.annotation.Column;
import com.ovoc01.dao.annotation.PrimaryKey;
import com.ovoc01.dao.java.BddObject;
import etu2074.framework.annotations.Link;
import etu2074.framework.controller.ModelView;
import testFramework.connection.Connnection;

import java.sql.Connection;

public class Departement extends BddObject {
    @PrimaryKey(isSerial = true)
    @Column(isNumber = true)
    Integer id;
    @Column
    String nom;
    @Column
    String chef;

    String redirect;

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getChef() {
        return chef;
    }

    public void setChef(String chef) {
        this.chef = chef;
    }

    public Departement() {

    }

    @Link(url= "dept-save")
    public ModelView save() throws Exception {
        Connection c = Connnection.pgCon();
        insert(c);
        c.commit();
        String message = "insert successfully";
        ModelView modelView = new ModelView("Test.jsp");
        modelView.addItem("message",message);
        return modelView;
    }

    @Link(url="testing")
    public ModelView daniel(){
        ModelView modelView = new ModelView();
        System.out.println("fdkjfk");
        modelView.addItem("redirect",getRedirect());
        modelView.setView("Home.jsp");
        return modelView;
    }
}
