package view.swing;

import view.QueryView;
import admin.model.QueryResult;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * 쿼리 실행을 위한 Swing GUI View 구현
 */
public class SwingQueryView extends JFrame implements QueryView {

    private JTextArea queryArea;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextArea messageArea;
    private JButton executeButton;
    private JButton example1Button;
    private JButton example2Button;
    private JButton example3Button;
    private JButton clearButton;
    private JButton backButton;

    private String currentChoice;
    private final Object choiceLock = new Object();

    // 예제 쿼리들
    private final String[] EXAMPLE_QUERIES = {
            "SELECT c.name AS 캠핑카명, rc.name AS 대여회사명,\n" +
                    "       COUNT(DISTINCT r.rental_id) AS 총대여횟수,\n" +
                    "       COUNT(DISTINCT em.external_maintenance_id) AS 외부정비횟수\n" +
                    "FROM Camper c\n" +
                    "JOIN RentalCompany rc ON c.rental_company_id = rc.rental_company_id\n" +
                    "LEFT JOIN Rental r ON c.camper_id = r.camper_id\n" +
                    "LEFT JOIN ExternalMaintenance em ON c.camper_id = em.camper_id\n" +
                    "WHERE c.rental_fee > (\n" +
                    "    SELECT AVG(rental_fee) FROM Camper\n" +
                    ")\n" +
                    "GROUP BY c.camper_id, c.name, rc.name\n" +
                    "HAVING COUNT(DISTINCT r.rental_id) > 0\n" +
                    "ORDER BY COUNT(DISTINCT r.rental_id) DESC",

            "SELECT e.employee_name AS 직원명, e.department_name AS 부서,\n" +
                    "       COUNT(DISTINCT im.internal_maintenance_id) AS 정비횟수,\n" +
                    "       e.salary AS 월급여\n" +
                    "FROM Employee e\n" +
                    "JOIN InternalMaintenance im ON e.employee_id = im.employee_id\n" +
                    "JOIN Camper c ON im.camper_id = c.camper_id\n" +
                    "WHERE e.role = '정비'\n" +
                    "GROUP BY e.employee_id, e.employee_name, e.department_name, e.salary\n" +
                    "ORDER BY 정비횟수 DESC",

            "SELECT cu.customer_name AS 고객명,\n" +
                    "       COUNT(DISTINCT r.rental_id) AS 대여횟수,\n" +
                    "       COUNT(DISTINCT em.external_maintenance_id) AS 정비의뢰횟수,\n" +
                    "       GROUP_CONCAT(DISTINCT ems.shop_name) AS 이용정비소목록\n" +
                    "FROM Customer cu\n" +
                    "JOIN Rental r ON cu.license_number = r.license_number\n" +
                    "LEFT JOIN ExternalMaintenance em ON cu.license_number = em.license_number\n" +
                    "LEFT JOIN ExternalMaintenanceShop ems ON em.shop_id = ems.shop_id\n" +
                    "WHERE r.rental_id IN (\n" +
                    "    SELECT rental_id FROM Rental\n" +
                    "    WHERE bill_amount > 100000\n" +
                    ")\n" +
                    "GROUP BY cu.customer_id, cu.customer_name\n" +
                    "HAVING COUNT(DISTINCT r.rental_id) >= 1\n" +
                    "ORDER BY COUNT(DISTINCT r.rental_id) DESC"
    };

    public SwingQueryView() {
        super("쿼리 실행");
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setVisible(false);
    }
    
    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // 쿼리 입력 영역
        queryArea = new JTextArea(15, 80);
        queryArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        queryArea.setText("-- SELECT 쿼리를 입력하세요\n");
        
        // 결과 테이블
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // 메시지 영역
        messageArea = new JTextArea(3, 80);
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(240, 240, 240));
        
        // 버튼들
        executeButton = new JButton("쿼리 실행");
        example1Button = new JButton("예제 1: 캠핑카 수익성");
        example2Button = new JButton("예제 2: 정비사 실적");
        example3Button = new JButton("예제 3: 고객 대여 패턴");
        clearButton = new JButton("지우기");
        backButton = new JButton("뒤로가기");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 상단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(executeButton);
        buttonPanel.add(example1Button);
        buttonPanel.add(example2Button);
        buttonPanel.add(example3Button);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.NORTH);
        
        // 중앙 분할 패널
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // 상단: 쿼리 입력 영역
        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("SQL 쿼리 입력"));
        queryPanel.add(new JScrollPane(queryArea), BorderLayout.CENTER);
        splitPane.setTopComponent(queryPanel);
        
        // 하단: 결과 테이블
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("실행 결과"));
        resultPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        splitPane.setBottomComponent(resultPanel);
        
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
        
        // 하단 메시지 영역
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder("메시지"));
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        messagePanel.setPreferredSize(new Dimension(0, 100));
        add(messagePanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        executeButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = "1";
                choiceLock.notify();
            }
        });
        
        example1Button.addActionListener(e -> {
            queryArea.setText(EXAMPLE_QUERIES[0]);
        });
        
        example2Button.addActionListener(e -> {
            queryArea.setText(EXAMPLE_QUERIES[1]);
        });
        
        example3Button.addActionListener(e -> {
            queryArea.setText(EXAMPLE_QUERIES[2]);
        });
        
        clearButton.addActionListener(e -> {
            queryArea.setText("-- SELECT 쿼리를 입력하세요\n");
            tableModel.setDataVector(new Vector<>(), new Vector<>());
            messageArea.setText("");
        });
        
        backButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = "0";
                choiceLock.notify();
            }
        });
    }
    
    @Override
    public String showQueryMenu() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            messageArea.setText("임의의 SELECT 쿼리를 실행할 수 있습니다.");
        });
        
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
    public String getCustomSQL() {
        return queryArea.getText().trim();
    }
    
    @Override
    public void showQueryResult(QueryResult result) {
        SwingUtilities.invokeLater(() -> {
            if (result.getRows().isEmpty()) {
                tableModel.setDataVector(new Vector<>(), new Vector<>());
                messageArea.setText("조회 결과가 없습니다.");
                return;
            }
            
            Vector<String> columns = new Vector<>(result.getColumnNames());
            Vector<Vector<Object>> data = new Vector<>();
            
            for (List<String> row : result.getRows()) {
                data.add(new Vector<>(row));
            }
            
            tableModel.setDataVector(data, columns);
            
            // 열 너비 자동 조정 - 간소화된 버전
            adjustColumnWidths(resultTable);
            
            messageArea.setText(String.format(
                "쿼리 실행 성공: %d개 행이 조회되었습니다.",
                result.getRowCount()
            ));
        });
    }
    
    /**
     * 테이블 열 너비를 컨텐츠에 맞게 자동 조정
     */
    private void adjustColumnWidths(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // 최소 너비
            
            // 헤더 너비 확인
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            Object headerValue = tableColumn.getHeaderValue();
            
            if (headerValue != null) {
                width = Math.max(width, headerValue.toString().length() * 10 + 20);
            }
            
            // 데이터 너비 확인 (처음 50행만)
            for (int row = 0; row < Math.min(table.getRowCount(), 50); row++) {
                Object cellValue = table.getValueAt(row, column);
                if (cellValue != null) {
                    int cellWidth = cellValue.toString().length() * 10 + 10;
                    width = Math.max(width, cellWidth);
                }
            }
            
            // 최대 너비 제한
            width = Math.min(width, 300);
            
            tableColumn.setPreferredWidth(width);
        }
    }
    
    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText("오류: " + message);
            JOptionPane.showMessageDialog(this, message, "쿼리 실행 오류", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    @Override
    public void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText("성공: " + message);
        });
    }
    
    @Override
    public void showProgress(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText(message);
        });
    }
}
