package com.aloc.aloc.global.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DbUrlLogger {

    private final DataSource dataSource;

    @PostConstruct
    public void printDbUrl() throws SQLException {
        String url = dataSource.getConnection().getMetaData().getURL();
        System.out.println("✅ 현재 연결된 DB URL: " + url);
    }
}
