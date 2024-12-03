package ddog.payment.application.auth;

import ddog.auth.config.jwt.JwtTokenProvider;
import ddog.auth.dto.TokenAccountInfoDto;
import ddog.domain.account.Role;
import ddog.payment.presentation.LoginResult;
import ddog.persistence.port.AccountPersist;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KakaoSocialService kakaoSocialService;
    private final AccountPersist accountPersist;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResult kakaoOAuthLogin(String kakaoAccessToken, HttpServletResponse response) {
        /* kakaoAccessToken 정보를 가지고 유저의 이메일 정보를 가져온다. */
        String email = kakaoSocialService.getKakaoEmail(kakaoAccessToken);
        Role role = Role.DAENGLE;

        if (!accountPersist.checkExistsAccountBy(email, role)) {
            return LoginResult.builder()
                    .isOnboarding(true)
                    .email(email)
                    .build();
        }
        Authentication authentication = getAuthentication(email, role);
        String accessToken = jwtTokenProvider.generateToken(authentication, response);
        return LoginResult.builder()
                .isOnboarding(false)
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    private Authentication getAuthentication(String email, Role role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        Long accountId = accountPersist.findAccountByEmailAndRole(email, role)
                .getAccountId();

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(email + "," + accountId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public LoginResult reGenerateAccessToken(String refreshToken, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(refreshToken.substring(7).trim())) {
            /* 추후에 INVALID_TOKEN 으로 변경 예정 */
            throw new RuntimeException();
        }

        TokenAccountInfoDto.TokenInfo tokenInfo = jwtTokenProvider.extractTokenInfoFromJwt(refreshToken);
        String email = tokenInfo.getEmail();

        Authentication authentication = getAuthentication(email, Role.DAENGLE);
        String accessToken = jwtTokenProvider.generateToken(authentication, response);
        return LoginResult.builder()
                .isOnboarding(false)
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

}