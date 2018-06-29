package br.com.aquiles.web.mb;

import br.com.aquiles.core.exception.ServiceException;
import br.com.aquiles.core.service.GenericService;
import br.com.aquiles.core.util.StringUtils;
import br.com.aquiles.security.bean.Funcao;
import br.com.aquiles.security.dto.Subject;
import br.com.aquiles.persistence.bean.AbstractEntity;
import br.com.aquiles.persistence.bean.AbstractEntityAtu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;

public abstract class BasicDetailMB<Bean extends AbstractEntity> extends AbstractMB {

    private static final long serialVersionUID = 1L;

    protected Bean bean;

    private String action;

    /**
     * Cria um novo bean
     */
    private void novo() {
        beforeNew();
        bean = getNewInstanceOfBean();
        afterNew();
    }

    /**
     * Cria uma instancia do tipo Bean
     */
    @SuppressWarnings("unchecked")
    private Bean getNewInstanceOfBean() {
        try {
            ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
            Class<Bean> type = (Class<Bean>) superClass.getActualTypeArguments()[0];
            Bean b = type.newInstance();
            b.setNovo(true);
            return b;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Altera um bean ja existente
     */
    private void alterar() {
        beforeAlter();
        Object id = getContextoUtil().popParamRequest("id");
        buscarBean(id);
        try {
            afterAlter();
        } catch (ServiceException e) {
            addErrorMessage(e.getMessage());
        }
    }

    /**
     * Salva um bean no banco de dados e o adiciona a lista de beans.
     */

    public String salvar(String outcome) {
        try {

            beforeSave();

            if (getContextoUtil().getFacesContext().isValidationFailed()) {
                return "";
            }

            if (bean instanceof AbstractEntityAtu) {
                Subject subject = (Subject) getContextoUtil().getParamSession("subject");
                ((AbstractEntityAtu) bean).setCdUsuAtu(subject.getLogin());
                ((AbstractEntityAtu) bean).setDhAtu(new Date());
            }
            getService().save(bean);

            addInfoMessage("Registro salvo com sucesso");
            afterSave();
            endConversation();
            return outcome + "?faces-redirect=true";
        } catch (ServiceException e) {
            logger.error(e);
            addErrorMessage(e.getMessage());
            return "";
        } catch (Exception e) {
            logger.error(e);
            addErrorMessage("Erro ao salvar. " + e.getMessage());
            return "";
        }
    }

    /**
     * Inicializa todos os objetos necessarios no ManagedBean.
     */
    @PostConstruct
    public void postConstruct() {

        init();

        String action = (String) getContextoUtil().popParamRequest("action");

        if (action == null) {
            action = Funcao.ADIC;
        }

        this.action = action;

        if (!isComponentePermitidoActivePage(action)) {
            try {
                logger.warn("O usuário " + getSubject().getLogin() + " não tem acesso a função " + action + ". Redirecionando para página list.xhtml");
                getContextoUtil().getExternalContext().redirect("list.xhtml");
                return;
            } catch (IOException e) {
                addErrorMessage("Erro ao redirecionar para página de listagem. " + e.getMessage());
            }
        }

        if (Funcao.ALTR.equals(action) || Funcao.VISU.equals(action)) {
            alterar();
        } else if (Funcao.ADIC.equals(action)) {
            novo();
        }


        lateInit();
    }

    /**
     * Chamado no inIcio do PostConstruct, antes de qualquer logica.
     */
    public void init() {

    }

    /**
     * Chamado apos toda a execucao do PostConstruct, ou seja, no final.
     * Diferente do método init() que é chamado no Inicio
     */
    public void lateInit() {
    }

    /**
     * Busca um Bean através do seu ID
     */
    @SuppressWarnings("unchecked")
    public void buscarBean(Object id) {
        try {
            ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
            Class<Bean> type = (Class<Bean>) superClass.getActualTypeArguments()[0];
            bean = (Bean) getService().findByClass(type, id);
        } catch (ServiceException e) {
            addErrorMessage(e.getMessage());
        }
    }

    // Abstract actions
    // ----------------------------------------------------------------
    /**
     * @deprecated This method will be removed in next versions
     * */
    @Deprecated
    public void setService(GenericService service){}
    public abstract GenericService getService();

    // Before/After Actions
    // --------------------------------------------------------

    public void beforeSave() throws ServiceException {
    }

    public void afterSave() {
    }

    public void beforeNew() {
    }

    public void afterNew() {
    }

    public void beforeAlter() {
    }

    public void afterAlter() throws ServiceException {
    }

    // Getters and Setters
    // ------------------------------------------------------------

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public Conversation getConversation() {
        return getConversation();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String buildPage(Object managedBean){
        StringBuilder stringBuffer = new StringBuilder();
        for(Field field: bean.getClass().getDeclaredFields()){
            switch (field.getType().toString()){
                case "class java.lang.String" :
                    stringBuffer.append("<div class=\"form-group\">");
                    stringBuffer.append("<label>"+ StringUtils.capitalize(field.getName())+ "</label>");
                    stringBuffer.append("<input jsf:id=\""+field.getName()+"\" type=\"text\" class=\"form-control\" jsf:value=\"#{"+StringUtils.uncapitalize(managedBean.getClass().getSimpleName())+".bean."+field.getName()+"}\"></input>");
                    stringBuffer.append("</div>");
                    break;
            }
        }
        return stringBuffer.toString();
    }
}
