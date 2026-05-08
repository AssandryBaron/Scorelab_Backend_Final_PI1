package co.escorelab.scorelabbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener el encabezado "Authorization"
        String authHeader = request.getHeader("Authorization");

        // 2. Si no trae Token o no empieza por "Bearer ", ignoramos y seguimos
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token puro
        String token = authHeader.substring(7);

        try {
            // 4. Extraer el correo del token
            String email = jwtService.extraerCorreo(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 🌟 CORRECCIÓN CLAVE: Extraer los roles del Token
                // Usamos el nuevo método de jwtService para obtener la lista de roles
                List<String> roles = jwtService.extraerRoles(token);

                // Convertimos esos roles (Strings) en objetos que Spring Security entiende (Authorities)
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // 5. Creamos el Token de Autenticación incluyendo las autoridades (roles)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Establecemos los detalles de la solicitud web (IP, sesión, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Guardamos al usuario en el contexto con sus permisos reales
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("✅ Usuario autenticado: " + email + " con roles: " + roles);
            }
        } catch (Exception e) {
            // Si el token es inválido o expiró
            System.out.println("❌ Error validando el token en JwtFilter: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}