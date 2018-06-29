package br.com.aquiles.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aquiles on 15/03/2017.
 */
public class FileUtils extends br.com.aquiles.core.util.FileUtils{
    public static void sendFileToBrowser(FacesContext fc, ByteArrayOutputStream baos, String contentType,String nome, String formato) throws IOException {
        if(nome==null) {
            nome = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        }
        if(formato!=null) {
            formato = "." + formato;
        }else {
            formato ="";
        }
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
        response.reset();
        response.setContentType(contentType);
        response.setContentLength(baos.size());

        response.setHeader("Content-Disposition", "attachment; filename=\""
                + nome +formato + "\"");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
        fc.responseComplete();
    }
    public static void sendFileToBrowser(FacesContext fc, ByteArrayOutputStream baos, String contentType, String nome) throws IOException {
        sendFileToBrowser(fc,baos,contentType,nome,null);
    }

    /**
     *  *
     *  * @param fc
     *  * @param resource
     *  * @return
     *  * @throws MalformedURLException
     *  
     */
    public static URL getResourceURL(FacesContext fc, String resource) throws MalformedURLException {
        return fc.getExternalContext().getResource(resource);
    }
}
