package admin.service;

import admin.model.QueryResult;
import java.sql.*;
import java.util.*;

/**
 * 복합 쿼리 실행 서비스
 * 정의서 요구사항: 4개 이상 테이블 조인 + 부속질의 + GROUP BY 사용
 */
public class ComplexQueryService {
    private Connection connection;
    
    public ComplexQueryService(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * 사용자 정의 SELECT 쿼리 실행
     */
    public QueryResult executeCustomQuery(String sql) throws SQLException {
        // 기본적인 보안 검사
        String upperSql = sql.toUpperCase().trim();
        if (!upperSql.startsWith("SELECT")) {
            throw new SQLException("SELECT 문만 실행 가능합니다.");
        }
        
        // 위험한 키워드 체크
        String[] dangerousKeywords = {"DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "CREATE", "TRUNCATE"};
        for (String keyword : dangerousKeywords) {
            if (upperSql.contains(keyword)) {
                throw new SQLException("위험한 키워드가 포함되어 있습니다: " + keyword);
            }
        }
        
        return executeQuery(sql);
    }
    
    /**
     * 공통 쿼리 실행 메서드
     */
    private QueryResult executeQuery(String sql) throws SQLException {
        QueryResult result = new QueryResult();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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
            result.setExecutedSql(sql);
        }
        
        return result;
    }
}
