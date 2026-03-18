package com.nanum.user.delivery.service;

import com.nanum.domain.delivery.dto.AddressBookDTO;
import com.nanum.domain.delivery.model.AddressBook;
import com.nanum.domain.member.model.Member;
import com.nanum.user.delivery.repository.AddressBookRepository;
import com.nanum.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressBookService {

    private final AddressBookRepository addressBookRepository;
    private final MemberRepository memberRepository;

    public List<AddressBookDTO.Response> getMyAddresses(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<AddressBook> addresses = addressBookRepository
                .findByMemberCodeOrderByIsDefaultDescCreatedAtDesc(member.getMemberCode());

        return addresses.stream().map(this::toResponse).toList();
    }

    @Transactional
    public Long createAddress(String memberId, AddressBookDTO.CreateRequest request) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String memberCode = member.getMemberCode();

        // 기본 배송지로 설정하는 경우 기존 기본 배송지 해제
        if (request.isDefault()) {
            resetDefaultAddresses(memberCode);
        }

        AddressBook addressBook = AddressBook.builder()
                .memberCode(memberCode)
                .addressName(request.getAddressName())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .isDefault(request.isDefault() ? "Y" : "N")
                .build();

        return addressBookRepository.save(addressBook).getId();
    }

    @Transactional
    public void updateAddress(String memberId, Long id, AddressBookDTO.UpdateRequest request) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        AddressBook addressBook = addressBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송지를 찾을 수 없습니다."));

        if (!addressBook.getMemberCode().equals(member.getMemberCode())) {
            throw new IllegalArgumentException("본인의 배송지만 수정할 수 있습니다.");
        }

        // 기본 배송지로 변경하는 경우 기존 기본 배송지 해제
        if (request.isDefault()) {
            resetDefaultAddresses(member.getMemberCode());
        }

        addressBook.update(
                request.getAddressName(),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getZipcode(),
                request.getAddress(),
                request.getAddressDetail(),
                request.isDefault() ? "Y" : "N"
        );
    }

    @Transactional
    public void deleteAddress(String memberId, Long id) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        AddressBook addressBook = addressBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송지를 찾을 수 없습니다."));

        if (!addressBook.getMemberCode().equals(member.getMemberCode())) {
            throw new IllegalArgumentException("본인의 배송지만 삭제할 수 있습니다.");
        }

        addressBookRepository.delete(addressBook);
    }

    @Transactional
    public void setDefault(String memberId, Long id) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        AddressBook addressBook = addressBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배송지를 찾을 수 없습니다."));

        if (!addressBook.getMemberCode().equals(member.getMemberCode())) {
            throw new IllegalArgumentException("본인의 배송지만 수정할 수 있습니다.");
        }

        resetDefaultAddresses(member.getMemberCode());
        addressBook.setDefaultAddress("Y");
    }

    private void resetDefaultAddresses(String memberCode) {
        List<AddressBook> defaults = addressBookRepository.findByMemberCodeAndIsDefault(memberCode, "Y");
        for (AddressBook addr : defaults) {
            addr.setDefaultAddress("N");
        }
    }

    private AddressBookDTO.Response toResponse(AddressBook addressBook) {
        return AddressBookDTO.Response.builder()
                .id(addressBook.getId())
                .addressName(addressBook.getAddressName())
                .receiverName(addressBook.getReceiverName())
                .receiverPhone(addressBook.getReceiverPhone())
                .zipcode(addressBook.getZipcode())
                .address(addressBook.getAddress())
                .addressDetail(addressBook.getAddressDetail())
                .isDefault("Y".equals(addressBook.getIsDefault()))
                .createdAt(addressBook.getCreatedAt())
                .build();
    }
}
