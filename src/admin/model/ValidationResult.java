package admin.model;

/**
 * 데이터 검증 결과를 담는 DTO
 */
public class ValidationResult {
    private boolean valid;
    private String errorMessage;
    private String suggestion;

    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
}