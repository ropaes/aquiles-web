package br.com.aquiles.web.mb;

import br.com.aquiles.core.exception.CoreException;
import br.com.aquiles.core.service.BasicService;
import br.com.aquiles.security.bean.Funcao;
import br.com.aquiles.security.dto.AutorizacaoDTO;
import br.com.aquiles.security.dto.Subject;
import br.com.aquiles.web.annotation.FindAll;
import br.com.aquiles.web.util.ContextoUtil;
import br.com.aquiles.web.util.MessagesUtil;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Classe Super que devera ser utilizada em todos os ManagedBeans
 *
 * @author enemias.junior, Ronaldo Lanhellas
 */
public abstract class AbstractMB implements Serializable {

    @Inject
    private BasicService basicService;

    @PostConstruct
    public void initAbstractMB() {
        loadFieldsWithFindAllAnnotation();
    }

    private void loadFieldsByClass(Class clazz, List<Field> fields) {
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            loadFieldsByClass(clazz.getSuperclass(), fields);
        }
    }

    private void loadFieldsWithFindAllAnnotation() {
        List<Field> fields = new ArrayList<>();
        loadFieldsByClass(this.getClass(), fields);

        for (Field field : fields) {
            field.setAccessible(true); // for convenience

            if (field.isAnnotationPresent(FindAll.class)) {
                Class clazz = loadClassByField(field);
                try {
                    FindAll findAllAnnotation = field.getAnnotation(FindAll.class);
                    field.set(this, basicService.findAll(clazz, findAllAnnotation.orderBy(), null, null, findAllAnnotation.qualifier(), false));
                } catch (Exception e) {
                    logger.error(e);
                    addErrorMessage(e.getMessage());
                }
            }
        }
    }

    private Class loadClassByField(Field f) {
        ParameterizedType type = (ParameterizedType) f.getGenericType();
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    @Inject
    protected transient Logger logger;

    @Inject
    private ContextoUtil contextoUtil;

    @Inject
    private Conversation conversation;

    public void beginConversation() {
        if (!getContextoUtil().getFacesContext().isPostback() && conversation.isTransient()) {
            conversation.setTimeout(3600000L);
            conversation.begin();
        }
    }

    public void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    public AbstractMB() {
        Locale.setDefault(new Locale("pt", "BR"));
    }

    /**
     * Diz para qual pagina deve ser redirecionado o fluxo de navegacao,
     * passando também alguns parametros de request.
     *
     * @author Ronaldo Lanhellas
     */
    public String navigateTo(String page, String param1, Object value1, String param2, Object value2) {
        contextoUtil.clearRequestParams();
        if (param1 != null && value1 != null) {
            contextoUtil.setParamRequest(param1, value1);

        }
        if (param2 != null && value2 != null) {
            contextoUtil.setParamRequest(param2, value2);
        }
        return page + "?faces-redirect=true";
    }

    public String navigateTo(String page, String param1, Object value1) {
        return navigateTo(page, param1, value1, null, null);
    }

    public String navigateToActionAlterar(Object id) {
        return navigateTo("detail", "action", Funcao.ALTR, "id", id);
    }

    public String navigateTo(String page) {
        return navigateTo(page, null, null);
    }

    public String getSiglaModulo() throws CoreException {
        String sgMdl = contextoUtil.getFacesContext().getExternalContext().getInitParameter("br.com.aquiles.web.sgMdl");
        if (sgMdl == null || "".equals(sgMdl.trim())) {
            throw new CoreException("Faltando configuração do br.com.aquiles.web.sgMdl no web-fragment.xml");
        }

        return sgMdl;
    }

    public String getColorTemplate() throws CoreException {
        String color = contextoUtil.getFacesContext().getExternalContext().getInitParameter("br.com.aquiles.web.color");
        if (color == null || "".equals(color.trim())) {
            throw new CoreException("Faltando configuração do br.com.aquiles.web.color no web-fragment.xml");
        }

        return color;
    }

    public Boolean getAutoCloseAlertMessage() {
        Object o = contextoUtil.getFacesContext().getExternalContext().getInitParameter("br.com.aquiles.web.autoCloseAlertMessage");
        if (o == null) {
            return Boolean.TRUE;
        } else {
            return Boolean.valueOf(o.toString());
        }
    }

    public boolean isComponentePermitidoActivePageSAVE() {
        return isComponentePermitidoActivePage(Funcao.SAVE);
    }

    public boolean isComponentePermitidoActivePageADIC() {
        return isComponentePermitidoActivePage(Funcao.ADIC);
    }

    public boolean isComponentePermitidoActivePageVISU() {
        return isComponentePermitidoActivePage(Funcao.VISU);
    }

    public boolean isComponentePermitidoActivePageALTR() {
        return isComponentePermitidoActivePage(Funcao.ALTR);
    }

    public boolean isComponentePermitidoActivePageEXCL() {
        return isComponentePermitidoActivePage(Funcao.EXCL);
    }

    public boolean isComponentePermitidoActivePageRETR() {
        //Por enquanto o componente de busca sempre será permitido
        //return isComponentePermitidoActivePage(Funcao.RETR);
        return true;
    }

    public boolean isComponentePermitidoActivePageCANC() {
        //Por enquanto o componente de busca sempre será permitido
        //return isComponentePermitidoActivePage(Funcao.CANC);
        return true;
    }

    public boolean isComponentePermitidoActivePage(String sgFuc) {
        String[] sgDocSgTrn = extractSgDocSgTrn();
        return isComponentePermitido(sgDocSgTrn[0], sgDocSgTrn[1], sgFuc);
    }

    public String[] extractSgDocSgTrn() {
        String contextPath = contextoUtil.getFacesContext().getExternalContext().getRequestServletPath();
        String[] contextPathSplitted = contextPath.split("/");
        String[] sgDocSgTrn = new String[2];
        //boolean canReadPath = false;
        //String sgDoc = "";
        //String sgTrn = "";
        for (int i = 0; i < contextPathSplitted.length; i++) {
            if (contextPathSplitted[i].equalsIgnoreCase("core")) {
                if ((i + 2) < contextPathSplitted.length) {//se tiver ainda mais 2 campos entao deve ter sgDoc e sgTrn
                    sgDocSgTrn[0] = contextPathSplitted[i + 1].toUpperCase();
                    sgDocSgTrn[1] = contextPathSplitted[i + 2].toUpperCase();
                }

                break;
            }
        }

        return sgDocSgTrn;
    }

    /**
     * Retorna a descrição da função de acordo com a TRANSAÇÃO atual
     */
    public String getDescricaoFuncao(String sgFuc) {

        //Caso a função for RETR então não precisa ir na base
        if (sgFuc != null && sgFuc.trim().equalsIgnoreCase("RETR")) {
            return "Buscar";
        }

        String[] sgDocSgTrn = extractSgDocSgTrn();
        String sgDoc = sgDocSgTrn[0].trim().toUpperCase();
        String sgTrn = sgDocSgTrn[1].trim().toUpperCase();
        String sgFuc_ = sgFuc != null ? sgFuc.trim().toUpperCase() : sgFuc;
        List<AutorizacaoDTO> autorizacoes = getSubject().getAutorizacao();
        for (AutorizacaoDTO auto : autorizacoes) {
            if (auto.getSgDoc().equals(sgDoc)) {
                if (auto.getSgTrn().equals(sgTrn)) {
                    if (auto.getSgFuc().equals(sgFuc_)) {
                        return auto.getFuncao().getDeFuc();
                    }
                }
            }
        }

        return "SEM-FUNCAO-DEFINIDA";
    }

    /**
     * Retorna a descrição da transação de acordo com a página atual
     */
    public String getDescricaoTransacao() {
        String[] sgDocSgTrn = extractSgDocSgTrn();
        String sgDoc = sgDocSgTrn[0].trim().toUpperCase();
        String sgTrn = sgDocSgTrn[1].trim().toUpperCase();
        List<AutorizacaoDTO> autorizacoes = getSubject().getAutorizacao();
        for (AutorizacaoDTO auto : autorizacoes) {
            if (auto.getSgDoc().equals(sgDoc)) {
                if (auto.getSgTrn().equals(sgTrn)) {
                    return auto.getTransacao().getDeTrn();
                }
            }
        }

        return "SEM-TRANSACAO-DEFINIDA";
    }

    public boolean isComponentePermitido(String sgDoc, String sgTrn, String sgFuc) {
        return isComponentePermitido(sgDoc, sgTrn, sgFuc, getSubject());
    }

    /**
     * Check if a component is authorized to be accessed by a specific subject (user)
     */
    public boolean isComponentePermitido(String sgDoc, String sgTrn, String sgFuc, Subject subject) {

        if (sgDoc == null || sgDoc.equals("") || sgDoc.equals("****")) {
            addErrorMessage("O SG_DOC deve ser informado ao verificar se o componente é permitido por este perfil");
        }

        List<AutorizacaoDTO> autorizacoes = getSubject().getAutorizacao();
        for (AutorizacaoDTO auto : autorizacoes) {
            if (auto.getSgDoc().equals(sgDoc)) {
                if (sgTrn != null && !sgTrn.equals("") && !sgTrn.equals("****")) {
                    if (auto.getSgTrn().equals(sgTrn)) {
                        if (sgFuc != null && !sgFuc.equals("") && !sgFuc.equals("****")) {
                            if (auto.getSgFuc().equals(sgFuc)) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Write Attribute on CustomValues within Subject object
     * */
    public void writeAttributeOnSubjectCustomValue(String key, String value){
        Subject subject = getSubject();
        if (subject!=null){
            subject.getCustomValues().put(key,value);
        }
    }

    /**
     * Read Attribute on CustomValues within Subject object
     * */
    public Object readAttributeOnSubjectCustomValue(String key){
        Subject subject = getSubject();
        if (subject!=null){
            return subject.getCustomValues().get(key);
        }

        return null;
    }

    public Subject getSubject() {
        return (Subject) contextoUtil.getParamSession("subject");
    }

    public void addErrorMessage(List<?> listaErro) {
        MessagesUtil.addErrorMessage(listaErro);
    }

    public void addErrorMessage(String erro) {
        MessagesUtil.addErrorMessage(erro);
    }

    public void addErrorMessage(Throwable throwable) {
        addErrorMessage(throwable.getMessage());
    }

    public void addInfoMessage(List<?> listaMsg) {
        MessagesUtil.addInfoMessage(listaMsg);
    }

    public void addInfoMessage(String msg) {
        MessagesUtil.addInfoMessage(msg);
    }

    public void addWarnMessage(List<?> listaMsg) {
        MessagesUtil.addWarnMessage(listaMsg);
    }

    public void addWarnMessage(String msg) {
        MessagesUtil.addWarnMessage(msg);
    }

    public void addFatalMessage(List<?> listaMsg) {
        MessagesUtil.addFatalMessage(listaMsg);
    }

    public void addFatalMessage(String msg) {
        MessagesUtil.addFatalMessage(msg);
    }

    public void redirect(String page) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(
                    FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath().toLowerCase()
                            + page);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public ContextoUtil getContextoUtil() {
        return contextoUtil;
    }
}