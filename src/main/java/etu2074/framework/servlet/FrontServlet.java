package etu2074.framework.servlet;
import etu2074.framework.controller.Model_view;
import etu2074.framework.loader.Loader;
import etu2074.framework.mapping.Mapping;
import etu2074.framework.url.Link;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import testFramework.Employe;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static testFramework.Employe.dynamicCast;

public class FrontServlet extends HttpServlet {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Mapping> mappingUrl;

    public FrontServlet(){

    }
    public HashMap<String, Mapping> getMappingUrl() {
        return mappingUrl;
    }

    public void setMappingUrl(HashMap<String, Mapping> mappingUrl) {
        this.mappingUrl = mappingUrl;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        try{
            super.init(servletConfig);
            String path = getInitParameter("pathos");
            mappingUrl = new HashMap<>();
            retrieveAllMappedMethod(path);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void retrieveAllMappedMethod(String paths) throws URISyntaxException, ClassNotFoundException {
        Set<Class> classSet = null;
        classSet = Loader.findAllClasses(paths);
        for(Class classes:classSet){
            System.out.println(classes.getName());
            Method[]methods = classes.getMethods();
            for (Method method:methods) {
                Link link = method.getAnnotation(Link.class);
              //  System.out.println(method.getName());
                if(link!=null){
                    mappingUrl.put(link.url(),new Mapping(classes.getName(),method,classes));
                }
            }
        }
    }
    private void retrieveAllMappedMethod() throws URISyntaxException, ClassNotFoundException {
        Set<Class> classSet = null;
            classSet = Loader.findAllClasses("etu2074.framework.controller");
        for(Class classes:classSet){
            Method[]methods = classes.getMethods();
            for (Method method:methods) {
                Link link = method.getAnnotation(Link.class);
                if(link!=null){
                    mappingUrl.put(link.url(),new Mapping(classes.getName(),method,classes));
                }
            }
        }
    }
    public final void dispatch(String URL) {
        try {
            getHttpServletRequest().getRequestDispatcher(URL).forward(getHttpServletRequest(),getHttpServletResponse());
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    public final void processRequest(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        setHttpServletRequest(request);setHttpServletResponse(response);
        Vector<String> stringVector = retrieveRequestUrl(request);
        PrintWriter writer = response.getWriter();
        try{
            System.out.println("===>url"+retrieveRequestUrl(request));
            String values = request.getRequestURI();
            writer.println(values);
            writer.println("<br>");
            HashMap<String,Mapping> list = getMappingUrl();
            writer.println(list);
            Model_view modelView = processRequest(request);
            if(modelView!=null){
                if(!modelView.getData().isEmpty()) addDataToRequest(modelView.getData());
                dispatch(modelView.getView());
            }
            redirection(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void addDataToRequest(HashMap<String,Object>data)
    {
        for (Map.Entry<String,Object>value:data.entrySet()){
            getHttpServletRequest().setAttribute(value.getKey(), value.getValue());
        }
    }


    @Deprecated
    private Model_view redirection(HttpServletRequest request) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Vector<String> links = retrieveRequestUrl(request);
        if(!links.isEmpty()){
            Mapping objectMapping = mappingUrl.get(links.get(0));
            if(objectMapping!=null){
                Object temp = objectMapping.getaClass().newInstance();
                Link link = objectMapping.getMethod().getAnnotation(Link.class);
                Model_view model_view= null;
                if(link.method().equals("POST")){
                    model_view= (Model_view) temp.getClass().getMethod(objectMapping.getMethod().getName(),HttpServletRequest.class).invoke(temp,request);
                }else{
                    model_view= (Model_view) temp.getClass().getMethod(objectMapping.getMethod().getName()).invoke(temp);
                }
                model_view.setMethod(link.method());
                return model_view;
            }
        }
        return null;
    }

    /**
     * Returns the mapping object for the specified URL.
     */
    private Mapping getMapping(String url) {
        return mappingUrl.get(url);
    }

    /**
     * Instantiates an object of the specified class.
     */
    private Object instantiate(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    /**
     * Invokes the specified method on the specified object with the specified request.
     * Throws an exception if the Link method does not match the HttpServletRequest method.
     */
    private Model_view invokeMethod(Object object, Method method,HashMap<Method,HashMap<String,Class<?>>>method_parameter, HttpServletRequest request) throws IllegalAccessException, InvocationTargetException {
        Link link = method.getAnnotation(Link.class);
        if (!link.method().equals(request.getMethod())) {
            throw new IllegalStateException("Link method (" + link.method() + ") does not match HttpServletRequest method (" + request.getMethod() + ")");
        }
        Model_view model_view = null;
        Map<String,String[]> request_parameter = request.getParameterMap(); //maka ny Map ny parametre rehetra nalefa tanaty requete
        model_view = requestMethod_matching(object,method,request_parameter,method_parameter);
        model_view.setMethod(link.method());
        return model_view;
    }

    /**
     * Processes the specified request and returns the corresponding model view.
     */
    private Model_view processRequest(HttpServletRequest request) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Vector<String> links = retrieveRequestUrl(request); //maka anle mapping url
        if (!links.isEmpty()) {
            Mapping objectMapping = getMapping(links.get(0)); //maka anle Mapping correspondant amle url nalaina tatsy ambony
            if (objectMapping != null) {
                Object temp = instantiate(objectMapping.getaClass());
                Method method = objectMapping.getMethod();
                return invokeMethod(temp, method,objectMapping.getMethod_parameter(), request);
            }
        }
        return null;
    }

    private Model_view requestMethod_matching(Object object,Method method,Map<String,String[]>request_params,HashMap<Method,HashMap<String,Class<?>>>method_parameter) throws InvocationTargetException, IllegalAccessException {
        if(method_parameter.size()>request_params.size()){
            throw new IllegalStateException("The number of argument between the request and the function does not match");
        } else if (method_parameter.isEmpty() && request_params.isEmpty()) {
            return (Model_view) method.invoke(object);
        }else{
            int numParams = request_params.size();
            Object[] paramValues = new Object[numParams];
            int i = 0;
            for (String params: request_params.keySet()) {
                paramValues[i] = request_params.get(params);
                i++;
            }
            return  (Model_view) method.invoke(object,paramValues);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
        processRequest(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        processRequest(request,response);
    }

    private  Vector<String>retrieveRequestUrl(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String [] linkURI=requestURI.split("/");
        Vector<String> stringVector = new Vector<>();
        for (String t: linkURI) {
            stringVector.add(t);
        }
        stringVector.remove(0);
        stringVector.remove(0);
        return stringVector;
    }


    private  String requestURL(HttpServletRequest request){
        Vector<String> requestURL = retrieveRequestUrl(request);
        String ur = "";
        for (String url:requestURL) {
            ur+=url;
        }
        return ur;
    }

    public static void main(String[] args) {
       //Set<Class>classSet = Loader.findAllClasses("etu2074.framework.controller");
        //System.out.println(classSet);
    }
}