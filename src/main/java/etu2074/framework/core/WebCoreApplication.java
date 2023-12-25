package etu2074.framework.core;

import com.google.gson.Gson;

import etu2074.framework.App;
import etu2074.framework.annotations.*;
import etu2074.framework.annotations.apienum.RequestType;
import etu2074.framework.annotations.cors.CrossOrigin;
import etu2074.framework.annotations.mapping.Controller;
import etu2074.framework.error.MissMatchMethodException;
import etu2074.framework.error.UrlNotFoundException;
import etu2074.framework.modelview.ModelView;
import etu2074.framework.loader.Loader;
import etu2074.framework.mapping.Mapping;
import etu2074.framework.redirection.Redirect;
import etu2074.framework.redirection.RedirectAttribute;
import etu2074.framework.res.APIResponse;
import etu2074.framework.res.status.HttpStatus;
import etu2074.framework.upload.FileUpload;
import etu2074.framework.utilities.Utilities;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class WebCoreApplication extends HttpServlet {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Mapping> mappingUrl;
    private HashMap<String, Object> controllerInstance;
    private String sessionId;
    private String profileName;
    private String uploadFolder;
    private String BASE_URL = "htt://";
    private String EXCEPTION_PAGE;
    Gson gson = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(WebCoreApplication.class);

    public WebCoreApplication() {

    }

    public final void setupBaseUrl() {
        BASE_URL = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":"
                + httpServletRequest.getServerPort() + httpServletRequest.getContextPath();
    }

    public void setEXCEPTION_PAGE(String eXCEPTION_PAGE) {
        EXCEPTION_PAGE = eXCEPTION_PAGE;
    }

    public String getEXCEPTION_PAGE() {
        return EXCEPTION_PAGE;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public HashMap<String, Object> getControllerInstance() {
        return controllerInstance;
    }

    public void setControllerInstance(HashMap<String, Object> controllerInstance) {
        this.controllerInstance = controllerInstance;
    }

    public String getUploadFolder() {
        return uploadFolder;
    }

    public void setUploadFolder(String uploadFolder) {
        this.uploadFolder = uploadFolder;
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
        try {

            super.init(servletConfig);
            LOGGER.info("Starting web application initialization");

            String path = getInitParameter("pathos");

            setProfileName(getInitParameter("profile_name"));
            setSessionId(getInitParameter("session_name"));
            setUploadFolder(getInitParameter("upload_folder"));
            setEXCEPTION_PAGE(getInitParameter("exception_page"));

            mappingUrl = new HashMap<>();
            controllerInstance = new HashMap<>();
            retrieveAllMappedMethod(path);

            addSingletonMap(path);
            LOGGER.info("Initialization of the web application finished");
        } catch (Exception e) {
            LOGGER.error("An error occurs", e);
        }
    }

    private void retrieveAllMappedMethod(String paths) throws IOException, ClassNotFoundException {
        ArrayList<Class<?>> classes = null;
        classes = Utilities.findClasses(paths);
        ArrayList<Class<?>> mappedClasses = new ArrayList<>();

        for (Class<?> clazz : classes) {
            Controller controller = clazz.getAnnotation(Controller.class);
            if (controller != null) {
                mappedClasses.add(clazz);
            }
        }
        setupMappingUrl(mappedClasses);
    }

    private void addSingletonMap(String path) throws URISyntaxException, ClassNotFoundException {
        Set<Class> classSet = null;
        classSet = Loader.findAllClasses(path);
        for (Class c : classSet) {
            if (c.isAnnotationPresent(Scope.class)) {
                controllerInstance.put(c.getName(), null);
            }
        }
    }

    private void retrieveAllMappedMethod() throws URISyntaxException, ClassNotFoundException {
        Set<Class> classSet = null;
        classSet = Loader.findAllClasses("etu2074.framework.controller");
        for (Class classes : classSet) {
            Method[] methods = classes.getMethods();
            for (Method method : methods) {
                Link link = method.getAnnotation(Link.class);
                
                if (link != null) {
                    mappingUrl.put(link.url(), new Mapping(classes.getName(), method, classes));
                }
            }
        }
    }

    private void setupMappingUrl(ArrayList<Class<?>> classes) {
        if (classes == null || !classes.isEmpty()) {
            for (Class<?> class1 : classes) {
                Controller controller = class1.getAnnotation(Controller.class);
                Method[] methods = class1.getMethods();
                for (Method method : methods) {
                    Link link = method.getAnnotation(Link.class);
                    if (link != null) {
                        mappingUrl.put(formatUrl(controller.value(), link.url()),
                                new Mapping(class1.getName(), method, class1));
                    }
                }
            }
        }
    }

    private String formatUrl(String controller, String link) {
        if (controller.trim().isEmpty()) {
            return link;
        }
        if (link.trim().isEmpty()) {
            return controller;
        }
        return controller + "/" + link;
    }

    public final void dispatch(String URL) {
        try {
            getServletContext().getRequestDispatcher("/" + URL).forward(getHttpServletRequest(),
                    getHttpServletResponse());
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    public final void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        setHttpServletRequest(request);
        setHttpServletResponse(response);

        requestDetails(request, response);
        if (BASE_URL == null || BASE_URL.isEmpty() || BASE_URL.isBlank()) {
            setupBaseUrl();
            LOGGER.info("baseURL: " + BASE_URL);
        }

        Vector<String> stringVector = retrieveRequestUrl(request);
        request.setAttribute("BASE_URL", BASE_URL);
        PrintWriter writer = response.getWriter();
        Method method = null;
        try {

            

            Object requestResponse = redirection(stringVector, request.getParameterMap(), request);

            // update
            String realUrl = formatUrl(stringVector);
            Mapping mapping = getMappingUrl().get(realUrl);
            method = mapping.getMethod();
            LOGGER.info("\u001B[32m METHOD CALLED:\u001B[0m "+method);

            Class<?> clazz = mapping.getaClass();

            checkRequestMethod(request, method);// verfie si le method est compatible avec le request

            RestApi restApi = method.getAnnotation(RestApi.class);
            RedirectTo redirect = method.getAnnotation(RedirectTo.class);
            CrossOrigin crossOrigin = clazz.getAnnotation(CrossOrigin.class);
            Download download = method.getAnnotation(Download.class);

            if (crossOrigin != null) {
                applyCrossOrigin(crossOrigin, response);
            }
            if (redirect != null) {
                LOGGER.warn("Redirect to :" + redirect.to());
                redirect(redirect.to(), (Redirect) requestResponse, request, response);

            } else if (download != null) {
                LOGGER.warn("download ");

            } else {
                if (restApi != null) {
                    toJson(requestResponse, response);
                } else {

                    dispatch((ModelView) requestResponse, response);
                }

            }
        } catch (Exception e) {
            LOGGER.error("\u001B[31m" + "Error occurs when processing the request" , e);
            displayError(e, method);
        }
    }

    public void requestDetails(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String scheme = request.getScheme();

        LOGGER.info("\u001B[32m {} REQUEST: \u001B[0m {}{}", method, uri,
                queryString != null ? "?" + queryString : "");
        LOGGER.info("\u001B[32m {} SCHEME: \u001B[0m {}", method, scheme);

    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private void displayError(Exception e, Method methodCalled) throws ServletException, IOException {
        RestApi restApi = methodCalled.getAnnotation(RestApi.class);
        if (restApi != null) {
            APIResponse apiResponse = new APIResponse();
            apiResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            apiResponse.addData("error", e.getMessage());
            toJson(apiResponse, getHttpServletResponse());
        } else {
            getHttpServletRequest().setAttribute("exception", e);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(EXCEPTION_PAGE);
            dispatcher.forward(getHttpServletRequest(), getHttpServletResponse());
        }
    }

    public void checkRequestMethod(HttpServletRequest request, Method methodCalled) throws MissMatchMethodException {
        String requestMethod = request.getMethod();
        RequestType requestType = methodCalled.getAnnotation(RestApi.class).method();
        if (requestType == null)
            return;
        if (requestMethod.toUpperCase().equals(requestType.getMethod()))
            return;
        throw new MissMatchMethodException(
                "The request method:" + requestMethod + " does not match on the endpoint request type "
                        + requestType.getMethod() + "");
    }

    public void applyCrossOrigin(CrossOrigin crossOrigin, HttpServletResponse response) {
        // Set allowed origins

        if (crossOrigin.origins().length > 0) {
            String allowedOrigins = String.join(", ", crossOrigin.origins());

            response.addHeader("Access-Control-Allow-Origin", allowedOrigins);
        }

        // Set allowed headers
        if (crossOrigin.allowedHeaders().length > 0) {
            String allowedHeaders = String.join(", ", crossOrigin.allowedHeaders());
            response.addHeader("Access-Control-Allow-Headers", allowedHeaders);
        }

        // Set exposed headers
        if (crossOrigin.exposedHeaders().length > 0) {
            String exposedHeaders = String.join(", ", crossOrigin.exposedHeaders());
            response.addHeader("Access-Control-Expose-Headers", exposedHeaders);
        }

        // Set allowed methods
        if (crossOrigin.methods().length > 0) {
            String allowedMethods = String.join(", ", crossOrigin.methods());
            response.addHeader("Access-Control-Allow-Methods", allowedMethods);
        }

        // Set allow credentials
        if (crossOrigin.allowCredentials()) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }

        // Set max age
        if (crossOrigin.maxAge() > -1) {
            response.addHeader("Access-Control-Max-Age", String.valueOf(crossOrigin.maxAge()));
        }
    }

    private void redirect(String to, Redirect redirectAttribute, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/" + to);
        if (redirectAttribute != null)
            request.setAttribute("redirect", redirectAttribute);
        requestDispatcher.forward(request, response);
    }

    private void dispatch(ModelView modelView, HttpServletResponse response) throws IOException {

        if (!modelView.isJson())
            dispatchNormal(modelView);
        else
            dispatchRestAPI(modelView, response);
    }

    private void toJson(Object object, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String jSon = null;
        PrintWriter writer = response.getWriter();
        if (object instanceof APIResponse) {
            APIResponse apiResponse = (APIResponse) object;
            jSon = gson.toJson(apiResponse.getData());
            response.setStatus(apiResponse.getStatus().getCode());

        } else {
            jSon = gson.toJson(object);
        }
        LOGGER.debug(jSon);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        writer.println(jSon);
        writer.flush();
    }

    private void dispatchRestAPI(ModelView modelView, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(modelView.getData());
        LOGGER.debug(json);
        response.getWriter().println(json);
    }

    private void dispatchNormal(ModelView modelView) {
        if (modelView.getData().size() > 0)
            addDataToRequest(modelView.getData()); // manampy attribut ao anatin'ilay httpServletRequest avy any @ ilay
                                                   // data ao anaty modelView
        if (modelView.getSessions().size() > 0)
            addDataToSession(modelView.getSessions()); // manampy session ao anatin'ilay HttpSession avy any @ ilay
                                                       // session anaty modelView
        if (modelView.getRemoveSession().size() > 0)
            removeSpecificSession((modelView.getRemoveSession())); // manala session iray na maro maro precis ao
                                                                   // anatin'ilay HttpSession
        if (modelView.isInvalidateSession())
            destroySession(); // mamono ny session rehetra
        dispatch(modelView.getView());
    }

    private void removeSpecificSession(LinkedList<String> removeSession) {
        for (String attName : removeSession) {
            httpServletRequest.getSession().removeAttribute(attName);
        }
    }

    private void destroySession() {
        int i = 0;
        httpServletRequest.getSession().invalidate();
    }

    private void addDataToSession(HashMap<String, Object> sessions) {
        HttpSession httpSession = getHttpServletRequest().getSession();
        for (Map.Entry<String, Object> value : sessions.entrySet()) {
            System.out.println(value.getKey() + " " + value.getValue());
            httpSession.setAttribute(value.getKey(), value.getValue());
        }
    }

    public boolean hasParts(HttpServletRequest request) {
        // Check if the request is multipart
        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith("multipart/")) {
            // Check if the request has any parts
            try {
                Collection<Part> parts = request.getParts();
                return !parts.isEmpty();
            } catch (Exception e) {
                // Handle any exceptions that may occur while retrieving parts
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void download(String imagePath, HttpServletResponse response) {
        try {
            Path sourcePath = Paths.get(imagePath);
            byte[] fileBytes = Files.readAllBytes(sourcePath);

            // Set the response content type and headers for downloading the file
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + sourcePath.getFileName() + "\"");

            // Write the file's content to the response output stream
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(fileBytes);
            }

            System.out.println("File downloaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to download the file.");
        }
    }

    private Object redirection(Vector<String> links, Map<String, String[]> requestParameter, HttpServletRequest request)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException,
            ServletException, IOException, InstantiationException, UrlNotFoundException {

        Collection<Part> parts = null;
        if (hasParts(request)) {
            parts = request.getParts();
        }

        if (!links.isEmpty()) {
            String realUrl = formatUrl(links);
            Mapping objectMapping = mappingUrl.get(realUrl);

           

            if (objectMapping != null) {
                Object temp = instanceToCall(objectMapping);
                instantiateObjectParameter(requestParameter, temp, parts);
                Method calledMethod = objectMapping.getMethod();
                RedirectAttribute redirectAttribute = checkIfRedirectionAttributeIsNeeded(calledMethod, request);

               
                return invokeMethod(calledMethod, temp, requestParameter, request.getSession(), redirectAttribute);
            } else {
                throw new UrlNotFoundException("Url not supported");
            }

        }
        return null;
    }

    private Object instanceToCall(Mapping mapping) throws IllegalAccessException, InstantiationException {
        Object object = mapping.getaClass().newInstance();
        if (mapping.getaClass().isAnnotationPresent(Scope.class)) {
            Object temp = controllerInstance.get(mapping.getaClass().getName());
            if (temp == null) {
                object = mapping.getaClass().newInstance();
                controllerInstance.replace(mapping.getaClass().getName(), object);
                return object;
            }
            // Utilities.resetObjectParameter(temp);
            return temp;
        }
        return object;
    }

    private Object invokeMethod(Method method, Object object, Map<String, String[]> requestParameter,
            HttpSession session, RedirectAttribute redirectAttribute)
            throws InvocationTargetException, IllegalAccessException {
        Vector<Parameter> parameters = methodParameters(method);// maka ny parametre rehetra anle fonction
        methodAuthentification(method, session); // mi-verifier hoe mety ilay authentification
        checkIfSessionIsNeeded(object, method, session);// mi-verifier hoe mila session ve ilay fonction
        LOGGER.info("redirect attribute : " + redirectAttribute);
        if (!parameters.isEmpty()) {
            Class<?>[] parameterClasses = parametersClass(parameters);// maka ny class anle parametre

            Object[] parameterValue = requestParamAttr(parameters, requestParameter, redirectAttribute);
            LOGGER.info("Paramter value "+ parameterValue[0]);
            return method.invoke(object, dynamicCast(parameterClasses, parameterValue));//
        } else {
            return method.invoke(object);
        }
    }

    private RedirectAttribute checkIfRedirectionAttributeIsNeeded(Method method, HttpServletRequest request) {
        Parameter[] parameters = method.getParameters();
        for (Parameter p : parameters) {
            if (p.getType() == RedirectAttribute.class) {
                Redirect redirect = (Redirect) request.getAttribute("redirect");
                if (redirect != null) {
                    return redirect.getRedirectAttribute();
                }
            }
        }
        return null;
    }

    private void checkIfSessionIsNeeded(Object object, Method method, HttpSession httpSession)
            throws IllegalAccessException {
        if (!method.isAnnotationPresent(Session.class))
            return;
        setSessionInObject(object, httpSession);
    }

    private void setSessionInObject(Object object, HttpSession httpSession)
            throws RuntimeException, IllegalAccessException {
        Field session = getSessionFieldInObject(object);
        if (session == null)
            throw new RuntimeException(
                    "To access Session with @Session annotation you should add a field  HashMap<String,Object> called session");
        sessionSessionInObject(object, session, httpSession);
    }

    private final Field getSessionFieldInObject(Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> field.getName().equals("session"))
                .findFirst().orElse(null);
    }

    private void sessionSessionInObject(Object object, Field field, HttpSession httpSession)
            throws IllegalAccessException {
        Enumeration<String> stringEnumeration = httpSession.getAttributeNames();
        HashMap<String, Object> sessionObject = new HashMap<>();
        while (stringEnumeration.hasMoreElements()) {
            String key = stringEnumeration.nextElement();
            sessionObject.put(key, httpSession.getAttribute(key));
        }
        field.setAccessible(true);
        field.set(object, sessionObject);
    }

    private void methodAuthentification(Method method, HttpSession session) throws RuntimeException {
        Authentification authentification = method.getAnnotation(Authentification.class);
        if (authentification == null)
            return;
        if (session.getAttribute(getProfileName()) == null
                || !session.getAttribute(getProfileName()).equals(authentification.auth()))
            throw new RuntimeException("Authentification failed");
    }

    private void checkSessionPresence(HttpSession session) {

    }

    private String listProfileNeeded(Authentification authentification) {
        return authentification.auth();
    }

    private void instantiateObjectParameter(Map<String, String[]> requestParameter, Object object,
            Collection<Part> partCollection) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, ServletException, IOException {
        Field[] fields = object.getClass().getDeclaredFields();// maka ny attribut rehetra declarer ao @ ilay objet
        Method[] methods = object.getClass().getDeclaredMethods();

        for (Field field : fields) {
            String[] parameter = requestParameter.get(field.getName());
            if (parameter != null && field.getType() != FileUpload.class) {
                String setter = Utilities.createSetter(field.getName());
                Method method_setter = stringMatchingMethod(methods, setter);
                Class<?>[] method_parameter = arrayMethodParameter(method_setter);
                method_setter.invoke(object, dynamicCast(method_parameter, parameter));
            } else if (partCollection != null && field.getType() == FileUpload.class) {
                Part part = getMatchingPart(field.getName(), partCollection);

                FileUpload fileUpload = instantiateFileUpload(part);
                String setter = Utilities.createSetter(field.getName());
                Method method_setter = stringMatchingMethod(methods, setter);
                assert method_setter != null;
                method_setter.invoke(object, fileUpload);
            }
        }
    }

    private Part getMatchingPart(String partName, Collection<Part> partCollection) {
        return partCollection.stream().filter(part -> part.getName().equals(partName)).findFirst().orElse(null);
    }

    private FileUpload instantiateFileUpload(Part part) throws IOException {
        return new FileUpload(part.getSubmittedFileName(), part.getInputStream().readAllBytes(), getUploadFolder());
    }

    /**
     * function who dynamically cast an Object with the matching classes
     *
     * @param classes
     * @param args
     * @return Object array
     */

    private Object[] dynamicCast(Class<?>[] classes, Object[] args) {
        Object[] array = new Object[classes.length];
        int i = 0;
        for (Class<?> cl : classes) {
            if (cl == Integer.class)
                array[i] = Integer.parseInt(String.valueOf(args[i]));
            else if (cl == Timestamp.class)
                array[i] = Timestamp.valueOf(String.valueOf(args[i]));
            else if (cl == Double.class)
                array[i] = Double.parseDouble(String.valueOf(args[i]));
            else if (cl == Float.class)
                array[i] = Float.parseFloat(String.valueOf(args[i]));
            else if (cl == RedirectAttribute.class)
                array[i] = (RedirectAttribute) args[i];
            else
                array[i] = (String.valueOf(args[i]));
            i++;
        }
        return array;
    }

    private Method stringMatchingMethod(Method[] methods, String method_name) {
        Method matchingMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(method_name)) {
                // System.out.println(method.getName());
                return method;
            }
        }
        return null;
    }

    private Class<?>[] arrayMethodParameter(Method method) {
        // Get the parameters of the method
        Parameter[] parameters = method.getParameters();
        // Create an array to store the classes of the parameter instances
        Class<?>[] paramClasses = new Class<?>[parameters.length];
        // Iterate through the parameters and get their classes
        for (int i = 0; i < parameters.length; i++) {
            paramClasses[i] = parameters[i].getType();
            // System.out.println(parameters[i].getType());
        }
        // Return the array of parameter classes
        return paramClasses;
    }

    private void addDataToRequest(HashMap<String, Object> data) {
        for (Map.Entry<String, Object> value : data.entrySet()) {
            System.out.println(value.getKey() + " " + value.getValue());
            getHttpServletRequest().setAttribute(value.getKey(), value.getValue());
        }
    }

    private Object[] requestParamAttr(Vector<Parameter> parameterVector, Map<String, String[]> requestParameter,
            RedirectAttribute attribute) {
        Vector<Object> objectVector = new Vector<>();
        for (Parameter params : parameterVector) {
            if (params.getType() == RedirectAttribute.class) {
                objectVector.add(attribute);
            } else {
                RequestParameter rqParams = params.getAnnotation(RequestParameter.class);
                String paramsName = (rqParams.name().equals("")) ? params.getName() : rqParams.name();
                Object[] val = requestParameter.get(paramsName);// maka an'ilay anle valeur anle
                objectVector.add(val[0]);// atao anaty anilay Vector daoly na null na tsy null

            }
        }
        return objectVector.toArray(new Object[0]);
    }

    private Vector<Parameter> methodParameters(Method method) {
        Vector<Parameter> parameterVector = new Vector<>();
        for (Parameter params : method.getParameters()) {
            if (params.isAnnotationPresent(RequestParameter.class) || params.getType() == RedirectAttribute.class)
                parameterVector.add(params);
        }
        return parameterVector;
    }

    private Class<?>[] parametersClass(Vector<Parameter> parameters) {
        if (parameters.isEmpty())
            return null;
        Class<?>[] classes = new Class[parameters.size()];
        int i = 0;
        for (Parameter params : parameters) {
            classes[i] = params.getType();
            i++;
        }
        return classes;
    }

    private Vector<Parameter> fileUpload(Vector<Parameter> parameters) {
        if (parameters.isEmpty())
            return null;
        int i = 0;
        Vector<Parameter> fileUploadList = new Vector<>();
        for (Parameter parameter : parameters) {
            if (parameter.getType() == FileUpload.class)
                fileUploadList.add(parameter);
        }
        return fileUploadList;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    private Vector<String> retrieveRequestUrl(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] linkURI = requestURI.split("/");
        Vector<String> stringVector = new Vector<>();
        for (String t : linkURI) {
            stringVector.add(t);
        }
        stringVector.remove(0);
        stringVector.remove(0);

        return stringVector;
    }

    private String formatUrl(Vector<String> stringVector) {
        StringBuilder sb = new StringBuilder();
        int vector_size = stringVector.size();
        
        for (int i = 0; i < vector_size; i++) {
            sb.append(stringVector.get(i));
            if (i + 1 < vector_size) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    private String requestURL(HttpServletRequest request) {
        Vector<String> requestURL = retrieveRequestUrl(request);
        String ur = "";
        for (String url : requestURL) {
            ur += url;
        }
        return ur;
    }
}
