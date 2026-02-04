package com.nanum.user.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.user.delivery.model.AddressBook;

import java.util.List;

public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
    List<AddressBook> findAllByMemberCode(String memberCode);
}
