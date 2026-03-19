package com.nanum.admin.order.service;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.order.model.QOrderMaster;
import com.nanum.user.order.repository.OrderRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public List<OrderDTO.Response> getOrders(String siteCd) {
        Manager manager = getCurrentManager();
        String targetSiteCd = manager.getSiteCd();

        if (manager.getMbType() == ManagerType.MASTER) {
            if (StringUtils.hasText(siteCd)) {
                targetSiteCd = siteCd;
            } else {
                targetSiteCd = null; // ALL
            }
        }

        Iterable<OrderMaster> orders;
        if (targetSiteCd != null) {
            orders = orderRepository.findAll(QOrderMaster.orderMaster.siteCd.eq(targetSiteCd));
        } else {
            orders = orderRepository.findAll();
        }

        return StreamSupport.stream(orders.spliterator(), false)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(Long id, OrderDTO.StatusUpdateRequest request) {
        OrderMaster order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        // Check permission?
        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("Access denied");
        }

        order.changeStatus(request.getStatus());
        // save is implicit in transaction but explicit save is fine
    }

    private OrderDTO.Response convertToResponse(OrderMaster order) {
        return OrderDTO.Response.builder()
                .orderId(order.getOrderId())
                .orderName(order.getOrderName())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .receiverName(order.getReceiverName())
                .createdAt(order.getCreatedAt())
                // items logic if needed. OrderMaster vs OrderDetail?
                // OrderMaster entity doesn't show List<OrderDetail>.
                // Check OrderMaster.java again?
                // It didn't have @OneToMany items.
                // So items might not be fetched here easily without repo changes.
                // For now, return empty items or fetch if repository supports.
                // Since user requested "Order List", maybe items are not needed in list view.
                .build();
    }

    private Manager getCurrentManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomManagerDetails) {
            return ((CustomManagerDetails) principal).getManager();
        }
        throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
    }
}
