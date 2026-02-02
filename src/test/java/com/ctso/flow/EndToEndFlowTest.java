package com.ctso.flow;

import com.ctso.admin.billing.service.BillingGenerationService;
import com.ctso.admin.schedule.service.AdminScheduleService;
import com.ctso.biz.vacation.dto.VacationRegisterDTO;
import com.ctso.biz.vacation.service.BizVacationService;
import com.ctso.domain.code.repository.CodeRepository;
import com.ctso.domain.asset.model.AssetDetail;
import com.ctso.domain.asset.model.AssetMaster;
import com.ctso.domain.asset.model.AssetServiceDay;
import com.ctso.domain.asset.model.BillingType;
import com.ctso.domain.asset.repository.AssetMasterRepository;
import com.ctso.domain.member.model.Member;
import com.ctso.domain.member.model.MemberRole;
import com.ctso.domain.member.model.MemberType;
import com.ctso.domain.member.repository.MemberRepository;
import com.ctso.domain.schedule.model.ScheduleDetail;
import com.ctso.domain.schedule.model.ScheduleMaster;
import com.ctso.domain.schedule.repository.ScheduleDetailRepository;
import com.ctso.domain.schedule.repository.ScheduleMasterRepository;
import com.ctso.domain.vacation.model.VacationRequest;
import com.ctso.domain.vacation.model.VacationType;
import com.ctso.domain.vacation.repository.VacationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class EndToEndFlowTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private AssetMasterRepository assetMasterRepository;
    @Autowired private AdminScheduleService adminScheduleService;
    @Autowired private ScheduleMasterRepository scheduleMasterRepository;
    @Autowired private ScheduleDetailRepository scheduleDetailRepository;
    @Autowired private BizVacationService bizVacationService;
    @Autowired private VacationRepository vacationRepository;
    @Autowired private CodeRepository codeRepository;
    @Autowired private BillingGenerationService billingGenerationService;
    @Autowired private com.ctso.domain.payment.repository.PaymentRepository paymentRepository;
    @Autowired private jakarta.persistence.EntityManager entityManager;

    @Test
    @DisplayName("End-to-End Flow: Contract -> Schedule -> Vacation -> Assign -> Complete -> Billing")
    void endToEndFlow() {
        // 1. Setup Data
        long timestamp = System.currentTimeMillis();

        // Fetch Service Code
        com.ctso.domain.code.model.Code serviceCode = codeRepository.findAllByCodeTypeAndDeleteYn("SERVICE_TYPE", "N").stream()
                .filter(c -> c.getDepth() == 2)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Service Type Code not found"));
        Integer serviceTypeCode = serviceCode.getCodeId();

        Member customer = memberRepository.save(Member.builder()
                .memberName("Test Customer")
                .memberLogin("c_" + timestamp)
                .password("pass")
                .role(MemberRole.ROLE_USER)
                .memberType(MemberType.USER)
                .address("Seoul Gangnam-gu")
                .addressDetail("101-202")
                .zipcode("12345")
                .mobilePhone("010-1111-2222")
                .email("cust_" + timestamp + "@test.com")
                .businessNumber("000-00-00000") // Dummy for Constraint
                .build());

        Member worker = memberRepository.save(Member.builder()
                .memberName("Test Worker")
                .memberLogin("w_" + timestamp)
                .password("pass")
                .role(MemberRole.ROLE_BIZ)
                .memberType(MemberType.BIZ)
                .address("Seoul Seocho-gu")
                .addressDetail("B01")
                .zipcode("67890")
                .mobilePhone("010-3333-4444")
                .email("work_" + timestamp + "@test.com")
                .businessNumber("123-45-67890") // Added Business Number
                .build());

        AssetMaster asset = AssetMaster.builder()
                .memberId(customer.getMemberId().intValue())
                .buildingAddress("Gangnam Finance Center")
                .buildingAddressDetail("101-202")
                .buildingType("OFFICE")
                .zipcode("12345")
                .contractStartDate(LocalDate.of(2024, 1, 1))
                .contractEndDate(LocalDate.of(2024, 12, 31))
                .build();
        
        AssetDetail detail = AssetDetail.builder()
                .serviceTypeCode(serviceTypeCode) // Dynamic Code ID
                .billingType(BillingType.PER_VISIT)
                .serviceCost(50000)
                .personnelCount(1)
                .build();
        
        detail.setAssetMaster(asset);
        
        AssetServiceDay serviceDay = AssetServiceDay.builder()
                .weekNumber(0) // Every week
                .visitDay("MON") // Every Monday
                .build();
        
        detail.getAssetServiceDays().add(serviceDay);
        serviceDay.setAssetDetail(detail);

        asset.getAssetDetails().add(detail);
        assetMasterRepository.save(asset);

        // 2. Generate Schedule for Jan 2024
        YearMonth targetMonth = YearMonth.of(2024, 1);
        adminScheduleService.generateSchedules(targetMonth);

        // 3. Verify Schedule Generation
        // Jan 1 2024 is Monday. So there should be schedules on Jan 1, 8, 15, 22, 29.
        List<ScheduleMaster> masters = scheduleMasterRepository.findAll();
        assertThat(masters).isNotEmpty();
        
        ScheduleMaster firstMaster = masters.stream()
                .filter(m -> m.getScheduleDate().equals(LocalDate.of(2024, 1, 1)))
                .findFirst().orElseThrow();
        
        // Verify Check unassigned Detail
        List<ScheduleDetail> details = scheduleDetailRepository.findAll();
        ScheduleDetail targetDetail = details.stream()
                .filter(d -> d.getScheduleMasterId().equals(firstMaster.getScheduleMasterId()))
                .findFirst().orElseThrow();
        
        assertThat(targetDetail.getMemberId()).isNull(); // Should be unassigned

        // 4. Register Vacation (Biz)
        VacationRegisterDTO vacationDTO = new VacationRegisterDTO();
        vacationDTO.setVacationType(VacationType.ANNUAL);
        vacationDTO.setStartDate(LocalDate.of(2024, 1, 2)); // Vacation on Tue (No schedule)
        vacationDTO.setEndDate(LocalDate.of(2024, 1, 3));
        vacationDTO.setReason("Rest");
        
        bizVacationService.registerVacation(worker.getMemberId(), vacationDTO);
        
        List<VacationRequest> vacations = vacationRepository.findAll();
        assertThat(vacations).hasSize(1);
        assertThat(vacations.get(0).getStatus().name()).isEqualTo("APPROVED"); // Auto-Approve Check

        // 5. Assign Worker
        adminScheduleService.assignWorker(targetDetail.getScheduleDetailId(), worker.getMemberId());
        
        // Verify Assignment
        ScheduleDetail assignedDetail = scheduleDetailRepository.findById(targetDetail.getScheduleDetailId()).get();
        assertThat(assignedDetail.getMemberId()).isEqualTo(worker.getMemberId());

        // 6. Execution (Complete)
        adminScheduleService.updateVisitStatus(firstMaster.getScheduleMasterId(), "COMPLETE");
        
        ScheduleMaster completedMaster = scheduleMasterRepository.findById(firstMaster.getScheduleMasterId()).get();
        assertThat(completedMaster.getVisitStatus()).isEqualTo("COMPLETE");

        // Flush and Clear Persistence Context to ensure Billing Service fetches fresh data with associations
        entityManager.flush();
        entityManager.clear();

        // 7. Billing Generation
        billingGenerationService.generateMonthlyBills(targetMonth);
        
        // Verify Billing
        long billCount = paymentRepository.count();
        assertThat(billCount).isGreaterThan(0);
    }
}
