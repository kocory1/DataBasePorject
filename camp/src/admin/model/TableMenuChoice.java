package admin.model;

/**
 * 테이블 관리 메뉴 선택 열거형
 */
public enum TableMenuChoice {
    VIEW_ALL,   // 전체 테이블 보기
    SELECT,     // 데이터 조회
    INSERT,     // 데이터 삽입
    UPDATE,     // 데이터 수정
    DELETE,     // 데이터 삭제
    STRUCTURE,  // 테이블 구조 보기
    STATISTICS, // 테이블 통계
    BACK        // 돌아가기
}
