package view;

import admin.model.MenuChoice;

/**
 * 관리자 메인 View 인터페이스
 */
public interface AdminView {
    void showWelcomeMessage();
    void showGoodbyeMessage();
    MenuChoice showMainMenu();
    void showError(String message);
    void showSuccess(String message);
    
    // 서브 View 접근자
    DatabaseView getDatabaseView();
    TableView getTableView();
    QueryView getQueryView();
    CamperDetailView getCamperDetailView();
}
