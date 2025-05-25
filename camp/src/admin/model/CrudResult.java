package admin.model;

/**
 * INSERT/UPDATE/DELETE 결과를 담는 DTO
 */
public class CrudResult {
    private boolean success;
    private int affectedRows;
    private String message;
    private String errorMessage;
    private int errorCode;
    private String sql;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public int getAffectedRows() { return affectedRows; }
    public void setAffectedRows(int affectedRows) { this.affectedRows = affectedRows; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }

    public String getSql() { return sql; }
    public void setSql(String sql) { this.sql = sql; }
}