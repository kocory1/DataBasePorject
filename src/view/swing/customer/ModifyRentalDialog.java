package view.swing.customer;

import User.dao_user.RentalDAO;
import User.dao_user.CamperDAO;
import User.model.Rental;
import User.model.Camper;
import User.model.Period;
import common.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 회원 대여 수정 다이얼로그
 * - 일정 변경(대여 시작일, 기간, 납입기한)
 * - 캠핑카 변경
 * (기존 GUI 레이아웃·컴포넌트 배치는 전부 그대로 유지됨)
 */
public class ModifyRentalDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    // -------------------------------
    // View 컴포넌트
    // -------------------------------
    private JTabbedPane tabbedPane;

    // [일정 변경] 탭
    private JTextField tfNewStartDate;
    private JTextField tfNewPeriod;
    private JButton btnUpdateDate;

    // [캠핑카 변경] 탭
    private JTable availableCampersTable;
    private DefaultTableModel availableCampersTableModel;
    private JButton btnUpdateCamper;

    // 하단: 취소 버튼
    private JButton btnCancel;

    // -------------------------------
    // 상태 변수
    // -------------------------------
    private int rentalId;               // 수정할 Rental ID
    private String currentLicense;      // 로그인한 회원의 license (운전면허번호)

    private boolean rentalModified = false;  // 수정 완료 여부 플래그

    // DAO
    private RentalDAO rentalDAO = new RentalDAO();
    private CamperDAO camperDAO = new CamperDAO();

    /**
     * 생성자
     * @param parent 부모 프레임
     * @param rentalId 수정할 대여 ID
     * @param license 로그인한 회원의 운전면허번호
     * @param existingCamperId 기존 등록된 캠핑카 ID
     * @param existingStartDate 기존 등록된 대여 시작일
     * @param existingPeriod 기존 등록된 대여 기간
     */
    public ModifyRentalDialog(JFrame parent,
                              int rentalId,
                              String license,
                              int existingCamperId,
                              java.sql.Date existingStartDate,
                              int existingPeriod) {
        super(parent, "대여 수정", true);
        this.rentalId = rentalId;
        this.currentLicense = license;

        initComponents();
        loadAvailableCampers(); // 가능한 캠핑카 목록 로드
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * 컴포넌트 초기화 및 레이아웃 설정
     * (기존 작성해두신 레이아웃 코드를 그대로 유지했습니다)
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        tabbedPane = new JTabbedPane();

        // -------------------------------
        // 1) 일정 변경 탭
        // -------------------------------
        JPanel panelDate = new JPanel(new GridLayout(3, 2, 5, 5));
        panelDate.setBorder(BorderFactory.createTitledBorder("대여 일정 변경"));

        panelDate.add(new JLabel("새 시작일 (YYYY-MM-DD):"));
        tfNewStartDate = new JTextField();
        panelDate.add(tfNewStartDate);

        panelDate.add(new JLabel("새 기간 (일):"));
        tfNewPeriod = new JTextField();
        panelDate.add(tfNewPeriod);

        btnUpdateDate = new JButton("일정 변경");
        panelDate.add(btnUpdateDate);
        panelDate.add(new JLabel("")); // 빈 셀 채우기

        tabbedPane.addTab("일정 변경", panelDate);

        // -------------------------------
        // 2) 캠핑카 변경 탭
        // -------------------------------
        JPanel panelCamper = new JPanel(new BorderLayout(5, 5));
        panelCamper.setBorder(BorderFactory.createTitledBorder("캠핑카 변경"));

        // 가능한 캠핑카 목록 테이블
        availableCampersTableModel = new DefaultTableModel(
            new Object[] { "캠핑카 ID", "이름", "차량번호", "좌석수", "대여요금" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableCampersTable = new JTable(availableCampersTableModel);
        availableCampersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane campersScroll = new JScrollPane(availableCampersTable);
        campersScroll.setPreferredSize(new Dimension(450, 200));
        
        panelCamper.add(new JLabel("현재 일정에 예약 가능한 캠핑카:"), BorderLayout.NORTH);
        panelCamper.add(campersScroll, BorderLayout.CENTER);

        btnUpdateCamper = new JButton("선택한 캠핑카로 변경");
        JPanel camperButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        camperButtonPanel.add(btnUpdateCamper);
        panelCamper.add(camperButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("캠핑카 변경", panelCamper);

        add(tabbedPane, BorderLayout.CENTER);

        // -------------------------------
        // 3) 하단 버튼 패널
        // -------------------------------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancel = new JButton("취소");
        bottomPanel.add(btnCancel);
        add(bottomPanel, BorderLayout.SOUTH);

        // -------------------------------
        // 4) 이벤트 리스너
        // -------------------------------
        btnUpdateDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRentalDate();
            }
        });

        btnUpdateCamper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRentalCamper();
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
     * 대여 일정(시작일, 기간, 납입기한) 수정 처리
     * ★ 기존 Date.valueOf(...) 부분을 SimpleDateFormat → java.sql.Date 변환으로 교체
     * ★ 기존 대여를 제외한 중복 체크 로직 추가
     */
    private void updateRentalDate() {
        String dateText = tfNewStartDate.getText().trim();
        String periodText = tfNewPeriod.getText().trim();

        if (dateText.isEmpty() || periodText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "시작일과 기간을 모두 입력해주세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.sql.Date newStartDate;
        int newPeriod;
        java.sql.Date newPaymentDueDate;
        try {
            // 1) 문자열 → java.util.Date 로 파싱 (YYYY-MM-DD)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date utilDate = sdf.parse(dateText);
            // 2) java.sql.Date 로 변환
            newStartDate = new java.sql.Date(utilDate.getTime());

            // 3) 기간 파싱
            newPeriod = Integer.parseInt(periodText);
            if (newPeriod <= 0) {
                JOptionPane.showMessageDialog(this,
                        "기간은 1 이상의 정수로 입력해주세요.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4) 납입기한 계산 (예: 시작일 + (기간 + 1)일)
            long dueMillis = newStartDate.getTime() + ((long)newPeriod + 1) * 24 * 60 * 60 * 1000;
            newPaymentDueDate = new java.sql.Date(dueMillis);
        } catch (ParseException pe) {
            JOptionPane.showMessageDialog(this,
                    "날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형태로 입력해주세요.",
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

        // ★ 개선된 중복 검사 (현재 수정 중인 대여 제외)
        try {
            int camperId = getCurrentCamperId();
            boolean overlap = isOverlappingExcludingCurrent(camperId, newStartDate, newPeriod, rentalId);
            
            if (overlap) {
                // 단순히 불가능하다고 알려주고, 예약된 기간 정보 표시
                showReservedPeriodsDialog(camperId, "선택한 기간은 다른 예약과 겹칩니다.");
                return;
            }

            // 중복이 없으면 기존 방식대로 업데이트
            boolean success = rentalDAO.updateRentalDate(rentalId, currentLicense, newStartDate, newPeriod, newPaymentDueDate);
            if (success) {
                rentalModified = true;
                JOptionPane.showMessageDialog(this,
                        "대여 일정이 수정되었습니다.",
                        "수정 완료",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "대여 일정 수정에 실패했습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "수정 중 오류 발생: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 대여 캠핑카 변경 처리 (테이블에서 선택한 캠핑카로)
     */
    private void updateRentalCamper() {
        int selectedRow = availableCampersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "변경할 캠핑카를 선택해주세요.",
                    "선택 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 테이블에서 선택된 캠핑카 ID 가져오기
        int newCamperId = (Integer) availableCampersTableModel.getValueAt(selectedRow, 0);
        
        try {
            // 현재 캠핑카와 같은지 확인
            int currentCamperId = getCurrentCamperId();
            if (currentCamperId == newCamperId) {
                JOptionPane.showMessageDialog(this,
                        "현재 캠핑카와 동일한 캠핑카입니다.",
                        "변경 불필요",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 선택된 캠핑카로 변경 (이미 가능한 캠핑카만 표시했으므로 중복 체크 불필요)
            boolean success = rentalDAO.updateRentalCamper(rentalId, currentLicense, newCamperId);
            if (success) {
                rentalModified = true;
                String camperName = (String) availableCampersTableModel.getValueAt(selectedRow, 1);
                JOptionPane.showMessageDialog(this,
                        "캠핑카가 '" + camperName + "'로 변경되었습니다.",
                        "변경 완료",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "캠핑카 변경에 실패했습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "변경 중 오류 발생: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 현재 일정에 예약 가능한 캠핑카 목록을 로드
     */
    private void loadAvailableCampers() {
        try {
            // 현재 대여의 시작일과 기간 조회
            java.sql.Date startDate = getExistingStartDate();
            int period = getExistingPeriod();
            
            // 해당 기간에 가능한 캠핑카 목록 조회
            List<Camper> availableCampers = camperDAO.getAvailableCampers(startDate, period);
            
            // 현재 캠핑카도 목록에 추가 (자기 자신은 당연히 가능)
            int currentCamperId = getCurrentCamperId();
            Camper currentCamper = camperDAO.getCamperById(currentCamperId);
            if (currentCamper != null) {
                // 중복 체크 후 추가
                boolean alreadyExists = availableCampers.stream()
                    .anyMatch(c -> c.getCamperId() == currentCamperId);
                if (!alreadyExists) {
                    availableCampers.add(0, currentCamper); // 맨 앞에 추가
                }
            }
            
            // 테이블에 데이터 설정
            availableCampersTableModel.setRowCount(0);
            for (Camper camper : availableCampers) {
                Object[] row = {
                    camper.getCamperId(),
                    camper.getName(),
                    camper.getVehicleNumber(),
                    camper.getSeats(),
                    String.format("%.0f원", camper.getRentalFee())
                };
                availableCampersTableModel.addRow(row);
                
                // 현재 캠핑카라면 선택 상태로 설정
                if (camper.getCamperId() == currentCamperId) {
                    int rowIndex = availableCampersTableModel.getRowCount() - 1;
                    availableCampersTable.setRowSelectionInterval(rowIndex, rowIndex);
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "가능한 캠핑카 목록을 불러오는 중 오류 발생: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 수정 여부 반환 */
    public boolean isRentalModified() {
        return rentalModified;
    }

    // -----------------------------------------------
    // 헬퍼 메서드들
    // -----------------------------------------------

    /**
     * 현재 수정 중인 대여를 제외하고 중복 체크
     */
    private boolean isOverlappingExcludingCurrent(int camperId, java.sql.Date startDate, int period, int excludeRentalId) throws SQLException {
        String sql = ""
            + "SELECT COUNT(*) AS cnt "
            + "FROM Rental "
            + "WHERE camper_id = ? "
            + "  AND rental_id != ? "
            + "  AND NOT ( DATE_ADD(rental_start_date, INTERVAL rental_period DAY) < ? "
            + "            OR rental_start_date > DATE_ADD(?, INTERVAL ? DAY) )";

        try (Connection conn = DBConnect.getUserConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, camperId);
            pstmt.setInt(2, excludeRentalId);
            pstmt.setDate(3, startDate);
            pstmt.setDate(4, startDate);
            pstmt.setInt(5, period);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        }
        return false;
    }

    /**
     * 예약된 기간 정보를 보여주는 다이얼로그
     */
    private void showReservedPeriodsDialog(int camperId, String message) {
        try {
            List<Period> periods = camperDAO.getRentalPeriodsForCamper(camperId);
            
            StringBuilder sb = new StringBuilder();
            sb.append(message).append("\n\n");
            sb.append("현재 예약된 기간:\n");
            
            if (periods.isEmpty()) {
                sb.append("예약된 기간이 없습니다.");
            } else {
                for (Period p : periods) {
                    sb.append("• ").append(p.getStartDate()).append(" ~ ").append(p.getEndDate()).append("\n");
                }
                sb.append("\n다른 날짜를 선택해 주세요.");
            }
            
            JOptionPane.showMessageDialog(this,
                    sb.toString(),
                    "예약 불가",
                    JOptionPane.WARNING_MESSAGE);
                    
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    message + "\n\n예약 기간 정보를 가져올 수 없습니다.",
                    "예약 불가",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 기존 대여의 camper_id를 반환
     */
    private int getCurrentCamperId() throws SQLException {
        String sql = "SELECT camper_id FROM Rental WHERE rental_id = ?";
        try (Connection conn = DBConnect.getUserConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("camper_id");
                }
            }
        }
        throw new SQLException("해당 렌탈 ID를 찾을 수 없습니다: " + rentalId);
    }

    /**
     * 기존 대여의 시작일을 반환
     */
    private java.sql.Date getExistingStartDate() throws SQLException {
        String sql = "SELECT rental_start_date FROM Rental WHERE rental_id = ?";
        try (Connection conn = DBConnect.getUserConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("rental_start_date");
                }
            }
        }
        throw new SQLException("해당 렌탈 ID를 찾을 수 없습니다: " + rentalId);
    }

    /**
     * 기존 대여의 기간을 반환
     */
    private int getExistingPeriod() throws SQLException {
        String sql = "SELECT rental_period FROM Rental WHERE rental_id = ?";
        try (Connection conn = DBConnect.getUserConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rental_period");
                }
            }
        }
        throw new SQLException("해당 렌탈 ID를 찾을 수 없습니다: " + rentalId);
    }
}
