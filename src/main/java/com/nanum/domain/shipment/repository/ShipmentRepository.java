package com.nanum.domain.shipment.repository;

import com.nanum.domain.shipment.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByManagerCodeAndDeleteYn(String managerCode, String deleteYn);
    List<Shipment> findByDeleteYn(String deleteYn);
    Optional<Shipment> findByShipmentCode(String shipmentCode);
}
