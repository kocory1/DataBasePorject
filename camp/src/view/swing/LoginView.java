package view.swing;

import common.DBConnect;
import admin.controller.AdminController;
import view.swing.customer.CustomerMainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * 로그인 화면 - 정의서 요구사항에 따른 최초 화면
 * 관리자/일반회원 선택 기능
 */
public class LoginView extends JFrame {
    private JButton adminButton;
    private JButton customerButton;
    private JPanel customerLoginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    
    public LoginView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("캠핑카 예약 시스템 - 로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 메인 버튼들
        adminButton = new JButton("관리자");
        customerButton = new JButton("일반 회원");
        
        // 버튼 스타일링
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
        adminButton.setFont(buttonFont);
        customerButton.setFont(buttonFont);
        adminButton.setPreferredSize(new Dimension(150, 50));
        customerButton.setPreferredSize(new Dimension(150, 50));
        
        // 고객 로그인 패널 (처음에는 숨김)
        customerLoginPanel = createCustomerLoginPanel();
        customerLoginPanel.setVisible(false);
    }
    
    private JPanel createCustomerLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "일반 회원 로그인", 
            0, 0, 
            new Font("맑은 고딕", Font.BOLD, 14)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // 아이디 라벨과 필드
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("아이디:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);
        
        // 비밀번호 라벨과 필드
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("비밀번호:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("로그인");
        cancelButton = new JButton("취소");
        
        loginButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.setPreferredSize(new Dimension(80, 30));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 제목 패널
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("캠핑카 예약 시스템");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // 중앙 패널
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // 선택 패널
        JPanel choicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        JLabel choiceLabel = new JLabel("사용자 유형을 선택하세요:");
        choiceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        
        JPanel labelPanel = new JPanel();
        labelPanel.add(choiceLabel);
        
        choicePanel.add(adminButton);
        choicePanel.add(customerButton);
        
        centerPanel.add(labelPanel, BorderLayout.NORTH);
        centerPanel.add(choicePanel, BorderLayout.CENTER);
        centerPanel.add(customerLoginPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // 하단 정보 패널
        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel("관리자: 자동 로그인 | 일반 회원: 아이디/비밀번호 입력");
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // 관리자 버튼 - 정의서: "별도의 패스워드 입력절차 없이 자동으로 root/1234 계정으로 접속"
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAdminLogin();
            }
        });
        
        // 일반 회원 버튼 - 로그인 패널 표시
        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCustomerLoginPanel();
            }
        });
        
        // 일반 회원 로그인 버튼
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCustomerLogin();
            }
        });
        
        // 취소 버튼
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCustomerLoginPanel();
            }
        });
        
        // 엔터키로 로그인
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCustomerLogin();
            }
        });
    }
    
    /**
     * 관리자 로그인 처리 - 자동 로그인 (정의서 요구사항)
     */
    private void handleAdminLogin() {
        try {
            // root/1234 계정으로 자동 연결 테스트
            Connection testConn = DBConnect.getRootConnection();
            testConn.close();

            JOptionPane.showMessageDialog(this,
                "관리자로 로그인되었습니다.", 
                "로그인 성공", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // 관리자 시스템 시작
            this.setVisible(false);
            SwingAdminView adminView = new SwingAdminView();
            AdminController adminController = new AdminController(adminView);
            adminController.start();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "관리자 로그인 실패:\n" + ex.getMessage() + 
                "\n\nMySQL 서버가 실행중인지, root 계정 비밀번호가 '1234'인지 확인하세요.", 
                "로그인 실패", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 일반 회원 로그인 패널 표시
     */
    private void showCustomerLoginPanel() {
        customerLoginPanel.setVisible(true);
        usernameField.requestFocus();
        pack(); // 크기 자동 조정
        setLocationRelativeTo(null);
    }
    
    /**
     * 일반 회원 로그인 패널 숨김
     */
    private void hideCustomerLoginPanel() {
        customerLoginPanel.setVisible(false);
        usernameField.setText("");
        passwordField.setText("");
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * 일반 회원 로그인 처리 - 정의서: "고객정보 테이블에 저장되어 있는 계정/비번과 일치하는지 확인"
     */
    private void handleCustomerLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "아이디와 비밀번호를 모두 입력해주세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (authenticateCustomer(username, password)) {
                JOptionPane.showMessageDialog(this, 
                    "일반 회원으로 로그인되었습니다.", 
                    "로그인 성공", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // 일반 회원 메인 화면 시작
                this.setVisible(false);
                
                // 실제 로그인 정보를 가져오기
                String licenseNumber = getLicenseNumberForUser(username);
                CustomerMainView customerView = new CustomerMainView(username, licenseNumber);
                customerView.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "아이디 또는 비밀번호가 일치하지 않습니다.", 
                    "로그인 실패", 
                    JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocus();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "로그인 처리 중 오류 발생:\n" + ex.getMessage(), 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 사용자 아이디로 운전면허번호 가져오기
     */
    private String getLicenseNumberForUser(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String licenseNumber = "";
        
        try {
            conn = DBConnect.getUserConnection();
            
            String sql = "SELECT license_number FROM customer WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                licenseNumber = rs.getString("license_number");
            }
            
        } catch (SQLException e) {
            System.err.println("운전면허번호 조회 오류: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        
        return licenseNumber;
    }
    
    /**
     * 고객 인증 - 정의서: "고객정보 테이블에 저장되어 있는 계정/비번과 일치하는지 확인"
     */
    private boolean authenticateCustomer(String username, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // user1/user1 계정으로 DB 접속
            conn = DBConnect.getUserConnection();
            
            // 고객정보 테이블에서 계정 확인 (실제 테이블 구조에 맞게 수정)
            String sql = "SELECT customer_id FROM customer WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();
            
            return rs.next(); // 결과가 있으면 true
            
        } catch (SQLException e) {
            // user1 계정 접속 실패 시
            throw new SQLException("데이터베이스 접속에 실패했습니다. user1 계정이 설정되어 있는지 확인하세요.");
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // 기본 Look and Feel 사용
                }
                
                new LoginView().setVisible(true);
            }
        });
    }
}
