package com.mekong.smart_service_booking.controller;

import com.mekong.smart_service_booking.entity.ServiceEntity;
import com.mekong.smart_service_booking.service.ServiceCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCrudService serviceCrudService;

    @GetMapping
    public List<ServiceEntity> getAll() {
        return serviceCrudService.getAllServices();
    }

    @GetMapping("/{id}")
    public ServiceEntity getById(@PathVariable UUID id) {
        return serviceCrudService.getServiceById(id);
    }

    @PostMapping
    public ServiceEntity create(@RequestBody ServiceEntity service) {
        return serviceCrudService.createService(service);
    }

    @PutMapping("/{id}")
    public ServiceEntity update(@PathVariable UUID id, @RequestBody ServiceEntity service) {
        return serviceCrudService.updateService(id, service);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceCrudService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}