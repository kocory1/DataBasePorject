package admin.model;

import java.util.List;

/**
 * 테이블 정보를 담는 DTO
 */
public class TableInfo {
    private String tableName;
    private List<ColumnInfo> columns;
    private QueryResult data;
    private int rowCount;
    private String error;

    // Getters and Setters
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public List<ColumnInfo> getColumns() { return columns; }
    public void setColumns(List<ColumnInfo> columns) { this.columns = columns; }

    public QueryResult getData() { return data; }
    public void setData(QueryResult data) { this.data = data; }

    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public boolean hasError() { return error != null; }
}