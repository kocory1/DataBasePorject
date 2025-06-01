
package admin.controller;

import admin.service.TableService;
import admin.model.*;
import view.TableView;
import java.sql.Connection;
import java.util.List;

/**
 * 테이블 관리 컨트롤러
 */
public class TableController {
    private TableView view;
    private TableService service;

    public TableController(TableView view, Connection connection) {
        this.view = view;
        this.service = new TableService(connection);
    }

    /**
     * 테이블 관리 메뉴 처리
     */
    public void handleTableManagement() {
        while (true) {
            TableMenuChoice choice = view.showTableMenu();

            switch (choice) {
                case VIEW_ALL:
                    handleViewAllTables();
                    break;
                case SELECT:
                    handleSelect();
                    break;
                case INSERT:
                    handleInsert();
                    break;
                case UPDATE:
                    handleUpdate();
                    break;
                case DELETE:
                    handleDelete();
                    break;
                case BACK:
                    return;
                default:
                    view.showError("잘못된 선택입니다.");
            }
        }
    }

    /**
     * 전체 테이블 조회 처리
     */
    private void handleViewAllTables() {
        try {
            List<TableInfo> tables = service.getAllTablesInfo();
            view.showAllTables(tables);
        } catch (Exception e) {
            view.showError("테이블 조회 실패: " + e.getMessage());
        }
    }

    /**
     * SELECT 처리
     */
    private void handleSelect() {
        try {
            // 1. 테이블 선택
            String tableName = view.selectTable(service.ALL_TABLES);
            if (tableName == null) return;

            // 2. 조건 입력
            String condition = view.getWhereCondition();

            // 3. 쿼리 실행
            QueryResult result = service.executeSelect(tableName, condition);

            // 4. 결과 표시
            view.showQueryResult(result);

        } catch (Exception e) {
            view.showError("조회 실패: " + e.getMessage());
        }
    }

    /**
     * INSERT 처리
     */
    private void handleInsert() {
        try {
            // 1. 테이블 선택
            String tableName = view.selectTable(service.ALL_TABLES);
            if (tableName == null) return;

            // 2. 테이블 구조 보여주기
            List<ColumnInfo> columns = service.getTableColumns(tableName);
            view.showTableStructure(columns);

            // 3. 입력 방식 선택
            InsertMethod method = view.selectInsertMethod();

            // 4. 데이터 입력 및 실행
            CrudResult result;
            if (method == InsertMethod.FULL_SQL) {
                String sql = view.getFullInsertSQL();
                result = service.executeInsertSql(sql);
            } else {
                String values = view.getInsertValues();
                result = service.executeInsert(tableName, values);
            }

            // 5. 결과 표시
            view.showCrudResult(result);

        } catch (Exception e) {
            view.showError("삽입 실패: " + e.getMessage());
        }
    }

    /**
     * UPDATE 처리
     */
    private void handleUpdate() {
        try {
            // 1. 테이블 선택
            String tableName = view.selectTable(service.ALL_TABLES);
            if (tableName == null) return;

            // 2. 현재 데이터 미리보기
            QueryResult preview = service.executeSelect(tableName, null);
            view.showDataPreview(preview, 5);

            // 3. SET 절과 WHERE 절 입력
            String setClause = view.getSetClause();
            String whereClause = view.getWhereClause();

            // 4. 실행
            CrudResult result = service.executeUpdate(tableName, setClause, whereClause);

            // 5. 결과 표시
            view.showCrudResult(result);

        } catch (Exception e) {
            view.showError("수정 실패: " + e.getMessage());
        }
    }

    /**
     * DELETE 처리
     */
    private void handleDelete() {
        try {
            // 1. 테이블 선택
            String tableName = view.selectTable(service.ALL_TABLES);
            if (tableName == null) return;

            // 2. 현재 데이터 미리보기
            QueryResult preview = service.executeSelect(tableName, null);
            view.showDataPreview(preview, 10);

            // 3. WHERE 절 입력
            String whereClause = view.getWhereClause();

            // 4. 최종 확인
            if (!view.confirmDelete(tableName, whereClause)) {
                view.showCancelled();
                return;
            }

            // 5. 실행
            CrudResult result = service.executeDelete(tableName, whereClause);

            // 6. 결과 표시
            view.showCrudResult(result);

        } catch (Exception e) {
            view.showError("삭제 실패: " + e.getMessage());
        }
    }
}
