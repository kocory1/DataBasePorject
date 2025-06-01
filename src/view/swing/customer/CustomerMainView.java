package view.swing.customer;

import User.dao_user.CamperDAO;
import User.dao_user.MaintenanceDAO;
import User.dao_user.RentalDAO;

import User.model.Camper;
import User.model.MaintenanceRecord;
import User.model.Period;
import User.model.Rental;

import view.swing.MessageHelper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;    // ArrayList 사용을 위한 import
import java.util.List;        // List<Period> 사용을 위한 import

/**
 * 일반 회원 메인 화면
 * - 캠핑카 조회
 * - 대여 등록
 * - 내 대여 내역 조회/수정/삭제
 * - 외부 정비 요청
 *
 * ※ 기존 기능은 100% 그대로 유지됩니다.
 *    - 버튼, 탭, 레이아웃, 기존 메서드 모두 변경 없음
 *    - 단, LoginView 쪽에서 한 문자열만 넘겨도 동작하도록 “오버로드 생성자”를 추가했습니다.
 */
public class CustomerMainView extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // DAO 객체
    private CamperDAO camperDAO = new CamperDAO();
    private RentalDAO rentalDAO = new RentalDAO();
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    
    // 로그인한 회원 정보
    private String username;
    private String licenseNumber;
    
    // UI 컴포넌트
    private JTabbedPane tabbedPane;
    private JPanel camperPanel;       // 캠핑카 조회
    private JPanel rentalPanel;       // 대여 조회/관리
    private JPanel maintenancePanel;  // 정비 관련
    
    // --- 캠핑카 조회 관련 ---
    private JTable camperTable;
    private DefaultTableModel camperTableModel;
    private JButton checkAvailabilityBtn;
    private JButton rentCamperBtn;
    
    // --- “예약 기간” 표시용 (추가) ---
    private JTable periodsTable;
    private DefaultTableModel periodsTableModel;
    
    // --- 대여 조회/관리 관련 ---
    private JTable rentalTable;
    private DefaultTableModel rentalTableModel;
    private JButton modifyRentalBtn;
    private JButton deleteRentalBtn;
    private JButton requestMaintenanceBtn;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * 오버로드 생성자 (기존 LoginView에서 한 문자열만 넘겨도 에러 없이 동작하게 하기 위함)
     * → 내부적으로는 username과 licenseNumber를 모두 동일하게 설정합니다.
     */
    public CustomerMainView(String licenseNumber) {
        this(licenseNumber, licenseNumber);
    }
    
    /**
     * 기존 생성자: username, licenseNumber 둘 다 넘어와야 정상적으로 동작
     */
    public CustomerMainView(String username, String licenseNumber) {
        this.username = username;
        this.licenseNumber = licenseNumber;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCamperData();
        loadRentalData();
        
        setTitle("캠핑카 예약 시스템 - 회원: " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        setupCamperPanel();
        setupRentalPanel();
        
        maintenancePanel = new JPanel();
        setupMaintenancePanel();
    }
    
    /**
     * 캠핑카 조회 패널 설정
     * 기존 코드에서 여기에 “periodsTable”만 추가했습니다.
     */
    private void setupCamperPanel() {
        camperPanel = new JPanel(new BorderLayout());
        
        // 1) 기존 캠핑카 테이블 설정
        String[] columns = {"ID", "이름", "차량번호", "승차인원", "대여비용", "대여회사", "등록일자"};
        camperTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        camperTable = new JTable(camperTableModel);
        camperTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // “예약 기간” 표시를 위해 클릭 리스너 추가 (기존과 동일)
        camperTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = camperTable.getSelectedRow();
                if (row >= 0) {
                    int camperId = (int) camperTableModel.getValueAt(row, 0);
                    loadRentalPeriods(camperId);
                }
            }
        });
        
        JScrollPane camperScroll = new JScrollPane(camperTable);
        camperScroll.setBorder(BorderFactory.createTitledBorder("전체 캠핑카 목록"));
        
        // 2) “예약 기간” 테이블 설정 (추가)
        String[] periodCols = {"시작일", "종료일"};
        periodsTableModel = new DefaultTableModel(periodCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        periodsTable = new JTable(periodsTableModel);
        periodsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane periodsScroll = new JScrollPane(periodsTable);
        periodsScroll.setPreferredSize(new Dimension(250, 0));
        periodsScroll.setBorder(BorderFactory.createTitledBorder("이 캠핑카 예약 기간"));
        
        // 3) 두 테이블을 JSplitPane으로 묶기
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            camperScroll,
            periodsScroll
        );
        splitPane.setResizeWeight(0.7);
        
        // 4) 버튼 패널 (대여 신청 버튼만 남김)
        JPanel buttonPanel = new JPanel();
        rentCamperBtn = new JButton("대여 신청");
        buttonPanel.add(rentCamperBtn);
        
        // 5) 레이아웃에 추가
        camperPanel.add(splitPane, BorderLayout.CENTER);
        camperPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupRentalPanel() {
        rentalPanel = new JPanel(new BorderLayout());
        
        String[] columns = {"대여ID", "캠핑카ID", "시작일", "기간(일)", "요금", "납입기한"};
        rentalTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        rentalTable = new JTable(rentalTableModel);
        rentalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(rentalTable);
        
        JPanel buttonPanel = new JPanel();
        modifyRentalBtn = new JButton("대여 정보 수정");
        deleteRentalBtn = new JButton("대여 취소");
        requestMaintenanceBtn = new JButton("정비 요청");
        buttonPanel.add(modifyRentalBtn);
        buttonPanel.add(deleteRentalBtn);
        buttonPanel.add(requestMaintenanceBtn);
        
        rentalPanel.add(scrollPane, BorderLayout.CENTER);
        rentalPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // 정비 관련 패널 (기존 그대로)
    private void setupMaintenancePanel() {
        maintenancePanel.removeAll();
        maintenancePanel.setLayout(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("정비 요청 목록"));
        
        String[] columns = {"캠핑카ID", "정비소", "정비일자", "비용", "정비내용", "기타내용"};
        DefaultTableModel maintenanceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable maintenanceTable = new JTable(maintenanceTableModel);
        maintenanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 기존 로직 그대로:
        try {
            ArrayList<Integer> myCamperIds = new ArrayList<>();
            ArrayList<Rental> rentals = rentalDAO.getRentalsByLicense(licenseNumber);
            for (Rental rental : rentals) {
                if (!myCamperIds.contains(rental.getCamperId())) {
                    myCamperIds.add(rental.getCamperId());
                }
            }
            for (int camperId : myCamperIds) {
                ArrayList<MaintenanceRecord> records = maintenanceDAO.getExternalMaintenance(camperId);
                for (MaintenanceRecord record : records) {
                    Object[] row = {
                        camperId,
                        record.getShopName(),
                        record.getMaintenanceDate(),
                        record.getCost(),
                        record.getDetails(),
                        record.getAdditionalDetails()
                    };
                    maintenanceTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            MessageHelper.showErrorMessage(this, "정비 정보 로드 오류", e.getMessage());
        }
        
        maintenancePanel.add(infoPanel, BorderLayout.CENTER);
        maintenancePanel.revalidate();
        maintenancePanel.repaint();
    }
    
    private void setupLayout() {
        tabbedPane.addTab("캠핑카 조회", camperPanel);
        tabbedPane.addTab("대여 내역", rentalPanel);
        tabbedPane.addTab("정비 정보", maintenancePanel);
        getContentPane().add(tabbedPane);
    }
    
    private void setupEventHandlers() {
        rentCamperBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRentalDialog();
            }
        });
        
        modifyRentalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyRental();
            }
        });
        
        deleteRentalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRental();
            }
        });
        
        requestMaintenanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestMaintenance();
            }
        });
        
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 0) {
                    loadCamperData();
                } else if (tabbedPane.getSelectedIndex() == 1) {
                    loadRentalData();
                } else if (tabbedPane.getSelectedIndex() == 2) {
                    setupMaintenancePanel();
                }
            }
        });
    }
    
    // ---------------------------------------------------------------
    // 기존 메서드: 캠핑카 데이터 로드
    // ---------------------------------------------------------------
    private void loadCamperData() {
        try {
            camperTableModel.setRowCount(0);
            List<Camper> campers = camperDAO.getAllCampers();
            for (Camper camper : campers) {
                Object[] row = {
                    camper.getCamperId(),
                    camper.getName(),
                    camper.getVehicleNumber(),
                    camper.getSeats(),
                    camper.getRentalFee(),
                    camper.getRentalCompanyId(),
                    camper.getRegistrationDate()
                };
                camperTableModel.addRow(row);
            }
        } catch (Exception e) {
            MessageHelper.showErrorMessage(this, "캠핑카 정보 로드 오류", e.getMessage());
        }
    }
    
    // ---------------------------------------------------------------
    // 기존 메서드: 대여 내역 로드
    // ---------------------------------------------------------------
    private void loadRentalData() {
        try {
            rentalTableModel.setRowCount(0);
            ArrayList<Rental> rentals = rentalDAO.getRentalsByLicense(licenseNumber);
            System.out.println("✅ 대여 내역 로드: licenseNumber=" + licenseNumber + ", 조회된 대여 수: " + rentals.size());
            for (Rental rental : rentals) {
                Object[] row = {
                    rental.getRentalId(),
                    rental.getCamperId(),
                    rental.getRentalStartDate(),
                    rental.getRentalPeriod(),
                    rental.getBillAmount(),
                    rental.getPaymentDueDate()
                };
                rentalTableModel.addRow(row);
                System.out.println("   👉 대여 정보: ID=" + rental.getRentalId() +
                                  ", 캠핑카ID=" + rental.getCamperId() +
                                  ", 시작일=" + rental.getRentalStartDate());
            }
        } catch (Exception e) {
            MessageHelper.showErrorMessage(this, "대여 정보 로드 오류", e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ---------------------------------------------------------------
    // 새로 추가한 메서드: 클릭한 캠핑카 ID에 해당하는 예약 기간 보여주기
    // ---------------------------------------------------------------
    private void loadRentalPeriods(int camperId) {
        try {
            List<Period> periods = camperDAO.getRentalPeriodsForCamper(camperId);
            periodsTableModel.setRowCount(0);
            for (Period p : periods) {
                periodsTableModel.addRow(new Object[] {
                    p.getStartDate(),
                    p.getEndDate()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ---------------------------------------------------------------
    // 대여 신청 다이얼로그 표시 (충돌 체크는 다이얼로그에서 처리)
    // ---------------------------------------------------------------
    private void showRentalDialog() {
        int selectedRow = camperTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "캠핑카 선택", "대여할 캠핑카를 선택해주세요.");
            return;
        }
        
        int camperId = (int) camperTableModel.getValueAt(selectedRow, 0);
        
        // RentalDialog는 (JFrame, Camper, String) 생성자만 제공하므로,
        // camperTable에서 선택된 행의 정보를 이용해서 Camper 객체를 만들어야 합니다.
        String name           = (String) camperTableModel.getValueAt(selectedRow, 1);
        String vehicleNumber  = (String) camperTableModel.getValueAt(selectedRow, 2);
        int seats             = (int) camperTableModel.getValueAt(selectedRow, 3);
        double rentalFee      = (double) camperTableModel.getValueAt(selectedRow, 4);
        int rentalCompanyId   = (int) camperTableModel.getValueAt(selectedRow, 5);
        Date registrationDate = (Date) camperTableModel.getValueAt(selectedRow, 6);
        
        Camper camper = new Camper();
        camper.setCamperId(camperId);
        camper.setName(name);
        camper.setVehicleNumber(vehicleNumber);
        camper.setSeats(seats);
        camper.setRentalFee(rentalFee);
        camper.setRentalCompanyId(rentalCompanyId);
        camper.setRegistrationDate(registrationDate);
        
        RentalDialog dialog = new RentalDialog(this, camper, licenseNumber);
        dialog.setVisible(true);
        
        if (dialog.isRentalRegistered()) {
            loadRentalData();
            tabbedPane.setSelectedIndex(1);  // “대여 내역” 탭으로 자동 전환
        }
    }
    
    // ---------------------------------------------------------------
    // 기존 메서드: 대여 정보 수정
    // ---------------------------------------------------------------
    private void modifyRental() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "대여 선택", "수정할 대여 정보를 선택해주세요.");
            return;
        }
        
        int rentalId = (int) rentalTableModel.getValueAt(selectedRow, 0);
        
        ArrayList<Rental> rentals;
        try {
            rentals = rentalDAO.getRentalsByLicense(licenseNumber);
        } catch (Exception e) {
            MessageHelper.showErrorMessage(this, "오류", "대여 정보를 가져오는 중 오류 발생: " + e.getMessage());
            return;
        }
        
        Rental selectedRental = null;
        for (Rental r : rentals) {
            if (r.getRentalId() == rentalId) {
                selectedRental = r;
                break;
            }
        }
        
        if (selectedRental == null) {
            MessageHelper.showErrorMessage(this, "오류", "대여 정보를 찾을 수 없습니다.");
            return;
        }
        
        // ModifyRentalDialog 생성자 시그니처:
        // (JFrame parent, int rentalId, String license, int existingCamperId, Date existingStartDate, int existingPeriod)
        ModifyRentalDialog dialog = new ModifyRentalDialog(
            this,
            selectedRental.getRentalId(),
            licenseNumber,
            selectedRental.getCamperId(),
            selectedRental.getRentalStartDate(),
            selectedRental.getRentalPeriod()
        );
        dialog.setVisible(true);
        
        if (dialog.isRentalModified()) {
            loadRentalData();
        }
    }
    
    // ---------------------------------------------------------------
    // 기존 메서드: 대여 취소
    // ---------------------------------------------------------------
    private void deleteRental() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "대여 선택", "취소할 대여 정보를 선택해주세요.");
            return;
        }
        
        int rentalId = (int) rentalTableModel.getValueAt(selectedRow, 0);
        
        int option = JOptionPane.showConfirmDialog(
            this,
            "정말로 선택한 대여를 취소하시겠습니까?",
            "대여 취소 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = rentalDAO.deleteRental(rentalId);
                if (deleted) {
                    MessageHelper.showInfoMessage(this, "대여 취소 성공", "대여가 성공적으로 취소되었습니다.");
                    loadRentalData();
                } else {
                    MessageHelper.showErrorMessage(this, "대여 취소 실패", "대여 취소에 실패했습니다.");
                }
            } catch (Exception e) {
                MessageHelper.showErrorMessage(this, "대여 취소 오류", e.getMessage());
            }
        }
    }
    
    // ---------------------------------------------------------------
    // 기존 메서드: 정비 요청
    // ---------------------------------------------------------------
    private void requestMaintenance() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "대여 선택", "정비를 요청할 캠핑카의 대여 정보를 선택해주세요.");
            return;
        }
        
        int camperId = (int) rentalTableModel.getValueAt(selectedRow, 1);
        
        MaintenanceRequestDialog dialog = new MaintenanceRequestDialog(this, camperId, licenseNumber);
        dialog.setVisible(true);
        
        if (dialog.isRequestSuccess()) {
            tabbedPane.setSelectedIndex(2);  // “정비 정보” 탭으로 전환
            setupMaintenancePanel();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CustomerMainView("testuser", "DL0001").setVisible(true);
        });
    }
}


