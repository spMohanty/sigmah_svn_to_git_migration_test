package org.sigmah.client.offline;

import com.bedatadriven.rebar.sql.client.GearsConnectionFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.remote.Authentication;

import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class ConnectionProvider implements Provider<Connection> {

    private final Authentication auth;
    private Connection conn;

    @Inject
    public ConnectionProvider(Authentication auth) {
        this.auth = auth;
    }

    public Connection get() {
        if(conn == null) {
            try {
                conn = GearsConnectionFactory.getConnection(auth.getLocalDbName());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return conn;
    }
}