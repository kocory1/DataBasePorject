// service/TableService.java
package admin.service;

import admin.model.*;
import java.sql.*;
import java.util.*;

/**
 * 테이블 관리 서비스 - UI 독립적인 비즈니스 로직
 * 콘솔이든 Swing이든 동일하게 사용 가능
 */
public class TableService {
    private Connection connection;

    // 시스템의 모든 테이블 목록
    public static final String[] ALL_TABLES = {
            "RentalCompany", "Camper", "Part", "Employee", "Customer",
            "Rental", "InternalMaintenance", "ExternalMaintenanceShop", "ExternalMaintenance"
    };

    public TableService(Connection connection) {
        this.connection = connection;
    }

    /**
     * 모든 테이블 정보 조회
     */
    public List<TableInfo> getAllTablesInfo() {
        List<TableInfo> tables = new ArrayList<>();

        for (String tableName : ALL_TABLES) {
            try {
                TableInfo tableInfo = getTableInfo(tableName);
                tables.add(tableInfo);
            } catch (SQLException e) {
                // 실패한 테이블은 에러 정보와 함께 추가
                TableInfo errorTable = new TableInfo();
                errorTable.setTableName(tableName);
                errorTable.setError("조회 실패: " + e.getMessage());
                tables.add(errorTable);
            }
        }

        return tables;
    }

    /**
     * 특정 테이블 정보 조회
     */
    public TableInfo getTableInfo(String tableName) throws SQLException {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);

        // 데이터 조회
        String sql = "SELECT * FROM " + tableName + " LIMIT 100";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            QueryResult result = ResultSetToQueryResult(rs);
            tableInfo.setData(result);
        }

        // 테이블 구조 조회
        List<ColumnInfo> columns = getTableColumns(tableName);
        tableInfo.setColumns(columns);

        // 데이터 개수 조회
        int count = getTableRowCount(tableName);
        tableInfo.setRowCount(count);

        return tableInfo;
    }

    /**
     * SELECT 쿼리 실행
     */
    public QueryResult executeSelect(String tableName, String whereCondition) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        if (whereCondition != null && !whereCondition.trim().isEmpty()) {
            sql += " WHERE " + whereCondition;
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return ResultSetToQueryResult(rs);
        }
    }

    /**
     * INSERT 실행
     */
    public CrudResult executeInsert(String tableName, String values) throws SQLException {
        String sql = "INSERT INTO " + tableName + " VALUES (" + values + ")";
        return executeUpdate(sql);
    }

    /**
     * INSERT 실행 (완전한 SQL문)
     */
    public CrudResult executeInsertSql(String sql) throws SQLException {
        return executeUpdate(sql);
    }

    /**
     * UPDATE 실행
     */
    public CrudResult executeUpdate(String tableName, String setClause, String whereCondition) throws SQLException {
        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereCondition;
        return executeUpdate(sql);
    }

    /**
     * DELETE 실행
     */
    public CrudResult executeDelete(String tableName, String whereCondition) throws SQLException {
        CrudResult result = new CrudResult();
        
        try {
            connection.setAutoCommit(false);
            
            // 외래키 체크 비활성화
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            }
            
            // 삭제 실행
            String sql = "DELETE FROM " + tableName + " WHERE " + whereCondition;
            try (Statement stmt = connection.createStatement()) {
                int affectedRows = stmt.executeUpdate(sql);
                result.setSql(sql);
                result.setSuccess(true);
                result.setAffectedRows(affectedRows);
                result.setMessage("삭제 성공");
            }
            
            // 외래키 체크 재활성화
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                // 롤백 시에도 외래키 체크 재활성화
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
                } catch (SQLException ignored) {}
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            
            result.setSuccess(false);
            result.setErrorMessage("삭제 실패: " + e.getMessage());
            result.setErrorCode(e.getErrorCode());
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }

    /**
     * 테이블 컬럼 정보 조회
     */
    public List<ColumnInfo> getTableColumns(String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();

        String sql = "DESCRIBE " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ColumnInfo column = new ColumnInfo();
                column.setName(rs.getString("Field"));
                column.setType(rs.getString("Type"));
                column.setNullable("YES".equals(rs.getString("Null")));
                column.setKey(rs.getString("Key"));
                column.setDefaultValue(rs.getString("Default"));
                column.setExtra(rs.getString("Extra"));
                columns.add(column);
            }
        }

        return columns;
    }

    /**
     * 테이블 통계 정보 조회
     */
    public Map<String, Integer> getTableStatistics() {
        Map<String, Integer> statistics = new HashMap<>();

        for (String tableName : ALL_TABLES) {
            try {
                int count = getTableRowCount(tableName);
                statistics.put(tableName, count);
            } catch (SQLException e) {
                statistics.put(tableName, -1); // 오류 표시
            }
        }

        return statistics;
    }

    /**
     * 테이블 행 개수 조회
     */
    public int getTableRowCount(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * SQL 유효성 검사
     */
    public ValidationResult validateSql(String sql) {
        ValidationResult result = new ValidationResult();

        // 기본적인 SQL 인젝션 방지
        String upperSql = sql.toUpperCase().trim();

        // 위험한 키워드 체크
        String[] dangerousKeywords = {"DROP", "TRUNCATE", "ALTER", "CREATE", "GRANT", "REVOKE"};
        for (String keyword : dangerousKeywords) {
            if (upperSql.contains(keyword)) {
                result.setValid(false);
                result.setErrorMessage("위험한 키워드가 포함되어 있습니다: " + keyword);
                return result;
            }
        }

        // 빈 SQL 체크
        if (sql.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("SQL이 비어있습니다.");
            return result;
        }

        result.setValid(true);
        return result;
    }

    // === Private Helper Methods ===

    /**
     * UPDATE/INSERT/DELETE 실행
     */
    private CrudResult executeUpdate(String sql) throws SQLException {
        CrudResult result = new CrudResult();
        result.setSql(sql);

        try (Statement stmt = connection.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            result.setSuccess(true);
            result.setAffectedRows(affectedRows);
            result.setMessage("실행 성공");
        } catch (SQLException e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            result.setErrorCode(e.getErrorCode());
            throw e; // 호출자가 처리할 수 있도록 다시 던짐
        }

        return result;
    }

    /**
     * ResultSet을 QueryResult로 변환
     */
    private QueryResult ResultSetToQueryResult(ResultSet rs) throws SQLException {
        QueryResult result = new QueryResult();

        // 메타데이터 처리
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }
        result.setColumnNames(columnNames);

        // 데이터 처리
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                row.add(value == null ? "NULL" : value);
            }
            rows.add(row);
        }
        result.setRows(rows);
        result.setRowCount(rows.size());

        return result;
    }
}