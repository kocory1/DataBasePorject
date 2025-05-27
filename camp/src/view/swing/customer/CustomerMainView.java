package view.swing.customer;

import User.dao_user.CamperDAO;
import User.dao_user.MaintenanceDAO;
import User.dao_user.RentalDAO;
import User.model.Camper;
import User.model.MaintenanceRecord;
import User.model.Rental;
import common.DBConnect;
import view.swing.MessageHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 일반 회원 메인 화면
 * - 캠핑카 조회
 * - 대여 등록
 * - 내 대여 내역 조회/수정/삭제
 * - 외부 정비 요청
 */
public class CustomerMainView extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 데이터 액세스 객체
    private CamperDAO camperDAO = new CamperDAO();
    private RentalDAO rentalDAO = new RentalDAO();
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    
    // 로그인한 고객 정보
    private String username;
    private String licenseNumber;  // 대여 조회/등록 시 필요
    
    // UI 컴포넌트
    private JTabbedPane tabbedPane;
    private JPanel camperPanel;    // 캠핑카 조회 패널
    private JPanel rentalPanel;    // 대여 조회/관리 패널
    private JPanel maintenancePanel; // 정비 관련 패널
    
    // 캠핑카 조회 관련
    private JTable camperTable;
    private DefaultTableModel camperTableModel;
    private JButton checkAvailabilityBtn;
    private JButton rentCamperBtn;
    
    // 대여 조회/관리 관련
    private JTable rentalTable;
    private DefaultTableModel rentalTableModel;
    private JButton modifyRentalBtn;
    private JButton deleteRentalBtn;
    private JButton requestMaintenanceBtn;
    
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
        
        // 캠핑카 조회 패널 설정
        setupCamperPanel();
        
        // 대여 조회/관리 패널 설정
        setupRentalPanel();
        
        // 정비 관련 패널 초기화
        maintenancePanel = new JPanel();
        setupMaintenancePanel();
    }
    
    private void setupCamperPanel() {
        camperPanel = new JPanel(new BorderLayout());
        
        // 테이블 설정
        String[] columns = {"ID", "이름", "차량번호", "승차인원", "대여비용", "대여회사", "등록일자"};
        camperTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 편집 불가능
            }
        };
        
        camperTable = new JTable(camperTableModel);
        camperTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(camperTable);
        
        // 버튼 패널 설정
        JPanel buttonPanel = new JPanel();
        checkAvailabilityBtn = new JButton("대여 가능 확인");
        rentCamperBtn = new JButton("대여 신청");
        
        buttonPanel.add(checkAvailabilityBtn);
        buttonPanel.add(rentCamperBtn);
        
        camperPanel.add(scrollPane, BorderLayout.CENTER);
        camperPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupRentalPanel() {
        rentalPanel = new JPanel(new BorderLayout());
        
        // 테이블 설정
        String[] columns = {"대여ID", "캠핑카ID", "시작일", "기간(일)", "요금", "납입기한"};
        rentalTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 편집 불가능
            }
        };
        
        rentalTable = new JTable(rentalTableModel);
        rentalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(rentalTable);
        
        // 버튼 패널 설정
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
    
    // 정비 관련 패널 설정
    private void setupMaintenancePanel() {
        maintenancePanel.removeAll();
        maintenancePanel.setLayout(new BorderLayout());
        
        // 내 정비 요청 목록 표시
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("정비 요청 목록"));
        
        // 테이블 설정
        String[] columns = {"캠핑카ID", "정비소", "정비일자", "비용", "정비내용", "기타내용"};
        DefaultTableModel maintenanceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 편집 불가능
            }
        };
        
        JTable maintenanceTable = new JTable(maintenanceTableModel);
        maintenanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 정비 요청 목록 가져오기
        try {
            // 대여 테이블에서 내가 대여한 캠핑카 ID 목록 가져오기
            ArrayList<Integer> myCamperIds = new ArrayList<>();
            ArrayList<Rental> rentals = rentalDAO.getRentalsByLicense(licenseNumber);
            
            for (Rental rental : rentals) {
                if (!myCamperIds.contains(rental.getCamperId())) {
                    myCamperIds.add(rental.getCamperId());
                }
            }
            
            // 각 캠핑카의 외부 정비 기록 가져오기
            MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
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
        // 대여 가능 확인 버튼
        checkAvailabilityBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkCamperAvailability();
            }
        });
        
        // 대여 신청 버튼
        rentCamperBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRentalDialog();
            }
        });
        
        // 대여 정보 수정 버튼
        modifyRentalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyRental();
            }
        });
        
        // 대여 취소 버튼
        deleteRentalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRental();
            }
        });
        
        // 정비 요청 버튼
        requestMaintenanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestMaintenance();
            }
        });
        
        // 테이블 새로고침을 위한 탭 변경 이벤트
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                loadCamperData();
            } else if (tabbedPane.getSelectedIndex() == 1) {
                loadRentalData();
            } else if (tabbedPane.getSelectedIndex() == 2) {
                setupMaintenancePanel();
            }
        });
    }
    
    // 캠핑카 데이터 로드
    private void loadCamperData() {
        try {
            camperTableModel.setRowCount(0);
            ArrayList<Camper> campers = camperDAO.getAllCampers();
            
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
    
    // 대여 내역 로드
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
                
                // 디버깅용 출력
                System.out.println("   👉 대여 정보: ID=" + rental.getRentalId() + 
                                  ", 캠핑카ID=" + rental.getCamperId() + 
                                  ", 시작일=" + rental.getRentalStartDate());
            }
            
        } catch (Exception e) {
            MessageHelper.showErrorMessage(this, "대여 정보 로드 오류", e.getMessage());
            e.printStackTrace(); // 디버깅용 스택 트레이스 출력
        }
    }
    
    // 캠핑카 대여 가능 확인
    private void checkCamperAvailability() {
        int selectedRow = camperTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "캠핑카 선택", "대여 가능 여부를 확인할 캠핑카를 선택해주세요.");
            return;
        }
        
        int camperId = (int) camperTableModel.getValueAt(selectedRow, 0);
        boolean isAvailable = camperDAO.isAvailable(camperId);
        
        // 대여 가능 여부에 대한 메시지 표시
        if (isAvailable) {
            MessageHelper.showInfoMessage(this, "대여 가능 확인", "선택한 캠핑카는 대여 가능합니다.");
        } else {
            MessageHelper.showWarningMessage(this, "대여 불가", "선택한 캠핑카는 이미 대여 중입니다.");
        }
    }
    
    // 대여 신청 다이얼로그 표시
    private void showRentalDialog() {
        int selectedRow = camperTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "캠핑카 선택", "대여할 캠핑카를 선택해주세요.");
            return;
        }
        
        int camperId = (int) camperTableModel.getValueAt(selectedRow, 0);
        boolean isAvailable = camperDAO.isAvailable(camperId);
        
        if (!isAvailable) {
            MessageHelper.showWarningMessage(this, "대여 불가", "선택한 캠핑카는 이미 대여 중입니다.");
            return;
        }
        
        // 대여 정보 입력 다이얼로그
        RentalDialog dialog = new RentalDialog(this, camperId, licenseNumber);
        dialog.setVisible(true);
        
        // 대여 등록이 성공하면 테이블 갱신
        if (dialog.isRentalRegistered()) {
            loadRentalData();
            tabbedPane.setSelectedIndex(1);  // 대여 내역 탭으로 전환
        }
    }
    
    // 대여 정보 수정
    private void modifyRental() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "대여 선택", "수정할 대여 정보를 선택해주세요.");
            return;
        }
        
        int rentalId = (int) rentalTableModel.getValueAt(selectedRow, 0);
        
        // 기존 대여 정보 가져오기
        ArrayList<Rental> rentals = rentalDAO.getRentalsByLicense(licenseNumber);
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
        
        // 대여 정보 수정 다이얼로그
        ModifyRentalDialog dialog = new ModifyRentalDialog(this, selectedRental);
        dialog.setVisible(true);
        
        // 수정이 성공하면 테이블 갱신
        if (dialog.isRentalModified()) {
            loadRentalData();
        }
    }
    
    // 대여 취소
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
                rentalDAO.deleteRental(rentalId);
                MessageHelper.showInfoMessage(this, "대여 취소 성공", "대여가 성공적으로 취소되었습니다.");
                loadRentalData();
            } catch (Exception e) {
                MessageHelper.showErrorMessage(this, "대여 취소 오류", e.getMessage());
            }
        }
    }
    
    // 정비 요청
    private void requestMaintenance() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageHelper.showWarningMessage(this, "대여 선택", "정비를 요청할 캠핑카의 대여 정보를 선택해주세요.");
            return;
        }
        
        int camperId = (int) rentalTableModel.getValueAt(selectedRow, 1);
        
        // 정비 요청 다이얼로그
        MaintenanceRequestDialog dialog = new MaintenanceRequestDialog(this, camperId, licenseNumber);
        dialog.setVisible(true);
        
        // 정비 요청이 성공하면 정비 정보 탭으로 전환
        if (dialog.isRequestSuccess()) {
            tabbedPane.setSelectedIndex(2);  // 정비 정보 탭으로 전환
            setupMaintenancePanel(); // 정비 정보 탭 갱신
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // 기본 Look and Feel 사용
            }
            
            // 테스트용 라이센스 번호
            new CustomerMainView("testuser", "DL0001").setVisible(true);
        });
    }
}