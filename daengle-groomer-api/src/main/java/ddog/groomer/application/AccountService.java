package ddog.groomer.application;

import ddog.auth.config.jwt.JwtTokenProvider;
import ddog.domain.account.Account;
import ddog.domain.account.Role;
import ddog.domain.groomer.Groomer;
import ddog.groomer.application.mapper.GroomerMapper;
import ddog.groomer.presentation.account.dto.*;
import ddog.persistence.port.AccountPersist;
import ddog.persistence.port.GroomerPersist;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountPersist accountPersist;
    private final GroomerPersist groomerPersist;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignUpResp signUp(SignUpReq request, HttpServletResponse response) {

        hasInvalidSignUpReqDataFormat(request);

        Account newAccount = Account.create(request.getEmail(), Role.GROOMER);
        Account savedAccount = accountPersist.save(newAccount);

        Groomer newGroomer = GroomerMapper.create(savedAccount.getAccountId(), request);
        groomerPersist.save(newGroomer);

        Authentication authentication = getAuthentication(savedAccount.getAccountId(), request.getEmail());
        String accessToken = jwtTokenProvider.generateToken(authentication, response);

        return SignUpResp.builder()
                .accessToken(accessToken)
                .build();
    }

    private void hasInvalidSignUpReqDataFormat(SignUpReq request) {
        Groomer.validateShopName(request.getShopName());
        Groomer.validateName(request.getName());
        Groomer.validatePhoneNumber(request.getPhoneNumber());
        Groomer.validateAddress(request.getAddress());
        Groomer.validateDetailAddress(request.getDetailAddress());
        Groomer.validateBusinessLicenses(request.getBusinessLicenses());
        Groomer.validateLicenses(request.getLicenses());
    }

    private Authentication getAuthentication(Long accountId, String email) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_GROOMER"));

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(email + "," + accountId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Transactional(readOnly = true)
    public ProfileInfo.ModifyPage getModifyPage(Long accountId) {
        Groomer groomer = groomerPersist.getGroomerByAccountId(accountId);
        return GroomerMapper.toModifyPage(groomer);
    }

    @Transactional
    public AccountResp modifyInfo(ModifyInfoReq request, Long accountId) {
        Groomer.validateIntroduction(request.getIntroduction());

        Groomer groomer = groomerPersist.getGroomerByAccountId(accountId);
        Groomer updatedGroomer = GroomerMapper.withUpdate(groomer, request);
        groomerPersist.save(updatedGroomer);

        return AccountResp.builder()
                .requestResult("미용사 정보가 성공적으로 수정 되었습니다.")
                .build();
    }
}
