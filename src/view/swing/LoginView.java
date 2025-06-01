package view.swing;

import admin.controller.AdminController;
import view.swing.customer.CustomerMainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static common.DBConnect.getUserConnection;

/**
 * 로그인 화면
 * - 관리자 / 일반회원 로그인 분기
 * (기존 레이아웃·컴포넌트 배치는 그대로 유지)
 */
public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton btnAdminLogin;
    private JButton btnCustomerLogin;

    public LoginView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("캠핑카 대여 시스템 - 로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 상단: 제목
        JLabel lblTitle = new JLabel("캠핑카 대여 시스템 로그인", SwingConstants.CENTER);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // 중앙: 아이디/비밀번호 입력
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        centerPanel.add(new JLabel("아이디(Username):"));
        usernameField = new JTextField();
        centerPanel.add(usernameField);

        centerPanel.add(new JLabel("비밀번호(Password):"));
        passwordField = new JPasswordField();
        centerPanel.add(passwordField);

        add(centerPanel, BorderLayout.CENTER);

        // 하단: 버튼(관리자 / 회원)
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdminLogin = new JButton("관리자 로그인");
        btnCustomerLogin = new JButton("회원 로그인");

        southPanel.add(btnAdminLogin);
        southPanel.add(btnCustomerLogin);
        add(southPanel, BorderLayout.SOUTH);

        // ActionListener 연결
        btnAdminLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginAsAdmin();
            }
        });
        btnCustomerLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCustomerLogin();
            }
        });

        setSize(420, 260);
        setLocationRelativeTo(null);
    }

    /**
     * 관리자 로그인 처리
     *  ★ 기존:
     *    Connection conn = Db1.getUserConnection();
     *    new AdminController(conn).showAdminView();
     *
     *  → 아래처럼 최소 변경만 적용
     */
    private void loginAsAdmin() {
        try {
            // 1) 기존에 작성해두신 SwingAdminView 인스턴스를 그대로 생성
            SwingAdminView adminView = new SwingAdminView();
            // 2) AdminController 생성자에 AdminView 타입으로 넘겨줌
            AdminController adminController = new AdminController(adminView);
            // 3) start() 메서드를 호출하여 관리자 화면을 띄움
            adminController.start();
            this.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "관리자 로그인 실패: " + ex.getMessage(),
                    "로그인 오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 일반 회원 로그인 처리
     *  - Db1.getUserConnection()에서 ClassNotFoundException이 발생할 수 있으므로, 
     *    해당 예외까지 잡도록 최소한으로 수정했습니다.
     */
    private void handleCustomerLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "아이디와 비밀번호를 모두 입력해주세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Db1.getUserConnection()에서 ClassNotFoundException 및 SQLException 처리
            conn = getUserConnection();
            String sql = "SELECT license_number FROM Customer WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String licenseNumber = rs.getString("license_number");
                JOptionPane.showMessageDialog(this,
                        "회원 로그인 성공",
                        "로그인 성공",
                        JOptionPane.INFORMATION_MESSAGE);

                // 반드시 licenseNumber를 넘겨서 CustomerMainView 생성
                CustomerMainView cmv = new CustomerMainView(licenseNumber);
                cmv.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "아이디 또는 비밀번호가 올바르지 않습니다.",
                        "로그인 실패",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "데이터베이스 접근 중 오류 발생: " + sqle.getMessage(),
                    "로그인 오류",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // 리소스 해제
            try {
                if (rs != null) rs.close();
            } catch (SQLException ignored) { }
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ignored) { }
            try {
                if (conn != null) conn.close();
            } catch (SQLException ignored) { }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}

