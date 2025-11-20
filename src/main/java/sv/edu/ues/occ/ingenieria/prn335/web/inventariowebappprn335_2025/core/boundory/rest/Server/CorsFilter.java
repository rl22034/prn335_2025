package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.rest.Server;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filtro CORS (Cross-Origin Resource Sharing) para permitir peticiones desde aplicaciones externas.
 *
 * ¿Qué es CORS?
 * - Los navegadores bloquean peticiones JavaScript desde un origen diferente por seguridad
 * - Origen = protocolo + dominio + puerto
 * - Ejemplo: file:// intentando acceder a http://localhost:9080 = orígenes diferentes
 *
 * Este filtro agrega los headers necesarios para permitir:
 * - Peticiones desde cualquier origen (*)
 * - Métodos HTTP: GET, POST, PUT, DELETE, OPTIONS
 * - Headers personalizados en las peticiones
 *
 * @Provider → Indica que esta clase es un proveedor JAX-RS
 *             JAX-RS lo registra automáticamente y lo aplica a todas las respuestas REST
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    /**
     * Método ejecutado DESPUÉS de cada petición REST, antes de enviar la respuesta al cliente.
     * Agrega los headers CORS necesarios a la respuesta.
     *
     * @param requestContext - Contexto de la petición (no usado aquí)
     * @param responseContext - Contexto de la respuesta donde agregamos los headers
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Permitir peticiones desde cualquier origen
        // En producción, cambiar "*" por el dominio específico: "https://miapp.com"
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");

        // Permitir estos métodos HTTP
        responseContext.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // Permitir estos headers en las peticiones
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Accept, Origin");

        // Permitir credenciales (cookies, headers de autenticación)
        // NOTA: Si usas credentials=true, NO puedes usar "*" en Allow-Origin
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "false");

        // Cache de preflight requests (peticiones OPTIONS) por 1 hora
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
    }
}
