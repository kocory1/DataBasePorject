package view.swing;

import view.CamperDetailView;
import admin.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

/**
 * 캠핑카 상세 조회를 위한 Swing GUI View 구현
 * 요구사항명세서 및 정의서 요구사항 준수:
 * - 캠핑카 선택 시 자체/외부 정비 내역 표시
 * - 부품 선택 시 재고와 공급회사 정보 표시  
 * - 정비소 선택 시 상세 정보 표시
 * - 하나의 윈도우에서 모든 기능 처리
 */
public class SwingCamperDetailView extends JFrame implements CamperDetailView {
    
    private JComboBox<CamperSummary> camperComboBox;
    private JTextArea detailArea;
    private JTable maintenanceTable;
    private DefaultTableModel tableModel;
    private JButton selectButton;
    private JButton internalMaintenanceButton;
    private JButton externalMaintenanceButton;
    private JButton backButton;
    
    // 부품/정비소 상세 정보 패널 추가 (요구사항: 부품 재고/공급회사 정보, 정비소 상세 정보)
    private JPanel detailInfoPanel;
    private JTextArea partDetailArea;
    private JTextArea shopDetailArea;
    private JTabbedPane detailTabbedPane;
    private JLabel statusLabel;
    
    private CamperSummary selectedCamper;
    private String currentChoice;
    private final Object choiceLock = new Object();
    
    // 현재 표시된 정비 내역 데이터 저장 (테이블 클릭 이벤트에서 사용)
    private List<InternalMaintenanceInfo> currentInternalMaintenance;
    private List<ExternalMaintenanceInfo> currentExternalMaintenance;
    private boolean showingInternalMaintenance = false;
    
    public SwingCamperDetailView() {
        super("캠핑카 상세 조회");
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setVisible(false);
    }
    
    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 900); // 크기 확대 (상세 정보 패널 추가로 인해)
        setLocationRelativeTo(null);
        
        // 기존 컴포넌트들
        camperComboBox = new JComboBox<>();
        camperComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CamperSummary) {
                    CamperSummary camper = (CamperSummary) value;
                    setText(String.format("%s (%s)", camper.getName(), camper.getVehicleNumber()));
                }
                return this;
            }
        });
        
        detailArea = new JTextArea(8, 50);
        detailArea.setEditable(false);
        detailArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        detailArea.setBackground(new Color(248, 248, 248));
        
        tableModel = new DefaultTableModel();
        maintenanceTable = new JTable(tableModel);
        maintenanceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        maintenanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        maintenanceTable.setRowHeight(25);
        
        selectButton = new JButton("캠핑카 선택");
        internalMaintenanceButton = new JButton("자체 정비 내역");
        externalMaintenanceButton = new JButton("외부 정비 내역");
        backButton = new JButton("뒤로가기");
        
        // 새로 추가된 컴포넌트들 (요구사항: 부품/정비소 상세 정보 표시)
        setupDetailInfoComponents();
        
        // 초기에는 정비 버튼 비활성화
        internalMaintenanceButton.setEnabled(false);
        externalMaintenanceButton.setEnabled(false);
    }
    
    /**
     * 부품/정비소 상세 정보 컴포넌트 초기화
     * 요구사항: 부품 재고/공급회사 정보, 정비소 상세 정보 표시
     */
    private void setupDetailInfoComponents() {
        detailInfoPanel = new JPanel(new BorderLayout());
        detailInfoPanel.setBorder(BorderFactory.createTitledBorder("상세 정보"));
        
        // 상태 라벨
        statusLabel = new JLabel("테이블에서 항목을 클릭하면 상세 정보가 표시됩니다.");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        detailTabbedPane = new JTabbedPane();
        
        // 부품 상세 정보 탭 (요구사항: 부품 재고와 공급회사정보)
        partDetailArea = new JTextArea(12, 30);
        partDetailArea.setEditable(false);
        partDetailArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        partDetailArea.setBackground(new Color(248, 248, 248));
        partDetailArea.setText("자체 정비 내역에서 부품을 선택하면\n부품 재고와 공급회사 정보가 표시됩니다.");
        
        JScrollPane partScrollPane = new JScrollPane(partDetailArea);
        detailTabbedPane.addTab("부품 재고 정보", partScrollPane);
        
        // 정비소 상세 정보 탭 (요구사항: 정비소 상세 정보)
        shopDetailArea = new JTextArea(12, 30);
        shopDetailArea.setEditable(false);
        shopDetailArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        shopDetailArea.setBackground(new Color(248, 248, 248));
        shopDetailArea.setText("외부 정비 내역에서 정비소를 선택하면\n정비소 상세 정보가 표시됩니다.");
        
        JScrollPane shopScrollPane = new JScrollPane(shopDetailArea);
        detailTabbedPane.addTab("정비소 상세 정보", shopScrollPane);
        
        detailInfoPanel.add(statusLabel, BorderLayout.NORTH);
        detailInfoPanel.add(detailTabbedPane, BorderLayout.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 상단 패널 - 캠핑카 선택
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("캠핑카 선택"));
        topPanel.add(new JLabel("캠핑카:"));
        topPanel.add(camperComboBox);
        topPanel.add(selectButton);
        add(topPanel, BorderLayout.NORTH);
        
        // 중앙 분할 패널 (상단: 캠핑카 정보, 하단: 정비내역+상세정보)
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // 상단: 캠핑카 상세 정보
        JPanel camperDetailPanel = new JPanel(new BorderLayout());
        camperDetailPanel.setBorder(BorderFactory.createTitledBorder("캠핑카 상세 정보"));
        camperDetailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        
        // 하단: 정비내역(좌측) + 상세정보(우측) 수평 분할
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 좌측: 정비 내역 테이블
        JPanel maintenancePanel = new JPanel(new BorderLayout());
        maintenancePanel.setBorder(BorderFactory.createTitledBorder("정비 내역"));
        
        // 정비 버튼 패널
        JPanel maintenanceButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maintenanceButtonPanel.add(internalMaintenanceButton);
        maintenanceButtonPanel.add(externalMaintenanceButton);
        
        // 안내 라벨 추가
        JLabel guideLabel = new JLabel("※ 테이블 행을 클릭하면 우측에 상세 정보가 표시됩니다");
        guideLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
        guideLabel.setForeground(new Color(100, 100, 100));
        maintenanceButtonPanel.add(Box.createHorizontalStrut(20));
        maintenanceButtonPanel.add(guideLabel);
        
        maintenancePanel.add(maintenanceButtonPanel, BorderLayout.NORTH);
        maintenancePanel.add(new JScrollPane(maintenanceTable), BorderLayout.CENTER);
        
        // 우측: 부품/정비소 상세 정보 패널
        bottomSplitPane.setLeftComponent(maintenancePanel);
        bottomSplitPane.setRightComponent(detailInfoPanel);
        bottomSplitPane.setDividerLocation(800);
        bottomSplitPane.setResizeWeight(0.6);
        
        mainSplitPane.setTopComponent(camperDetailPanel);
        mainSplitPane.setBottomComponent(bottomSplitPane);
        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setResizeWeight(0.25);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // 하단 버튼 패널
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtonPanel.add(backButton);
        add(bottomButtonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        selectButton.addActionListener(e -> {
            if (camperComboBox.getSelectedItem() != null) {
                synchronized (choiceLock) {
                    selectedCamper = (CamperSummary) camperComboBox.getSelectedItem();
                    choiceLock.notify();
                }
            }
        });
        
        internalMaintenanceButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = "1";
                showingInternalMaintenance = true;
                detailTabbedPane.setSelectedIndex(0);
                statusLabel.setText("자체 정비 내역에서 항목을 클릭하면 부품 재고 정보가 표시됩니다.");
                choiceLock.notify();
            }
        });
        
        externalMaintenanceButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = "2";
                showingInternalMaintenance = false;
                detailTabbedPane.setSelectedIndex(1);
                statusLabel.setText("외부 정비 내역에서 항목을 클릭하면 정비소 상세 정보가 표시됩니다.");
                choiceLock.notify();
            }
        });
        
        backButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = "0";
                choiceLock.notify();
            }
        });
        
        // 테이블 클릭 이벤트 - 요구사항: 부품/정비소 선택 시 상세 정보 표시
        maintenanceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = maintenanceTable.getSelectedRow();
                if (selectedRow >= 0) {
                    handleTableRowClick(selectedRow);
                }
            }
        });
    }
    
    /**
     * 테이블 행 클릭 처리 - 요구사항: 부품/정비소 선택 시 상세 정보 표시
     */
    private void handleTableRowClick(int selectedRow) {
        if (showingInternalMaintenance && currentInternalMaintenance != null && 
            selectedRow < currentInternalMaintenance.size()) {
            // 자체 정비 내역 선택 시 부품 재고와 공급회사 정보 표시
            InternalMaintenanceInfo maintenance = currentInternalMaintenance.get(selectedRow);
            showPartDetailInfo(maintenance);
            statusLabel.setText("선택된 부품: " + maintenance.getPartName());
            
        } else if (!showingInternalMaintenance && currentExternalMaintenance != null && 
                   selectedRow < currentExternalMaintenance.size()) {
            // 외부 정비 내역 선택 시 정비소 상세 정보 표시
            ExternalMaintenanceInfo maintenance = currentExternalMaintenance.get(selectedRow);
            showShopDetailInfo(maintenance);
            statusLabel.setText("선택된 정비소: " + maintenance.getShopName());
        }
    }
    
    /**
     * 부품 상세 정보 표시 - 요구사항: 부품 재고와 공급회사정보
     */
    private void showPartDetailInfo(InternalMaintenanceInfo maintenance) {
        StringBuilder sb = new StringBuilder();
        sb.append("🔧 부품 상세 정보\n");
        sb.append("=================================\n\n");
        
        sb.append("📋 기본 정보\n");
        sb.append("• 부품명: ").append(maintenance.getPartName()).append("\n");
        sb.append("• 부품 단가: ").append(String.format("%,d원", maintenance.getPartPrice().intValue())).append("\n");
        sb.append("• 정비일자: ").append(maintenance.getMaintenanceDate()).append("\n\n");
        
        sb.append("📦 재고 정보\n");
        sb.append("• 현재 재고: ").append(maintenance.getStockQuantity()).append("개\n");
        sb.append("• 입고일자: ").append(maintenance.getEntryDate()).append("\n");
        sb.append("• 공급회사: ").append(maintenance.getSupplierName()).append("\n\n");
        
        sb.append("👨‍🔧 정비 담당자 정보\n");
        sb.append("• 담당자: ").append(maintenance.getEmployeeName()).append("\n");
        sb.append("• 부서: ").append(maintenance.getDepartmentName()).append("\n");
        sb.append("• 역할: ").append(maintenance.getRole()).append("\n");
        sb.append("• 정비 소요시간: ").append(maintenance.getMaintenanceDurationMinutes()).append("분\n");
        
        partDetailArea.setText(sb.toString());
        detailTabbedPane.setSelectedIndex(0);
    }
    
    /**
     * 정비소 상세 정보 표시 - 요구사항: 정비소 상세 정보
     */
    private void showShopDetailInfo(ExternalMaintenanceInfo maintenance) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏪 정비소 상세 정보\n");
        sb.append("=================================\n\n");
        
        sb.append("🏢 기본 정보\n");
        sb.append("• 정비소명: ").append(maintenance.getShopName()).append("\n");
        sb.append("• 주소: ").append(maintenance.getShopAddress()).append("\n");
        sb.append("• 전화번호: ").append(maintenance.getShopPhone()).append("\n");
        sb.append("• 담당자: ").append(maintenance.getManagerName()).append("\n");
        sb.append("• 담당자 이메일: ").append(maintenance.getManagerEmail()).append("\n\n");
        
        sb.append("🔧 정비 내역\n");
        sb.append("• 정비일자: ").append(maintenance.getRepairDate()).append("\n");
        sb.append("• 정비내용: ").append(maintenance.getMaintenanceDetails()).append("\n");
        sb.append("• 수리비용: ").append(String.format("%,d원", maintenance.getRepairCost().intValue())).append("\n");
        sb.append("• 납입기한: ").append(maintenance.getPaymentDueDate()).append("\n");
        sb.append("• 고객명: ").append(maintenance.getCustomerName()).append("\n\n");
        
        if (maintenance.getAdditionalMaintenanceDetails() != null && !maintenance.getAdditionalMaintenanceDetails().trim().isEmpty()) {
            sb.append("📝 추가 정비 사항\n");
            sb.append("• ").append(maintenance.getAdditionalMaintenanceDetails()).append("\n");
        }
        
        shopDetailArea.setText(sb.toString());
        detailTabbedPane.setSelectedIndex(1);
    }
    
    // CamperDetailView 인터페이스 구현
    
    @Override
    public CamperSummary selectCamper(List<CamperSummary> campers) {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            camperComboBox.removeAllItems();
            for (CamperSummary camper : campers) {
                camperComboBox.addItem(camper);
            }
            if (!campers.isEmpty()) {
                camperComboBox.setSelectedIndex(0);
            }
        });
        
        synchronized (choiceLock) {
            try {
                choiceLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        return selectedCamper;
    }
    
    @Override
    public void showCamperDetail(CamperDetailInfo detail) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("🚐 캠핑카 정보\n");
            sb.append("========================================\n");
            sb.append("캠핑카 ID: ").append(detail.getCamperId()).append("\n");
            sb.append("캠핑카명: ").append(detail.getName()).append("\n");
            sb.append("차량번호: ").append(detail.getVehicleNumber()).append("\n");
            sb.append("승차인원: ").append(detail.getSeats()).append("명\n");
            sb.append("대여비용: ").append(String.format("%,d원", detail.getRentalFee().intValue())).append("\n");
            sb.append("등록일자: ").append(detail.getRegistrationDate()).append("\n");
            sb.append("상세정보: ").append(detail.getDetails()).append("\n");
            sb.append("\n🏢 대여회사 정보\n");
            sb.append("========================================\n");
            sb.append("회사명: ").append(detail.getCompanyName()).append("\n");
            sb.append("주소: ").append(detail.getCompanyAddress()).append("\n");
            sb.append("전화번호: ").append(detail.getCompanyPhone()).append("\n");
            sb.append("담당자: ").append(detail.getManagerName()).append("\n");
            sb.append("이메일: ").append(detail.getManagerEmail()).append("\n");
            
            detailArea.setText(sb.toString());
            
            // 정비 버튼 활성화
            internalMaintenanceButton.setEnabled(true);
            externalMaintenanceButton.setEnabled(true);
            
            statusLabel.setText("정비 내역 버튼을 클릭하여 정비 기록을 확인하세요.");
        });
    }
    
    @Override
    public String showDetailOptions() {
        synchronized (choiceLock) {
            try {
                choiceLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "0";
            }
        }
        
        if ("0".equals(currentChoice)) {
            SwingUtilities.invokeLater(() -> setVisible(false));
        }
        
        return currentChoice;
    }
    
    @Override
    public void showInternalMaintenanceDetail(List<InternalMaintenanceInfo> maintenanceList) {
        SwingUtilities.invokeLater(() -> {
            // 현재 데이터 저장 (테이블 클릭 이벤트에서 사용)
            currentInternalMaintenance = maintenanceList;
            showingInternalMaintenance = true;
            
            Vector<String> columns = new Vector<>();
            columns.add("정비ID");
            columns.add("정비일자");
            columns.add("정비시간(분)");
            columns.add("부품명");
            columns.add("부품단가");
            columns.add("정비담당자");
            columns.add("부서");
            
            Vector<Vector<Object>> data = new Vector<>();
            for (InternalMaintenanceInfo info : maintenanceList) {
                Vector<Object> row = new Vector<>();
                row.add(info.getInternalMaintenanceId());
                row.add(info.getMaintenanceDate());
                row.add(info.getMaintenanceDurationMinutes());
                row.add(info.getPartName());
                row.add(String.format("%,d원", info.getPartPrice().intValue()));
                row.add(info.getEmployeeName());
                row.add(info.getDepartmentName());
                data.add(row);
            }
            
            tableModel.setDataVector(data, columns);
            
            if (maintenanceList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "자체 정비 내역이 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("자체 정비 내역이 없습니다.");
            } else {
                statusLabel.setText("자체 정비 내역 " + maintenanceList.size() + "건 - 행을 클릭하면 부품 상세정보를 확인할 수 있습니다.");
            }
            
            // 부품 정보 탭으로 전환
            detailTabbedPane.setSelectedIndex(0);
        });
    }
    
    @Override
    public void showExternalMaintenanceDetail(List<ExternalMaintenanceInfo> maintenanceList) {
        SwingUtilities.invokeLater(() -> {
            // 현재 데이터 저장 (테이블 클릭 이벤트에서 사용)
            currentExternalMaintenance = maintenanceList;
            showingInternalMaintenance = false;
            
            Vector<String> columns = new Vector<>();
            columns.add("정비ID");
            columns.add("정비일자");
            columns.add("정비내역");
            columns.add("수리비용");
            columns.add("납입기한");
            columns.add("정비소명");
            columns.add("정비소주소");
            columns.add("정비소전화");
            
            Vector<Vector<Object>> data = new Vector<>();
            for (ExternalMaintenanceInfo info : maintenanceList) {
                Vector<Object> row = new Vector<>();
                row.add(info.getExternalMaintenanceId());
                row.add(info.getRepairDate());
                row.add(info.getMaintenanceDetails());
                row.add(String.format("%,d원", info.getRepairCost().intValue()));
                row.add(info.getPaymentDueDate());
                row.add(info.getShopName());
                row.add(info.getShopAddress());
                row.add(info.getShopPhone());
                data.add(row);
            }
            
            tableModel.setDataVector(data, columns);
            
            if (maintenanceList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "외부 정비 내역이 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("외부 정비 내역이 없습니다.");
            } else {
                statusLabel.setText("외부 정비 내역 " + maintenanceList.size() + "건 - 행을 클릭하면 정비소 상세정보를 확인할 수 있습니다.");
            }
            
            // 정비소 정보 탭으로 전환
            detailTabbedPane.setSelectedIndex(1);
        });
    }
    
    @Override
    public void showPartDetails(List<InternalMaintenanceInfo> maintenanceList) {
        // 부품 상세 정보는 테이블 클릭 이벤트로 처리됨
        showInternalMaintenanceDetail(maintenanceList);
    }
    
    @Override
    public void showShopDetails(List<ExternalMaintenanceInfo> maintenanceList) {
        // 정비소 상세 정보는 테이블 클릭 이벤트로 처리됨
        showExternalMaintenanceDetail(maintenanceList);
    }
    
    @Override
    public void showProgress(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("🔄 " + message);
        });
    }
    
    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("❌ " + message);
            JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    @Override
    public void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("✅ " + message);
            JOptionPane.showMessageDialog(this, message, "성공", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
