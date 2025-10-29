package core.boundory.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests para MessageHelper - Utilidad de Mensajes JSF")
public class MenssageHelperTest {

        private FacesContext facesContext;
        private UIViewRoot viewRoot;
        private ResourceBundle resourceBundle;
        private MockedStatic<FacesContext> facesContextMock;
        private MockedStatic<ResourceBundle> resourceBundleMock;

        @BeforeEach
        void setUp() {
            // Mock de FacesContext
            facesContext = mock(FacesContext.class);
            facesContextMock = mockStatic(FacesContext.class);
            facesContextMock.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            // Mock de UIViewRoot
            viewRoot = mock(UIViewRoot.class);
            when(facesContext.getViewRoot()).thenReturn(viewRoot);
            when(viewRoot.getLocale()).thenReturn(new Locale("es", "SV"));

            // Mock de ResourceBundle
            resourceBundle = mock(ResourceBundle.class);
            resourceBundleMock = mockStatic(ResourceBundle.class);
            resourceBundleMock.when(() -> ResourceBundle.getBundle(eq("crud"), any(Locale.class)))
                    .thenReturn(resourceBundle);
        }

        @AfterEach
        void tearDown() {
            if (facesContextMock != null) {
                facesContextMock.close();
            }
            if (resourceBundleMock != null) {
                resourceBundleMock.close();
            }
        }

        @Nested
        @DisplayName("getMessage - Obtener mensajes del bundle")
        class GetMessageTests {

            @Test
            @DisplayName("getMessage() debe retornar el mensaje correcto del bundle")
            void getMessage_conClaveValida_debeRetornarMensaje() {
                // Given
                String key = "mensaje.exito";
                String expectedMessage = "Operación exitosa";
                when(resourceBundle.getString(key)).thenReturn(expectedMessage);

                // When
                String resultado = MessageHelper.getMessage(key);

                // Then
                assertEquals(expectedMessage, resultado,
                        "Debe retornar el mensaje del bundle");
                verify(resourceBundle).getString(key);
            }

            @Test
            @DisplayName("getMessage() con clave inexistente debe retornar formato ???clave???")
            void getMessage_conClaveInvalida_debeRetornarFormatoError() {
                // Given
                String key = "clave.inexistente";
                when(resourceBundle.getString(key))
                        .thenThrow(new MissingResourceException("", "", key));

                // When
                String resultado = MessageHelper.getMessage(key);

                // Then
                assertEquals("???" + key + "???", resultado,
                        "Debe retornar el formato de error con la clave");
            }

            @Test
            @DisplayName("getMessage() cuando FacesContext es null debe retornar formato de error")
            void getMessage_conFacesContextNull_debeRetornarFormatoError() {
                // Given
                facesContextMock.when(FacesContext::getCurrentInstance).thenReturn(null);
                String key = "mensaje.test";

                // When
                String resultado = MessageHelper.getMessage(key);

                // Then
                assertEquals("???" + key + "???", resultado,
                        "Debe manejar el error cuando FacesContext es null");
            }

            @Test
            @DisplayName("getMessage() con parámetros debe formatear correctamente")
            void getMessage_conParametros_debeFormatearCorrectamente() {
                // Given
                String key = "mensaje.parametros";
                String template = "Usuario %s creó %d registros";
                String expectedMessage = "Usuario Juan creó 5 registros";
                when(resourceBundle.getString(key)).thenReturn(template);

                // When
                String resultado = MessageHelper.getMessage(key, "Juan", 5);

                // Then
                assertEquals(expectedMessage, resultado,
                        "Debe formatear el mensaje con los parámetros");
            }

            @Test
            @DisplayName("getMessage() con parámetros vacíos debe retornar mensaje sin formatear")
            void getMessage_sinParametros_debeRetornarMensajeSinFormatear() {
                // Given
                String key = "mensaje.simple";
                String expectedMessage = "Mensaje simple";
                when(resourceBundle.getString(key)).thenReturn(expectedMessage);

                // When
                String resultado = MessageHelper.getMessage(key);

                // Then
                assertEquals(expectedMessage, resultado,
                        "Debe retornar el mensaje sin formatear");
            }

            @Test
            @DisplayName("getMessage() con múltiples parámetros debe formatear todos")
            void getMessage_conMultiplesParametros_debeFormatearTodos() {
                // Given
                String key = "mensaje.varios";
                String template = "Producto: %s, Precio: %.2f, Stock: %d";
                when(resourceBundle.getString(key)).thenReturn(template);

                // When
                String resultado = MessageHelper.getMessage(key, "Laptop", 1500.50, 10);

                // Then
                assertTrue(resultado.contains("Laptop"),
                        "Debe contener el primer parámetro");
                assertTrue(resultado.contains("1500"),
                        "Debe contener el segundo parámetro");
                assertTrue(resultado.contains("10"),
                        "Debe contener el tercer parámetro");
            }
        }

        @Nested
        @DisplayName("addInfoMessage - Agregar mensajes informativos")
        class AddInfoMessageTests {

            @Test
            @DisplayName("addInfoMessage() debe agregar mensaje INFO correctamente")
            void addInfoMessage_debeAgregarMensajeInfo() {
                // Given
                String summaryKey = "mensaje.titulo.exito";
                String detailKey = "mensaje.crear.exito";
                when(resourceBundle.getString(summaryKey)).thenReturn("Éxito");
                when(resourceBundle.getString(detailKey)).thenReturn("Registro creado");

                // When
                MessageHelper.addInfoMessage(summaryKey, detailKey);

                // Then
                verify(facesContext).addMessage(isNull(), argThat(message ->
                        message.getSeverity() == FacesMessage.SEVERITY_INFO &&
                                message.getSummary().equals("Éxito") &&
                                message.getDetail().equals("Registro creado")
                ));
            }

            @Test
            @DisplayName("addInfoMessage() debe usar getMessage() internamente")
            void addInfoMessage_debeUsarGetMessage() {
                // Given
                String summaryKey = "titulo";
                String detailKey = "detalle";
                when(resourceBundle.getString(summaryKey)).thenReturn("Título Info");
                when(resourceBundle.getString(detailKey)).thenReturn("Detalle Info");

                // When
                MessageHelper.addInfoMessage(summaryKey, detailKey);

                // Then
                verify(resourceBundle).getString(summaryKey);
                verify(resourceBundle).getString(detailKey);
            }
        }

        @Nested
        @DisplayName("addErrorMessage - Agregar mensajes de error")
        class AddErrorMessageTests {

            @Test
            @DisplayName("addErrorMessage() debe agregar mensaje ERROR correctamente")
            void addErrorMessage_debeAgregarMensajeError() {
                // Given
                String summaryKey = "mensaje.titulo.error";
                String detailKey = "mensaje.error.crear";
                when(resourceBundle.getString(summaryKey)).thenReturn("Error");
                when(resourceBundle.getString(detailKey)).thenReturn("No se pudo crear");

                // When
                MessageHelper.addErrorMessage(summaryKey, detailKey);

                // Then
                verify(facesContext).addMessage(isNull(), argThat(message ->
                        message.getSeverity() == FacesMessage.SEVERITY_ERROR &&
                                message.getSummary().equals("Error") &&
                                message.getDetail().equals("No se pudo crear")
                ));
            }

            @Test
            @DisplayName("addErrorMessage() con detalle personalizado debe concatenar el error")
            void addErrorMessage_conDetallePersonalizado_debeConcatenar() {
                // Given
                String summaryKey = "mensaje.titulo.error";
                String detailKey = "mensaje.error.base";
                String errorDetail = "NullPointerException en línea 42";
                when(resourceBundle.getString(summaryKey)).thenReturn("Error");
                when(resourceBundle.getString(detailKey)).thenReturn("Error de sistema");

                // When
                MessageHelper.addErrorMessage(summaryKey, detailKey, errorDetail);

                // Then
                verify(facesContext).addMessage(isNull(), argThat(message ->
                        message.getSeverity() == FacesMessage.SEVERITY_ERROR &&
                                message.getSummary().equals("Error") &&
                                message.getDetail().contains("Error de sistema: " + errorDetail)
                ));
            }

            @Test
            @DisplayName("addErrorMessage() con detalle null debe manejarlo correctamente")
            void addErrorMessage_conDetalleNull_debeManejarCorrectamente() {
                // Given
                String summaryKey = "error.titulo";
                String detailKey = "error.detalle";
                when(resourceBundle.getString(summaryKey)).thenReturn("Error");
                when(resourceBundle.getString(detailKey)).thenReturn("Detalle error");

                // When
                MessageHelper.addErrorMessage(summaryKey, detailKey, null);

                // Then
                verify(facesContext).addMessage(isNull(), argThat(message ->
                        message.getSeverity() == FacesMessage.SEVERITY_ERROR &&
                                message.getDetail().contains("null")
                ));
            }

            @Test
            @DisplayName("addErrorMessage() con detalle vacío debe concatenar correctamente")
            void addErrorMessage_conDetalleVacio_debeConcatenar() {
                // Given
                String summaryKey = "error";
                String detailKey = "detalle";
                String errorDetail = "";
                when(resourceBundle.getString(summaryKey)).thenReturn("Error");
                when(resourceBundle.getString(detailKey)).thenReturn("Base");

                // When
                MessageHelper.addErrorMessage(summaryKey, detailKey, errorDetail);

                // Then
                verify(facesContext).addMessage(isNull(), argThat(message ->
                        message.getDetail().equals("Base: ")
                ));
            }
        }

        @Nested
        @DisplayName("addWarnMessage - Agregar mensajes de advertencia")
        class AddWarnMessageTests {

            @Test
            @DisplayName("addWarnMessage() debe agregar mensaje WARN correctamente")
            void addWarnMessage_debeAgregarMensajeWarn() {
                // Given
                String summaryKey = "mensaje.titulo.advertencia";
                String detailKey = "mensaje.advertencia.stock";
                when(resourceBundle.getString(summaryKey)).thenReturn("Advertencia");
                when(resourceBundle.getString(detailKey)).thenReturn("Stock bajo");

                // When
                MessageHelper.addWarnMessage(summaryKey, detailKey);

                // Then
                verify(facesContext).addMessage(isNull(), argThat(message ->
                        message.getSeverity() == FacesMessage.SEVERITY_WARN &&
                                message.getSummary().equals("Advertencia") &&
                                message.getDetail().equals("Stock bajo")
                ));
            }

            @Test
            @DisplayName("addWarnMessage() debe usar getMessage() internamente")
            void addWarnMessage_debeUsarGetMessage() {
                // Given
                String summaryKey = "warn.titulo";
                String detailKey = "warn.detalle";
                when(resourceBundle.getString(summaryKey)).thenReturn("Título Warn");
                when(resourceBundle.getString(detailKey)).thenReturn("Detalle Warn");

                // When
                MessageHelper.addWarnMessage(summaryKey, detailKey);

                // Then
                verify(resourceBundle).getString(summaryKey);
                verify(resourceBundle).getString(detailKey);
            }
        }

        @Nested
        @DisplayName("Integración - Severidades de mensajes")
        class IntegracionSeveridadTests {

            @Test
            @DisplayName("Debe distinguir correctamente entre INFO, ERROR y WARN")
            void debeDistinguirSeveridades() {
                // Given
                when(resourceBundle.getString(anyString())).thenReturn("Test");

                // When - agregar diferentes tipos de mensajes
                MessageHelper.addInfoMessage("info", "info");
                MessageHelper.addErrorMessage("error", "error");
                MessageHelper.addWarnMessage("warn", "warn");

                // Then - verificar que se agregaron 3 mensajes con diferentes severidades
                verify(facesContext, times(3)).addMessage(isNull(), any(FacesMessage.class));
            }

            @Test
            @DisplayName("Mensajes con claves inválidas deben usar formato de error")
            void mensajesConClavesInvalidas_debenUsarFormatoError() {
                // Given
                when(resourceBundle.getString(anyString()))
                        .thenThrow(new MissingResourceException("", "", ""));

                // When
                MessageHelper.addInfoMessage("invalida1", "invalida2");

                // Then
                verify(facesContext).addMessage(isNull(), argThat(message ->
                        message.getSummary().startsWith("???") &&
                                message.getDetail().startsWith("???")
                ));
            }
        }

        @Nested
        @DisplayName("Casos Edge - Manejo de errores")
        class CasosEdgeTests {

            @Test
            @DisplayName("getMessage() debe manejar excepciones generales")
            void getMessage_conExcepcionGeneral_debeManejar() {
                // Given
                String key = "test.key";
                facesContextMock.when(FacesContext::getCurrentInstance)
                        .thenThrow(new RuntimeException("Error simulado"));

                // When
                String resultado = MessageHelper.getMessage(key);

                // Then
                assertEquals("???" + key + "???", resultado,
                        "Debe retornar formato de error ante cualquier excepción");
            }

                @Test
                @DisplayName("addInfoMessage() con FacesContext null debe lanzar NullPointerException")
                void addInfoMessage_conFacesContextNull_debeLanzarNPE() {
                    // Given
                    facesContextMock.when(FacesContext::getCurrentInstance).thenReturn(null);

                    // When/Then
                    assertThrows(NullPointerException.class, () ->
                                    MessageHelper.addInfoMessage("test", "test"),
                            "Debe lanzar NullPointerException cuando FacesContext es null"
                    );
                }
            }

            @Test
            @DisplayName("getMessage() con caracteres especiales en la clave")
            void getMessage_conCaracteresEspeciales_debeManejar() {
                // Given
                String key = "mensaje.con.puntos.y_guiones";
                String expected = "Mensaje especial";
                when(resourceBundle.getString(key)).thenReturn(expected);

                // When
                String resultado = MessageHelper.getMessage(key);

                // Then
                assertEquals(expected, resultado,
                        "Debe manejar claves con caracteres especiales");
            }
        }
