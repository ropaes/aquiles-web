package br.com.aquiles.web.util;

import org.apache.log4j.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author Enemias Junior, Ronaldo Lanhellas
 * @since 1.0.0
 */
public class MessagesUtil implements Serializable {

    private static final long serialVersionUID = -5934331120012186882L;
    private static Logger logger = Logger.getLogger(MessagesUtil.class);

    public static void addErrorMessage(List<?> listaErro) {
        for (int i = 0; i < listaErro.size(); i++) {
            addErrorMessage((String) listaErro.get(i));
        }
    }

    public static void addErrorMessage(String erro) {
        logger.error(erro);
        //Trata a mensagem de erro, retirando o pacote de classe caso haja
        if (erro.contains("Protocolo")) {
            int index = erro.indexOf("Protocolo");
            erro = erro.substring(index - 1, erro.length());
        }
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, erro, erro);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, facesMsg);
        fc.validationFailed();
    }

    public static void addInfoMessage(List<?> listaMsg) {
        for (int i = 0; i < listaMsg.size(); i++) {
            addInfoMessage((String) listaMsg.get(i));
        }
    }

    public static void addInfoMessage(String msg) {
        logger.info(msg);
        //Trata a mensagem de info, retirando o pacote de classe caso haja
        if (msg.contains("Protocolo")) {
            int index = msg.indexOf("Protocolo");
            msg = msg.substring(index - 1, msg.length());
        }
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, facesMsg);
    }

    public static void addWarnMessage(List<?> listaMsg) {
        for (int i = 0; i < listaMsg.size(); i++) {
            addWarnMessage((String) listaMsg.get(i));
        }
    }

    public static void addWarnMessage(String msg) {
        logger.warn(msg);
        //Trata a mensagem de info, retirando o pacote de classe caso haja
        if (msg.contains("Protocolo")) {
            int index = msg.indexOf("Protocolo");
            msg = msg.substring(index - 1, msg.length());
        }
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, facesMsg);
    }

    public static void addFatalMessage(List<?> listaMsg) {
        for (int i = 0; i < listaMsg.size(); i++) {
            addFatalMessage((String) listaMsg.get(i));
        }
    }

    public static void addFatalMessage(String msg) {
        logger.fatal(msg);
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, msg);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, facesMsg);
    }

}