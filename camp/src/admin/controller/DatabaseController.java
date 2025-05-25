package admin.controller;

import admin.service.DatabaseService;
import view.DatabaseView;
import java.sql.Connection;

/**
 * 데이터베이스 초기화 컨트롤러
 */
public class DatabaseController {
    private DatabaseView view;
    private DatabaseService service;

    public DatabaseController(DatabaseView view, Connection connection) {
        this.view = view;
        this.service = new DatabaseService(connection);
    }

    /**
     * 데이터베이스 초기화 처리
     */
    public void handleInitialization() {
        // 1. 사용자 확인
        if (!view.confirmInitialization()) {
            view.showCancelled();
            return;
        }

        // 2. 초기화 실행
        try {
            view.showProgress("초기화 중...");

            boolean success = service.executeInitScript();

            if (success) {
                view.showSuccess("데이터베이스 초기화 완료!");
            } else {
                view.showError("초기화 중 일부 오류 발생");
            }

        } catch (Exception e) {
            view.showError("초기화 실패: " + e.getMessage());
        }
    }
}
