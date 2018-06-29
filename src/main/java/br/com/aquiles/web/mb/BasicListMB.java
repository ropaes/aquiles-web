package br.com.aquiles.web.mb;

import br.com.aquiles.core.dto.ColumnPrint;
import br.com.aquiles.core.exception.ServiceException;
import br.com.aquiles.core.service.GenericService;
import br.com.aquiles.core.util.PrintGeneratorUtils;
import br.com.aquiles.persistence.bean.AbstractEntity;
import br.com.aquiles.persistence.bean.Selectable;
import br.com.aquiles.web.annotation.OrderByListMB;
import br.com.aquiles.web.util.ELExpressionUtil;
import br.com.aquiles.web.util.FileUtils;

import javax.annotation.PostConstruct;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Responsavel pela listagem de beans, geralmente a primeira pagina *-list.
 *
 * @author Ronaldo Lanhellas
 */
public abstract class BasicListMB<Bean extends AbstractEntity> extends AbstractMB {

    private static final long serialVersionUID = 1L;

    protected List<Bean> beans = new ArrayList<Bean>();

    private boolean selectedAll;
    private int selectedCount;
    private Bean lastSelected;
    protected List<Bean> selecionados;

    @Inject
    private ELExpressionUtil elExpressionUtil;

    /**
     * Usado para selecionar todos os registro do map 'registrosSelecionados'. O
     * renderResponse() garante que os valores não serão sobreescritos por conta
     * do LifeCycle do JSF.
     */
    public void selectAll(ValueChangeEvent event) {

        this.selectedAll = Boolean.valueOf(event.getNewValue().toString());

        for (Bean b : beans) {
            Selectable sbean = (Selectable) b;
            sbean.setSelected(selectedAll);
        }

        if (selectedAll) {
            selecionados = new ArrayList<Bean>(beans);
            lastSelected = selecionados.isEmpty() ? null :selecionados.get(0);
        } else {
            lastSelected = null;
            selecionados = new ArrayList<Bean>();
        }

        selectedCount = selecionados.size();
        getContextoUtil().getFacesContext().renderResponse();
    }

    public void generateHTML() {
        try {
            sendFileToBrowser(getContextoUtil().getFacesContext(), PrintGeneratorUtils.createHTML(beans, getColumnsToPrint()), "text/html", "html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generatePDF() {
        try {
            sendFileToBrowser(getContextoUtil().getFacesContext(), PrintGeneratorUtils.createPDF(beans, getColumnsToPrint()), "application/pdf", "pdf");
        } catch (Exception e) {
            addErrorMessage("Erro ao gerar o PDF. " + e.getMessage());
        }
    }

    public void generateXLS() {
        try {
            sendFileToBrowser(getContextoUtil().getFacesContext(), PrintGeneratorUtils.createXLS(beans, getColumnsToPrint()), "application/vnd.ms-excel", "xls");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     * @author ROnaldo Lanhellas
     * Envia o PDF ao browser através do response do FacesContext
     */
    private void sendFileToBrowser(FacesContext fc, ByteArrayOutputStream baos, String contentType, String formato) throws IOException {
        FileUtils.sendFileToBrowser(fc, baos, contentType, null, formato);
    }

    /**
     * Remove os beans que estão com valor TRUE no map 'registrosSelecionados'
     */
    public void removeSelected() {
        try {
            int count = 0;
            Iterator<Bean> it = beans.iterator();
            beforeRemove();
            while (it.hasNext()) {
                Selectable sbean = (Selectable) it.next();
                if (sbean.isSelected()) {
                    beforeBeanRemove((Bean) sbean);
                    if (getContextoUtil().getFacesContext().isValidationFailed()) {
                        break;
                    }
                    getService().delete((AbstractEntity) sbean);
                    it.remove();
                    afterRemove();
                    count++;
                }
            }
            clearSelected();

            if (count > 0) {
                addInfoMessage(count + " Registro(s) removido(s) com sucesso");
            }
        } catch (Exception e) {
            logger.error(e);
            addErrorMessage(e.getMessage());
        }
    }

    /**
     * Inicializa todos os objetos necessarios no ManagedBean.
     */
    @PostConstruct
    public void postConstruct() {
        init();
        clearSelected();
        buscarBeans();
        lateInit();
    }

    /**
     * Chamado no inicio do PostConstruct, antes de qualquer logica.
     */
    public void init() {
    }

    /**
     * Chamado apos toda a execucao do PostConstruct, ou seja, no final.
     * Diferente do método init() que é chamado no Inicio
     */
    public void lateInit() {
    }

    // Filter actions --------------------------------------------------------

    public void beforeBuscarBeans() {
    }

    public void afterBuscarBeans() {
    }

    /**
     * Busca os Beans no banco de acordo com o filtro selecionado.
     */
    public void buscarBeans() {
        beforeBuscarBeans();
        try {
            beans = findAllBeans();
        } catch (ServiceException e) {
            addErrorMessage(e.getMessage());
        }
        afterBuscarBeans();
    }

    // Abstract actions
    // ----------------------------------------------------------------

    /**
     * @deprecated This method will be removed in next versions
     */
    @Deprecated
    public void setService(GenericService service) {
    }

    public abstract GenericService getService();

    /**
     * Método responsável por retornar a lista de colunas que devem ser mostradas no arquivo de impressão (PDF, XLS, DOC ..)
     */
    public abstract List<ColumnPrint> getColumnsToPrint();

    /**
     * Retorna todos os beans do banco de dados. Utilizado para o método
     * buscarBeans() quando não ha filtro selecionado.
     */
    public List<Bean> findAllBeans() throws ServiceException {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<Bean> type = (Class<Bean>) superClass.getActualTypeArguments()[0];
        return (List<Bean>) getService().findAll(type, searchOrderByToFindAllBeans());
    }

    private String searchOrderByToFindAllBeans() {
        if (this.getClass().isAnnotationPresent(OrderByListMB.class)) {
            OrderByListMB orderByListMB = this.getClass().getAnnotation(OrderByListMB.class);
            return orderByListMB.orderBy();
        }
        return "id DESC";
    }

    // Before/After Actions
    // --------------------------------------------------------

    public void beforeRemove() {
    }

    public void beforeBeanRemove(Bean bean) {
    }

    public void afterRemove() {
    }

    public List<Bean> getBeans() {
        return beans;
    }

    public void setBeans(List<Bean> beans) {
        this.beans = beans;
    }

    public boolean isSelectedAll() {
        return selectedAll;
    }

    public void setSelectedAll(boolean selectedAll) {
        this.selectedAll = selectedAll;
    }

    public void selectRow(ValueChangeEvent event) {
        Boolean selected = (Boolean) event.getNewValue();
        Bean bean = (Bean) ((UIInput) event.getSource()).getAttributes().get("bean");
        if (selected) {
            lastSelected = bean;
            this.selecionados.add(bean);
            selectedCount++;
        } else {
            this.selecionados.remove(bean);
            selectedCount--;
        }
    }

    /**
     * Resolve a expression pointing to selected beans in the list. The expression is executed to each bean inside
     * this list and when a FALSE result is found the method return immediately with FALSE, otherwise TRUE is returned
     * in the end of execution.
     *
     * @param expression      The expression to be resolved. Example: {@code #{idSit.value eq 'PAB'}}
     * @param managedBeanName The name of managedBean inside JSF Context. Example: {@code tituloListMB}
     */
    public Boolean resolveBooleanExpressionSelectedBeans(String managedBeanName, String expression) {
        try {
            for (int i = 0; i < selecionados.size(); i++) {
                Object oResolved = elExpressionUtil.resolveExpression("#{" + managedBeanName + ".selecionados[" + i + "]." +
                        expression + "}");
                if (oResolved.equals(Boolean.FALSE)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public String clearSelected() {
        selectedCount = 0;
        this.selecionados = new LinkedList<Bean>();
        return "";
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(int selectedCount) {
        this.selectedCount = selectedCount;
    }

    public Bean getLastSelected() {
        return lastSelected;
    }

    public void setLastSelected(Bean lastSelected) {
        this.lastSelected = lastSelected;
    }

    public List<Bean> getSelecionados() {
        return selecionados;
    }

    public void setSelecionados(List<Bean> selecionados) {
        this.selecionados = selecionados;
    }
}
