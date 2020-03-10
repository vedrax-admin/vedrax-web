package com.vedrax.util;

import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBIntegrationTestUtils {

  private DBIntegrationTestUtils() {
  }

  public static void resetAutoIncrementColumns(ApplicationContext applicationContext, String... tables)
    throws SQLException {
    DataSource dataSource = applicationContext.getBean(DataSource.class);

    String resetSqlTemplate = "ALTER TABLE %s ALTER COLUMN id RESTART WITH 1";

    try (Connection connection = dataSource.getConnection()) {
      for (String table : tables) {
        try (Statement statement = connection.createStatement()) {
          String sqlStatement = String.format(resetSqlTemplate, table);
          statement.execute(sqlStatement);
        }
      }
    }
  }
}
