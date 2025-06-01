package view.swing.customer;

import User.dbConnect.Db1;               // ★ 수정: 기존 common.DBConnect → User.dbConnect.Db1
import User.dao_user.RentalDAO;
import User.model.Rental;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    private JTextField tfNewCamperId;
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
        // 기존 레이아웃에 맞춰 기존 예약 정보를 보여주려면, 여기서 DAO 호출 등을 추가하시면 됩니다.
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
        JPanel panelCamper = new JPanel(new GridLayout(2, 2, 5, 5));
        panelCamper.setBorder(BorderFactory.createTitledBorder("캠핑카 변경"));

        panelCamper.add(new JLabel("새 캠핑카 ID:"));
        tfNewCamperId = new JTextField();
        panelCamper.add(tfNewCamperId);

        btnUpdateCamper = new JButton("캠핑카 변경");
        panelCamper.add(btnUpdateCamper);
        panelCamper.add(new JLabel("")); // 빈 셀 채우기

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

        // 중복 검사 (예: getCurrentCamperId()는 DB에서 조회해야 함)
        try {
            int camperId = getCurrentCamperId();
            boolean overlap = rentalDAO.isOverlapping(camperId, newStartDate, newPeriod);
            if (overlap) {
                JOptionPane.showMessageDialog(this,
                        "이미 예약된 기간과 겹칩니다.",
                        "중복 예약",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 실제 수정 수행
            boolean success = rentalDAO.updateRentalDate(rentalId, currentLicense, newStartDate, newPeriod, newPaymentDueDate);
            if (success) {
                rentalModified = true;  // 필드가 없었다면 아래에 선언 추가
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
     * 대여 캠핑카 ID만 변경 처리
     */
    private void updateRentalCamper() {
        String camperIdText = tfNewCamperId.getText().trim();
        if (camperIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "새 캠핑카 ID를 입력해주세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int newCamperId;
        try {
            newCamperId = Integer.parseInt(camperIdText);
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(this,
                    "캠핑카 ID는 숫자로만 입력해야 합니다.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 중복 검사: 기존 대여 일정(startDate, period)을 알아야 함
        try {
            java.sql.Date existingStartDate = getExistingStartDate();
            int existingPeriod = getExistingPeriod();

            boolean overlap = rentalDAO.isOverlapping(newCamperId, existingStartDate, existingPeriod);
            if (overlap) {
                JOptionPane.showMessageDialog(this,
                        "새 캠핑카 ID가 해당 일정과 겹치는 예약이 있습니다.",
                        "중복 예약",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 실제 수정 수행
            boolean success = rentalDAO.updateRentalCamper(rentalId, currentLicense, newCamperId);
            if (success) {
                rentalModified = true;  // 필드가 없었다면 아래에 선언 추가
                JOptionPane.showMessageDialog(this,
                        "캠핑카가 변경되었습니다.",
                        "수정 완료",
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
                    "수정 중 오류 발생: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 수정 여부 반환 */
    public boolean isRentalModified() {
        return rentalModified;
    }

    // -----------------------------------------------
    // 아래 두 메서드는 실제 DB 조회 로직을 구현해야 합니다.
    // 예시로 간단하게 stub(임시값) 형태로 작성했습니다.
    // -----------------------------------------------

    /**
     * 기존 대여의 camper_id를 반환해야 함.
     * 실제로는 RentalDAO 등을 통해 DB 조회하여 리턴.
     */
    private int getCurrentCamperId() {
        // TODO: DB에서 rentalId에 해당하는 레코드를 조회하여 camper_id를 반환
        return 0;
    }

    /**
     * 기존 대여의 시작일을 반환해야 함.
     * 실제로는 DB에서 조회하여 반환.
     */
    private java.sql.Date getExistingStartDate() {
        // TODO: DB에서 rentalId에 해당하는 레코드를 조회하여 rental_start_date를 반환
        return new java.sql.Date(System.currentTimeMillis());
    }

    /**
     * 기존 대여의 기간을 반환해야 함.
     * 실제로는 DB에서 조회하여 반환.
     */
    private int getExistingPeriod() {
        // TODO: DB에서 rentalId에 해당하는 레코드를 조회하여 rental_period를 반환
        return 1;
    }

    // -------------------------------
    // 필드 선언이 누락되어 있었다면 아래를 추가하세요:
    // private boolean rentalModified = false;
    // -------------------------------
}
