package kr.or.futur.futurcertification.config.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;

    @Value("${springboot.jwt.secret}")
    private String secretKey;
    private final long tokenValidMillisecond = 1000L * 60 * 60;

    @PostConstruct
    protected void init() {
        log.info("[JwtTokenProvider] secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("[JwtTokenProvider] secretKey 초기화 완료");
    }

    public String createToken(String userUid, List<String> roles) {
        log.info("[JwtTokenProvider/createToken] 토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(userUid);
        claims.put("roles", roles);

        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        log.info("[JwtTokenProvider/createToken] 토큰 생성 완료");
        return token;
    }

    public Authentication getAuthentication(String token) {
        log.info("[JwtTokenProvider/getAuthentication] 토큰 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        log.info("[JwtTokenProvider/getAuthentication] 토큰 조회 완료 Username : {}", userDetails.getUsername());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        log.info("[JwtTokenProvider/getUsername] 토큰에서 회원 정보 추출 시작");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token).getBody().getSubject();

        log.info("[JwtTokenProvider/getUsername] 토큰에서 회원 정보 추출 완료 info : {}", info);

        return info;
    }

    public String resolveToken(HttpServletRequest request) {
        log.info("[JwtTokenProvider/resolverToken] HTTP 헤더에서 Token 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String token) {
        log.info("[JwtTokenProvider/validateToken] 토큰 유효성 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("[JwtTokenProvider/validateToken] 토큰 유효성 체크 실패");
            return false;
        }
    }
}
