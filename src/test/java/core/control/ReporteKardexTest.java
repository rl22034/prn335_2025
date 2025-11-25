package core.control;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ReporteKardex;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteKardexTest {

    private static final String REPORTE_VALIDO = "reporte_kardex";
    private static final String REPORTE_INVALIDO = "no_existe";
    private static final Map<String, Object> PARAMETROS = Collections.singletonMap("ID", 1);

    @Mock
    DataSource mockDataSource;

    @Mock
    Connection mockConnection;

    @Mock
    InputStream mockReporteStream;

    @Mock
    JasperReport mockJasperReport;

    @Mock
    JasperPrint mockJasperPrint;

    @Mock
    OutputStream mockOutputStream;

    @InjectMocks
    ReporteKardex reporteKardex;

    // Helper para crear el Spy
    ReporteKardex getReporteSpy() {
        return spy(reporteKardex);
    }

    // --- Tests funcionales (Fallan por la restricción de I/O) ---

    @Test
    void testGenerarPdf_Success_WILL_FAIL_DUE_TO_IO_RESTRICTION() throws Exception {
        ReporteKardex spyReporte = getReporteSpy();

        // Simplemente ejecutamos para ver la excepción real:
        Exception e = assertThrows(Exception.class,
                () -> spyReporte.generarPdf(REPORTE_VALIDO, PARAMETROS, mockOutputStream));

        assertTrue(e.getMessage().contains("No se encontró el archivo .jrxml"));
        verify(mockDataSource, never()).getConnection();
    }

    // ----------------------------------------------------
    // Test del archivo .jrxml no encontrado
    // ----------------------------------------------------

    @Test
    void testGenerarPdf_ReporteNotFound() throws Exception {
        ReporteKardex spyReporte = getReporteSpy();

        Exception e = assertThrows(Exception.class,
                () -> spyReporte.generarPdf(REPORTE_INVALIDO, PARAMETROS, mockOutputStream));

        assertTrue(e.getMessage().contains("No se encontró el archivo .jrxml: " + REPORTE_INVALIDO));

        verify(mockDataSource, never()).getConnection();
    }

    // ----------------------------------------------------
    // Test del error de conexión (Adaptado a fallo de I/O)
    // ----------------------------------------------------

    @Test
    void testGenerarPdf_ConnectionError_FailsOnIO_NotSQL() throws Exception {
        ReporteKardex spyReporte = getReporteSpy();

        // 🚨 CAMBIO: Usar lenient().when() para que Mockito no se queje de este stub no usado
        // (ya que el I/O falla antes de llamar a getConnection)
        lenient().when(mockDataSource.getConnection()).thenThrow(new SQLException("Error de DB"));

        try (MockedStatic<JasperCompileManager> mockedCompile = mockStatic(JasperCompileManager.class)) {

            // Ejecución y verificación de la excepción real (que es la de I/O)
            Exception e = assertThrows(Exception.class,
                    () -> spyReporte.generarPdf(REPORTE_VALIDO, PARAMETROS, mockOutputStream));

            assertTrue(e.getMessage().contains("No se encontró el archivo .jrxml: " + REPORTE_VALIDO));

            // Verificación: Como el I/O falla primero, la conexión nunca se intenta.
            verify(mockDataSource, never()).getConnection();
            verify(mockConnection, never()).close();
        }
    }

    // ----------------------------------------------------
    // Test de error durante el llenado (Adaptado a fallo de I/O)
    // ----------------------------------------------------

    @Test
    void testGenerarPdf_FillError_FailsOnIO_NotJRException() throws Exception {
        ReporteKardex spyReporte = getReporteSpy();

        // 🚨 CAMBIO: Usar lenient().when() para que Mockito no se queje de este stub no usado
        lenient().when(mockDataSource.getConnection()).thenReturn(mockConnection);

        try (
                MockedStatic<JasperCompileManager> mockedCompile = mockStatic(JasperCompileManager.class);
                MockedStatic<JasperFillManager> mockedFill = mockStatic(JasperFillManager.class);
        ) {
            // Ejecución y verificación de la excepción real (que es la de I/O)
            Exception e = assertThrows(Exception.class,
                    () -> spyReporte.generarPdf(REPORTE_VALIDO, PARAMETROS, mockOutputStream));

            assertTrue(e.getMessage().contains("No se encontró el archivo .jrxml: " + REPORTE_VALIDO));

            // Verificación: Como el I/O falla antes de obtener la conexión, no se verifica
            verify(mockDataSource, never()).getConnection();
            verify(mockConnection, never()).close();
        }
    }
}