package br.com.aquiles.web.util;

import br.com.aquiles.web.mb.UtilMB;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class JsfPhaseListener implements PhaseListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    @Named(value = "utilMB")
    private UtilMB utilMB;

    @Override
    public void afterPhase(PhaseEvent arg0) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.getMessageList().isEmpty()) {
            Object aquilesBodyId = utilMB.getContextoUtil().getParamSession("aquiles-body-id");
            if (aquilesBodyId != null) {
                fc.getPartialViewContext().getRenderIds().add(aquilesBodyId.toString() + ":aquiles-messages" + ":panelGroupAquilesMessageContainer");
            }
        }
    }

    @Override
    public void beforePhase(PhaseEvent arg0) {
        //
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

}
