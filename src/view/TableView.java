package view;

import admin.model.*;
import java.util.List;

/**
 * 테이블 관리 View 인터페이스
 */
public interface TableView {
    TableMenuChoice showTableMenu();
    String selectTable(String[] tables);
    String getWhereCondition();
    String getSetClause();
    String getWhereClause();
    String getInsertValues();
    boolean confirmDelete(String tableName, String condition);
    
    void showAllTables(List<TableInfo> tables);
    void showQueryResult(QueryResult result);
    void showCrudResult(CrudResult result);
    void showTableStructure(List<ColumnInfo> columns);
    void showDataPreview(QueryResult result, int limit);
    void showError(String message);
    void showCancelled();
}
