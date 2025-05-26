package view.swing;

import admin.model.CrudResult;
import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * 사용자 메시지 표시와 관련된 헬퍼 클래스
 * 성공, 오류, 취소 등 다양한 유형의 메시지를 처리합니다.
 */
public class MessageHelper {
    
    private final JFrame parentFrame;
    private final JTextArea messageArea;
    
    public MessageHelper(JFrame parentFrame, JTextArea messageArea) {
        this.parentFrame = parentFrame;
        this.messageArea = messageArea;
    }
    
    /**
     * 성공 메시지 표시
     * @param message 메시지 내용
     */
    public void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            // 메시지 영역에 성공 메시지 표시
            messageArea.setForeground(new Color(0, 120, 0));
            messageArea.setText("✅ " + message);
            
            // 1초 후 메시지 영역 색상 원래대로 되돌리기
            scheduleResetColor();
        });
    }
    
    /**
     * 오류 메시지 표시
     * @param message 메시지 내용
     */
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            // 메시지 영역에 오류 표시
            messageArea.setForeground(new Color(200, 0, 0));
            messageArea.setText("❌ 오류: " + message);
            
            // 오류 대화상자 표시
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
            panel.add(iconLabel, BorderLayout.WEST);
            
            JTextArea textArea = new JTextArea(message);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(panel.getBackground());
            textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panel.add(textArea, BorderLayout.CENTER);
            
            JOptionPane.showMessageDialog(
                parentFrame,
                panel,
                "오류",
                JOptionPane.ERROR_MESSAGE
            );
            
            // 1초 후 메시지 영역 색상 원래대로 되돌리기
            scheduleResetColor();
        });
    }
    
    /**
     * 취소 메시지 표시
     */
    public void showCancelled() {
        SwingUtilities.invokeLater(() -> {
            messageArea.setForeground(new Color(128, 128, 128));
            messageArea.setText("ℹ️ 작업이 취소되었습니다.");
            
            // 1초 후 메시지 영역 색상 원래대로 되돌리기
            scheduleResetColor();
        });
    }
    
    /**
     * CRUD 작업 결과 표시
     * @param result CRUD 작업 결과
     * @param queryArea SQL 쿼리 표시 영역
     */
    public void showCrudResult(CrudResult result, JTextArea queryArea) {
        SwingUtilities.invokeLater(() -> {
            if (result.isSuccess()) {
                // 성공 메시지
                messageArea.setForeground(new Color(0, 120, 0));
                messageArea.setText(String.format("✅ 성공: %d개 행이 영향을 받았습니다.", result.getAffectedRows()));
                
                // 쿼리 영역에 실행된 SQL 표시
                if (queryArea != null) {
                    queryArea.setText(result.getSql() != null ? result.getSql() : result.getMessage());
                }
                
                // 성공 대화상자 표시
                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JLabel successLabel = new JLabel(
                    String.format("<html><b>작업이 성공적으로 완료되었습니다.</b><br>%d개 행이 영향을 받았습니다.</html>", 
                    result.getAffectedRows())
                );
                successLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                successLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
                successLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                panel.add(successLabel, BorderLayout.CENTER);
                
                JOptionPane.showMessageDialog(
                    parentFrame,
                    panel,
                    "작업 완료",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // 실패 메시지
                showError(result.getMessage());
            }
            
            // 1초 후 메시지 영역 색상 원래대로 되돌리기
            scheduleResetColor();
        });
    }
    
    /**
     * 일반 정보 메시지 표시
     * @param message 메시지 내용
     */
    public void showInfo(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setForeground(Color.BLACK);
            messageArea.setText(message);
        });
    }
    
    /**
     * 경고 메시지 표시
     * @param message 메시지 내용
     */
    public void showWarning(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setForeground(new Color(200, 120, 0));
            messageArea.setText("⚠️ " + message);
            
            // 1초 후 메시지 영역 색상 원래대로 되돌리기
            scheduleResetColor();
        });
    }
    
    /**
     * 진행 상황 메시지 표시
     * @param message 메시지 내용
     */
    public void showProgress(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setForeground(new Color(0, 100, 150));
            messageArea.setText("🔄 " + message);
        });
    }
    
    /**
     * 1초 후 메시지 영역 텍스트 색상을 기본값으로 되돌리는 타이머 설정
     */
    private void scheduleResetColor() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> messageArea.setForeground(Color.BLACK));
            }
        }, 1000);
    }
}