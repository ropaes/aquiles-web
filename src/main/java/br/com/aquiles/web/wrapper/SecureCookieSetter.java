package br.com.aquiles.web.wrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by Diorge Jorge on 26/04/2018.
 */
public class SecureCookieSetter extends HttpServletResponseWrapper {
    public SecureCookieSetter(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookie.setSecure(true);
        super.addCookie(cookie);
    }

    @Override
    public void addHeader(String name, String value) {
        if ((name.equals("Set-Cookie")) && (!value.matches("(^|.*;)\\s*Secure"))) {
            value = value + ";Secure";
        }
        super.addHeader(name, value);
    }

    @Override
    public void setHeader(String name, String value) {
        if ((name.equals("Set-Cookie")) && (!value.matches("(^|.*;)\\s*Secure"))) {
            value = value + ";Secure";
        }
        super.setHeader(name, value);
    }
}
