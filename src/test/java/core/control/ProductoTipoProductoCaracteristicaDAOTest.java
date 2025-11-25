package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.ProductoTipoProductoCaracteristica;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoTipoProductoCaracteristicaDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<ProductoTipoProductoCaracteristica> typedQueryPTPC;

    @Mock
    private TypedQuery<Long> typedQueryLong;

    @Mock
    private Query query;

    private ProductoTipoProductoCaracteristicaDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        dao = new ProductoTipoProductoCaracteristicaDAO();

        Field emField = ProductoTipoProductoCaracteristicaDAO.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(dao, entityManager);
    }

    private ProductoTipoProductoCaracteristica crearPTPC(UUID id) {
        ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
        ptpc.setId(id);
        ptpc.setValor("Valor Test");
        return ptpc;
    }

    // ========== Tests para getEntityManager ==========
    @Test
    void testGetEntityManager() {
        assertEquals(entityManager, dao.getEntityManager());
    }

    // ========== Tests para constructor ==========
    @Test
    void testConstructor() {
        ProductoTipoProductoCaracteristicaDAO newDao = new ProductoTipoProductoCaracteristicaDAO();
        assertNotNull(newDao);
        assertEquals(ProductoTipoProductoCaracteristica.class, newDao.getEntityClass());
    }

    // ========== Tests para eliminarPorProductoTipoProducto ==========
    @Test
    void testEliminarPorProductoTipoProductoExitoso() {
        UUID idProductoTipoProducto = UUID.randomUUID();

        when(entityManager.createNamedQuery("ProductoTipoProductoCaracteristica.eliminarPorProductoTipoProducto"))
                .thenReturn(query);
        when(query.setParameter("idProductoTipoProducto", idProductoTipoProducto)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(3);

        assertDoesNotThrow(() -> dao.eliminarPorProductoTipoProducto(idProductoTipoProducto));

        verify(entityManager).flush();
        verify(entityManager).clear();
    }

    @Test
    void testEliminarPorProductoTipoProductoCero() {
        UUID idProductoTipoProducto = UUID.randomUUID();

        when(entityManager.createNamedQuery("ProductoTipoProductoCaracteristica.eliminarPorProductoTipoProducto"))
                .thenReturn(query);
        when(query.setParameter("idProductoTipoProducto", idProductoTipoProducto)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        assertDoesNotThrow(() -> dao.eliminarPorProductoTipoProducto(idProductoTipoProducto));

        verify(entityManager).flush();
        verify(entityManager).clear();
    }

    @Test
    void testEliminarPorProductoTipoProductoConException() {
        UUID idProductoTipoProducto = UUID.randomUUID();

        when(entityManager.createNamedQuery("ProductoTipoProductoCaracteristica.eliminarPorProductoTipoProducto"))
                .thenThrow(new RuntimeException("Error de BD"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dao.eliminarPorProductoTipoProducto(idProductoTipoProducto));

        assertTrue(exception.getMessage().contains("Error al eliminar características"));
    }

    @Test
    void testEliminarPorProductoTipoProductoConExceptionEnExecute() {
        UUID idProductoTipoProducto = UUID.randomUUID();

        when(entityManager.createNamedQuery("ProductoTipoProductoCaracteristica.eliminarPorProductoTipoProducto"))
                .thenReturn(query);
        when(query.setParameter("idProductoTipoProducto", idProductoTipoProducto)).thenReturn(query);
        when(query.executeUpdate()).thenThrow(new RuntimeException("Error al ejecutar"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dao.eliminarPorProductoTipoProducto(idProductoTipoProducto));

        assertTrue(exception.getMessage().contains("Error al eliminar características"));
    }

    // ========== Tests para findByProductoTipoProducto ==========
    @Test
    void testFindByProductoTipoProductoExitoso() {
        UUID idProductoTipoProducto = UUID.randomUUID();
        List<ProductoTipoProductoCaracteristica> lista = Arrays.asList(
                crearPTPC(UUID.randomUUID()),
                crearPTPC(UUID.randomUUID())
        );

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.findByProductoTipoProducto",
                ProductoTipoProductoCaracteristica.class))
                .thenReturn(typedQueryPTPC);
        when(typedQueryPTPC.setParameter("idProductoTipoProducto", idProductoTipoProducto))
                .thenReturn(typedQueryPTPC);
        when(typedQueryPTPC.getResultList()).thenReturn(lista);

        List<ProductoTipoProductoCaracteristica> resultado =
                dao.findByProductoTipoProducto(idProductoTipoProducto);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void testFindByProductoTipoProductoVacio() {
        UUID idProductoTipoProducto = UUID.randomUUID();

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.findByProductoTipoProducto",
                ProductoTipoProductoCaracteristica.class))
                .thenReturn(typedQueryPTPC);
        when(typedQueryPTPC.setParameter("idProductoTipoProducto", idProductoTipoProducto))
                .thenReturn(typedQueryPTPC);
        when(typedQueryPTPC.getResultList()).thenReturn(List.of());

        List<ProductoTipoProductoCaracteristica> resultado =
                dao.findByProductoTipoProducto(idProductoTipoProducto);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByProductoTipoProductoConException() {
        UUID idProductoTipoProducto = UUID.randomUUID();

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.findByProductoTipoProducto",
                ProductoTipoProductoCaracteristica.class))
                .thenThrow(new RuntimeException("Error de BD"));

        List<ProductoTipoProductoCaracteristica> resultado =
                dao.findByProductoTipoProducto(idProductoTipoProducto);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByProductoTipoProductoConExceptionEnGetResultList() {
        UUID idProductoTipoProducto = UUID.randomUUID();

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.findByProductoTipoProducto",
                ProductoTipoProductoCaracteristica.class))
                .thenReturn(typedQueryPTPC);
        when(typedQueryPTPC.setParameter("idProductoTipoProducto", idProductoTipoProducto))
                .thenReturn(typedQueryPTPC);
        when(typedQueryPTPC.getResultList()).thenThrow(new RuntimeException("Error"));

        List<ProductoTipoProductoCaracteristica> resultado =
                dao.findByProductoTipoProducto(idProductoTipoProducto);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ========== Tests para existeCaracteristica ==========
    @Test
    void testExisteCaracteristicaTrue() {
        UUID idProductoTipoProducto = UUID.randomUUID();
        Long idTipoProductoCaracteristica = 1L;

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.existeCaracteristica",
                Long.class))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("idProductoTipoProducto", idProductoTipoProducto))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("idTipoProductoCaracteristica", idTipoProductoCaracteristica))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(1L);

        boolean resultado = dao.existeCaracteristica(idProductoTipoProducto, idTipoProductoCaracteristica);

        assertTrue(resultado);
    }

    @Test
    void testExisteCaracteristicaFalse() {
        UUID idProductoTipoProducto = UUID.randomUUID();
        Long idTipoProductoCaracteristica = 1L;

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.existeCaracteristica",
                Long.class))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("idProductoTipoProducto", idProductoTipoProducto))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("idTipoProductoCaracteristica", idTipoProductoCaracteristica))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(0L);

        boolean resultado = dao.existeCaracteristica(idProductoTipoProducto, idTipoProductoCaracteristica);

        assertFalse(resultado);
    }

    @Test
    void testExisteCaracteristicaConException() {
        UUID idProductoTipoProducto = UUID.randomUUID();
        Long idTipoProductoCaracteristica = 1L;

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.existeCaracteristica",
                Long.class))
                .thenThrow(new RuntimeException("Error de BD"));

        boolean resultado = dao.existeCaracteristica(idProductoTipoProducto, idTipoProductoCaracteristica);

        assertFalse(resultado);
    }

    @Test
    void testExisteCaracteristicaConExceptionEnGetSingleResult() {
        UUID idProductoTipoProducto = UUID.randomUUID();
        Long idTipoProductoCaracteristica = 1L;

        when(entityManager.createNamedQuery(
                "ProductoTipoProductoCaracteristica.existeCaracteristica",
                Long.class))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("idProductoTipoProducto", idProductoTipoProducto))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("idTipoProductoCaracteristica", idTipoProductoCaracteristica))
                .thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenThrow(new RuntimeException("Error"));

        boolean resultado = dao.existeCaracteristica(idProductoTipoProducto, idTipoProductoCaracteristica);

        assertFalse(resultado);
    }
}