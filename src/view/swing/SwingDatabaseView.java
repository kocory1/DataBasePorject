package view.swing;

import view.DatabaseView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Swing 기반 데이터베이스 초기화 View 구현
 * 정의서 요구사항: 하나의 윈도우에서 모든 작업 처리
 */
public class SwingDatabaseView extends JDialog implements DatabaseView {
    
    private boolean userConfirmed = false;
    private Object confirmLock = new Object();
    
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JButton confirmButton;
    private JButton cancelButton;
    
    public SwingDatabaseView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("데이터베이스 초기화");
        setModal(true);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        statusLabel = new JLabel("데이터베이스 초기화 준비");
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("대기 중");
        
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 248, 248));
        
        confirmButton = new JButton("초기화 실행");
        cancelButton = new JButton("취소");
        
        confirmButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        
        // 버튼 초기 상태 명시적 설정
        confirmButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        topPanel.add(statusPanel, BorderLayout.NORTH);
        
        topPanel.add(progressBar, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 중앙 로그 패널
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("초기화 로그"));
        add(logScrollPane, BorderLayout.CENTER);
        
        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 초기 경고 메시지 표시
        showInitialWarning();
    }
    
    private void setupEventHandlers() {
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (confirmLock) {
                    userConfirmed = true;
                    confirmLock.notify();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (confirmLock) {
                    userConfirmed = false;
                    confirmLock.notify();
                }
            }
        });
    }
    
    private void showInitialWarning() {
        SwingUtilities.invokeLater(() -> {
            logArea.append("⚠️ 데이터베이스 초기화 경고 ⚠️\n");
            logArea.append("=====================================\n");
            logArea.append("• 기존 DBTEST 데이터베이스가 완전히 삭제됩니다.\n");
            logArea.append("• 새로운 테이블과 샘플 데이터가 생성됩니다.\n");
            logArea.append("• ini.sql 파일이 실행됩니다.\n");
            logArea.append("• 사용자 계정이 재설정됩니다 (user1/user1).\n");
            logArea.append("=====================================\n");
            logArea.append("계속하려면 '초기화 실행' 버튼을 클릭하세요.\n\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    // DatabaseView 인터페이스 구현
    
    @Override
    public boolean confirmInitialization() {
        SwingUtilities.invokeLater(() -> {
            // 버튼 상태 초기화 (혹시 이전 상태가 남아있을 경우를 대비)
            confirmButton.setEnabled(true);
            cancelButton.setEnabled(true);
            cancelButton.setText("취소");
            
            setVisible(true);
            logArea.append("🤔 사용자 확인을 기다리고 있습니다...\n");
        });
        
        synchronized (confirmLock) {
            try {
                confirmLock.wait();
                return userConfirmed;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }
    
    @Override
    public void showProgress(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            progressBar.setIndeterminate(true);
            progressBar.setString("진행 중...");
            
            logArea.append("🔄 " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            
            // 버튼 비활성화
            confirmButton.setEnabled(false);
            cancelButton.setText("닫기");
        });
    }
    
    @Override
    public void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("초기화 완료");
            progressBar.setIndeterminate(false);
            progressBar.setValue(100);
            progressBar.setString("완료");
            
            logArea.append("✅ " + message + "\n");
            logArea.append("\n초기화가 성공적으로 완료되었습니다!\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            
            // 완료 알림
            JOptionPane.showMessageDialog(this, message, "초기화 완료", JOptionPane.INFORMATION_MESSAGE);
            
            // 창 닫기
            dispose();
        });
    }
    
    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("초기화 실패");
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
            progressBar.setString("실패");
            
            logArea.append("❌ " + message + "\n");
            logArea.append("\n초기화 중 오류가 발생했습니다.\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            
            // 오류 알림
            JOptionPane.showMessageDialog(this, message, "초기화 실패", JOptionPane.ERROR_MESSAGE);
            
            // 창 닫기
            dispose();
        });
    }
    
    @Override
    public void showCancelled() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("초기화 취소됨");
            logArea.append("❌ 사용자가 초기화를 취소했습니다.\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            
            // 창 닫기
            dispose();
        });
    }
}
