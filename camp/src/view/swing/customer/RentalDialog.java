package view.swing.customer;

import User.dao_user.CamperDAO;
import User.dao_user.RentalDAO;
import User.model.Rental;
import common.DBConnect;
import view.swing.MessageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 캠핑카 대여 등록 다이얼로그
 */
public class RentalDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private int camperId;
    private String licenseNumber;
    private boolean rentalRegistered = false;
    
    // UI 컴포넌트
    private JComboBox<String> companyIdComboBox;
    private HashMap<String, Integer> companyMap; // 회사명 -> 회사ID 매핑
    private JTextField startDateField;
    private JTextField periodField;
    private JTextField billAmountField;
    private JTextField dueDateField;
    private JTextField additionalDescField;
    private JTextField additionalAmountField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public RentalDialog(JFrame parent, int camperId, String licenseNumber) {
        super(parent, "캠핑카 대여 등록", true);
        
        this.camperId = camperId;
        this.licenseNumber = licenseNumber;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // 대여 회사 목록 가져오기
        companyMap = new HashMap<>();
        ArrayList<String[]> companyList = getCompanyList();
        String[] companyNames = new String[companyList.size()];
        
        for (int i = 0; i < companyList.size(); i++) {
            String[] company = companyList.get(i);
            int companyId = Integer.parseInt(company[0]);
            String companyName = company[1];
            String displayName = companyId + " - " + companyName;
            
            companyNames[i] = displayName;
            companyMap.put(displayName, companyId);
        }
        
        companyIdComboBox = new JComboBox<>(companyNames);
        
        // 현재 캠핑카의 회사 ID 가져오기
        int defaultCompanyId = getCamperCompanyId(camperId);
        
        // 회사 ID에 해당하는 항목을 기본 선택으로 설정
        for (int i = 0; i < companyNames.length; i++) {
            String item = companyNames[i];
            int itemId = companyMap.get(item);
            if (itemId == defaultCompanyId) {
                companyIdComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        startDateField = new JTextField(10);
        periodField = new JTextField(10);
        billAmountField = new JTextField(10);
        dueDateField = new JTextField(10);
        additionalDescField = new JTextField(20);
        additionalAmountField = new JTextField(10);
        
        saveButton = new JButton("대여 등록");
        cancelButton = new JButton("취소");
        
        // 기본값 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        startDateField.setText(dateFormat.format(cal.getTime()));
        
        cal.add(Calendar.DATE, 7);  // 7일 후
        dueDateField.setText(dateFormat.format(cal.getTime()));
        
        periodField.setText("3");  // 기본 대여 기간: 3일
        additionalAmountField.setText("0");  // 기본 추가 요금: 0원
        
        // 캠핑카 대여 비용 가져와서 설정
        double rentalFee = getCamperRentalFee(camperId);
        billAmountField.setText(String.valueOf(rentalFee));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 라벨과 필드 배치
        addFormField(formPanel, gbc, "캠핑카 ID:", String.valueOf(camperId), true);
        addFormField(formPanel, gbc, "운전면허번호:", licenseNumber, true);
        addFormField(formPanel, gbc, "대여회사 선택:", companyIdComboBox);
        addFormField(formPanel, gbc, "대여 시작일 (yyyy-MM-dd):", startDateField);
        addFormField(formPanel, gbc, "대여 기간 (일):", periodField);
        addFormField(formPanel, gbc, "대여 요금:", billAmountField);
        addFormField(formPanel, gbc, "납입 기한 (yyyy-MM-dd):", dueDateField);
        addFormField(formPanel, gbc, "추가 요금 내역:", additionalDescField);
        addFormField(formPanel, gbc, "추가 요금:", additionalAmountField);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(new JLabel("캠핑카 대여 정보를 입력하세요", JLabel.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComboBox<?> comboBox) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        panel.add(comboBox, gbc);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, String value, boolean disabled) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        JTextField field = new JTextField(value, 10);
        field.setEnabled(!disabled);
        panel.add(field, gbc);
    }
    
    /**
     * 대여 회사 목록 가져오기
     */
    private ArrayList<String[]> getCompanyList() {
        ArrayList<String[]> companyList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnect.getUserConnection();
            String sql = "SELECT rental_company_id, name FROM rentalcompany ORDER BY rental_company_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] company = new String[2];
                company[0] = rs.getString("rental_company_id");
                company[1] = rs.getString("name");
                companyList.add(company);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 대여 회사 목록 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        
        return companyList;
    }
    
    /**
     * 캠핑카의 대여 회사 ID 가져오기
     */
    private int getCamperCompanyId(int camperId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int companyId = 1; // 기본값
        
        try {
            conn = DBConnect.getUserConnection();
            String sql = "SELECT rental_company_id FROM camper WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                companyId = rs.getInt("rental_company_id");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 캠핑카 회사 정보 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        
        return companyId;
    }
    
    /**
     * 캠핑카의 대여 비용 가져오기
     */
    private double getCamperRentalFee(int camperId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double rentalFee = 0.0; // 기본값
        
        try {
            conn = DBConnect.getUserConnection();
            String sql = "SELECT rental_fee FROM camper WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                rentalFee = rs.getDouble("rental_fee");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 캠핑카 대여 비용 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        
        return rentalFee;
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRental();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void saveRental() {
        try {
            // 입력값 검증
            if (startDateField.getText().trim().isEmpty() ||
                periodField.getText().trim().isEmpty() ||
                billAmountField.getText().trim().isEmpty() ||
                dueDateField.getText().trim().isEmpty()) {
                
                MessageHelper.showWarningMessage(this, "입력 오류", "모든 필수 필드를 입력해주세요.");
                return;
            }
            
            // 대여 정보 생성
            Rental rental = new Rental();
            rental.setCamperId(camperId);
            rental.setLicenseNumber(licenseNumber);
            
            try {
                // 선택된 회사 ID 가져오기
                String selectedCompany = (String) companyIdComboBox.getSelectedItem();
                if (selectedCompany == null) {
                    MessageHelper.showWarningMessage(this, "입력 오류", "대여회사를 선택해주세요.");
                    return;
                }
                
                int companyId = companyMap.get(selectedCompany);
                rental.setRentalCompanyId(companyId);
                
                System.out.println("선택된 대여 회사: " + selectedCompany + " (ID: " + companyId + ")");
            } catch (Exception e) {
                MessageHelper.showWarningMessage(this, "입력 오류", "대여회사 선택 중 오류가 발생했습니다: " + e.getMessage());
                return;
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date rentalStartDate = null;
            try {
                rentalStartDate = new Date(dateFormat.parse(startDateField.getText().trim()).getTime());
                rental.setRentalStartDate(rentalStartDate);
                rental.setPaymentDueDate(new Date(dateFormat.parse(dueDateField.getText().trim()).getTime()));
            } catch (ParseException e) {
                MessageHelper.showWarningMessage(this, "입력 오류", "날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식으로 입력해주세요.");
                return;
            }
            
            int rentalPeriod = 0;
            try {
                rentalPeriod = Integer.parseInt(periodField.getText().trim());
                rental.setRentalPeriod(rentalPeriod);
                rental.setBillAmount(Double.parseDouble(billAmountField.getText().trim()));
                rental.setAdditionalChargesAmount(Double.parseDouble(additionalAmountField.getText().trim()));
            } catch (NumberFormatException e) {
                MessageHelper.showWarningMessage(this, "입력 오류", "대여 기간, 요금, 추가 요금은 숫자로 입력해주세요.");
                return;
            }
            
            rental.setAdditionalChargesDescription(additionalDescField.getText().trim());
            
            // 다시 한번 대여 가능 여부 확인
            CamperDAO camperDAO = new CamperDAO();
            if (!camperDAO.isAvailable(camperId)) {
                MessageHelper.showWarningMessage(this, "대여 불가", "선택한 캠핑카는 이미 대여 중입니다.");
                return;
            }
            
            // 대여 등록
            RentalDAO rentalDAO = new RentalDAO();
            rentalDAO.insertRental(rental);
            
            rentalRegistered = true;
            MessageHelper.showInfoMessage(this, "대여 등록 성공", "캠핑카 대여가 성공적으로 등록되었습니다.");
            dispose();
            
        } catch (Exception e) {
            MessageHelper.showErrorMessage(this, "대여 등록 오류", e.getMessage());
        }
    }
    
    public boolean isRentalRegistered() {
        return rentalRegistered;
    }
}
