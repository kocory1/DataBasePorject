package view.swing;

import admin.model.InsertMethod;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 테이블 관리 화면에서 사용되는 다이얼로그 관련 도우미 클래스
 * SQL 쿼리 작성, 테이블 선택 등 다양한 입력 다이얼로그를 제공
 */
public class TableDialogHelper {
    
    private final JFrame parentFrame;
    
    public TableDialogHelper(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    /**
     * WHERE 조건 입력 다이얼로그 표시
     */
    public String getWhereCondition() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        JLabel label = new JLabel("<html>WHERE 조건을 입력하세요:<br><small>예: id = 1 또는 name LIKE '%김%'<br>전체 조회는 비워두세요</small></html>");
        JTextArea textArea = new JTextArea(5, 30);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "조건 입력",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            return textArea.getText().trim();
        }
        
        return null;
    }
    
    /**
     * SET 절 입력 다이얼로그 표시
     */
    public String getSetClause() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        JLabel label = new JLabel("<html>SET 절을 입력하세요:<br><small>예: name='홍길동', age=30, email='user@example.com'</small></html>");
        JTextArea textArea = new JTextArea(5, 30);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "SET 절 입력",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            return textArea.getText().trim();
        }
        
        return null;
    }
    
    /**
     * WHERE 절 입력 다이얼로그 표시
     */
    public String getWhereClause() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        JLabel label = new JLabel("<html>WHERE 절을 입력하세요:<br><small>예: id = 1 또는 name LIKE '%김%' AND age > 20</small></html>");
        JTextArea textArea = new JTextArea(5, 30);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "WHERE 절 입력",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            return textArea.getText().trim();
        }
        
        return null;
    }
    
    /**
     * INSERT SQL 전체 입력 다이얼로그 표시
     */
    public String getFullInsertSQL(JTextArea queryArea) {
        // 쿼리 에디터에 힌트 표시
        SwingUtilities.invokeLater(() -> {
            StringBuilder hintBuilder = new StringBuilder();
            hintBuilder.append("-- 아래에 INSERT 문을 작성하세요:\n");
            hintBuilder.append("-- 예시:\n");
            hintBuilder.append("INSERT INTO Customer (username, password, license_number, customer_name, address, phone, email)\n");
            hintBuilder.append("VALUES ('newuser', 'pass123', 'DL1234', '홍길동', '서울시 강남구', '010-1234-5678', 'user@example.com');\n\n");
            
            queryArea.setText(hintBuilder.toString());
            queryArea.setCaretPosition(hintBuilder.length());
        });
        
        // 사용자 정의 패널 생성
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 300));
        
        JLabel label = new JLabel("<html><b>INSERT SQL 문을 작성하세요</b><br>" +
                "<small>테이블 및 컬럼 정보를 정확히 입력해주세요.</small></html>");
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton viewTablesButton = new JButton("테이블 정보 보기");
        viewTablesButton.addActionListener(e -> showTableInfo(panel));
        
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(label, BorderLayout.CENTER);
        northPanel.add(viewTablesButton, BorderLayout.EAST);
        
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(queryArea), BorderLayout.CENTER);
        
        int option = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "INSERT SQL 입력",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (option == JOptionPane.OK_OPTION) {
            return queryArea.getText();
        }
        
        return null;
    }
    
    /**
     * 테이블 정보 보기 다이얼로그 표시
     */
    private void showTableInfo(JPanel parent) {
        String[] tableInfo = {
            "RentalCompany: rental_company_id, name, address, phone, manager_name, manager_email",
            "Camper: camper_id, name, vehicle_number, seats, image_url, details, rental_fee, rental_company_id, registration_date",
            "Part: part_id, part_name, part_price, stock_quantity, entry_date, supplier_name",
            "Employee: employee_id, employee_name, phone, address, salary, dependents_count, department_name, role",
            "Customer: customer_id, username, password, license_number, customer_name, address, phone, email, previous_rental_date, previous_camper_type",
            "Rental: rental_id, camper_id, license_number, rental_company_id, rental_start_date, rental_period, bill_amount, payment_due_date, additional_charges_description, additional_charges_amount",
            "InternalMaintenance: internal_maintenance_id, camper_id, part_id, maintenance_date, maintenance_duration_minutes, employee_id",
            "ExternalMaintenanceShop: shop_id, shop_name, shop_address, shop_phone, manager_name, manager_email",
            "ExternalMaintenance: external_maintenance_id, camper_id, shop_id, rental_company_id, license_number, maintenance_details, repair_date, repair_cost, payment_due_date, additional_maintenance_details"
        };
        
        JTextArea textArea = new JTextArea(String.join("\n\n", tableInfo));
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        JOptionPane.showMessageDialog(
            parent, 
            scrollPane, 
            "테이블 정보", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * VALUES 값 입력 다이얼로그 표시
     */
    public String getInsertValues() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        JLabel label = new JLabel("<html>VALUES 값들을 입력하세요:<br><small>예: '홍길동', 30, 'user@example.com'<br>문자열은 작은따옴표로 감싸주세요</small></html>");
        JTextArea textArea = new JTextArea(5, 30);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "VALUES 입력",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            return textArea.getText().trim();
        }
        
        return null;
    }
    
    /**
     * INSERT 방식 선택 다이얼로그 표시
     */
    public InsertMethod selectInsertMethod() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("<html><b>INSERT 방식 선택</b></html>");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        
        // 옵션 1: VALUES만 입력
        JRadioButton valuesOnlyOption = new JRadioButton("VALUES만 입력");
        valuesOnlyOption.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        valuesOnlyOption.setSelected(true);
        JLabel valuesDesc = new JLabel("<html><small>열 이름은 자동으로 생성됩니다. 값만 입력하면 됩니다.</small></html>");
        valuesDesc.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));
        
        // 옵션 2: 전체 SQL 입력
        JRadioButton fullSqlOption = new JRadioButton("전체 SQL 입력");
        fullSqlOption.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        JLabel sqlDesc = new JLabel("<html><small>INSERT INTO 테이블명 (열1, 열2, ...) VALUES (값1, 값2, ...)</small></html>");
        sqlDesc.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));
        
        // 라디오 버튼 그룹 설정
        ButtonGroup group = new ButtonGroup();
        group.add(valuesOnlyOption);
        group.add(fullSqlOption);
        
        // 패널에 추가
        optionsPanel.add(valuesOnlyOption);
        optionsPanel.add(valuesDesc);
        optionsPanel.add(fullSqlOption);
        optionsPanel.add(sqlDesc);
        
        panel.add(optionsPanel, BorderLayout.CENTER);
        
        // 대화상자 표시
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "입력 방식 선택",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            return valuesOnlyOption.isSelected() ? InsertMethod.VALUES_ONLY : InsertMethod.FULL_SQL;
        }
        
        return null;
    }
    
    /**
     * 테이블 선택 다이얼로그 표시
     */
    public String selectTable(String[] tables, Consumer<String> messageUpdater) {
        // 테이블 선택 대화상자 생성
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(400, 300));
        
        // 설명 레이블
        JLabel label = new JLabel("<html><b>테이블을 선택하세요</b></html>");
        panel.add(label, BorderLayout.NORTH);
        
        // 테이블 목록 (아이콘과 설명 포함)
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String table : tables) {
            listModel.addElement(table);
        }
        
        JList<String> tableList = new JList<>(listModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String tableName = (String) value;
                setText(tableName + " - " + TableUtils.getTableDescription(tableName));
                setIcon(UIManager.getIcon("FileView.fileIcon"));
                
                // 선택된 아이템 색상 조정
                if (isSelected) {
                    setBackground(new Color(51, 153, 255));
                    setForeground(Color.WHITE);
                }
                
                // 모든 아이템에 패딩 추가
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                return this;
            }
        });
        
        // 테이블 목록을 스크롤 패널에 추가
        JScrollPane scrollPane = new JScrollPane(tableList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 테이블 선택 대화상자 표시
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "테이블 선택",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        // 사용자가 OK를 클릭하고 테이블을 선택했으면 선택된 테이블 반환
        if (result == JOptionPane.OK_OPTION && tableList.getSelectedValue() != null) {
            String selectedTable = tableList.getSelectedValue();
            messageUpdater.accept("선택된 테이블: " + selectedTable);
            return selectedTable;
        }
        
        return null;
    }
    
    /**
     * 삭제 확인 다이얼로그 표시
     */
    public boolean confirmDelete(String tableName, String condition) {
        StringBuilder message = new StringBuilder();
        message.append("<html><div style='width: 350px; font-size: 12px;'>");
        message.append("<b style='color: #cc0000; font-size: 14px;'>⚠️ 주의: 이 작업은 되돌릴 수 없습니다!</b><br><br>");
        message.append("다음 데이터를 삭제하려고 합니다:<br><br>");
        message.append("<b>테이블:</b> ").append(tableName).append("<br>");
        
        if (condition != null && !condition.trim().isEmpty()) {
            message.append("<b>조건:</b> ").append(condition.replace("<", "&lt;").replace(">", "&gt;")).append("<br><br>");
        } else {
            message.append("<b style='color: #cc0000;'>조건 없음 - 테이블의 모든 데이터가 삭제됩니다!</b><br><br>");
        }
        
        message.append("정말로 이 작업을 진행하시겠습니까?<br>");
        message.append("삭제된 데이터는 복구할 수 없습니다.</div></html>");
        
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message.toString());
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(label, BorderLayout.CENTER);
        
        // 확인 체크박스 추가
        JCheckBox confirmCheckBox = new JCheckBox("삭제를 확인합니다");
        confirmCheckBox.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        panel.add(confirmCheckBox, BorderLayout.SOUTH);
        
        Object[] options = {"삭제", "취소"};
        
        int result = JOptionPane.showOptionDialog(
            parentFrame,
            panel,
            "삭제 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1] // 기본값은 취소
        );
        
        // 확인 체크박스를 선택하고 삭제 버튼을 눌렀을 경우만 true 반환
        return result == JOptionPane.YES_OPTION && confirmCheckBox.isSelected();
    }
    
    /**
     * 오류 메시지 표시
     */
    public void showError(String message, Consumer<String> messageUpdater) {
        // 메시지 영역에 오류 표시
        messageUpdater.accept("❌ 오류: " + message);
        
        // 오류 대화상자 표시
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        panel.add(iconLabel, BorderLayout.WEST);
        
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(panel.getBackground());
        textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(textArea, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(
            parentFrame,
            panel,
            "오류",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
