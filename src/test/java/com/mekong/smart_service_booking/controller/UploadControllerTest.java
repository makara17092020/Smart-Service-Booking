package com.mekong.smart_service_booking.controller;

import com.mekong.smart_service_booking.service.CloudStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UploadControllerTest {

        private MockMvc mvc;

        @BeforeEach
        void setUp() {
                // create controller with a simple stub implementation to avoid mocking frameworks
                UploadController controller = new UploadController(new StubCloudStorageService());
                mvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

        @Test
        void uploadFile_success() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.txt",
                                MediaType.TEXT_PLAIN_VALUE,
                                "hello world".getBytes()
                );

                mvc.perform(multipart("/api/uploads")
                                                .file(file)
                                                .contentType(MediaType.MULTIPART_FORM_DATA))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.public_id").value("stub-id"))
                                .andExpect(jsonPath("$.secure_url").value("https://example.com/stub.txt"));
        }

        // Simple stub that simulates a successful upload/destroy without external dependencies
        private static class StubCloudStorageService implements CloudStorageService {
                @Override
                public Map<String, Object> upload(org.springframework.web.multipart.MultipartFile file, Map<String, Object> options) {
                        return Map.of(
                                        "public_id", "stub-id",
                                        "secure_url", "https://example.com/stub.txt"
                        );
                }

                @Override
                public Map<String, Object> destroy(String publicId) {
                        return Map.of("result", "ok");
                }
        }
}
