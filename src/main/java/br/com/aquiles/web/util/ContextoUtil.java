/**
 * 
 */
package br.com.aquiles.web.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * Classe responsavel por retornar os contextos da aplicacao
 * 
 * @author enemias.junior
 * @version 1.0.0 , em 01/07/2014
 * @since 1.0.0
 */
@Named
@ApplicationScoped
public class ContextoUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContext facesContext;

	private static final String IDENT_REQUEST_PARAM = "@";

	@Produces
	@RequestScoped
	public FacesContext getFacesContext() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context == null)
			throw new ContextNotActiveException("FacesContext is not active");
		return context;
	}

	/**
	 * Retornar a instância ExternalContext para a instância FacesContext.
	 * 
	 * @return ExternalContext
	 */
	public ExternalContext getExternalContext() {
		return facesContext.getExternalContext();
	}

	/**
	 * Retorna um String contendo o caminho real para um determinado caminho
	 * virtual.
	 * 
	 * @return realPath
	 */
	public String getRealPath() {
		return getExternalContext().getRealPath("");
	}

	public HttpSession getSessao() {
		return (HttpSession) getExternalContext().getSession(true);
	}

	public HttpServletResponse getResponse() {
		return (HttpServletResponse) getExternalContext().getResponse();
	}

	public HttpServletRequest getRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	/**
	 * Método para guardar os objetos entre os beans
	 * 
	 * @param key
	 * @param object
	 */
	public void setParamSession(String key, Object object) {
		getSessao().setAttribute(key, object);

	}

	public Object getParamSession(String key) {
		return getSessao().getAttribute(key);
	}

	public void removeAttribute(String key) {
		getSessao().removeAttribute(key);
	}

	public Object getParamRequest(String nome) {
		if (nome.startsWith(IDENT_REQUEST_PARAM)) {
			return getParamSession(nome);
		} else {
			return getParamSession(IDENT_REQUEST_PARAM + nome);
		}
	}

	public Object popParamRequest(String nome) {
		Object r = getParamRequest(nome);
		removeParamRequest(nome);
		return r;
	}

	public void removeParamRequest(String nome) {
		if (nome.startsWith(IDENT_REQUEST_PARAM)) {
			removeAttribute(nome);
		} else {
			removeAttribute(IDENT_REQUEST_PARAM + nome);
		}
	}

	public void setParamRequest(String nome, Object valor) {
		if (nome.startsWith(IDENT_REQUEST_PARAM)) {
			setParamSession(nome, valor);
		} else {
			setParamSession(IDENT_REQUEST_PARAM + nome, valor);
		}
	}

	public void clearRequestParams() {
		Enumeration<String> attrs = getSessao().getAttributeNames();
		while(attrs.hasMoreElements()){
			String key = attrs.nextElement();
			if (key.startsWith(IDENT_REQUEST_PARAM)) {
				removeParamRequest(key);
			}
		}
	}

}
