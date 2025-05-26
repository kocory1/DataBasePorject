package view.swing;

import view.TableView;
import admin.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * 테이블 관리를 위한 Swing GUI View 구현
 * <p>
 * 이 클래스는 캠핑카 예약 시스템의 데이터베이스 테이블을 관리하기 위한 그래픽 인터페이스를 제공합니다.
 * 각 기능은 다음과 같은 전문화된 헬퍼 클래스로 분리되어 있습니다:
 * </p>
 * <ul>
 *   <li>{@link TableDialogHelper} - 대화상자 및 사용자 입력 관련 기능</li>
 *   <li>{@link MessageHelper} - 사용자 메시지 표시 관련 기능</li>
 *   <li>{@link TableOperationsHelper} - 테이블 데이터 조작 및 표시 관련 기능</li>
 *   <li>{@link TableUtils} - 테이블 렌더링 및 유틸리티 기능</li>
 * </ul>
 * <p>
 * 이 클래스는 기본적인 UI 레이아웃 구성과 이벤트 핸들링에 집중하고,
 * 구체적인 기능 구현은 각 헬퍼 클래스에 위임합니다.
 * </p>
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
    
    // 헬퍼 클래스들
    private TableDialogHelper dialogHelper;
    private MessageHelper messageHelper;
    private TableOperationsHelper tableOperationsHelper;
    
    public SwingTableView() {
        super("테이블 관리");
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setVisible(false);
        
        // 헬퍼 클래스 초기화
        dialogHelper = new TableDialogHelper(this);
        messageHelper = new MessageHelper(this, messageArea);
        tableOperationsHelper = new TableOperationsHelper(
            resultTable, 
            tableModel, 
            queryArea, 
            message -> messageHelper.showInfo(message)
        );
    }
    
    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);  // 창 크기 증가
        setLocationRelativeTo(null);
        
        // 버튼 초기화 및 스타일링
        initializeButtons();
        
        // 테이블 초기화
        initializeTable();
        
        // 텍스트 영역 초기화
        initializeTextAreas();
    }
    
    /**
     * 버튼 컴포넌트 초기화
     */
    private void initializeButtons() {
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 12);
        Dimension buttonSize = new Dimension(150, 35);
        
        // 버튼 생성 및 설정
        viewAllButton = createStyledButton("전체 테이블 보기", buttonFont, buttonSize);
        selectButton = createStyledButton("조회 (SELECT)", buttonFont, buttonSize);
        insertButton = createStyledButton("삽입 (INSERT)", buttonFont, buttonSize);
        updateButton = createStyledButton("수정 (UPDATE)", buttonFont, buttonSize);
        deleteButton = createStyledButton("삭제 (DELETE)", buttonFont, buttonSize);
        backButton = createStyledButton("뒤로가기", buttonFont, buttonSize);
        
        // 뒤로가기 버튼 색상 설정
        backButton.setBackground(new Color(255, 200, 200));
    }
    
    /**
     * 일관된 스타일의 버튼 생성
     */
    private JButton createStyledButton(String text, Font font, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * 테이블 컴포넌트 초기화
     */
    private void initializeTable() {
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 테이블 수정 방지
            }
        };
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 12));
        resultTable.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setFillsViewportHeight(true);
    }
    
    /**
     * 텍스트 영역 컴포넌트 초기화
     */
    private void initializeTextAreas() {
        queryArea = new JTextArea(5, 50);
        queryArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        queryArea.setLineWrap(true);
        queryArea.setWrapStyleWord(true);
        
        messageArea = new JTextArea(3, 50);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        messageArea.setBackground(new Color(245, 245, 245));
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        add(createMenuPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createMessagePanel(), BorderLayout.SOUTH);
    }
    
    /**
     * 상단 메뉴 패널 생성
     */
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        menuPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        menuPanel.add(viewAllButton);
        menuPanel.add(selectButton);
        menuPanel.add(insertButton);
        menuPanel.add(updateButton);
        menuPanel.add(deleteButton);
        menuPanel.add(Box.createHorizontalStrut(20)); // 간격 추가
        menuPanel.add(backButton);
        
        return menuPanel;
    }
    
    /**
     * 중앙 컨텐츠 패널 생성
     */
    private JSplitPane createContentPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        splitPane.setTopComponent(createQueryPanel());
        splitPane.setBottomComponent(createResultPanel());
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.2); // 테이블이 더 많은 공간을 차지하도록
        
        return splitPane;
    }
    
    /**
     * 쿼리 입력 패널 생성
     */
    private JPanel createQueryPanel() {
        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "쿼리/입력 영역", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                new Font("맑은 고딕", Font.BOLD, 12)
        ));
        
        // 쿼리 영역에 라인 번호 추가
        JScrollPane queryScroll = new JScrollPane(queryArea);
        queryScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        queryPanel.add(queryScroll, BorderLayout.CENTER);
        
        return queryPanel;
    }
    
    /**
     * 결과 테이블 패널 생성
     */
    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "결과", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                new Font("맑은 고딕", Font.BOLD, 12)
        ));
        
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        resultPanel.add(tableScroll, BorderLayout.CENTER);
        
        return resultPanel;
    }
    
    /**
     * 하단 메시지 패널 생성
     */
    private JPanel createMessagePanel() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "메시지", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                new Font("맑은 고딕", Font.BOLD, 12)
        ));
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        messagePanel.setPreferredSize(new Dimension(0, 120));
        
        return messagePanel;
    }
    
    private void setupEventHandlers() {
        // 각 버튼에 대한 이벤트 핸들러 설정
        setupButtonHandlers();
    }
    
    /**
     * 버튼 이벤트 핸들러 설정
     */
    private void setupButtonHandlers() {
        viewAllButton.addActionListener(e -> handleMenuChoice(TableMenuChoice.VIEW_ALL));
        selectButton.addActionListener(e -> handleMenuChoice(TableMenuChoice.SELECT));
        insertButton.addActionListener(e -> handleMenuChoice(TableMenuChoice.INSERT));
        updateButton.addActionListener(e -> handleMenuChoice(TableMenuChoice.UPDATE));
        deleteButton.addActionListener(e -> handleMenuChoice(TableMenuChoice.DELETE));
        backButton.addActionListener(e -> handleMenuChoice(TableMenuChoice.BACK));
    }
    
    /**
     * 메뉴 선택 처리
     */
    private void handleMenuChoice(TableMenuChoice choice) {
        synchronized (choiceLock) {
            currentChoice = choice;
            choiceLock.notify();
        }
    }
    
    @Override
    public TableMenuChoice showTableMenu() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            messageHelper.showInfo("테이블 관리 메뉴가 열렸습니다.");
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
        return dialogHelper.selectTable(tables, message -> messageArea.setText(message));
    }
    
    @Override
    public String getWhereCondition() {
        return dialogHelper.getWhereCondition();
    }
    
    @Override
    public String getSetClause() {
        return dialogHelper.getSetClause();
    }
    
    @Override
    public String getWhereClause() {
        return dialogHelper.getWhereClause();
    }
    
    @Override
    public String getFullInsertSQL() {
        return dialogHelper.getFullInsertSQL(queryArea);
    }
    
    @Override
    public String getInsertValues() {
        return dialogHelper.getInsertValues();
    }
    
    @Override
    public InsertMethod selectInsertMethod() {
        return dialogHelper.selectInsertMethod();
    }
    
    @Override
    public boolean confirmDelete(String tableName, String condition) {
        return dialogHelper.confirmDelete(tableName, condition);
    }
    
    @Override
    public void showAllTables(List<TableInfo> tables) {
        tableOperationsHelper.showAllTables(tables);
    }
    
    @Override
    public void showQueryResult(QueryResult result) {
        tableOperationsHelper.showQueryResult(result);
    }
    
    @Override
    public void showCrudResult(CrudResult result) {
        messageHelper.showCrudResult(result, queryArea);
    }
    
    @Override
    public void showTableStructure(List<ColumnInfo> columns) {
        tableOperationsHelper.showTableStructure(columns);
    }

    @Override
    public void showDataPreview(QueryResult result, int limit) {
        tableOperationsHelper.showDataPreview(result, limit);
    }

    @Override
    public void showError(String message) {
        messageHelper.showError(message);
    }
    
    @Override
    public void showCancelled() {
        messageHelper.showCancelled();
    }
}
