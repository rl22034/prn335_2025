package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Named("localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

    private String idioma;

    @Inject
    FacesContext facesContext;

    private Map<String, Locale> idiomasDisponibles;
    private Locale locale;

    // ⭐ MenuModel para el SplitButton
    private MenuModel menuIdiomas;

    @PostConstruct
    public void init() {
        idiomasDisponibles = new LinkedHashMap<>();
        idiomasDisponibles.put("English", new Locale("en"));
        idiomasDisponibles.put("Español", new Locale("es"));

        this.locale = facesContext.getApplication().getDefaultLocale();

        if (this.locale == null) {
            this.locale = new Locale("es");
        }

        this.idioma = this.locale.getLanguage();

        // ⭐ Inicializar el menú del SplitButton
        inicializarMenuIdiomas();
    }

    // ⭐ Método para inicializar el menú desplegable
    private void inicializarMenuIdiomas() {
        menuIdiomas = new DefaultMenuModel();

        // Opción Español
        DefaultMenuItem itemEspanol = DefaultMenuItem.builder()
                .value("🇪🇸 Español")
                .command("#{localeBean.cambiarIdioma('es')}")
                .ajax(false)
                .build();

        // Opción English
        DefaultMenuItem itemIngles = DefaultMenuItem.builder()
                .value("🇺🇸 English")
                .command("#{localeBean.cambiarIdioma('en')}")
                .ajax(false)
                .build();

        menuIdiomas.getElements().add(itemEspanol);
        menuIdiomas.getElements().add(itemIngles);
    }

    // ⭐ Método para cambiar idioma
    public void cambiarIdioma(String nuevoIdioma) {
        this.idioma = nuevoIdioma;
        this.locale = new Locale(nuevoIdioma);

        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot().setLocale(this.locale);

        // Forzar recarga completa
        try {
            ExternalContext ec = context.getExternalContext();
            String viewId = context.getViewRoot().getViewId();
            String url = ec.getRequestContextPath() + viewId;
            ec.redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ⭐ Obtener nombre del idioma actual
    public String getNombreIdiomaActual() {
        if ("es".equals(idioma)) {
            return "Español";
        } else if ("en".equals(idioma)) {
            return "English";
        }
        return "Español";
    }

    // Getters y Setters

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Map<String, Locale> getIdiomasDisponibles() {
        return idiomasDisponibles;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public MenuModel getMenuIdiomas() {
        return menuIdiomas;
    }

    public void setMenuIdiomas(MenuModel menuIdiomas) {
        this.menuIdiomas = menuIdiomas;
    }
}