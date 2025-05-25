package view.swing;

import view.*;
import admin.model.MenuChoice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Swing 기반 관리자 메인 View 구현
 * 정의서 요구사항에 따른 GUI 구현
 */
public class SwingAdminView extends JFrame implements AdminView {
    
    // 서브 View들
    private SwingDatabaseView databaseView;
    private SwingTableView tableView;
    private SwingQueryView queryView;
    private SwingCamperDetailView camperDetailView;
    
    // UI 컴포넌트들
    private JButton initDbButton;
    private JButton tableManagementButton;
    private JButton camperDetailButton;
    private JButton queryExecutionButton;
    private JButton exitButton;
    
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    
    // 현재 선택된 메뉴
    private MenuChoice currentChoice = null;
    private Object choiceLock = new Object();
    
    public SwingAdminView() {
        initializeSubViews();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeSubViews() {
        this.databaseView = new SwingDatabaseView();
        this.tableView = new SwingTableView();
        this.queryView = new SwingQueryView();
        this.camperDetailView = new SwingCamperDetailView();
    }
    
    private void initializeComponents() {
        setTitle("캠핑카 예약 시스템 - 관리자 모드");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // 메뉴 버튼들
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(180, 50);
        
        initDbButton = new JButton("데이터베이스 초기화");
        tableManagementButton = new JButton("테이블 관리");
        camperDetailButton = new JButton("캠핑카 상세 조회");
        queryExecutionButton = new JButton("쿼리 실행");
        exitButton = new JButton("종료");
        
        JButton[] buttons = {initDbButton, tableManagementButton, camperDetailButton, queryExecutionButton, exitButton};
        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
        }
        
        // 종료 버튼 색상 변경
        exitButton.setBackground(new Color(255, 100, 100));
        exitButton.setForeground(Color.WHITE);
        
        // 로그 영역
        logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(240, 240, 240));
        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("시스템 로그"));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 상단 제목 패널
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("관리자 메뉴");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // 중앙 메뉴 패널
        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        menuPanel.add(initDbButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        menuPanel.add(tableManagementButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        menuPanel.add(camperDetailButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        menuPanel.add(queryExecutionButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        menuPanel.add(exitButton, gbc);
        
        add(menuPanel, BorderLayout.CENTER);
        
        // 하단 로그 패널
        add(logScrollPane, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // 창 닫기 이벤트
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
        
        // 버튼 이벤트들
        initDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (choiceLock) {
                    currentChoice = MenuChoice.DATABASE_INIT;
                    choiceLock.notify();
                }
            }
        });
        
        tableManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (choiceLock) {
                    currentChoice = MenuChoice.TABLE_MANAGEMENT;
                    choiceLock.notify();
                }
            }
        });
        
        camperDetailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (choiceLock) {
                    currentChoice = MenuChoice.CAMPER_DETAIL;
                    choiceLock.notify();
                }
            }
        });
        
        queryExecutionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (choiceLock) {
                    currentChoice = MenuChoice.QUERY_EXECUTION;
                    choiceLock.notify();
                }
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });
    }
    
    private void handleExit() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "정말로 프로그램을 종료하시겠습니까?",
            "종료 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            synchronized (choiceLock) {
                currentChoice = MenuChoice.EXIT;
                choiceLock.notify();
            }
        }
    }
    
    // AdminView 인터페이스 구현
    
    @Override
    public void showWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            logMessage("관리자 시스템에 오신 것을 환영합니다!");
            logMessage("메뉴를 선택하여 원하는 기능을 실행하세요.");
        });
    }
    
    @Override
    public void showGoodbyeMessage() {
        SwingUtilities.invokeLater(() -> {
            logMessage("관리자 시스템을 종료합니다. 안녕히 가세요!");
            
            // 2초 후 창 닫기
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    System.exit(0);
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    @Override
    public MenuChoice showMainMenu() {
        // 버튼 클릭을 기다림
        synchronized (choiceLock) {
            try {
                choiceLock.wait();
                return currentChoice;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return MenuChoice.EXIT;
            }
        }
    }
    
    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            logMessage("❌ 오류: " + message);
            JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    @Override
    public void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            logMessage("✅ 성공: " + message);
            JOptionPane.showMessageDialog(this, message, "성공", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    // 서브 View 접근자들
    
    @Override
    public DatabaseView getDatabaseView() {
        return databaseView;
    }
    
    @Override
    public TableView getTableView() {
        return tableView;
    }
    
    @Override
    public QueryView getQueryView() {
        return queryView;
    }
    
    @Override
    public CamperDetailView getCamperDetailView() {
        return camperDetailView;
    }
    
    // 유틸리티 메서드
    
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
            );
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public void logInfo(String message) {
        logMessage("ℹ️ " + message);
    }
    
    public void logWarning(String message) {
        logMessage("⚠️ " + message);
    }
}
