package view;

import admin.model.*;
import java.util.List;

/**
 * 캠핑카 상세 조회 View 인터페이스
 */
public interface CamperDetailView {
    CamperSummary selectCamper(List<CamperSummary> campers);
    void showProgress(String message);
    void showError(String message);
    void showSuccess(String message);
    
    void showCamperDetail(CamperDetailInfo detail);
    String showDetailOptions();
    
    // 현재 선택된 캠핑카 정보 반환 (새로운 캠핑카 선택 시 사용)
    CamperSummary getSelectedCamper();
    
    void showInternalMaintenanceDetail(List<InternalMaintenanceInfo> maintenanceList);
    void showExternalMaintenanceDetail(List<ExternalMaintenanceInfo> maintenanceList);
    void showPartDetails(List<InternalMaintenanceInfo> maintenanceList);
    void showShopDetails(List<ExternalMaintenanceInfo> maintenanceList);
}
