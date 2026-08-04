package com.fatmakahveci.blog.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FlywayMigrationITests {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void schemaIsMigratedToLatestVersion() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("6");
    }

    @Test
    void migratedSchemaContainsAllApplicationTables() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM sqlite_master
                WHERE type = 'table' AND name IN ('posts', 'tags', 'post_tags', 'users')
                """, Integer.class);

        assertThat(tableCount).isEqualTo(4);
    }

    @Test
    void migratedSchemaEnforcesUniquePostTitles() {
        Integer uniqueIndexCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM pragma_index_list('posts')
                WHERE "unique" = 1
                """, Integer.class);

        assertThat(uniqueIndexCount).isPositive();
    }
}
