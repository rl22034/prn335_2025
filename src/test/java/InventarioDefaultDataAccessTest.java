import com.sun.nio.sctp.IllegalReceiveException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioDefaultDataAccessTest {

    @Mock
    private EntityManager mockEntityManager;

    private InventarioDefaultDataAccess<TipoAlmacen> inventarioDAO;

    private List<TipoAlmacen> listaPrueba;
    @BeforeEach
    void setUp() {
        // Crear implementación de la clase abstracta
        inventarioDAO = new InventarioDefaultDataAccess<TipoAlmacen>(TipoAlmacen.class) {
            @Override
            public EntityManager getEntityManager() {
                return mockEntityManager;
            }
        };

        // Lista de datos de práctica para los métodos

        listaPrueba = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            TipoAlmacen ta = new TipoAlmacen();
            ta.setId(i);
            ta.setNombre("Almacen" + i);
            ta.setActivo(true);
            listaPrueba.add(ta);
        }
    }

    // ==================== TESTS PARA CREAR ====================

    // ==================== TESTS DEL INGENIERO ====================
    @Test
    public void crear(){

        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("Almacen");

        assertThrows(IllegalArgumentException.class, () -> {
            inventarioDAO.crear(null);
        });

        InventarioDefaultDataAccess<TipoAlmacen> inventarioNull = new InventarioDefaultDataAccess<TipoAlmacen>(TipoAlmacen.class) {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
        assertThrows(IllegalStateException.class, ()->{
            inventarioNull.crear(tipoAlmacen);
        });

        doNothing().when(mockEntityManager).persist(any(TipoAlmacen.class));
        inventarioDAO.crear(tipoAlmacen);
        verify(mockEntityManager, times(1)).persist(any(TipoAlmacen.class));

        doThrow(new RuntimeException()).when(mockEntityManager).persist(any(TipoAlmacen.class));
        assertThrows(RuntimeException.class, ()->{
            inventarioDAO.crear(tipoAlmacen);
        });


        verify(mockEntityManager, times(2)).persist(any(TipoAlmacen.class));
    }

 /*
    @Test
    public void crearPersistemConExito() throws Exception {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();

        inventarioDAO.crear(tipoAlmacen);

        verify(mockEntityManager, times(1)).persist(tipoAlmacen);
        System.out.println("Verificación exitosa: 'persist' fue llamado.");
    }

    @Test
    public void crearConRegistroNulo() throws Exception {
        try {
            inventarioDAO.crear(null);
            fail("Deberia haber lanzado una excepción");
        } catch (IllegalArgumentException e) {
            assertEquals("El registro no puede ser nulo", e.getMessage());
        }
        verify(mockEntityManager, never()).persist(any());
    }

    @Test
    public void crearFalloEnPersistencia() throws Exception {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();

        doThrow(new RuntimeException()).when(mockEntityManager).persist(any());

        try {
            inventarioDAO.crear(tipoAlmacen);
            fail("No deberia haberse creado");
        } catch (RuntimeException e) {
            assertEquals("No se puede crear la entidad de registro", e.getMessage());
        }
        verify(mockEntityManager, times(1)).persist(tipoAlmacen);

    }

    @Test
    public void crearEntityManagerNulo() throws Exception {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();

        InventarioDefaultDataAccess<TipoAlmacen> tipoAlmacenNull =
                new InventarioDefaultDataAccess<TipoAlmacen>(TipoAlmacen.class) {
                    @Override
                    public EntityManager getEntityManager() {
                        return null;
                    }
                };

        assertThrows(IllegalStateException.class, () -> {
            tipoAlmacenNull.crear(tipoAlmacen);
        });

        verify(mockEntityManager, never()).persist(any());
    }
*/
    // ==================== TESTS PARA BUSCAR POR ID ====================

    // ==================== TEST DEL INGENIERO ==========================
    @Test
    public void finById(){
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("Almacen1");

        assertThrows(IllegalArgumentException.class, () -> {
            inventarioDAO.finById(null);
        });

        InventarioDefaultDataAccess<TipoAlmacen> inventarioNull = new InventarioDefaultDataAccess<>(TipoAlmacen.class) {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
        assertThrows(IllegalStateException.class, () -> {
            inventarioNull.finById(tipoAlmacen);
        });

        // ========= CASO EXITOSO =========
        when(mockEntityManager.find(any(), any())).thenReturn(tipoAlmacen);
        TipoAlmacen resultado = inventarioDAO.finById(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Almacen1", resultado.getNombre());
        verify(mockEntityManager, times(1)).find(eq(TipoAlmacen.class), eq(1));

        // ========= CASO DE ERROR =========
        doThrow(new RuntimeException()).when(mockEntityManager).find(any(), any());
        RuntimeException thrown= assertThrows(RuntimeException.class, ()->{
            inventarioDAO.finById(1);
        });

        assertEquals("Error al obtener la entidad", thrown.getMessage());
        verify(mockEntityManager, times(2)).find(eq(TipoAlmacen.class), eq(1));

    }
/*
    @Test
    public void buscarPorIdConExito() throws Exception {
        TipoAlmacen registro = new TipoAlmacen();
        registro.setId(1);
        registro.setNombre("almacenP");

        when(mockEntityManager.find(any(), any())).thenReturn(registro);

        TipoAlmacen resultado = inventarioDAO.finById(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("almacenP", resultado.getNombre());

        verify(mockEntityManager, times(1)).find(eq(TipoAlmacen.class), eq(1));
    }

    @Test
    public void buscarPorIdNulo() {
        try {
            inventarioDAO.finById(null);
            fail("Algo salio mal");
        } catch (IllegalArgumentException e) {
        }
        verify(mockEntityManager, never()).find(any(), any());
    }

    @Test
    public void buscarPorIdEntityNull() {
        InventarioDefaultDataAccess<TipoAlmacen> tipoAlmacenNull =
                new InventarioDefaultDataAccess<TipoAlmacen>(TipoAlmacen.class) {
                    @Override
                    public EntityManager getEntityManager() {
                        return null;
                    }
                };

        int idPrueba = 1;

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            tipoAlmacenNull.finById(idPrueba);
        });

    }

    @Test
    public void buscarPorIdNoEncontrado() {
        doThrow(new RuntimeException()).when(mockEntityManager).find(any(), any());

        int idPrueba = 1;
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventarioDAO.finById(idPrueba);
        });

    }
*/
    // ==================== TESTS PARA ACTUALIZAR ====================

    @Test
    public void update (){
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("almacenP");

        InventarioDefaultDataAccess<TipoAlmacen> inventarioDaoNull = new InventarioDefaultDataAccess<>(TipoAlmacen.class) {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
        assertThrows(IllegalArgumentException.class, ()->{
            inventarioDAO.update(null);
        });

        assertThrows(IllegalStateException.class, ()->{
            inventarioDaoNull.update(tipoAlmacen);
        });

        when(mockEntityManager.merge(any(TipoAlmacen.class))).thenReturn(tipoAlmacen);
        TipoAlmacen resultado = inventarioDAO.update(tipoAlmacen);
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("almacenP", resultado.getNombre());
        verify(mockEntityManager, times(1)).merge(tipoAlmacen);

        doThrow(new RuntimeException()).when(mockEntityManager).merge(any(TipoAlmacen.class));
        RuntimeException thrown = assertThrows(RuntimeException.class, ()->{
            inventarioDAO.update(tipoAlmacen);
        });

        assertEquals("Error al actualizar la entidad: null", thrown.getMessage());

        verify(mockEntityManager, times(2)).merge(tipoAlmacen);

    }
/*

    @Test
    public void actualizarConExito() {
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("almacen1");

        when(mockEntityManager.merge(any(TipoAlmacen.class))).thenReturn(tipoAlmacen);

        TipoAlmacen resultado = inventarioDAO.update(tipoAlmacen);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("almacen1", resultado.getNombre());

        verify(mockEntityManager, times(1)).merge(eq(tipoAlmacen));
    }

    @Test
    public void actualizarEntidadNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            inventarioDAO.update(null);
        });

        verify(mockEntityManager, never()).merge(any());
    }

    @Test
    public void actualizarEntityNull() {
        InventarioDefaultDataAccess<TipoAlmacen> tipoAlmacenDaoNull =
                new InventarioDefaultDataAccess<>(TipoAlmacen.class) {
                    @Override
                    public EntityManager getEntityManager() {
                        return null;
                    }
                };

        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("almacen1");

        assertThrows(IllegalStateException.class, () -> {
            tipoAlmacenDaoNull.update(tipoAlmacen);
        });
    }

    @Test
    public void actualizarFallo() {
        doThrow(new RuntimeException()).when(mockEntityManager).merge(any());

        TipoAlmacen tipoAlmacen = new TipoAlmacen();

        assertThrows(RuntimeException.class, () -> {
            inventarioDAO.update(tipoAlmacen);
        });
    }
*/
    // ==================== TESTS PARA DELETE ====================

    @Test
    public void delete(){
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setId(1);
        tipoAlmacen.setNombre("almacen1");
        InventarioDefaultDataAccess<TipoAlmacen> inventarioDaoNull = new InventarioDefaultDataAccess<>(TipoAlmacen.class) {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };

        assertThrows(IllegalArgumentException.class,()->{
            inventarioDAO.delete(null);
        });

        assertThrows(IllegalStateException.class,()->{
            inventarioDaoNull.delete(tipoAlmacen);
        });

        // ==================== EXITO ====================
        when(mockEntityManager.contains(any(TipoAlmacen.class))).thenReturn(true);
        inventarioDAO.delete(tipoAlmacen);
        verify(mockEntityManager, times(1)).remove(eq(tipoAlmacen));

        when(mockEntityManager.contains(any(TipoAlmacen.class))).thenReturn(false);
        when(mockEntityManager.merge(any(TipoAlmacen.class))).thenReturn(tipoAlmacen);
        inventarioDAO.delete(tipoAlmacen);
        verify(mockEntityManager, times(1)).merge(eq(tipoAlmacen));
        verify(mockEntityManager, times(2)).remove(eq(tipoAlmacen));

        // ==================== ERROR ====================
        doThrow(new RuntimeException()).when(mockEntityManager).remove(any());

        RuntimeException thrown = assertThrows(RuntimeException.class,()->{
            inventarioDAO.delete(tipoAlmacen);
        });

        assertEquals("Error al eliminar entidad: null", thrown.getMessage());

        verify(mockEntityManager, times(3)).remove(eq(tipoAlmacen));
    }

    // ==================== TESTS PARA FIND RANGE ====================


    @Test
    public void findRange(){
        TipoAlmacen tipoAlmacen = new TipoAlmacen();
        int first = 1;
        int max = 10;
        assertThrows(IllegalArgumentException.class, ()->{
            inventarioDAO.findRange(-1,10);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            inventarioDAO.findRange(10,-1);
        });

        InventarioDefaultDataAccess<TipoAlmacen> inventarioDaoNull = new InventarioDefaultDataAccess<>(TipoAlmacen.class) {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
        assertThrows(IllegalStateException.class, ()->{
            inventarioDaoNull.findRange(first,max);
        });

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery mockCriteriaQuery = mock(CriteriaQuery.class);
        Root mockRoot = mock(Root.class);
        TypedQuery mockTypedQuery = mock(TypedQuery.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(TipoAlmacen.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(TipoAlmacen.class)).thenReturn(mockRoot);
        when(mockCriteriaQuery.select(mockRoot)).thenReturn(mockCriteriaQuery);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(listaPrueba);

        List<TipoAlmacen> resultado = inventarioDAO.findRange(0,10);

        assertNotNull(resultado);
        assertEquals(listaPrueba.size(), resultado.size());

        when(mockTypedQuery.getResultList()).thenThrow(new RuntimeException("Error simulado de la base de datos"));


        assertThrows(RuntimeException.class, () -> {
            inventarioDAO.findRange(0, 10);
        });


    }

    // ==================== TESTS PARA COUNT ====================

    @Test
    public void count(){
        InventarioDefaultDataAccess<TipoAlmacen> inventarioDaoNull = new InventarioDefaultDataAccess<>(TipoAlmacen.class) {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
        assertThrows(IllegalStateException.class, ()->{
            inventarioDaoNull.count();
        });

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> mockCriteriaQuery = mock(CriteriaQuery.class);
        Root mockRoot = mock(Root.class);
        TypedQuery mockTypedQuery = mock(TypedQuery.class);
        Expression mockExpression = mock(Expression.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Long.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(TipoAlmacen.class)).thenReturn(mockRoot);
        when(mockCriteriaBuilder.count(mockRoot)).thenReturn(mockExpression);
        when(mockCriteriaQuery.select(mockExpression)).thenReturn(mockCriteriaQuery);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);

        Long conteoEsperado = 5L;
        when(mockTypedQuery.getSingleResult()).thenReturn(conteoEsperado);


        Long conteoActual = inventarioDAO.count();


        assertNotNull(conteoActual);
        assertEquals(conteoEsperado, conteoActual);

        when(mockTypedQuery.getSingleResult()).thenThrow(new RuntimeException());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventarioDAO.count();
        });
        assertEquals("Error en los parámetros de la consulta: null", thrown.getMessage());
    }

}