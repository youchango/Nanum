package com.nanum.domain.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class AddressBookDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String addressName;
        @NotBlank(message = "수령인 이름은 필수입니다.")
        private String receiverName;
        private String receiverPhone;
        @NotBlank(message = "우편번호는 필수입니다.")
        private String zipcode;
        @NotBlank(message = "주소는 필수입니다.")
        private String address;
        @NotBlank(message = "상세주소는 필수입니다.")
        private String addressDetail;
        @JsonProperty("isDefault")
        private boolean isDefault;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String addressName;
        @NotBlank(message = "수령인 이름은 필수입니다.")
        private String receiverName;
        private String receiverPhone;
        @NotBlank(message = "우편번호는 필수입니다.")
        private String zipcode;
        @NotBlank(message = "주소는 필수입니다.")
        private String address;
        @NotBlank(message = "상세주소는 필수입니다.")
        private String addressDetail;
        @JsonProperty("isDefault")
        private boolean isDefault;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String addressName;
        private String receiverName;
        private String receiverPhone;
        private String zipcode;
        private String address;
        private String addressDetail;
        @JsonProperty("isDefault")
        private boolean isDefault;
        private LocalDateTime createdAt;
    }
}
