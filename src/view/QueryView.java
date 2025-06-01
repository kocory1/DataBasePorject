package view;

import admin.model.QueryResult;

/**
 * 쿼리 실행 View 인터페이스
 */
public interface QueryView {
    String showQueryMenu();  // 메뉴 선택 결과를 문자열로 반환
    void showError(String message);
    void showSuccess(String message);
    void showProgress(String message);
    String getCustomSQL();
    void showQueryResult(QueryResult result);
}
