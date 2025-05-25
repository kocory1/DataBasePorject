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
     * 테스트용 복합 쿼리 1: 캠핑카별 수익성 분석
     * 5개 테이블 조인 + 부속질의 + GROUP BY
     */
    public QueryResult executeProfitabilityAnalysis() throws SQLException {
        String sql = """
            SELECT 
                c.name as 캠핑카명,
                rc.name as 렌탈회사명,
                COUNT(r.rental_id) as 총대여횟수,
                SUM(r.bill_amount + COALESCE(r.additional_charges_amount, 0)) as 총대여수익,
                COALESCE(maintenance_cost.총정비비용, 0) as 총정비비용,
                (SUM(r.bill_amount + COALESCE(r.additional_charges_amount, 0)) - COALESCE(maintenance_cost.총정비비용, 0)) as 순수익
            FROM Camper c
            JOIN RentalCompany rc ON c.rental_company_id = rc.rental_company_id
            LEFT JOIN Rental r ON c.camper_id = r.camper_id
            LEFT JOIN (
                SELECT 
                    em.camper_id,
                    SUM(em.repair_cost) as 총정비비용
                FROM ExternalMaintenance em
                GROUP BY em.camper_id
            ) maintenance_cost ON c.camper_id = maintenance_cost.camper_id
            WHERE c.camper_id IN (
                SELECT DISTINCT camper_id 
                FROM Rental 
                WHERE rental_start_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
            )
            GROUP BY c.camper_id, c.name, rc.name, maintenance_cost.총정비비용
            HAVING COUNT(r.rental_id) > 0
            ORDER BY 순수익 DESC
            """;
        
        return executeQuery(sql);
    }
    
    /**
     * 테스트용 복합 쿼리 2: 직원별 정비 실적 및 부품 사용 현황
     * 4개 테이블 조인 + 부속질의 + GROUP BY
     */
    public QueryResult executeEmployeeMaintenanceStats() throws SQLException {
        String sql = """
            SELECT 
                e.employee_name as 직원명,
                e.department_name as 부서명,
                COUNT(im.internal_maintenance_id) as 정비건수,
                AVG(im.maintenance_duration_minutes) as 평균정비시간_분,
                SUM(p.part_price) as 사용부품총비용,
                (SELECT COUNT(*) 
                 FROM InternalMaintenance 
                 WHERE employee_id = e.employee_id 
                 AND maintenance_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)) as 최근6개월정비건수
            FROM Employee e
            JOIN InternalMaintenance im ON e.employee_id = im.employee_id
            JOIN Part p ON im.part_id = p.part_id
            JOIN Camper c ON im.camper_id = c.camper_id
            WHERE e.role = '정비' 
            AND im.maintenance_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
            GROUP BY e.employee_id, e.employee_name, e.department_name
            HAVING COUNT(im.internal_maintenance_id) >= 1
            ORDER BY 사용부품총비용 DESC, 정비건수 DESC
            """;
        
        return executeQuery(sql);
    }
    
    /**
     * 테스트용 복합 쿼리 3: 고객별 대여 패턴 및 정비 요청 분석
     * 5개 테이블 조인 + 부속질의 + GROUP BY
     */
    public QueryResult executeCustomerRentalPatterns() throws SQLException {
        String sql = """
            SELECT 
                cust.customer_name as 고객명,
                COUNT(DISTINCT r.rental_id) as 총대여횟수,
                AVG(r.rental_period) as 평균대여기간_일,
                SUM(r.bill_amount + COALESCE(r.additional_charges_amount, 0)) as 총지불금액,
                COUNT(DISTINCT em.external_maintenance_id) as 외부정비요청횟수,
                COALESCE(SUM(em.repair_cost), 0) as 정비총비용,
                rc.name as 주로이용하는렌탈회사,
                (SELECT MAX(rental_start_date) 
                 FROM Rental 
                 WHERE driver_license_number = cust.license_number) as 최근대여일
            FROM Customer cust
            JOIN Rental r ON cust.license_number = r.driver_license_number
            JOIN Camper c ON r.camper_id = c.camper_id
            JOIN RentalCompany rc ON r.rental_company_id = rc.rental_company_id
            LEFT JOIN ExternalMaintenance em ON (cust.license_number = em.driver_license_number 
                                               AND r.camper_id = em.camper_id)
            WHERE cust.customer_id IN (
                SELECT customer_id 
                FROM Customer 
                WHERE previous_rental_date IS NOT NULL
            )
            AND r.rental_start_date >= DATE_SUB(CURDATE(), INTERVAL 2 YEAR)
            GROUP BY cust.customer_id, cust.customer_name, rc.name
            HAVING COUNT(DISTINCT r.rental_id) >= 1
            ORDER BY 총지불금액 DESC, 총대여횟수 DESC
            """;
        
        return executeQuery(sql);
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
