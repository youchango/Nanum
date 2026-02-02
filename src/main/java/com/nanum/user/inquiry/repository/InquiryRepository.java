package com.nanum.user.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.user.inquiry.model.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer>, InquiryRepositoryCustom {
}
