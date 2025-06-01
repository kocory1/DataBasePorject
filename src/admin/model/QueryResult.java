// model/QueryResult.java
package admin.model;

import java.util.List;

/**
 * SELECT 쿼리 결과를 담는 DTO
 */
public class QueryResult {
    private List<String> columnNames;
    private List<List<String>> rows;
    private int rowCount;
    private String executedSql;

    // Getters and Setters
    public List<String> getColumnNames() { return columnNames; }
    public void setColumnNames(List<String> columnNames) { this.columnNames = columnNames; }

    public List<List<String>> getRows() { return rows; }
    public void setRows(List<List<String>> rows) { this.rows = rows; }

    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }

    public String getExecutedSql() { return executedSql; }
    public void setExecutedSql(String executedSql) { this.executedSql = executedSql; }

    public boolean isEmpty() { return rows == null || rows.isEmpty(); }

    public int getColumnCount() {
        return columnNames == null ? 0 : columnNames.size();
    }
}

