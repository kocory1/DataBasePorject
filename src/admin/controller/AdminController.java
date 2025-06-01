// controller/AdminController.java
package admin.controller;

import view.*;
import admin.model.MenuChoice;
import common.DBConnect;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 관리자 기능 총괄 컨트롤러
 * View와 Service 사이의 중재자 역할
 */
public class AdminController {
    private AdminView view;
    private DatabaseController databaseController;
    private TableController tableController;
    private QueryController queryController;
    private CamperDetailController camperDetailController;
    private Connection connection;

    public AdminController(AdminView view) {
        this.view = view;
        initializeConnection();
        initializeSubControllers();
    }

    /**
     * 애플리케이션 시작
     */
    public void start() {
        // 별도의 스레드에서 실행하여 GUI가 멈추지 않도록 함
        new Thread(() -> {
            try {
                view.showWelcomeMessage();

                if (!testDatabaseConnection()) {
                    view.showError("데이터베이스 연결 실패");
                    return;
                }

                showMainMenu();

            } catch (Exception e) {
                view.showError("시스템 오류: " + e.getMessage());
            } finally {
                cleanup();
            }
        }).start();
    }

    /**
     * 메인 메뉴 처리
     */
    private void showMainMenu() {
        while (true) {
            MenuChoice choice = view.showMainMenu();

            switch (choice) {
                case DATABASE_INIT:
                    databaseController.handleInitialization();
                    break;
                case TABLE_MANAGEMENT:
                    tableController.handleTableManagement();
                    break;
                case CAMPER_DETAIL:
                    camperDetailController.handleCamperDetailView();
                    break;
                case QUERY_EXECUTION:
                    queryController.handleQueryExecution();
                    break;
                case EXIT:
                    view.showGoodbyeMessage();
                    return;
                default:
                    view.showError("잘못된 선택입니다.");
            }
        }
    }

    private void initializeConnection() {
        try {
            this.connection = DBConnect.getRootConnection();
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 연결 실패", e);
        }
    }

    private void initializeSubControllers() {
        this.databaseController = new DatabaseController(view.getDatabaseView(), connection);
        this.tableController = new TableController(view.getTableView(), connection);
        this.queryController = new QueryController(view.getQueryView(), connection);
        this.camperDetailController = new CamperDetailController(view.getCamperDetailView(), connection);
    }

    private boolean testDatabaseConnection() {
        return DBConnect.testConnection();
    }

    private void cleanup() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("연결 종료 중 오류: " + e.getMessage());
        }
    }
}