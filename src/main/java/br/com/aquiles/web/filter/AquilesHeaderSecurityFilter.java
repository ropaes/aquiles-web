package br.com.aquiles.web.filter;

import br.com.aquiles.web.wrapper.SecureCookieSetter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Diorge Jorge on 26/04/2018.
 */
public class AquilesHeaderSecurityFilter implements Filter {

    private String mode = "SAMEORIGIN";
    private String type = "nosniff";
    private String xssMode = "1; mode=block";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configMode = filterConfig.getInitParameter("mode");
        String configType = filterConfig.getInitParameter("type");
        String configXssMode = filterConfig.getInitParameter("xssMode");
        if ( configMode != null ) {
            mode = configMode;
        }
        if ( configType != null ) {
            type = configType;
        }
        if ( configXssMode != null ) {
            xssMode = configXssMode;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse res = new SecureCookieSetter((HttpServletResponse)servletResponse);
        res.addHeader("X-FRAME-OPTIONS", mode );
        res.addHeader("X-Content-Type-Options",type);
        res.addHeader("X-XSS-Protection",xssMode);
        res.addHeader("Set-Cookie", "JSESSIONID=" + ((HttpServletRequest)servletRequest).getSession().getId() + ";HttpOnly");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
