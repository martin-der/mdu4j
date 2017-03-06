package net.tetrakoopa.mdu4j.admin.front.servlet.bean.log;

import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;

public enum RetrievalMode implements HtmlParameterEnum, ActionAttribute {

    @Label("Full scan")
    @ActionAttribute.Info(quickInfo = "Afficher les tous les fichiers présents dans tous les réperoires des 'apprenders'")
    FULL_SCAN("full-scan"),

    @Label("Appenders")
    @ActionAttribute.Info(quickInfo = "Afficher les journeaux définis dans les 'apprenders'")
    APPENDERS("appenders"),

    @Label("Appenders and rolled files")
    @ActionAttribute.Info(quickInfo = "Afficher les journeaux définis dans les 'apprenders', ainsi que les fichiers de roulement")
    APPENDERS_AND_ROLLED("appenders-and-rolled");

    private final String parameterName;

    RetrievalMode(String parameterName) {
        this.parameterName = parameterName;
    }

    public static RetrievalMode fromParameterName(String name) throws NoSuchEnumException {
        return Util.fromParameterName(RetrievalMode.class, name);
    }

    public static String[] buildActionsParameterName() {
        return Util.buildActionsParameterName(LogAction.class);
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

}
