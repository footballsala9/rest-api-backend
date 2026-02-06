package com.example.demo.main.web;

import com.example.demo.main.service.PdfGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PdfController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(PdfController.class)
class PdfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PdfGenerationService pdfGenerationService;

    @Test
    void generateTaggedPdf() throws Exception {
        byte[] mockPdfContent = "Fake PDF Content".getBytes();
        when(pdfGenerationService.generateTaggedPdf()).thenReturn(mockPdfContent);

        mockMvc.perform(get("/api/pdf/tagged"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"tagged-report.pdf\""))
                .andExpect(content().bytes(mockPdfContent));
    }
}
