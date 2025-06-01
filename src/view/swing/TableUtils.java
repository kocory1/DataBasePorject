package view.swing;

import admin.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * 테이블 관련 유틸리티 클래스
 * 테이블 렌더링, 포맷팅, 데이터 변환 등 테이블 관련 공통 기능을 제공합니다.
 */
public class TableUtils {
    
    private TableUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
    }
    
    /**
     * 테이블 열 너비를 컨텐츠에 맞게 자동 조정
     * @param table 대상 테이블
     */
    public static void adjustColumnWidths(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // 최소 너비
            
            // 헤더 너비 확인
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            Object headerValue = tableColumn.getHeaderValue();
            
            if (headerValue != null) {
                width = Math.max(width, headerValue.toString().length() * 10 + 20);
            }
            
            // 데이터 너비 확인 (처음 100행만)
            for (int row = 0; row < Math.min(table.getRowCount(), 100); row++) {
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
    
    /**
     * 테이블명에 따른 한글 설명을 반환
     * @param tableName 테이블명
     * @return 테이블 설명
     */
    public static String getTableDescription(String tableName) {
        if (tableName == null) {
            return "";
        }
        
        switch (tableName.toLowerCase()) {
            case "rentalcompany":
                return "캠핑카 대여 회사 정보";
            case "camper":
                return "캠핑카 정보";
            case "part":
                return "부품 재고 정보";
            case "employee":
                return "직원 정보";
            case "customer":
                return "고객 정보";
            case "rental":
                return "캠핑카 대여 기록";
            case "internalmaintenance":
                return "자체 정비 기록";
            case "externalmaintenanceshop":
                return "외부 정비소 정보";
            case "externalmaintenance":
                return "외부 정비 기록";
            default:
                return "";
        }
    }
    
    /**
     * 키 타입에 적절한 아이콘을 추가하여 반환
     * @param keyType 키 타입 문자열
     * @return 아이콘이 추가된 키 타입 문자열
     */
    public static String getKeyTypeWithIcon(String keyType) {
        if (keyType == null || keyType.isEmpty()) {
            return "";
        }
        
        switch (keyType.toUpperCase()) {
            case "PRI":
                return "🔑 PK";
            case "UNI":
                return "🔒 UNI";
            case "MUL":
                return "🔗 FK";
            default:
                return keyType;
        }
    }
    
    /**
     * 테이블에 행 번호를 표시하는 기능 추가
     * @param table 대상 테이블
     * @return 행 번호가 있는 스크롤 패널
     */
    public static JScrollPane createTableWithRowNumbers(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        
        // 행 번호 테이블 생성 및 설정
        RowNumberTable rowTable = new RowNumberTable(table);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
        
        return scrollPane;
    }
    
    /**
     * 테이블에 NULL 허용 및 키 타입 표시를 위한 셀 렌더러 설정
     * @param table 대상 테이블
     */
    public static void setupColumnInfoRenderer(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // NULL 허용 여부 컬럼에 색상 적용
                if (column == 2) {
                    if ("O".equals(value)) {
                        c.setForeground(Color.BLUE);
                    } else if ("X".equals(value)) {
                        c.setForeground(new Color(200, 0, 0));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } else if (column == 3 && value != null && !value.toString().isEmpty()) {
                    // 키 타입에 색상 적용
                    c.setForeground(new Color(0, 120, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
    }
    
    /**
     * 테이블 구조 정보 표시를 위한 데이터 벡터 생성
     * @param columns 컬럼 정보 리스트
     * @return 데이터 벡터와 컬럼 헤더
     */
    public static Vector<Vector<Object>> createColumnInfoDataVector(List<ColumnInfo> columns) {
        Vector<Vector<Object>> data = new Vector<>();
        
        for (ColumnInfo col : columns) {
            Vector<Object> row = new Vector<>();
            row.add(col.getName());
            row.add(col.getType());
            row.add(col.isNullable() ? "O" : "X");
            row.add(getKeyTypeWithIcon(col.getKey()));
            row.add(col.getDefaultValue() != null ? col.getDefaultValue() : "");
            row.add(col.getExtra() != null ? col.getExtra() : "");
            data.add(row);
        }
        
        return data;
    }
    
    /**
     * 테이블 구조 정보를 위한 컬럼 헤더 벡터 생성
     * @return 컬럼 헤더 벡터
     */
    public static Vector<String> createColumnInfoHeaderVector() {
        Vector<String> columnHeaders = new Vector<>();
        columnHeaders.add("컬럼명");
        columnHeaders.add("데이터 타입");
        columnHeaders.add("NULL 허용");
        columnHeaders.add("키 타입");
        columnHeaders.add("기본값");
        columnHeaders.add("추가 속성");
        return columnHeaders;
    }
    
    /**
     * 테이블 구조 정보를 문자열로 변환
     * @param columns 컬럼 정보 리스트
     * @return 포맷팅된 문자열
     */
    public static String formatColumnInfoToString(List<ColumnInfo> columns) {
        StringBuilder sb = new StringBuilder("테이블 구조:\n");
        
        for (ColumnInfo col : columns) {
            sb.append(String.format("- %s (%s) %s %s\n", 
                col.getName(),
                col.getType(),
                col.isNullable() ? "NULL 가능" : "NOT NULL",
                col.getKey() != null && !col.getKey().isEmpty() ? col.getKey() : ""
            ));
        }
        
        return sb.toString();
    }
    
    /**
     * 행 번호를 표시하는 테이블 클래스
     */
    public static class RowNumberTable extends JTable {
        public RowNumberTable(JTable table) {
            super(new RowNumberTableModel(table.getModel()));
            setFocusable(false);
            setAutoCreateColumnsFromModel(false);
            setSelectionModel(table.getSelectionModel());
            setRowHeight(table.getRowHeight());
            
            TableColumn column = new TableColumn();
            column.setHeaderValue("No.");
            column.setCellRenderer(new RowNumberRenderer());
            addColumn(column);
            
            getColumnModel().getColumn(0).setPreferredWidth(50);
            setPreferredScrollableViewportSize(getPreferredSize());
        }
        
        /**
         * 행 번호를 제공하는 테이블 모델
         */
        private static class RowNumberTableModel extends AbstractTableModel {
            private TableModel source;
            
            public RowNumberTableModel(TableModel source) {
                this.source = source;
            }
            
            @Override
            public int getRowCount() {
                return source.getRowCount();
            }
            
            @Override
            public int getColumnCount() {
                return 1;
            }
            
            @Override
            public Object getValueAt(int row, int column) {
                return row + 1;
            }
        }
        
        /**
         * 행 번호 셀 렌더러
         */
        private static class RowNumberRenderer extends DefaultTableCellRenderer {
            public RowNumberRenderer() {
                setHorizontalAlignment(JLabel.CENTER);
                setBackground(new Color(240, 240, 240));
                setFont(new Font("맑은 고딕", Font.BOLD, 12));
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                return this;
            }
        }
    }
}