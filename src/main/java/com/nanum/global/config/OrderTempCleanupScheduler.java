package com.nanum.global.config;

import com.nanum.domain.order.model.OrderTemp;
import com.nanum.user.order.repository.OrderTempRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 30분마다 PENDING 상태의 만료된 OrderTemp를 EXPIRED로 변경합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTempCleanupScheduler {

    private final OrderTempRepository orderTempRepository;

    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분
    @Transactional
    public void cleanupExpiredOrderTemps() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<OrderTemp> expiredTemps = orderTempRepository.findByStatusAndCreatedAtBefore("PENDING", threshold);

        if (expiredTemps.isEmpty()) {
            return;
        }

        for (OrderTemp temp : expiredTemps) {
            temp.setStatus("EXPIRED");
        }

        log.info("만료된 임시 주문 {} 건 EXPIRED 처리 완료", expiredTemps.size());
    }
}
