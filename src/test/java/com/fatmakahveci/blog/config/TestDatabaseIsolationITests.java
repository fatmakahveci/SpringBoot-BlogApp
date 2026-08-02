package com.fatmakahveci.blog.config;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestDatabaseIsolationITests {

    @Autowired
    private DataSource dataSource;

    @Test
    void testsUseAnInMemorySQLiteDatabaseInsteadOfTheApplicationDatabase() throws Exception {
        try (var connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();

            assertThat(url)
                    .startsWith("jdbc:sqlite:file:")
                    .contains("mode=memory")
                    .contains("cache=shared")
                    .doesNotContain("sample.db");
        }
    }
}
