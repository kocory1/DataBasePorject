package admin.model;

/**
 * INSERT 방법 선택 열거형
 */
public enum InsertMethod {
    FULL_SQL,    // 완전한 INSERT 문 입력
    VALUES_ONLY  // 값만 입력 (컬럼 순서대로)
}
