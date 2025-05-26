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
}
