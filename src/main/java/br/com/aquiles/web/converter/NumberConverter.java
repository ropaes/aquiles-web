package br.com.aquiles.web.converter;

import br.com.aquiles.core.util.StringUtils;
import br.com.aquiles.web.exception.AquilesConverterException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("aquiles.NumberConverter")
public class NumberConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s == null) {
            return s;
        }
        s = StringUtils.removerMascaras(s);
        try {
            Long.parseLong(s);
        } catch (NumberFormatException n) {
            throw new AquilesConverterException("O valor " + s + " não é um número válido.");
        }
        return s;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return o == null ? "" : o.toString();
    }
}
