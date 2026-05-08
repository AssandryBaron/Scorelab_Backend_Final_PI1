package co.escorelab.scorelabbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Llave secreta para firmar los tokens
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Tiempo de expiración: 1 día (en milisegundos)
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Genera el token incluyendo el email como subject y el rol como un claim extra.
     */
    public String generarToken(String email, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", role); // Guardamos el rol bajo la llave "rol"

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    /**
     * Extrae el email (subject) del token.
     */
    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * 🌟 MÉTODO CORREGIDO: Extrae los roles como una lista.
     * Esto es lo que invoca tu JwtFilter.
     */
    public List<String> extraerRoles(String token) {
        final Claims claims = extraerTodosLosClaims(token);
        String rol = claims.get("rol", String.class);

        // Si el rol existe, lo devuelve en una lista (Spring Security prefiere listas de permisos)
        return (rol != null) ? List.of(rol) : List.of();
    }

    /**
     * Método genérico para extraer cualquier claim.
     */
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parsea el token y devuelve todos los datos (claims).
     */
    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}