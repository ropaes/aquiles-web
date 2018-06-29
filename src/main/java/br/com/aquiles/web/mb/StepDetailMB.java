package br.com.aquiles.web.mb;

import br.com.aquiles.persistence.bean.AbstractEntity;

import java.util.LinkedList;

public abstract class StepDetailMB<Bean extends AbstractEntity> extends BasicDetailMB<Bean> {
    private Integer step=1;
    private LinkedList<String> stepLabels=new LinkedList<>();

    public String stepUP() {
        step++;
        return "detail" + step + "?faces-redirect=true";
    }

    public String stepDown() {
        step--;
        if (step == 1 || step < 1) {
            step = 1;
            return "detail?faces-redirect=true";
        }
        return "detail" + step + "?faces-redirect=true";
    }

    public String stepOn(Long step) {
        this.step = step.intValue();
        if (step == 1) {
            return "detail?faces-redirect=true";
        }
        return "detail" + step + "?faces-redirect=true";
    }

    public String stepClass(Long step) {
        String classBoostrap = "btn-default";
        if (step < this.step) classBoostrap = "btn-success";
        if (step == this.step.longValue()) classBoostrap = "btn-primary";
        return classBoostrap;
    }

    public LinkedList<String> getStepLabels() {
        return stepLabels;
    }

    public void setStepLabels(LinkedList<String> stepLabels) {
        this.stepLabels = stepLabels;
    }

    /**
     * Deve ser executado para montar os passos
     * Ex: putStepLabels("Passo 1","Passo 2","Passo 3")
     *
     * @param labels
     */
    public void putStepLabels(String... labels) {
        for (String label : labels) {
            this.stepLabels.add(label);
        }
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
