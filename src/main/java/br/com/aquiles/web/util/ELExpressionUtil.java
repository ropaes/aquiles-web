package br.com.aquiles.web.util;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * This class has methods to work with EL Expression.
 *
 * @author Ronaldo Lanhellas
 */
@Named
@RequestScoped
public class ELExpressionUtil implements Serializable {

    @Inject
    private ContextoUtil contextoUtil;

    /**
     * Resolve the expression passed as argument.
     * <br /><br />The sintax should be:<br />
     * {@code #{yourManagedBeanName.yourBean.yourProperty}}
     */
    public Object resolveExpression(String expression) {
        FacesContext facesContext = contextoUtil.getFacesContext();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        ValueExpression valueExp =
                elFactory.createValueExpression(elContext, expression,
                        Object.class);
        return valueExp.getValue(elContext);
    }

}
