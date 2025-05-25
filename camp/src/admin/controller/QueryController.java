package admin.controller;

import admin.service.ComplexQueryService;
import admin.model.*;
import view.QueryView;
import java.sql.Connection;

/**
 * 쿼리 실행 컨트롤러
 */
public class QueryController {
    private QueryView view;
    private ComplexQueryService service;

    public QueryController(QueryView view, Connection connection) {
        this.view = view;
        this.service = new ComplexQueryService(connection);
    }

    /**
     * 쿼리 실행 처리
     */
    public void handleQueryExecution() {
        while (true) {
            String choice = view.showQueryMenu();

            switch (choice) {
                case "1":
                    handleCustomQuery();
                    break;
                case "2":
                    handleTestQuery1();
                    break;
                case "3":
                    handleTestQuery2();
                    break;
                case "4":
                    handleTestQuery3();
                    break;
                case "0":
                    return;
                default:
                    view.showError("잘못된 선택입니다.");
            }
        }
    }

    /**
     * 사용자 정의 쿼리 실행
     */
    private void handleCustomQuery() {
        try {
            String sql = view.getCustomSQL();
            if (sql == null || sql.trim().isEmpty()) return;

            view.showProgress("쿼리 실행 중...");
            QueryResult result = service.executeCustomQuery(sql);
            view.showQueryResult(result);

        } catch (Exception e) {
            view.showError("쿼리 실행 실패: " + e.getMessage());
        }
    }

    /**
     * 테스트 쿼리 1: 캠핑카별 수익성 분석
     */
    private void handleTestQuery1() {
        try {
            view.showProgress("캠핑카별 수익성 분석 중...");
            QueryResult result = service.executeProfitabilityAnalysis();
            view.showQueryResult(result);
            view.showSuccess("캠핑카별 수익성 분석 완료!");

        } catch (Exception e) {
            view.showError("수익성 분석 실패: " + e.getMessage());
        }
    }

    /**
     * 테스트 쿼리 2: 직원별 정비 실적 분석
     */
    private void handleTestQuery2() {
        try {
            view.showProgress("직원별 정비 실적 분석 중...");
            QueryResult result = service.executeEmployeeMaintenanceStats();
            view.showQueryResult(result);
            view.showSuccess("직원별 정비 실적 분석 완료!");

        } catch (Exception e) {
            view.showError("정비 실적 분석 실패: " + e.getMessage());
        }
    }

    /**
     * 테스트 쿼리 3: 고객별 대여 패턴 분석
     */
    private void handleTestQuery3() {
        try {
            view.showProgress("고객별 대여 패턴 분석 중...");
            QueryResult result = service.executeCustomerRentalPatterns();
            view.showQueryResult(result);
            view.showSuccess("고객별 대여 패턴 분석 완료!");

        } catch (Exception e) {
            view.showError("대여 패턴 분석 실패: " + e.getMessage());
        }
    }
}
