package com.temirlan.crud.Service;

import com.temirlan.crud.Dto.MyUserDTO;
import com.temirlan.crud.Entity.MyUser;
import com.temirlan.crud.Repository.AccountsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional

@Setter
@Getter
@AllArgsConstructor
public class ServiceClass {
    private final AccountsRepository accountsRepository;

    // Create
    public String addAccountToBD(MyUserDTO accountsDto) {
        MyUser newAccount = new MyUser();

        newAccount.setId(accountsDto.getId());
        newAccount.setAge(accountsDto.getAge());
        newAccount.setName(accountsDto.getName());
        newAccount.setSurname(accountsDto.getSurname());

        accountsRepository.save(newAccount);
        return accountsDto.getName() + " has been successfully added to the DB";
    }

    // Read
    public List<MyUser> showAllAccounts() {
        return accountsRepository.findAll();
    }

    // Update
    public String updateAccount(Integer id, MyUserDTO accountsDto) {
        MyUser accountToUpdate = accountsRepository.findById(id).orElseThrow(RuntimeException::new);
        String initialName = accountToUpdate.getName();

        accountToUpdate.setAge(accountsDto.getAge());
        accountToUpdate.setName(accountsDto.getName());
        accountToUpdate.setSurname(accountsDto.getSurname());

        String changedName = accountToUpdate.getName();
        return "Account " + initialName + " has been successfully updated to " + changedName;
    }

    // Delete
    public String deleteAccount(Integer id) {
            if (id > 0) {
                MyUser accountToDelete = accountsRepository.findById(id).orElseThrow(RuntimeException::new);
                accountsRepository.delete(accountToDelete);
                return accountToDelete.getName() + " has been successfully removed from DB";
            } else {
                return "Please enter id higher than 0";
            }
    }
}