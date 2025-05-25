package view.swing;

import view.QueryView;
import admin.model.QueryResult;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private JButton exampleButton;
    private JButton clearButton;
    private JButton backButton;
    
    private String currentChoice;
    private final Object choiceLock = new Object();
    
    // 예제 쿼리들
    private final String[] EXAMPLE_QUERIES = {
        "-- 4개 이상 테이블 조인 + 부속질의 + GROUP BY 예제 1\n" +
        "SELECT c.name AS 캠핑카명, cc.name AS 대여회사명,\n" +
        "       COUNT(DISTINCT r.rental_id) AS 총대여횟수,\n" +
        "       COUNT(DISTINCT er.external_repair_id) AS 외부정비횟수\n" +
        "FROM camping_car c\n" +
        "JOIN camping_car_company cc ON c.company_id = cc.company_id\n" +
        "LEFT JOIN rental r ON c.car_id = r.car_id\n" +
        "LEFT JOIN external_repair er ON c.car_id = er.car_id\n" +
        "WHERE c.rental_price > (\n" +
        "    SELECT AVG(rental_price) FROM camping_car\n" +
        ")\n" +
        "GROUP BY c.car_id, c.name, cc.name\n" +
        "HAVING COUNT(DISTINCT r.rental_id) > 0\n" +
        "ORDER BY 총대여횟수 DESC",
        
        "-- 4개 이상 테이블 조인 + 부속질의 + GROUP BY 예제 2\n" +
        "SELECT e.name AS 직원명, e.department AS 부서,\n" +
        "       COUNT(DISTINCT ir.internal_repair_id) AS 정비횟수,\n" +
        "       SUM(p.unit_price * 1) AS 사용부품총액\n" +
        "FROM employee e\n" +
        "JOIN internal_repair ir ON e.employee_id = ir.employee_id\n" +
        "JOIN part p ON ir.part_id = p.part_id\n" +
        "JOIN camping_car c ON ir.car_id = c.car_id\n" +
        "WHERE e.role = '정비' AND EXISTS (\n" +
        "    SELECT 1 FROM internal_repair ir2\n" +
        "    WHERE ir2.employee_id = e.employee_id\n" +
        "    AND ir2.repair_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)\n" +
        ")\n" +
        "GROUP BY e.employee_id, e.name, e.department\n" +
        "HAVING COUNT(DISTINCT ir.internal_repair_id) > 2",
        
        "-- 4개 이상 테이블 조인 + 부속질의 + GROUP BY 예제 3\n" +
        "SELECT cu.customer_name AS 고객명,\n" +
        "       COUNT(DISTINCT r.rental_id) AS 대여횟수,\n" +
        "       COUNT(DISTINCT er.external_repair_id) AS 정비의뢰횟수,\n" +
        "       GROUP_CONCAT(DISTINCT rs.name) AS 이용정비소목록\n" +
        "FROM customer cu\n" +
        "JOIN rental r ON cu.driver_license_number = r.driver_license_number\n" +
        "LEFT JOIN external_repair er ON cu.driver_license_number = er.driver_license_number\n" +
        "LEFT JOIN repair_shop rs ON er.shop_id = rs.shop_id\n" +
        "WHERE r.rental_id IN (\n" +
        "    SELECT rental_id FROM rental\n" +
        "    WHERE rental_price > 100000\n" +
        ")\n" +
        "GROUP BY cu.customer_id, cu.customer_name\n" +
        "HAVING COUNT(DISTINCT r.rental_id) >= 2"
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
        exampleButton = new JButton("예제 쿼리 보기");
        clearButton = new JButton("지우기");
        backButton = new JButton("뒤로가기");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 상단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(executeButton);
        buttonPanel.add(exampleButton);
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
        
        exampleButton.addActionListener(e -> {
            String[] options = {"예제 1", "예제 2", "예제 3"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "테스트용 예제 쿼리를 선택하세요:",
                "예제 쿼리 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice >= 0 && choice < EXAMPLE_QUERIES.length) {
                queryArea.setText(EXAMPLE_QUERIES[choice]);
            }
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
            messageArea.setText(String.format(
                "쿼리 실행 성공: %d개 행이 조회되었습니다. (실행 시간: %dms)",
                result.getRowCount(),
                result.getColumnNames()
            ));
        });
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
