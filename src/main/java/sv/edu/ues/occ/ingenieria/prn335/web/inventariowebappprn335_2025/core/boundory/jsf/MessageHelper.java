package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.ResourceBundle;

public class MessageHelper {

    private static final String BUNDLE_NAME = "crud";


    /**
     * Obtiene un mensaje del bundle según el locale actual
     */
    public static String getMessage(String key) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = ResourceBundle.getBundle(
                    BUNDLE_NAME,
                    context.getViewRoot().getLocale()
            );
            return bundle.getString(key);
        } catch (Exception e) {
            return "???" + key + "???";
        }
    }

    /**
     * Obtiene un mensaje del bundle con parámetros
     */
    public static String getMessage(String key, Object... params) {
        String message = getMessage(key);
        return String.format(message, params);
    }

    /**
     * Agrega un mensaje de información
     */
    public static void addInfoMessage(String summaryKey, String detailKey) {
        FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                getMessage(summaryKey),
                getMessage(detailKey)
        );
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Agrega un mensaje de error
     */
    public static void addErrorMessage(String summaryKey, String detailKey) {
        FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                getMessage(summaryKey),
                getMessage(detailKey)
        );
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Agrega un mensaje de error con detalle personalizado
     */
    public static void addErrorMessage(String summaryKey, String detailKey, String errorDetail) {
        FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                getMessage(summaryKey),
                getMessage(detailKey) + ": " + errorDetail
        );
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Agrega un mensaje de advertencia
     */
    public static void addWarnMessage(String summaryKey, String detailKey) {
        FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_WARN,
                getMessage(summaryKey),
                getMessage(detailKey)
        );
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}