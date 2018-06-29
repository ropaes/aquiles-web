package br.com.aquiles.web.mb;

import br.com.aquiles.core.exception.CoreException;
import br.com.aquiles.security.bean.Documento;
import br.com.aquiles.security.dto.MenuParent;
import br.com.aquiles.security.dto.Subject;
import br.com.aquiles.security.exception.LoginException;
import br.com.aquiles.security.service.LoginService;
import br.com.aquiles.security.service.SecurityService;
import br.com.aquiles.security.util.MenuUtils;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

@SessionScoped
@Named(value = "utilMB")
public class UtilMB extends AbstractMB {


    @Inject
    @Named(value = "loginService")
    private LoginService loginService;

    @Inject
    @Named(value = "securityService")
    private SecurityService securityService;

    private String login;
    private String senha;

    private List<MenuParent> menus;
    private List<Documento> documentos;

    public Date getServerDate(){
        return new Date();
    }

    public String doLogin() {
        try {
            Subject subject = loginService.login(login, senha.trim(), getSiglaModulo());
            return finalizeLogin(subject);
        } catch (LoginException e) {
            addErrorMessage(e.getMessage());
            return "";
        } catch (CoreException e) {
            addErrorMessage(e.getMessage());
            return "";
        }
    }

    public String finalizeLogin(Subject subject) {
        getContextoUtil().setParamSession("subject", subject);
        construirMenus();
        return navigateTo("/core/index.xhtml");
    }

    public String doLogout() {
        getContextoUtil().getSessao().invalidate();
        addInfoMessage("Logout realizado com sucesso");
        return navigateTo("/security/index.xhtml");
    }

    private void construirMenus() {
        menus = MenuUtils.construirMenus(((Subject) getContextoUtil().getParamSession("subject")).getAutorizacao());
    }

    public List<MenuParent> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuParent> menus) {
        this.menus = menus;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Subject getSubject() {
        return (Subject) getContextoUtil().getParamSession("subject");
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }
}
