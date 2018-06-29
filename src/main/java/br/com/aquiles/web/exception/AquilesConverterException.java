package br.com.aquiles.web.exception;

import javax.faces.application.FacesMessage;
import javax.faces.convert.ConverterException;

/**
 * Created by rlanhellas on 29/03/2017.
 */
public class AquilesConverterException extends ConverterException {

    public AquilesConverterException(String msg) {
        super((new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg)));
    }
}
