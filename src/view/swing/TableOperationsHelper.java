package view.swing;

import admin.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * 테이블 데이터 표시 관련 헬퍼 클래스
 * 테이블 데이터 조회, 구조 표시, 미리보기 등의 기능을 제공합니다.
 */
public class TableOperationsHelper {
    
    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private final Consumer<String> messageHandler;
    
    public TableOperationsHelper(JTable resultTable, DefaultTableModel tableModel, 
                                Consumer<String> messageHandler) {
        this.resultTable = resultTable;
        this.tableModel = tableModel;
        this.messageHandler = messageHandler;
    }
    
    // 이전 생성자를 유지하여 호환성 보장 (다른 곳에서 사용할 경우를 대비)
    public TableOperationsHelper(JTable resultTable, DefaultTableModel tableModel, 
                                JTextArea queryArea, Consumer<String> messageHandler) {
        this(resultTable, tableModel, messageHandler);
    }
    
    /**
     * 전체 테이블 목록 표시
     */
    public void showAllTables(List<TableInfo> tables) {
        SwingUtilities.invokeLater(() -> {
            Vector<String> columns = new Vector<>();
            columns.add("테이블명");
            columns.add("레코드 수");
            columns.add("설명");
            
            Vector<Vector<Object>> data = new Vector<>();
            for (TableInfo table : tables) {
                Vector<Object> row = new Vector<>();
                row.add(table.getTableName());
                row.add(table.getRowCount());
                
                // 테이블 설명 추가
                String description = TableUtils.getTableDescription(table.getTableName());
                row.add(description);
                
                data.add(row);
            }
            
            tableModel.setDataVector(data, columns);
            messageHandler.accept(String.format("전체 %d개 테이블을 조회했습니다.", tables.size()));
            
            // 테이블 열 너비 자동 조정
            TableUtils.adjustColumnWidths(resultTable);
        });
    }
    
    /**
     * 쿼리 결과 표시
     */
    public void showQueryResult(QueryResult result) {
        SwingUtilities.invokeLater(() -> {
            if (result.getRows().isEmpty()) {
                tableModel.setDataVector(new Vector<>(), new Vector<>());
                messageHandler.accept("조회 결과가 없습니다.");
                return;
            }
            
            Vector<String> columns = new Vector<>(result.getColumnNames());
            Vector<Vector<Object>> data = new Vector<>();
            
            for (List<String> row : result.getRows()) {
                data.add(new Vector<>(row));
            }
            
            tableModel.setDataVector(data, columns);
            messageHandler.accept(String.format("%d개 행이 조회되었습니다.", result.getRowCount()));
            
            // 테이블 열 너비 자동 조정
            TableUtils.adjustColumnWidths(resultTable);
        });
    }
    
    /**
     * 테이블 구조 정보 표시
     */
    public void showTableStructure(List<ColumnInfo> columns) {
        SwingUtilities.invokeLater(() -> {
            // 테이블 컬럼 구조를 테이블로 표시
            Vector<String> columnHeaders = TableUtils.createColumnInfoHeaderVector();
            Vector<Vector<Object>> data = TableUtils.createColumnInfoDataVector(columns);
            
            tableModel.setDataVector(data, columnHeaders);
            messageHandler.accept("테이블 구조 조회 완료 - " + columns.size() + "개 컬럼");
            
            // 테이블 열 너비 자동 조정
            TableUtils.adjustColumnWidths(resultTable);
            
            // NULL 허용 컬럼 색상 설정
            TableUtils.setupColumnInfoRenderer(resultTable);
        });
    }
    
    /**
     * 데이터 미리보기 표시
     */
    public void showDataPreview(QueryResult result, int limit) {
        SwingUtilities.invokeLater(() -> {
            if (result.getRows().isEmpty()) {
                messageHandler.accept("미리보기: 데이터가 없습니다.");
                return;
            }
            
            // limit가 -1이면 전체 데이터 표시, 그렇지 않으면 제한
            int count = (limit == -1) ? result.getRowCount() : Math.min(result.getRowCount(), limit);
            
            if (limit == -1) {
                messageHandler.accept(String.format("전체 데이터: %d개 행 표시", result.getRowCount()));
            } else {
                messageHandler.accept(String.format("미리보기: 상위 %d개 행 표시 (전체 %d개)", count, result.getRowCount()));
            }
            
            Vector<String> columns = new Vector<>(result.getColumnNames());
            Vector<Vector<Object>> data = new Vector<>();
            
            for (int i = 0; i < count; i++) {
                data.add(new Vector<>(result.getRows().get(i)));
            }
            
            tableModel.setDataVector(data, columns);
            
            // 테이블 열 너비 자동 조정
            TableUtils.adjustColumnWidths(resultTable);
            
            // 행 번호 표시를 위한 JTable 설정
            try {
                JScrollPane scrollPane = (JScrollPane) resultTable.getParent().getParent();
                TableUtils.RowNumberTable rowTable = new TableUtils.RowNumberTable(resultTable);
                scrollPane.setRowHeaderView(rowTable);
                scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            } catch (Exception e) {
                // 레이아웃이 예상과 다를 경우 무시
                e.printStackTrace();
            }
            
            // 행 간격 설정
            resultTable.setIntercellSpacing(new Dimension(5, 1));
        });
    }
}