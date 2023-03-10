package project.toy.api.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import project.toy.api.config.security.data.CustomMemberDetails;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private final String secretKey;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public JwtTokenProvider(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds) {
        this.secretKey = secretKey;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String createToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomMemberDetails principal = (CustomMemberDetails) authentication.getPrincipal();

        Date now = new Date();

        return Jwts.builder()
                .setSubject(String.valueOf(principal.getId()))
                .claim("name", principal.getName())
//                .claim("email", principal.getEmail())
//                .claim("password", principal.getPassword())
                .claim("roleType", principal.getRoleType())
                .signWith(key)
                .setIssuedAt(now) // 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidityInMilliseconds)) // 만료일자
                .compact();
    }

    // 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roleType").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        CustomMemberDetails principal = CustomMemberDetails.builder()
                .id(Long.parseLong(claims.getSubject()))
                .name(String.valueOf(claims.get("name")))
//                .email(String.valueOf(claims.get("email")))
//                .password(String.valueOf(claims.get("password")))
                .roleType(String.valueOf(claims.get("roleType")))
                .roleType(authorities.toString())
                .build();
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // token 유효성 검증
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}

