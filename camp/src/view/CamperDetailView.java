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
    
    void showInternalMaintenanceDetail(List<InternalMaintenanceInfo> maintenanceList);
    void showExternalMaintenanceDetail(List<ExternalMaintenanceInfo> maintenanceList);
    void showPartDetails(List<InternalMaintenanceInfo> maintenanceList);
    void showShopDetails(List<ExternalMaintenanceInfo> maintenanceList);
}
