package br.com.aquiles.web.converter;

import br.com.aquiles.core.validator.format.CNPJFormatter;
import br.com.aquiles.core.validator.format.CPFFormatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Created by aquiles on 23/01/2017.
 */
@FacesConverter("aquiles.CpfCnpjConverter")
public class CPFCNPJConverter implements Converter{
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
       return s;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if(o.toString().length()==11){
            CPFFormatter cpfFormatter = new CPFFormatter();
            return cpfFormatter.format(o.toString());
        }else if (o.toString().length()==14){
            CNPJFormatter cnpjFormatter = new CNPJFormatter();
            return cnpjFormatter.format(o.toString());
        }
       return o.toString();
    }
}
