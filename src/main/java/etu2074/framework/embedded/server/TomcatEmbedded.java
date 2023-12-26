package etu2074.framework.embedded.server;

import java.io.File;

import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;

import etu2074.framework.utilities.Utilities;
import jakarta.servlet.ServletException;

public class TomcatEmbedded extends TomcatEmbeddedServletContainer{

    private static TomcatEmbedded embedded;

    public TomcatEmbedded(Tomcat tomcat) {
        super(tomcat);
    }

    public static TomcatEmbedded getInstance(int port,String contextPath, String warLocation) {
		if(embedded==null){
            
			Tomcat tomcat = new Tomcat();
			tomcat.setPort(8989);
			new File(tomcat.getServer().getCatalinaBase(), "webapps").mkdirs();
			tomcat.setBaseDir(Utilities.GET_CURRENT_LOCATION()+"/temp");
			tomcat.getServer().setParentClassLoader(TomcatEmbedded.class.getClassLoader());
			tomcat.addWebapp(contextPath, warLocation);
			embedded = new TomcatEmbedded(tomcat);
		}
		return embedded;
	}
    
}
