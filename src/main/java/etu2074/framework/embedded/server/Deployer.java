package etu2074.framework.embedded.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.eclipse.jdt.internal.compiler.batch.Main;

import java.net.URL;

public class Deployer {

  public static void run(Class<?> mainClass) throws IOException, LifecycleException {
    Properties properties = new Properties();
    InputStream inputStream = mainClass.getClassLoader().getResourceAsStream("ever-flow.properties");
    properties.load(inputStream);
    ServerProps serverProps = loadServerProps(properties);
    String warLocation = properties.getProperty("server.warLocation");
    TomcatEmbedded embedded = TomcatEmbedded.getInstance(Integer.parseInt(serverProps.getPort()), serverProps.getContextPath(),warLocation);
    embedded.start();
  }

  public static ServerProps loadServerProps(Properties properties) {
    ServerProps serverProps = new ServerProps();
    String port = properties.getProperty("server.port");
    String contextPath = properties.getProperty("server.contextPath");
    String host = properties.getProperty("server.host");
    serverProps.setPort(port);
    serverProps.setContextPath(contextPath);
    serverProps.setHost(host);
    return serverProps;
  }

  private static void run(ServerProps serverProps) throws LifecycleException {
    String appBase = ".";
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(Integer.parseInt(serverProps.getPort()));
    tomcat.setHostname(serverProps.getDefaultHost());
    tomcat.getHost().setAppBase(appBase);
    tomcat.addWebapp("/"+serverProps.getContextPath(), appBase);

    //annotation scanning
    Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        ctx.addLifecycleListener(new ContextConfig());
        // Add the JAR/folder containing this class to PreResources
        final WebResourceRoot root = new StandardRoot(ctx);
        final URL url = findClassLocation(Main.class);
        root.createWebResourceSet(WebResourceRoot.ResourceSetType.PRE, "/WEB-INF/classes", url, "/");
        ctx.setResources(root);

        //create connection
        tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();
  }

  // Tries to find the URL of the JAR or directory containing {@code clazz}.
  private static URL findClassLocation(Class<?> clazz) {
    return clazz.getProtectionDomain().getCodeSource().getLocation();
  }
}
