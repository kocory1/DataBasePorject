package view.swing.customer;

import User.dao_user.CamperDAO;
import User.dao_user.RentalDAO;
import User.model.Camper;
import User.model.Period;
import User.model.Rental;
import common.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

// ↓ java.sql 풀 패키지 import 추가
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 회원 대여 등록 다이얼로그
 * - 선택된 캠핑카의 예약된 기간을 상단에 표시
 * - 대여 시작일, 대여 기간 입력 후 중복 검사 → 등록
 */
public class RentalDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private Camper selectedCamper;       // 선택된 캠핑카 정보
    private String currentLicense;       // 현재 로그인한 회원 운전면허번호

    // UI 컴포넌트
    private JTable periodsTable;         // 이미 예약된 기간 목록을 보여줄 테이블
    private DefaultTableModel periodsTableModel;

    private JTextField tfStartDate;      // 대여 시작일 입력 (YYYY-MM-DD)
    private JTextField tfPeriod;         // 대여 기간 입력 (일)
    private JButton btnCheckOverlap;     // 중복 예약 확인 버튼
    private JButton btnRegister;         // 등록 버튼
    private JButton btnCancel;           // 취소 버튼

    // DAO
    private CamperDAO camperDAO = new CamperDAO();
    private RentalDAO rentalDAO = new RentalDAO();

    // ← 새로 추가: 등록 성공 여부를 저장할 플래그
    private boolean rentalRegistered = false;

    public RentalDialog(JFrame parent, Camper camper, String license) {
        super(parent, "대여 등록", true);

        if (camper == null) {
            JOptionPane.showMessageDialog(parent,
                    "캠핑카가 선택되지 않았습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        this.selectedCamper = camper;
        this.currentLicense = license;

        initComponents();
        loadRentalPeriods();  // 다이얼로그가 열릴 때, 예약된 기간을 바로 조회하여 표시
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * UI 컴포넌트 초기화 및 레이아웃 설정
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // =========================
        // 1) 이미 예약된 기간 표시 (상단)
        // =========================
        periodsTableModel = new DefaultTableModel(
            new Object[] { "시작일", "종료일" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        periodsTable = new JTable(periodsTableModel);
        JScrollPane periodsScroll = new JScrollPane(periodsTable);
        periodsScroll.setPreferredSize(new Dimension(420, 160));
        periodsScroll.setBorder(BorderFactory.createTitledBorder(
            selectedCamper.getName() + " 예약된 기간"
        ));
        add(periodsScroll, BorderLayout.NORTH);

        // =========================
        // 2) 대여 정보 입력 (중앙)
        // =========================
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("대여 정보 입력"));

        inputPanel.add(new JLabel("대여 시작일 (YYYY-MM-DD):"));
        tfStartDate = new JTextField();
        inputPanel.add(tfStartDate);

        inputPanel.add(new JLabel("대여 기간 (일):"));
        tfPeriod = new JTextField();
        inputPanel.add(tfPeriod);

        btnCheckOverlap = new JButton("중복 예약 확인");
        inputPanel.add(btnCheckOverlap);

        btnRegister = new JButton("등록");
        inputPanel.add(btnRegister);

        add(inputPanel, BorderLayout.CENTER);

        // =========================
        // 3) 하단: 취소 버튼
        // =========================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancel = new JButton("취소");
        bottomPanel.add(btnCancel);
        add(bottomPanel, BorderLayout.SOUTH);

        // =========================
        // 4) 이벤트 리스너
        // =========================
        btnCheckOverlap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkOverlap();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerRental();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * 다이얼로그가 열릴 때 호출되어, 이 캠핑카의 예약된 기간을 조회하여 테이블에 표시
     */
    private void loadRentalPeriods() {
        try {
            int camperId = selectedCamper.getCamperId();
            List<Period> periods = camperDAO.getRentalPeriodsForCamper(camperId);
            periodsTableModel.setRowCount(0);
            for (Period p : periods) {
                periodsTableModel.addRow(new Object[]{
                    p.getStartDate(),
                    p.getEndDate()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "예약 기간 조회 중 오류 발생: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * “중복 예약 확인” 버튼 클릭 시, 입력된 시작일/기간으로 중복 검사
     */
    private void checkOverlap() {
        String startText = tfStartDate.getText().trim();
        String periodText = tfPeriod.getText().trim();

        if (startText.isEmpty() || periodText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "대여 시작일과 기간을 모두 입력해주세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.sql.Date startDate;
        int period;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date utilDate = sdf.parse(startText);
            startDate = new java.sql.Date(utilDate.getTime());

            period = Integer.parseInt(periodText);
            if (period <= 0) {
                JOptionPane.showMessageDialog(this,
                        "기간은 1 이상의 정수를 입력하세요.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (ParseException pe) {
            JOptionPane.showMessageDialog(this,
                    "날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형식으로 입력하세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(this,
                    "기간은 숫자로만 입력해야 합니다.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean overlap = rentalDAO.isOverlapping(
                selectedCamper.getCamperId(), startDate, period
            );
            if (overlap) {
                JOptionPane.showMessageDialog(this,
                        "이미 예약된 기간과 겹칩니다.",
                        "중복 예약",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "해당 기간은 예약 가능합니다.",
                        "중복 없음",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "중복 검사 중 오류 발생: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * “등록” 버튼 클릭 시 실제 DB에 INSERT 수행
     * 등록이 성공하면 rentalRegistered 플래그를 true 로 설정
     */
    private void registerRental() {
        String startText = tfStartDate.getText().trim();
        String periodText = tfPeriod.getText().trim();

        if (startText.isEmpty() || periodText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "대여 시작일과 기간을 모두 입력해주세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.sql.Date startDate;
        int period;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date utilDate = sdf.parse(startText);
            startDate = new java.sql.Date(utilDate.getTime());

            period = Integer.parseInt(periodText);
            if (period <= 0) {
                JOptionPane.showMessageDialog(this,
                        "기간은 1 이상의 정수를 입력하세요.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (ParseException pe) {
            JOptionPane.showMessageDialog(this,
                    "날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형식으로 입력하세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(this,
                    "기간은 숫자로만 입력해야 합니다.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 등록 전 충돌 체크 자동 수행
        try {
            boolean overlap = rentalDAO.isOverlapping(
                selectedCamper.getCamperId(), startDate, period
            );
            if (overlap) {
                JOptionPane.showMessageDialog(this,
                        "선택한 기간은 이미 예약된 기간과 겹칩니다.\n다른 날짜를 선택해주세요.",
                        "대여 불가",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "대여 가능 여부 확인 중 오류 발생: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 납입기한 계산(예: 시작일 + (기간+1)일)
        long dueMillis = startDate.getTime() + (long)(period + 1) * 24 * 60 * 60 * 1000;
        java.sql.Date paymentDueDate = new java.sql.Date(dueMillis);

        // 새 Rental 객체 생성
        Rental r = new Rental();
        r.setRentalId(generateNewRentalId());               // DB에서 MAX+1 조회
        r.setCamperId(selectedCamper.getCamperId());
        r.setLicenseNumber(currentLicense);
        r.setRentalCompanyId(selectedCamper.getRentalCompanyId());
        r.setRentalStartDate(startDate);
        r.setRentalPeriod(period);
        r.setBillAmount(selectedCamper.getRentalFee() * period);  
            // ※ 예시: “요금 = 시간당 비용 × 기간” 형태. 실제 로직에 맞춰 변경하세요.
        r.setPaymentDueDate(paymentDueDate);
        r.setAdditionalChargesDescription("");
        r.setAdditionalChargesAmount(0.0);

        // INSERT 수행
        try {
            rentalDAO.insertRental(r);
            rentalRegistered = true;   // ← 등록 성공 시 플래그를 true 로 설정
            JOptionPane.showMessageDialog(this,
                    "대여가 정상적으로 등록되었습니다.",
                    "등록 완료",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "대여 등록 중 오류 발생: " + ex.getMessage(),
                    "등록 오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 새로운 rental_id 생성 (DB에서 MAX(rental_id)+1 조회)
     */
    private int generateNewRentalId() {
        int newId = 1;
        String sql = "SELECT COALESCE(MAX(rental_id), 0) + 1 FROM Rental";
        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                newId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("렌탈 ID 생성 중 오류: " + e.getMessage());
            // 오류 발생 시에도 적절한 ID 생성을 위해 현재 시간을 이용한 임시 ID 생성
            newId = (int)(System.currentTimeMillis() % 100000);
        }
        return newId;
    }

    /** ← 새로 추가: 등록 성공 여부를 반환하는 메서드 */
    public boolean isRentalRegistered() {
        return rentalRegistered;
    }
}

