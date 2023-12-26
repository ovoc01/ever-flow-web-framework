package etu2074.framework.embedded.server;

public class ServerProps {
    private String port;
    private String host;
    private String contextPath;

    public ServerProps(String port, String contextPath) {
        setPort(port);
        setContextPath(contextPath);
    }

    public ServerProps() {
    }

    public void setContextPath(String contextPath) {
        if(contextPath==null||contextPath.trim().isEmpty()) throw new IllegalArgumentException("Context path cannot be null or empty");
        this.contextPath = contextPath;
    }

    public void setPort(String port) {
        if(port == null || port.isEmpty())this.port = "8080";
        else this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDefaultHost(){
        return "localhost";
    }


}
