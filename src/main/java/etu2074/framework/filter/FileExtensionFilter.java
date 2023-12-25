package etu2074.framework.filter;

import jakarta.servlet.*;

import java.io.IOException;


public class FileExtensionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = ((jakarta.servlet.http.HttpServletRequest) request).getRequestURI();

        if(!requestURI.endsWith(".js") && !requestURI.endsWith(".css"))
        if (!requestURI.endsWith(".js") && !requestURI.endsWith(".css")) {
            chain.doFilter(request, response);
        }else{
            request.getServletContext().getNamedDispatcher("default").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
