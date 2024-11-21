package ddog.daengleserver.infrastructure;

import ddog.daengleserver.application.repository.AccountRepository;
import ddog.daengleserver.domain.Account;
import ddog.daengleserver.global.auth.config.enums.Role;
import ddog.daengleserver.implementation.AccountException;
import ddog.daengleserver.implementation.enums.AccountExceptionType;
import ddog.daengleserver.infrastructure.jpa.AccountJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    @Override
    public boolean checkExistsAccountBy(String email, Role role) {
        return accountJpaRepository.existsByEmailAndRole(email, role);
    }

    @Override
    public void save(Account account) {
        accountJpaRepository.save(AccountJpaEntity.from(account));
    }

    @Override
    public Account findBy(long id) {
        return accountJpaRepository.findById(id)
                .orElseThrow(() -> new AccountException(AccountExceptionType.NOT_FOUND_ACCOUNT))
                .toModel();
    }

    @Override
    public Account findAccountByEmailAndRole(String email, Role role) {
        Optional<AccountJpaEntity> byEmailAndRole = accountJpaRepository.findByEmailAndRole(email, role);
        AccountJpaEntity accountJpaEntity = byEmailAndRole.orElseThrow(() -> new AccountException(AccountExceptionType.NOT_FOUND_ACCOUNT));
        Account model = accountJpaEntity.toModel();
        return model;
    }
}
