package com.origin;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

public class App {

    public static void main(String[] args) throws Exception {

        DataSource dataSource = createDataSource();
        Connection conn = dataSource.getConnection();

        try {
            updatePerson(conn);
            deletePerson(conn);
            insertPerson(conn);
            getFilteredPersons(conn);
            getAllPersons(conn);
        }
        catch (SQLException e) {
            String errorCode = e.getSQLState();
            // 08000 - connection exception
            if (Objects.equals(errorCode, "08000")) {
                //retry query after re-establishing connection
            }
            // 42601 - syntax error
            else if (Objects.equals(errorCode, "42601")) {
                // throw error so that we can see the failure
                throw e;
            } else {
                System.out.printf("SQL failed with error code: %s$n", errorCode);
            }
        }
    }

    private static DataSource createDataSource() {
        final String url = "jdbc:postgresql://localhost:5432/family_tree?user=postgres&password=postgres";
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }

    private static void getAllPersons(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from persons");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.printf("id:%d first_name: %s last_name:%s", rs.getLong("id"),
            rs.getString("first_name"), rs.getString("last_name"));
        }
    }

    private static void getFilteredPersons(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from persons where first_name = ?");
        stmt.setString(1, "sam");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.printf("id:%d first_name: %s last_name:%s", rs.getLong("id"),
            rs.getString("first_name"), rs.getString("last_name"));
        }
    }

    private static void insertPerson(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into persons (first_name, last_name, birthday, gender, death_day) values (?, ?, ?, ?, ?)");
        stmt.setString(1, "sam");
        stmt.setString(2, "smith");
        stmt.setDate(3, new Date(1980, 1, 1));
        stmt.setString(4, "m");
        stmt.setDate(5, null);
        int insetedRows = stmt.executeUpdate();
        System.out.printf("inserted %d rows", insetedRows);
    }

    private static void updatePerson(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("update persons set first_name = ? where id = ?");
        stmt.setString(1, "sam");
        stmt.setLong(2, 1);
        int updatedRows = stmt.executeUpdate();
        System.out.printf("updated %d rows", updatedRows);
    }

    private static void deletePerson(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("delete from persons where id = ?");
        stmt.setLong(1, 1);
        int deletedRows = stmt.executeUpdate();
        System.out.printf("deleted %d rows", deletedRows);
    }
}
