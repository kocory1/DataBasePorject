package admin.controller;

import admin.service.CamperDetailService;
import admin.model.*;
import view.CamperDetailView;
import java.sql.Connection;
import java.util.List;

/**
 * 캠핑카 상세 조회 컨트롤러
 */
public class CamperDetailController {
    private CamperDetailView view;
    private CamperDetailService service;

    public CamperDetailController(CamperDetailView view, Connection connection) {
        this.view = view;
        this.service = new CamperDetailService(connection);
    }

    /**
     * 캠핑카 상세 조회 처리
     */
    public void handleCamperDetailView() {
        try {
            // 1. 캠핑카 목록 조회
            List<CamperSummary> campers = service.getAllCampers();
            
            // 2. 캠핑카 선택
            CamperSummary selectedCamper = view.selectCamper(campers);
            if (selectedCamper == null) {
                return;
            }

            // 3. 선택된 캠핑카 상세 정보 조회
            view.showProgress("캠핑카 상세 정보 조회 중...");
            CamperDetailInfo detail = service.getCamperDetail(selectedCamper.getCamperId());

            // 4. 상세 정보 표시
            view.showCamperDetail(detail);

            // 5. 추가 옵션 처리
            handleDetailOptions(detail);

        } catch (Exception e) {
            view.showError("캠핑카 상세 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 상세 정보 화면에서의 추가 옵션 처리
     */
    private void handleDetailOptions(CamperDetailInfo detail) {
        while (true) {
            String choice = view.showDetailOptions();

            switch (choice) {
                case "1":
                    // 자체 정비 내역 상세 보기
                    view.showInternalMaintenanceDetail(detail.getInternalMaintenanceList());
                    break;
                case "2":
                    // 외부 정비 내역 상세 보기
                    view.showExternalMaintenanceDetail(detail.getExternalMaintenanceList());
                    break;
                case "3":
                    // 부품 정보 보기 (내부 정비에서 사용된 부품들)
                    showPartDetails(detail);
                    break;
                case "4":
                    // 정비소 정보 보기 (외부 정비에서 사용된 정비소들)
                    showShopDetails(detail);
                    break;
                case "5":
                    // 새 캠핑카 선택 처리 - UI에서 캠핑카 선택 버튼을 누른 경우
                    CamperSummary newSelectedCamper = view.getSelectedCamper();
                    if (newSelectedCamper != null) {
                        try {
                            view.showProgress("새 캠핑카 정보 조회 중...");
                            CamperDetailInfo newDetail = service.getCamperDetail(newSelectedCamper.getCamperId());
                            view.showCamperDetail(newDetail);
                            
                            // 현재 디테일 정보 업데이트하고 계속 처리
                            detail = newDetail;
                        } catch (Exception e) {
                            view.showError("캠핑카 정보 조회 실패: " + e.getMessage());
                        }
                    }
                    break;
                case "0":
                    return;
                default:
                    view.showError("잘못된 선택입니다.");
            }
        }
    }

    /**
     * 부품 상세 정보 표시
     */
    private void showPartDetails(CamperDetailInfo detail) {
        try {
            List<InternalMaintenanceInfo> maintenanceList = detail.getInternalMaintenanceList();
            view.showPartDetails(maintenanceList);
        } catch (Exception e) {
            view.showError("부품 정보 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 정비소 상세 정보 표시
     */
    private void showShopDetails(CamperDetailInfo detail) {
        try {
            List<ExternalMaintenanceInfo> maintenanceList = detail.getExternalMaintenanceList();
            view.showShopDetails(maintenanceList);
        } catch (Exception e) {
            view.showError("정비소 정보 조회 실패: " + e.getMessage());
        }
    }
}
