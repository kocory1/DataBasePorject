package view.swing.customer;

import User.dao_user.RentalDAO;
import User.model.Rental;
import view.swing.MessageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 대여 정보 수정 다이얼로그
 */
public class ModifyRentalDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private Rental rental;
    private boolean rentalModified = false;
    
    // UI 컴포넌트
    private JTextField rentalIdField;
    private JTextField camperIdField;
    private JTextField licenseField;
    private JTextField companyIdField;
    private JTextField startDateField;
    private JTextField periodField;
    private JTextField billAmountField;
    private JTextField dueDateField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public ModifyRentalDialog(JFrame parent, Rental rental) {
        super(parent, "대여 정보 수정", true);
        
        this.rental = rental;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        rentalIdField = new JTextField(String.valueOf(rental.getRentalId()), 10);
        rentalIdField.setEnabled(false);  // 수정 불가
        
        camperIdField = new JTextField(String.valueOf(rental.getCamperId()), 10);
        camperIdField.setEnabled(false);  // 수정 불가
        
        licenseField = new JTextField(rental.getLicenseNumber(), 10);
        licenseField.setEnabled(false);  // 수정 불가
        
        companyIdField = new JTextField(String.valueOf(rental.getRentalCompanyId()), 10);
        companyIdField.setEnabled(false);  // 수정 불가
        
        startDateField = new JTextField(dateFormat.format(rental.getRentalStartDate()), 10);
        startDateField.setEnabled(false);  // 수정 불가
        
        periodField = new JTextField(String.valueOf(rental.getRentalPeriod()), 10);
        billAmountField = new JTextField(String.valueOf(rental.getBillAmount()), 10);
        
        dueDateField = new JTextField(
            rental.getPaymentDueDate() != null ? 
            dateFormat.format(rental.getPaymentDueDate()) : "", 10);
        
        saveButton = new JButton("수정 저장");
        cancelButton = new JButton("취소");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 라벨과 필드 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("대여 ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(rentalIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("캠핑카 ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(camperIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("운전면허번호:"), gbc);
        gbc.gridx = 1;
        formPanel.add(licenseField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("대여회사 ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(companyIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("대여 시작일:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startDateField, gbc);
        
        // 수정 가능한 필드들 - 강조 표시
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel periodLabel = new JLabel("대여 기간 (일):");
        periodLabel.setForeground(Color.BLUE);
        formPanel.add(periodLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(periodField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel billLabel = new JLabel("대여 요금:");
        billLabel.setForeground(Color.BLUE);
        formPanel.add(billLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(billAmountField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel dueLabel = new JLabel("납입 기한 (yyyy-MM-dd):");
        dueLabel.setForeground(Color.BLUE);
        formPanel.add(dueLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dueDateField, gbc);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("* 파란색 항목만 수정 가능합니다."));
        
        add(new JLabel("대여 정보 수정", JLabel.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.AFTER_LAST_LINE);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveModifiedRental();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void saveModifiedRental() {
        try {
            // 입력값 검증
            if (periodField.getText().trim().isEmpty() ||
                billAmountField.getText().trim().isEmpty() ||
                dueDateField.getText().trim().isEmpty()) {
                
                MessageHelper.showWarningMessage(this, "입력 오류", "모든 필수 필드를 입력해주세요.");
                return;
            }
            
            // 수정된 대여 정보 업데이트
            try {
                rental.setRentalPeriod(Integer.parseInt(periodField.getText().trim()));
            } catch (NumberFormatException e) {
                MessageHelper.showWarningMessage(this, "입력 오류", "대여 기간은 숫자로 입력해주세요.");
                return;
            }
            
            try {
                rental.setBillAmount(Double.parseDouble(billAmountField.getText().trim()));
            } catch (NumberFormatException e) {
                MessageHelper.showWarningMessage(this, "입력 오류", "대여 요금은 숫자로 입력해주세요.");
                return;
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                rental.setPaymentDueDate(new Date(dateFormat.parse(dueDateField.getText().trim()).getTime()));
            } catch (ParseException e) {
                MessageHelper.showWarningMessage(this, "입력 오류", "납입 기한 날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식으로 입력해주세요.");
                return;
            }
            
            // 대여 정보 수정
            RentalDAO rentalDAO = new RentalDAO();
            rentalDAO.updateRental(rental);
            
            rentalModified = true;
            MessageHelper.showInfoMessage(this, "수정 성공", "대여 정보가 성공적으로 수정되었습니다.");
            dispose();
            
        } catch (Exception e) {
            MessageHelper.showErrorMessage(this, "수정 오류", e.getMessage());
        }
    }
    
    public boolean isRentalModified() {
        return rentalModified;
    }
}