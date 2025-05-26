package test;

import dao_user.*;
import model.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class UserTestMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CustomerDAO customerDAO = new CustomerDAO();
        CamperDAO camperDAO = new CamperDAO();
        RentalDAO rentalDAO = new RentalDAO();

        System.out.print("아이디: ");
        String id = sc.nextLine();
        System.out.print("비밀번호: ");
        String pw = sc.nextLine();

        if (!customerDAO.login(id, pw)) {
            System.out.println("\u274c 로그인 실패");
            return;
        }

        System.out.println("\u2705 로그인 성공");

        while (true) {
            System.out.println("\n===== 회원 메뉴 =====");
            System.out.println("1. 캠핑카 전체 조회");
            System.out.println("2. 캠핑카 대여 가능 여부 확인");
            System.out.println("3. 캠핑카 대여 등록");
            System.out.println("4. 내 대여 내역 조회");
            System.out.println("5. 대여 정보 수정");
            System.out.println("6. 대여 취소");
            System.out.println("0. 종료");
            System.out.print("선택 >> ");
            int menu = Integer.parseInt(sc.nextLine());

            try {
                if (menu == 0) {
                    System.out.println("프로그램 종료");
                    break;

                } else if (menu == 1) {
                    for (Camper c : camperDAO.getAllCampers()) {
                        System.out.println("ID: " + c.getCamperId() + ", 모델: " + c.getName() + ", 차량번호: " + c.getVehicleNumber() + ", 요금: " + c.getRentalFee());
                    }

                } else if (menu == 2) {
                    System.out.print("캠핑카 ID 입력: ");
                    int camperId = Integer.parseInt(sc.nextLine());
                    boolean available = camperDAO.isAvailable(camperId);
                    System.out.println(available ? "대여 가능" : "대여 불가");

                } else if (menu == 3) {
                    Rental r = new Rental();
                    System.out.print("캠핑카 ID: ");
                    r.setCamperId(Integer.parseInt(sc.nextLine()));
                    System.out.print("차량 번호: ");
                    r.setLicenseNumber(sc.nextLine());
                    System.out.print("렌탈 회사 ID: ");
                    r.setRentalCompanyId(Integer.parseInt(sc.nextLine()));
                    System.out.print("대여 시작일 (yyyy-MM-dd): ");
                    r.setRentalStartDate(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(sc.nextLine()).getTime()));
                    System.out.print("대여 기간(일): ");
                    r.setRentalPeriod(Integer.parseInt(sc.nextLine()));
                    System.out.print("총 요금: ");
                    r.setBillAmount(Double.parseDouble(sc.nextLine()));
                    System.out.print("결제 마감일 (yyyy-MM-dd): ");
                    r.setPaymentDueDate(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(sc.nextLine()).getTime()));
                    System.out.print("추가 요금 설명 (없으면 enter): ");
                    r.setAdditionalChargesDescription(sc.nextLine());
                    System.out.print("추가 요금 금액: ");
                    r.setAdditionalChargesAmount(Double.parseDouble(sc.nextLine()));

                    rentalDAO.insertRental(r);

                } else if (menu == 4) {
                    System.out.print("차량 번호 입력: ");
                    String license = sc.nextLine();
                    ArrayList<Rental> list = rentalDAO.getRentalsByLicense(license);
                    if (list.isEmpty()) {
                        System.out.println("해당 차량 번호의 대여 기록이 없습니다.");
                    } else {
                        for (Rental r : list) {
                            System.out.println("대여ID: " + r.getRentalId() +
                                               ", 캠핑카ID: " + r.getCamperId() +
                                               ", 시작일: " + r.getRentalStartDate() +
                                               ", 기간: " + r.getRentalPeriod() +
                                               ", 요금: " + r.getBillAmount());
                        }
                    }

                } else if (menu == 5) {
                    Rental r = new Rental();
                    System.out.print("수정할 rental_id: ");
                    r.setRentalId(Integer.parseInt(sc.nextLine()));
                    System.out.print("새 대여 기간: ");
                    r.setRentalPeriod(Integer.parseInt(sc.nextLine()));
                    System.out.print("새 총 요금: ");
                    r.setBillAmount(Double.parseDouble(sc.nextLine()));
                    System.out.print("새 결제 마감일 (yyyy-MM-dd): ");
                    r.setPaymentDueDate(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(sc.nextLine()).getTime()));
                    rentalDAO.updateRental(r);

                } else if (menu == 6) {
                    System.out.print("삭제할 rental_id 입력: ");
                    int rentalId = Integer.parseInt(sc.nextLine());
                    rentalDAO.deleteRental(rentalId);

                } else {
                    System.out.println("\u274c 잘못된 메뉴 선택");
                }

            } catch (Exception e) {
                System.out.println("\u274c 오류 발생: " + e.getMessage());
            }
        }

        sc.close();
    }
}
