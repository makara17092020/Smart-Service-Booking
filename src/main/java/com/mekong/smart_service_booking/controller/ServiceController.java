package com.mekong.smart_service_booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekong.smart_service_booking.entity.ServiceEntity;
import com.mekong.smart_service_booking.service.ServiceCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCrudService serviceCrudService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public List<ServiceEntity> getAll() {
        return serviceCrudService.getAllServices();
    }

    @GetMapping("/{id}")
    public ServiceEntity getById(@PathVariable UUID id) {
        return serviceCrudService.getServiceById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceEntity> create(
            @RequestPart("service") String serviceJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        
        ServiceEntity service = objectMapper.readValue(serviceJson, ServiceEntity.class);
        return ResponseEntity.ok(serviceCrudService.createService(service, image));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceEntity> update(
            @PathVariable UUID id,
            @RequestPart("service") String serviceJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        
        ServiceEntity service = objectMapper.readValue(serviceJson, ServiceEntity.class);
        return ResponseEntity.ok(serviceCrudService.updateService(id, service, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceCrudService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}