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
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // ✨ NUEVO MÉTODO: Evita que el filtro intercepte y valide tokens en rutas públicas o de estadísticas
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Si el frontend está llamando a estos endpoints, saltamos este filtro por completo
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/public/")
                || path.startsWith("/api/estadisticas/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Si no hay token, simplemente seguimos. SecurityConfig decidirá si la ruta requiere uno o no.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extraerCorreo(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<String> roles = jwtService.extraerRoles(token);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                for (String rol : roles) {
                    authorities.add(new SimpleGrantedAuthority(rol));
                    if (!rol.startsWith("ROLE_")) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
                    }
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Si el token falla (expirado o mal formado), limpiamos el contexto
            SecurityContextHolder.clearContext();
            System.out.println("⚠️ Intento de acceso con JWT inválido/expirado: " + e.getMessage());

            // 🔥 CORRECCIÓN ADICIONAL: Si el token está corrupto y es una ruta que requiere autenticación,
            // enviamos directamente el error 401/403 controlado para que el frontend sepa que debe desloguearse.
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"exito\":false,\"mensaje\":\"Sesión inválida o expirada.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}