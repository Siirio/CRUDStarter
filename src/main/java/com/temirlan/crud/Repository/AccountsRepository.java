package com.temirlan.crud.Repository;

import com.temirlan.crud.Entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //репа для таблицы Accounts
public interface AccountsRepository extends JpaRepository<MyUser, Integer> {
    Optional<MyUser> findByEmail(String email);
}
