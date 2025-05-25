package view.swing;

import view.TableView;
import admin.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * 테이블 관리를 위한 Swing GUI View 구현
 */
public class SwingTableView extends JFrame implements TableView {
    
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextArea queryArea;
    private JTextArea messageArea;
    private TableMenuChoice currentChoice;
    private final Object choiceLock = new Object();
    
    // 메뉴 버튼들
    private JButton viewAllButton;
    private JButton selectButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton;
    
    public SwingTableView() {
        super("테이블 관리");
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setVisible(false);
    }
    
    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // 버튼 초기화
        viewAllButton = new JButton("전체 테이블 보기");
        selectButton = new JButton("조회 (SELECT)");
        insertButton = new JButton("삽입 (INSERT)");
        updateButton = new JButton("수정 (UPDATE)");
        deleteButton = new JButton("삭제 (DELETE)");
        backButton = new JButton("뒤로가기");
        
        // 결과 테이블
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // 텍스트 영역
        queryArea = new JTextArea(5, 50);
        queryArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        messageArea = new JTextArea(3, 50);
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(240, 240, 240));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 상단 메뉴 패널
        JPanel menuPanel = new JPanel(new FlowLayout());
        menuPanel.add(viewAllButton);
        menuPanel.add(selectButton);
        menuPanel.add(insertButton);
        menuPanel.add(updateButton);
        menuPanel.add(deleteButton);
        menuPanel.add(backButton);
        add(menuPanel, BorderLayout.NORTH);
        
        // 중앙 분할 패널
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // 상단: 쿼리 입력 영역
        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("쿼리/입력 영역"));
        queryPanel.add(new JScrollPane(queryArea), BorderLayout.CENTER);
        splitPane.setTopComponent(queryPanel);
        
        // 하단: 결과 테이블
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("결과"));
        resultPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        splitPane.setBottomComponent(resultPanel);
        
        splitPane.setDividerLocation(150);
        add(splitPane, BorderLayout.CENTER);
        
        // 하단 메시지 영역
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder("메시지"));
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        messagePanel.setPreferredSize(new Dimension(0, 100));
        add(messagePanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        viewAllButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = TableMenuChoice.VIEW_ALL;
                choiceLock.notify();
            }
        });
        
        selectButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = TableMenuChoice.SELECT;
                choiceLock.notify();
            }
        });
        
        insertButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = TableMenuChoice.INSERT;
                choiceLock.notify();
            }
        });
        
        updateButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = TableMenuChoice.UPDATE;
                choiceLock.notify();
            }
        });
        
        deleteButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = TableMenuChoice.DELETE;
                choiceLock.notify();
            }
        });
        
        backButton.addActionListener(e -> {
            synchronized (choiceLock) {
                currentChoice = TableMenuChoice.BACK;
                choiceLock.notify();
            }
        });
    }
    
    @Override
    public TableMenuChoice showTableMenu() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            messageArea.setText("테이블 관리 메뉴가 열렸습니다.");
        });
        
        synchronized (choiceLock) {
            try {
                choiceLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return TableMenuChoice.BACK;
            }
        }
        
        if (currentChoice == TableMenuChoice.BACK) {
            SwingUtilities.invokeLater(() -> setVisible(false));
        }
        
        return currentChoice;
    }
    
    @Override
    public String selectTable(String[] tables) {
//        return (String) SwingUtilities.invokeAndWait(() -> {
//            return JOptionPane.showInputDialog(
//                this,
//                "테이블을 선택하세요:",
//                "테이블 선택",
//                null,
//                tables,
//                tables[0]
//            );
//        }, null);
        return "미구현";
    }
    
    @Override
    public String getWhereCondition() {
        return JOptionPane.showInputDialog(
            this,
            "WHERE 조건을 입력하세요 (전체 조회는 엔터):",
            "조건 입력",
            JOptionPane.QUESTION_MESSAGE
        );
    }
    
    @Override
    public String getSetClause() {
        return JOptionPane.showInputDialog(
            this,
            "SET 절을 입력하세요 (예: name='홍길동', age=30):",
            "SET 절 입력",
            JOptionPane.QUESTION_MESSAGE
        );
    }
    
    @Override
    public String getWhereClause() {
        return JOptionPane.showInputDialog(
            this,
            "WHERE 절을 입력하세요:",
            "WHERE 절 입력",
            JOptionPane.QUESTION_MESSAGE
        );
    }
    
    @Override
    public String getFullInsertSQL() {
        queryArea.setText("INSERT INTO 테이블명 (컬럼1, 컬럼2, ...) VALUES (값1, 값2, ...)");
        return JOptionPane.showInputDialog(
            this,
            "전체 INSERT SQL을 입력하세요:",
            "INSERT SQL 입력",
            JOptionPane.QUESTION_MESSAGE
        );
    }
    
    @Override
    public String getInsertValues() {
        return JOptionPane.showInputDialog(
            this,
            "VALUES 값들을 입력하세요 (예: '홍길동', 30, 'user@email.com'):",
            "VALUES 입력",
            JOptionPane.QUESTION_MESSAGE
        );
    }
    
    @Override
    public InsertMethod selectInsertMethod() {
        String[] options = {"VALUES만 입력", "전체 SQL 입력"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "입력 방식을 선택하세요:",
            "입력 방식 선택",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        return choice == 0 ? InsertMethod.VALUES_ONLY : InsertMethod.FULL_SQL;
    }
    
    @Override
    public boolean confirmDelete(String tableName, String condition) {
        String message = String.format(
            "정말로 삭제하시겠습니까?\n테이블: %s\n조건: %s",
            tableName,
            condition != null ? condition : "전체"
        );
        
        int result = JOptionPane.showConfirmDialog(
            this,
            message,
            "삭제 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        return result == JOptionPane.YES_OPTION;
    }
    
    @Override
    public void showAllTables(List<TableInfo> tables) {
        SwingUtilities.invokeLater(() -> {
            Vector<String> columns = new Vector<>();
            columns.add("테이블명");
            columns.add("레코드 수");
            
            Vector<Vector<Object>> data = new Vector<>();
            for (TableInfo table : tables) {
                Vector<Object> row = new Vector<>();
                row.add(table.getTableName());
                row.add(table.getRowCount());
                data.add(row);
            }
            
            tableModel.setDataVector(data, columns);
            messageArea.setText(String.format("전체 %d개 테이블을 조회했습니다.", tables.size()));
        });
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
            queryArea.setText(result.getExecutedSql().toString());
            messageArea.setText(String.format("%d개 행이 조회되었습니다.", result.getRowCount()));
        });
    }
    
    @Override
    public void showCrudResult(CrudResult result) {
        SwingUtilities.invokeLater(() -> {
            if (result.isSuccess()) {
                messageArea.setText(String.format("성공: %d개 행이 영향을 받았습니다.", result.getAffectedRows()));
            } else {
                messageArea.setText("실패: " + result.getMessage());
            }
            queryArea.setText(result.getMessage());
        });
    }
    
    @Override
    public void showTableStructure(List<ColumnInfo> columns) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder("테이블 구조:\n");
            for (ColumnInfo col : columns) {
                sb.append(String.format("- %s (%s) %s\n", 
                    col.getName(),
                    col.getType(),
                    col.isNullable() ? "NULL 가능" : "NOT NULL"
                ));
            }
            queryArea.setText(sb.toString());
        });
    }
    
    @Override
    public void showDataPreview(QueryResult result, int limit) {
        SwingUtilities.invokeLater(() -> {
            if (result.getRows().isEmpty()) {
                messageArea.setText("미리보기: 데이터가 없습니다.");
                return;
            }
            
            int count = Math.min(result.getRowCount(), limit);
            messageArea.setText(String.format("미리보기: 상위 %d개 행 표시 (전체 %d개)", count, result.getRowCount()));
            
            Vector<String> columns = new Vector<>(result.getColumnNames());
            Vector<Vector<Object>> data = new Vector<>();
            
            for (int i = 0; i < count; i++) {
                data.add(new Vector<>(result.getRows().get(i)));
            }
            
            tableModel.setDataVector(data, columns);
        });
    }
    
    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText("오류: " + message);
            JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    @Override
    public void showCancelled() {
        SwingUtilities.invokeLater(() -> {
            messageArea.setText("작업이 취소되었습니다.");
        });
    }
}
