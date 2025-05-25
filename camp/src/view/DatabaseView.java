package view;

/**
 * 데이터베이스 초기화 View 인터페이스
 */
public interface DatabaseView {
    boolean confirmInitialization();
    void showProgress(String message);
    void showSuccess(String message);
    void showError(String message);
    void showCancelled();
}
