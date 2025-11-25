package core.boundory.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.filter.CharacterEncodingFilter;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class CharacterEncodingFilterTest {

    @Mock
    // Mocks de las dependencias del Servlet API
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private FilterConfig mockFilterConfig;

    @InjectMocks
    // La clase que vamos a probar
    private CharacterEncodingFilter filter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Test doFilter (Lógica Central) ---

    @Test
    void testDoFilter_SetsCharacterEncodingAndContinuesChain() throws IOException, ServletException {

        // Act
        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Assert

        // 1. Verificar que se forzó la codificación UTF-8 en la petición
        verify(mockRequest, times(1)).setCharacterEncoding("UTF-8");

        // 2. Verificar que se forzó la codificación UTF-8 en la respuesta
        verify(mockResponse, times(1)).setCharacterEncoding("UTF-8");

        // 3. Verificar que se llamó a chain.doFilter (continuación de la cadena)
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);

        // Cobertura: 100% del método doFilter
    }

    // --- 2. Test init ---

    @Test
    void testInit() throws ServletException {
        // Act
        filter.init(mockFilterConfig);

        // Assert: No hay lógica, solo se verifica que se puede llamar sin errores.
        // Cobertura: 100% del método init
    }

    // --- 3. Test destroy ---

    @Test
    void testDestroy() {
        // Act
        filter.destroy();

        // Assert: No hay lógica, solo se verifica que se puede llamar sin errores.
        // Cobertura: 100% del método destroy
    }
}
