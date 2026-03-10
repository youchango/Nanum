package com.nanum.user.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nanum.user.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Delivery", description = "Delivery API")
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

}
