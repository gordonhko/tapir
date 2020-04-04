package com.fusui.tapir.service.dal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public interface DataSource {
    public void init(Properties prop);
    public Connection getConnection() throws SQLException;
}
