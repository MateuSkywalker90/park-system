package com.moliveira.demo_park_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JasperService {

    private static final String JASPER_SOURCE = "classpath:reports/";

    private final ResourceLoader resourceLoader;
    private final DataSource dataSource;

    /**
     * Generates the parking history PDF for one client.
     * <p>
     * Parameters are built per call: this service is a singleton shared by all
     * requests, so keeping them in a field let concurrent requests overwrite each
     * other's CPF and return another client's report. The connection and the
     * report stream are closed after every call.
     */
    public byte[] generateParkingReport(String cpf) {
        Map<String, Object> params = new HashMap<>();
        params.put("IMAGE_SOURCE", JASPER_SOURCE);
        params.put("REPORT_LOCALE", new Locale("pt", "BR"));
        params.put("CPF", cpf);

        Resource resource = resourceLoader.getResource(JASPER_SOURCE.concat("parking.jasper"));
        try (InputStream stream = resource.getInputStream();
             Connection connection = dataSource.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(stream, params, connection);
            return JasperExportManager.exportReportToPdf(print);
        } catch (IOException | JRException | SQLException e) {
            log.error("Jasper Reports ::: ", e);
            throw new RuntimeException(e);
        }
    }
}
