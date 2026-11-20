package com.moliveira.demo_park_api.service;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JasperServiceTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    private JasperService jasperService;

    @BeforeEach
    void setUp() throws Exception {
        jasperService = new JasperService(resourceLoader, dataSource);
        when(resourceLoader.getResource(anyString())).thenAnswer(i -> new ByteArrayResource(new byte[]{1}));
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void eachReportUsesOnlyItsOwnClientCpf() {
        List<Map<String, Object>> paramsPerCall = new ArrayList<>();

        try (MockedStatic<JasperFillManager> fill = mockStatic(JasperFillManager.class);
             MockedStatic<JasperExportManager> export = mockStatic(JasperExportManager.class)) {
            fill.when(() -> JasperFillManager.fillReport(any(InputStream.class), anyMap(), any(Connection.class)))
                    .thenAnswer(invocation -> {
                        paramsPerCall.add(new HashMap<>(invocation.getArgument(1)));
                        return mock(JasperPrint.class);
                    });
            export.when(() -> JasperExportManager.exportReportToPdf(any(JasperPrint.class)))
                    .thenReturn("%PDF-".getBytes());

            jasperService.generateParkingReport("11111111111");
            jasperService.generateParkingReport("22222222222");
        }

        assertThat(paramsPerCall).hasSize(2);
        assertThat(paramsPerCall.get(0)).containsEntry("CPF", "11111111111");
        assertThat(paramsPerCall.get(1)).containsEntry("CPF", "22222222222");
    }

    @Test
    void closesTheDatabaseConnectionAfterEveryReport() throws Exception {
        try (MockedStatic<JasperFillManager> fill = mockStatic(JasperFillManager.class);
             MockedStatic<JasperExportManager> export = mockStatic(JasperExportManager.class)) {
            fill.when(() -> JasperFillManager.fillReport(any(InputStream.class), anyMap(), any(Connection.class)))
                    .thenReturn(mock(JasperPrint.class));
            export.when(() -> JasperExportManager.exportReportToPdf(any(JasperPrint.class)))
                    .thenReturn("%PDF-".getBytes());

            jasperService.generateParkingReport("11111111111");
            jasperService.generateParkingReport("22222222222");
        }

        verify(connection, times(2)).close();
    }
}
