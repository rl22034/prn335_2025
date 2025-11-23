package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

@Stateless
public class ReporteKardex {

    @Resource(lookup = "jdbc/pgdb")
    private DataSource dataSource;

    public void generarPdf(String nombreReporte, Map<String, Object> parametros, OutputStream salida) throws Exception {
        // 1. Cargar el archivo fuente .jrxml (NO el .jasper)
        // Asegúrate de copiar el .jrxml a la carpeta resources/reports/
        InputStream reporteStream = getClass().getClassLoader().getResourceAsStream("reports/" + nombreReporte + ".jrxml");

        if (reporteStream == null) {
            throw new Exception("No se encontró el archivo .jrxml: " + nombreReporte);
        }

        try (Connection conexion = dataSource.getConnection()) {
            // 2. COMPILAR EL REPORTE EN TIEMPO DE EJECUCIÓN
            // Esto elimina el error de versión porque usa la librería actual del proyecto
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

            // 3. Llenar el reporte
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parametros, conexion);

            // 4. Exportar a PDF
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
            exporter.exportReport();
        }
    }
}