package core.boundory.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.menu.MenuModel;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.LocaleBean;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocaleBeanTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private Application application;

    @Mock
    private UIViewRoot viewRoot;

    @Mock
    private ExternalContext externalContext;

    @InjectMocks
    private LocaleBean localeBean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(facesContext.getApplication()).thenReturn(application);
    }

    @Test
    void testInitConLocaleDefault() {
        Locale localeDefault = new Locale("en");
        when(application.getDefaultLocale()).thenReturn(localeDefault);

        localeBean.init();

        assertEquals("en", localeBean.getIdioma());
        assertEquals(localeDefault, localeBean.getLocale());
        assertNotNull(localeBean.getIdiomasDisponibles());
        assertEquals(2, localeBean.getIdiomasDisponibles().size());
        assertNotNull(localeBean.getMenuIdiomas());
    }

    @Test
    void testInitConLocaleNull() {
        when(application.getDefaultLocale()).thenReturn(null);

        localeBean.init();

        assertEquals("es", localeBean.getIdioma());
        assertEquals(new Locale("es"), localeBean.getLocale());
    }

    @Test
    void testIdiomasDisponibles() {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));

        localeBean.init();

        Map<String, Locale> idiomas = localeBean.getIdiomasDisponibles();
        assertTrue(idiomas.containsKey("English"));
        assertTrue(idiomas.containsKey("Español"));
        assertEquals(new Locale("en"), idiomas.get("English"));
        assertEquals(new Locale("es"), idiomas.get("Español"));
    }

    @Test
    void testMenuIdiomasInicializado() {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));

        localeBean.init();

        MenuModel menu = localeBean.getMenuIdiomas();
        assertNotNull(menu);
        assertEquals(2, menu.getElements().size());
    }

    @Test
    void testCambiarIdiomaExitoso() throws IOException {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));
        localeBean.init();

        try (MockedStatic<FacesContext> fcMock = mockStatic(FacesContext.class)) {
            fcMock.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getViewRoot()).thenReturn(viewRoot);
            when(facesContext.getExternalContext()).thenReturn(externalContext);
            when(viewRoot.getViewId()).thenReturn("/index.xhtml");
            when(externalContext.getRequestContextPath()).thenReturn("/app");

            localeBean.cambiarIdioma("en");

            assertEquals("en", localeBean.getIdioma());
            assertEquals(new Locale("en"), localeBean.getLocale());
            verify(viewRoot).setLocale(new Locale("en"));
            verify(externalContext).redirect("/app/index.xhtml");
        }
    }

    @Test
    void testCambiarIdiomaConIOException() throws IOException {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));
        localeBean.init();

        try (MockedStatic<FacesContext> fcMock = mockStatic(FacesContext.class)) {
            fcMock.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getViewRoot()).thenReturn(viewRoot);
            when(facesContext.getExternalContext()).thenReturn(externalContext);
            when(viewRoot.getViewId()).thenReturn("/index.xhtml");
            when(externalContext.getRequestContextPath()).thenReturn("/app");
            doThrow(new IOException("Error de redirección")).when(externalContext).redirect(anyString());

            // No debe lanzar excepción, solo imprime el stack trace
            assertDoesNotThrow(() -> localeBean.cambiarIdioma("en"));

            assertEquals("en", localeBean.getIdioma());
        }
    }

    @Test
    void testGetNombreIdiomaActualEspanol() {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));
        localeBean.init();

        localeBean.setIdioma("es");
        assertEquals("Español", localeBean.getNombreIdiomaActual());
    }

    @Test
    void testGetNombreIdiomaActualIngles() {
        when(application.getDefaultLocale()).thenReturn(new Locale("en"));
        localeBean.init();

        localeBean.setIdioma("en");
        assertEquals("English", localeBean.getNombreIdiomaActual());
    }

    @Test
    void testGetNombreIdiomaActualOtro() {
        when(application.getDefaultLocale()).thenReturn(new Locale("fr"));
        localeBean.init();

        localeBean.setIdioma("fr");
        assertEquals("Español", localeBean.getNombreIdiomaActual());
    }

    @Test
    void testGetNombreIdiomaActualNull() {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));
        localeBean.init();

        localeBean.setIdioma(null);
        assertEquals("Español", localeBean.getNombreIdiomaActual());
    }

    @Test
    void testSettersYGetters() {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));
        localeBean.init();

        // Test setIdioma/getIdioma
        localeBean.setIdioma("fr");
        assertEquals("fr", localeBean.getIdioma());

        // Test setLocale/getLocale
        Locale nuevoLocale = new Locale("de");
        localeBean.setLocale(nuevoLocale);
        assertEquals(nuevoLocale, localeBean.getLocale());

        // Test setMenuIdiomas/getMenuIdiomas
        MenuModel nuevoMenu = mock(MenuModel.class);
        localeBean.setMenuIdiomas(nuevoMenu);
        assertEquals(nuevoMenu, localeBean.getMenuIdiomas());
    }

    @Test
    void testGetIdiomasDisponibles() {
        when(application.getDefaultLocale()).thenReturn(new Locale("es"));
        localeBean.init();

        Map<String, Locale> idiomas = localeBean.getIdiomasDisponibles();
        assertNotNull(idiomas);
        assertEquals(2, idiomas.size());
    }
}
