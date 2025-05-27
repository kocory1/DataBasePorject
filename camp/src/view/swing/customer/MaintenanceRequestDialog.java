package view.swing.customer;

import User.dao_user.MaintenanceDAO;
import view.swing.MessageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 외부 정비 요청 다이얼로그
 */
public class MaintenanceRequestDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private int camperId;
    private String licenseNumber;
    private boolean requestSuccess = false;
    
    // UI 컴포넌트
    private JComboBox<String> shopComboBox;
    private ArrayList<String[]> shopList; // [shop_id, shop_name]
    private JTextArea detailsArea;
    private JTextField dateField;
    private JTextField costField;
    private JTextArea additionalDetailsArea; // 기타정비내역 필드 추가
    
    private JButton requestButton;
    private JButton cancelButton;
    
    public MaintenanceRequestDialog(JFrame parent, int camperId, String licenseNumber) {
        super(parent, "외부 정비 요청", true);
        
        this.camperId = camperId;
        this.licenseNumber = licenseNumber;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setSize(500, 550);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // 정비소 목록을 DB에서 가져오기
        MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
        shopList = maintenanceDAO.getAllShops();
        
        String[] shopNames = new String[shopList.size()];
        for (int i = 0; i < shopList.size(); i++) {
            shopNames[i] = shopList.get(i)[1]; // shop_name만 표시
        }
        
        shopComboBox = new JComboBox<>(shopNames);
        
        detailsArea = new JTextArea(5, 20);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        
        additionalDetailsArea = new JTextArea(5, 20);
        additionalDetailsArea.setLineWrap(true);
        additionalDetailsArea.setWrapStyleWord(true);
        
        // 현재 날짜를 기본값으로
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        dateField = new JTextField(dateFormat.format(cal.getTime()), 15);
        
        costField = new JTextField("0", 15);
        
        requestButton = new JButton("정비 요청");
        cancelButton = new JButton("취소");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);  // 여백 증가
        gbc.anchor = GridBagConstraints.WEST;
        
        // 라벨과 필드 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("캠핑카 ID:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;  // 수평 여백 추가
        formPanel.add(new JLabel(String.valueOf(camperId)), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("운전면허번호:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(new JLabel(licenseNumber), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("정비소 선택:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(shopComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("수리 예정일:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(dateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("예상 비용:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(costField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("정비 내용:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;  // 수직 여백 추가
        formPanel.add(new JScrollPane(detailsArea), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        formPanel.add(new JLabel("기타 정비 내역:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(additionalDetailsArea), gbc);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(requestButton);
        buttonPanel.add(cancelButton);
        
        // 제목 패널
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("외부 정비소 정비 요청", JLabel.CENTER));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        requestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitMaintenanceRequest();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void submitMaintenanceRequest() {
        // 입력값 검증
        if (detailsArea.getText().trim().isEmpty()) {
            MessageHelper.showWarningMessage(this, "입력 오류", "정비 내용을 입력해주세요.");
            return;
        }
        
        // 날짜 검증
        Date repairDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            repairDate = dateFormat.parse(dateField.getText().trim());
            
            // 오늘 날짜와 비교
            Date today = new Date();
            if (repairDate.before(today)) {
                MessageHelper.showWarningMessage(this, "입력 오류", "수리 예정일은 오늘 이후 날짜여야 합니다.");
                return;
            }
        } catch (ParseException e) {
            MessageHelper.showWarningMessage(this, "입력 오류", "날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식으로 입력해주세요.");
            return;
        }
        
        // 비용 검증
        double repairCost = 0;
        try {
            repairCost = Double.parseDouble(costField.getText().trim());
            if (repairCost < 0) {
                MessageHelper.showWarningMessage(this, "입력 오류", "예상 비용은 0 이상이어야 합니다.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageHelper.showWarningMessage(this, "입력 오류", "예상 비용은 숫자로 입력해주세요.");
            return;
        }
        
        // 선택한 정비소 ID 가져오기
        int selectedIndex = shopComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= shopList.size()) {
            MessageHelper.showWarningMessage(this, "선택 오류", "정비소를 선택해주세요.");
            return;
        }
        
        int shopId = Integer.parseInt(shopList.get(selectedIndex)[0]);
        String maintenanceDetails = detailsArea.getText().trim();
        String additionalDetails = additionalDetailsArea.getText().trim();
        
        // 정비 요청 등록
        MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
        boolean success = maintenanceDAO.insertExternalMaintenance(
            camperId, shopId, licenseNumber, maintenanceDetails, repairDate, repairCost, additionalDetails);
        
        
        if (success) {
            requestSuccess = true;
            MessageHelper.showInfoMessage(this, "정비 요청 성공", "정비 요청이 성공적으로 등록되었습니다.");
            dispose();
        } else {
            MessageHelper.showErrorMessage(this, "정비 요청 실패", "정비 요청 등록 중 오류가 발생했습니다.");
        }
    }
    
    public boolean isRequestSuccess() {
        return requestSuccess;
    }
}
