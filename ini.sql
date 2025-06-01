

-- 1. DB 삭제 및 생성
DROP DATABASE IF EXISTS DBTEST;
CREATE DATABASE DBTEST;
USE DBTEST;

-- 2. 사용자 생성 및 권한 부여
DROP USER IF EXISTS 'user1'@'localhost';
CREATE USER 'user1'@'localhost' IDENTIFIED BY 'user1';

-- 3. 테이블 생성

CREATE TABLE RentalCompany (
    rental_company_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    phone VARCHAR(20),
    manager_name VARCHAR(50),
    manager_email VARCHAR(100)
);

CREATE TABLE Camper (
    camper_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    vehicle_number VARCHAR(20) UNIQUE,
    seats INT,
    image_url VARCHAR(255),
    details TEXT,
    rental_fee DECIMAL(10,2),
    rental_company_id INT,
    registration_date DATE,
    FOREIGN KEY (rental_company_id) REFERENCES RentalCompany(rental_company_id)
);

CREATE TABLE Part (
    part_id INT PRIMARY KEY,
    part_name VARCHAR(100) NOT NULL,
    part_price DECIMAL(10,2),
    stock_quantity INT,
    entry_date DATE,
    supplier_name VARCHAR(100)
);

CREATE TABLE Employee (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(200),
    salary DECIMAL(10,2),
    dependents_count INT,
    department_name VARCHAR(50),
    role ENUM('관리', '사무', '정비') NOT NULL
);

CREATE TABLE Customer (
    customer_id INT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100),
    license_number VARCHAR(20) UNIQUE,
    customer_name VARCHAR(50),
    address VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    previous_rental_date DATE,
    previous_camper_type VARCHAR(50)
);

CREATE TABLE Rental (
    rental_id INT PRIMARY KEY,
    camper_id INT,
    license_number VARCHAR(20),
    rental_company_id INT,
    rental_start_date DATE,
    rental_period INT,
    bill_amount DECIMAL(10,2),
    payment_due_date DATE,
    additional_charges_description TEXT,
    additional_charges_amount DECIMAL(10,2),
    FOREIGN KEY (camper_id) REFERENCES Camper(camper_id),
    FOREIGN KEY (license_number) REFERENCES Customer(license_number),
    FOREIGN KEY (rental_company_id) REFERENCES RentalCompany(rental_company_id)
);

CREATE TABLE InternalMaintenance (
    internal_maintenance_id INT PRIMARY KEY,
    camper_id INT,
    part_id INT,
    maintenance_date DATE,
    maintenance_duration_minutes INT,
    employee_id INT,
    FOREIGN KEY (camper_id) REFERENCES Camper(camper_id),
    FOREIGN KEY (part_id) REFERENCES Part(part_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE ExternalMaintenanceShop (
    shop_id INT PRIMARY KEY,
    shop_name VARCHAR(100),
    shop_address VARCHAR(200),
    shop_phone VARCHAR(20),
    manager_name VARCHAR(50),
    manager_email VARCHAR(100)
);

CREATE TABLE ExternalMaintenance (
    external_maintenance_id INT PRIMARY KEY,
    camper_id INT,
    shop_id INT,
    rental_company_id INT,
    license_number VARCHAR(20),
    maintenance_details TEXT,
    repair_date DATE,
    repair_cost DECIMAL(10,2),
    payment_due_date DATE,
    additional_maintenance_details TEXT,
    FOREIGN KEY (camper_id) REFERENCES Camper(camper_id),
    FOREIGN KEY (shop_id) REFERENCES ExternalMaintenanceShop(shop_id),
    FOREIGN KEY (rental_company_id) REFERENCES RentalCompany(rental_company_id),
    FOREIGN KEY (license_number) REFERENCES Customer(license_number)
);

-- 4. 권한 적용
FLUSH PRIVILEGES;

-- 5. 샘플 데이터 삽입

USE DBTEST;

-- 샘플 데이터 삽입

-- RentalCompany
INSERT INTO RentalCompany VALUES (1, '서울캠핑카렌탈', '서울시 강남구 테헤란로 123', '010-1111-1001', '김경영', 'kim@seoulcamping.com');
INSERT INTO RentalCompany VALUES (2, '부산여행렌탈', '부산시 해운대구 해변로 456', '010-1111-1002', '이바다', 'lee@busantravel.com');
INSERT INTO RentalCompany VALUES (3, '제주힐링캠핑', '제주시 연동 중앙로 789', '010-1111-1003', '박제주', 'park@jejuhealing.com');
INSERT INTO RentalCompany VALUES (4, '강원산악캠핑', '강원도 춘천시 공지로 321', '010-1111-1004', '최산악', 'choi@gangwoncamp.com');
INSERT INTO RentalCompany VALUES (5, '경기패밀리카', '경기도 수원시 영통로 654', '010-1111-1005', '정가족', 'jung@familycar.com');
INSERT INTO RentalCompany VALUES (6, '전남해안렌탈', '전남 여수시 돌산로 987', '010-1111-1006', '황해안', 'hwang@jeonnamcoast.com');
INSERT INTO RentalCompany VALUES (7, '충북내륙캠핑', '충북 청주시 상당로 147', '010-1111-1007', '오내륙', 'oh@chungbukcamp.com');
INSERT INTO RentalCompany VALUES (8, '대구도심렌탈', '대구시 중구 국채보상로 258', '010-1111-1008', '류도심', 'ryu@daegurental.com');
INSERT INTO RentalCompany VALUES (9, '인천공항렌탈', '인천시 중구 공항로 369', '010-1111-1009', '임공항', 'lim@airportrental.com');
INSERT INTO RentalCompany VALUES (10, '울산산업렌탈', '울산시 남구 삼산로 741', '010-1111-1010', '권산업', 'kwon@ulsanrental.com');
INSERT INTO RentalCompany VALUES (11, '광주호남렌탈', '광주시 서구 상무로 852', '010-1111-1011', '송호남', 'song@honamrental.com');
INSERT INTO RentalCompany VALUES (12, '대전과학렌탈', '대전시 유성구 대학로 963', '010-1111-1012', '문과학', 'moon@sciencerental.com');
INSERT INTO RentalCompany VALUES (13, '경남해변캠핑', '경남 통영시 바다로 123', '010-1111-1013', '조해변', 'cho@gyeongnambeach.com');
INSERT INTO RentalCompany VALUES (14, '충남온천렌탈', '충남 아산시 온천로 456', '010-1111-1014', '한온천', 'han@hotspringrental.com');
INSERT INTO RentalCompany VALUES (15, '전북산촌캠핑', '전북 전주시 한옥로 789', '010-1111-1015', '노산촌', 'no@jeonbukcamp.com');

-- Camper
INSERT INTO Camper VALUES (1, '가족형 대형캠핑카', 'VN0001', 6, 'family_large.jpg', '6인승 풀옵션 가족형 캠핑카, 침실2개, 화장실 완비', 80000, 1, '2025-01-15');
INSERT INTO Camper VALUES (2, '커플형 소형캠핑카', 'VN0002', 2, 'couple_small.jpg', '2인승 커플 전용 캠핑카, 로맨틱한 인테리어', 55000, 2, '2025-01-20');
INSERT INTO Camper VALUES (3, '모험가형 중형캠핑카', 'VN0003', 4, 'adventure_mid.jpg', '4인승 오프로드 전용 캠핑카, 견고한 설계', 70000, 3, '2025-02-01');
INSERT INTO Camper VALUES (4, '럭셔리 프리미엄캠핑카', 'VN0004', 4, 'luxury_premium.jpg', '프리미엄 내장재, 최고급 사양의 4인승 캠핑카', 120000, 4, '2025-02-10');
INSERT INTO Camper VALUES (5, '친환경 에코캠핑카', 'VN0005', 3, 'eco_friendly.jpg', '친환경 소재 3인승 캠핑카, 태양광 패널 장착', 65000, 5, '2025-02-20');
INSERT INTO Camper VALUES (6, '스포츠형 캠핑카', 'VN0006', 4, 'sports_type.jpg', '스포츠 활동 특화 4인승 캠핑카, 장비 보관함 대형', 75000, 6, '2025-03-01');
INSERT INTO Camper VALUES (7, '여행자형 장거리캠핑카', 'VN0007', 5, 'traveler_long.jpg', '장거리 여행 특화 5인승, 연료탱크 대용량', 85000, 7, '2025-03-10');
INSERT INTO Camper VALUES (8, '캠핑초보자용 이지캠핑카', 'VN0008', 3, 'easy_beginner.jpg', '초보자도 쉽게 운전할 수 있는 3인승 캠핑카', 50000, 8, '2025-03-20');
INSERT INTO Camper VALUES (9, '겨울특화 방한캠핑카', 'VN0009', 4, 'winter_special.jpg', '겨울철 방한 시설 완비 4인승 캠핑카', 90000, 9, '2025-04-01');
INSERT INTO Camper VALUES (10, '펜션형 대형캠핑카', 'VN0010', 8, 'pension_large.jpg', '8인승 대형 펜션형 캠핑카, 거실공간 넓음', 100000, 10, '2025-04-10');
INSERT INTO Camper VALUES (11, '바베큐특화 캠핑카', 'VN0011', 4, 'bbq_special.jpg', '바베큐 시설 특화 4인승 캠핑카', 70000, 11, '2025-04-20');
INSERT INTO Camper VALUES (12, '반려동물동반 캠핑카', 'VN0012', 4, 'pet_friendly.jpg', '반려동물 동반 가능한 4인승 캠핑카', 65000, 12, '2025-05-01');
INSERT INTO Camper VALUES (13, '낚시전용 캠핑카', 'VN0013', 3, 'fishing_special.jpg', '낚시 장비 보관 특화 3인승 캠핑카', 60000, 13, '2025-05-05');
INSERT INTO Camper VALUES (14, '사진작가용 캠핑카', 'VN0014', 2, 'photographer.jpg', '사진 장비 보관 특화 2인승 캠핑카', 75000, 14, '2025-05-10');
INSERT INTO Camper VALUES (15, '힐링테라피 캠핑카', 'VN0015', 3, 'healing_therapy.jpg', '힐링과 휴식 특화 3인승 캠핑카', 80000, 15, '2025-05-15');

-- Part
INSERT INTO Part VALUES (1, '엔진오일', 45000, 25, '2025-04-01', '모빌테크놀로지');
INSERT INTO Part VALUES (2, '브레이크패드', 120000, 15, '2025-04-05', '브렘보코리아');
INSERT INTO Part VALUES (3, '타이어', 180000, 20, '2025-04-10', '한국타이어');
INSERT INTO Part VALUES (4, '배터리', 85000, 12, '2025-04-15', 'LG에너지솔루션');
INSERT INTO Part VALUES (5, '에어필터', 35000, 30, '2025-04-20', '만필터코리아');
INSERT INTO Part VALUES (6, '냉각수', 25000, 40, '2025-04-25', '쿨텍');
INSERT INTO Part VALUES (7, '점화플러그', 15000, 50, '2025-05-01', 'NGK');
INSERT INTO Part VALUES (8, '연료필터', 28000, 35, '2025-05-05', '만필터코리아');
INSERT INTO Part VALUES (9, '벨트', 42000, 18, '2025-05-10', '게이츠코리아');
INSERT INTO Part VALUES (10, '워셔액', 8000, 60, '2025-05-15', '소낙스');
INSERT INTO Part VALUES (11, '미션오일', 55000, 22, '2025-05-20', '모빌테크놀로지');
INSERT INTO Part VALUES (12, '쇼크업소버', 150000, 10, '2025-05-25', 'KYB');
INSERT INTO Part VALUES (13, '헤드라이트', 95000, 14, '2025-05-30', '필립스');
INSERT INTO Part VALUES (14, '와이퍼블레이드', 18000, 45, '2025-04-03', '보쉬');
INSERT INTO Part VALUES (15, '연료호스', 32000, 25, '2025-04-08', '현대모비스');

-- Employee
INSERT INTO Employee VALUES (1, '김정비', '010-2222-2001', '서울시 중구 정비로 123', 2800000, 2, '정비부', '정비');
INSERT INTO Employee VALUES (2, '이관리', '010-2222-2002', '서울시 중구 관리로 456', 3500000, 1, '관리부', '관리');
INSERT INTO Employee VALUES (3, '박사무', '010-2222-2003', '서울시 중구 사무로 789', 2600000, 0, '사무부', '사무');
INSERT INTO Employee VALUES (4, '최정비', '010-2222-2004', '부산시 해운대구 수리로 321', 2900000, 3, '정비부', '정비');
INSERT INTO Employee VALUES (5, '정관리', '010-2222-2005', '부산시 해운대구 운영로 654', 3600000, 2, '관리부', '관리');
INSERT INTO Employee VALUES (6, '한사무', '010-2222-2006', '대구시 중구 업무로 987', 2550000, 1, '사무부', '사무');
INSERT INTO Employee VALUES (7, '오정비', '010-2222-2007', '인천시 중구 기술로 147', 3000000, 2, '정비부', '정비');
INSERT INTO Employee VALUES (8, '황관리', '010-2222-2008', '광주시 서구 총괄로 258', 3700000, 1, '관리부', '관리');
INSERT INTO Employee VALUES (9, '임사무', '010-2222-2009', '대전시 유성구 처리로 369', 2650000, 0, '사무부', '사무');
INSERT INTO Employee VALUES (10, '권정비', '010-2222-2010', '울산시 남구 점검로 741', 2950000, 4, '정비부', '정비');
INSERT INTO Employee VALUES (11, '송관리', '010-2222-2011', '제주시 연동 책임로 852', 3800000, 2, '관리부', '관리');
INSERT INTO Employee VALUES (12, '문사무', '010-2222-2012', '강원도 춘천시 조정로 963', 2700000, 1, '사무부', '사무');
INSERT INTO Employee VALUES (13, '조정비', '010-2222-2013', '경기도 수원시 전문로 159', 3100000, 3, '정비부', '정비');
INSERT INTO Employee VALUES (14, '노관리', '010-2222-2014', '충남 아산시 총관로 753', 3900000, 1, '관리부', '관리');
INSERT INTO Employee VALUES (15, '윤사무', '010-2222-2015', '전북 전주시 담당로 486', 2750000, 2, '사무부', '사무');

-- Customer
INSERT INTO Customer VALUES (1, 'camper_lee', 'pass123!', 'DL0001', '이영희', '서울시 종로구 세종대로 175', '010-3333-3001', 'younghee@naver.com', '2025-03-15', '가족형캠핑카');
INSERT INTO Customer VALUES (2, 'travel_kim', 'kim2025@', 'DL0002', '김철수', '부산시 해운대구 우동 1533', '010-3333-3002', 'chulsu@gmail.com', '2025-03-20', '커플형캠핑카');
INSERT INTO Customer VALUES (3, 'adventure_park', 'park!789', 'DL0003', '박모험', '대구시 중구 동성로 123', '010-3333-3003', 'adventure@hanmail.net', '2025-04-01', '모험가형캠핑카');
INSERT INTO Customer VALUES (4, 'luxury_choi', 'luxury#456', 'DL0004', '최부자', '인천시 연수구 송도동 999', '010-3333-3004', 'luxury@daum.net', '2025-04-10', '럭셔리캠핑카');
INSERT INTO Customer VALUES (5, 'eco_jung', 'eco2025$', 'DL0005', '정환경', '광주시 서구 화정로 567', '010-3333-3005', 'eco.jung@outlook.com', '2025-04-15', '친환경캠핑카');
INSERT INTO Customer VALUES (6, 'sports_han', 'sports321*', 'DL0006', '한운동', '대전시 유성구 과학로 890', '010-3333-3006', 'sports.han@yahoo.com', '2025-04-20', '스포츠형캠핑카');
INSERT INTO Customer VALUES (7, 'travel_oh', 'travel987&', 'DL0007', '오여행', '울산시 남구 삼산로 234', '010-3333-3007', 'travel.oh@hotmail.com', '2025-05-01', '장거리캠핑카');
INSERT INTO Customer VALUES (8, 'easy_hwang', 'easy654%', 'DL0008', '황초보', '경기도 수원시 팔달구 인계로 456', '010-3333-3008', 'easy.hwang@nate.com', '2025-05-05', '초보자용캠핑카');
INSERT INTO Customer VALUES (9, 'winter_lim', 'winter123^', 'DL0009', '임겨울', '강원도 춘천시 중앙로 789', '010-3333-3009', 'winter.lim@gmail.com', '2025-05-10', '방한캠핑카');
INSERT INTO Customer VALUES (10, 'family_kwon', 'family456&', 'DL0010', '권대가족', '제주시 연동 중앙로 321', '010-3333-3010', 'family.kwon@naver.com', '2025-05-12', '펜션형캠핑카');
INSERT INTO Customer VALUES (11, 'bbq_song', 'bbq789*', 'DL0011', '송바베큐', '충남 아산시 온천로 654', '010-3333-3011', 'bbq.song@daum.net', '2025-05-15', '바베큐캠핑카');
INSERT INTO Customer VALUES (12, 'pet_moon', 'pet321!', 'DL0012', '문반려', '전북 전주시 한옥마을길 987', '010-3333-3012', 'pet.moon@hanmail.net', '2025-05-18', '반려동물캠핑카');
INSERT INTO Customer VALUES (13, 'fish_cho', 'fish654@', 'DL0013', '조낚시', '경남 통영시 바다로 147', '010-3333-3013', 'fish.cho@outlook.com', '2025-05-20', '낚시캠핑카');
INSERT INTO Customer VALUES (14, 'photo_han', 'photo987#', 'DL0014', '한사진', '충북 청주시 상당로 258', '010-3333-3014', 'photo.han@yahoo.com', '2025-05-22', '사진가캠핑카');
INSERT INTO Customer VALUES (15, 'healing_no', 'healing123$', 'DL0015', '노힐링', '전남 여수시 돌산로 369', '010-3333-3015', 'healing.no@hotmail.com', '2025-05-25', '힐링캠핑카');

-- Rental (5월 중순부터 6월 말까지 더 많은 데이터)
INSERT INTO Rental VALUES (1, 1, 'DL0001', 1, '2025-05-15', 3, 240000, '2025-05-14', '주말 할증료', 30000);
INSERT INTO Rental VALUES (2, 2, 'DL0002', 2, '2025-05-18', 2, 110000, '2025-05-17', '청소비', 20000);
INSERT INTO Rental VALUES (3, 3, 'DL0003', 3, '2025-05-22', 4, 280000, '2025-05-21', '연료비', 50000);
INSERT INTO Rental VALUES (4, 4, 'DL0004', 4, '2025-05-25', 5, 600000, '2025-05-24', '보험료', 80000);
INSERT INTO Rental VALUES (5, 5, 'DL0005', 5, '2025-05-28', 3, 195000, '2025-05-27', '장거리 할증', 25000);
INSERT INTO Rental VALUES (6, 6, 'DL0006', 6, '2025-06-01', 2, 150000, '2025-05-31', '주말 할증료', 35000);
INSERT INTO Rental VALUES (7, 7, 'DL0007', 7, '2025-06-05', 7, 595000, '2025-06-04', '연료비', 75000);
INSERT INTO Rental VALUES (8, 8, 'DL0008', 8, '2025-06-08', 2, 100000, '2025-06-07', '청소비', 15000);
INSERT INTO Rental VALUES (9, 9, 'DL0009', 9, '2025-06-12', 4, 360000, '2025-06-11', '겨울장비 대여', 40000);
INSERT INTO Rental VALUES (10, 10, 'DL0010', 10, '2025-06-15', 6, 600000, '2025-06-14', '대형차 할증', 100000);
INSERT INTO Rental VALUES (11, 11, 'DL0011', 11, '2025-06-18', 3, 210000, '2025-06-17', '바베큐 장비', 30000);
INSERT INTO Rental VALUES (12, 12, 'DL0012', 12, '2025-06-22', 2, 130000, '2025-06-21', '펫케어비', 25000);
INSERT INTO Rental VALUES (13, 13, 'DL0013', 13, '2025-06-25', 4, 240000, '2025-06-24', '낚시장비 대여', 35000);
INSERT INTO Rental VALUES (14, 14, 'DL0014', 14, '2025-06-28', 2, 150000, '2025-06-27', '사진장비 보험', 20000);
INSERT INTO Rental VALUES (15, 15, 'DL0015', 15, '2025-06-30', 3, 240000, '2025-06-29', '힐링패키지', 40000);
INSERT INTO Rental VALUES (16, 1, 'DL0003', 1, '2025-05-20', 2, 160000, '2025-05-15', '조기예약 할인', -10000);
INSERT INTO Rental VALUES (17, 3, 'DL0005', 3, '2025-05-20', 5, 350000, '2025-05-19', '장기대여 할인', -20000);
INSERT INTO Rental VALUES (18, 5, 'DL0007', 5, '2025-06-03', 3, 195000, '2025-06-02', '친환경 할인', -15000);
INSERT INTO Rental VALUES (19, 8, 'DL0010', 8, '2025-06-10', 4, 200000, '2025-06-09', '초보자 패키지', 25000);
INSERT INTO Rental VALUES (20, 12, 'DL0015', 12, '2025-06-20', 5, 325000, '2025-06-19', '반려동물 케어', 50000);

-- InternalMaintenance (4~5월 사이 정비 기록)
INSERT INTO InternalMaintenance VALUES (1, 1, 1, '2025-04-05', 120, 1);
INSERT INTO InternalMaintenance VALUES (2, 2, 2, '2025-04-08', 180, 4);
INSERT INTO InternalMaintenance VALUES (3, 3, 3, '2025-04-12', 240, 7);
INSERT INTO InternalMaintenance VALUES (4, 4, 4, '2025-04-15', 90, 10);
INSERT INTO InternalMaintenance VALUES (5, 5, 5, '2025-04-18', 150, 13);
INSERT INTO InternalMaintenance VALUES (6, 6, 6, '2025-04-22', 200, 2);
INSERT INTO InternalMaintenance VALUES (7, 7, 7, '2025-04-25', 110, 5);
INSERT INTO InternalMaintenance VALUES (8, 8, 8, '2025-04-28', 160, 8);
INSERT INTO InternalMaintenance VALUES (9, 9, 9, '2025-05-02', 135, 11);
INSERT INTO InternalMaintenance VALUES (10, 10, 10, '2025-05-05', 210, 1);
INSERT INTO InternalMaintenance VALUES (11, 11, 11, '2025-05-08', 180, 4);
INSERT INTO InternalMaintenance VALUES (12, 12, 12, '2025-05-12', 145, 7);
INSERT INTO InternalMaintenance VALUES (13, 13, 13, '2025-05-15', 120, 10);
INSERT INTO InternalMaintenance VALUES (14, 14, 14, '2025-05-18', 190, 13);
INSERT INTO InternalMaintenance VALUES (15, 15, 15, '2025-05-22', 165, 2);
INSERT INTO InternalMaintenance VALUES (16, 1, 3, '2025-04-10', 90, 5);
INSERT INTO InternalMaintenance VALUES (17, 5, 7, '2025-04-20', 75, 8);

-- ExternalMaintenanceShop
INSERT INTO ExternalMaintenanceShop VALUES (1, '서울정비센터', '서울시 강남구 테헤란로 456', '02-555-0001', '김정비', 'kim@seoulservice.com');
INSERT INTO ExternalMaintenanceShop VALUES (2, '부산오토케어', '부산시 해운대구 센텀로 789', '051-555-0002', '이수리', 'lee@busanauto.com');
INSERT INTO ExternalMaintenanceShop VALUES (3, '대구모터스', '대구시 중구 달구벌대로 321', '053-555-0003', '박엔진', 'park@daegumotor.com');
INSERT INTO ExternalMaintenanceShop VALUES (4, '인천카서비스', '인천시 연수구 컨벤시아대로 654', '032-555-0004', '최브레이크', 'choi@incheoncar.com');
INSERT INTO ExternalMaintenanceShop VALUES (5, '광주정비공업', '광주시 서구 상무중앙로 987', '062-555-0005', '정타이어', 'jung@gwangjurepair.com');
INSERT INTO ExternalMaintenanceShop VALUES (6, '대전테크센터', '대전시 유성구 과학로 147', '042-555-0006', '한배터리', 'han@daejeontech.com');
INSERT INTO ExternalMaintenanceShop VALUES (7, '울산산업정비', '울산시 남구 산업로 258', '052-555-0007', '오오일', 'oh@ulsanindust.com');
INSERT INTO ExternalMaintenanceShop VALUES (8, '경기오토', '경기도 수원시 영통구 광교로 369', '031-555-0008', '황필터', 'hwang@gyeonggiauto.com');
INSERT INTO ExternalMaintenanceShop VALUES (9, '강원정비소', '강원도 춘천시 중앙로 741', '033-555-0009', '임쇼크', 'lim@gangwonrepair.com');
INSERT INTO ExternalMaintenanceShop VALUES (10, '제주카케어', '제주시 연동 중앙로 852', '064-555-0010', '권라이트', 'kwon@jejucar.com');
INSERT INTO ExternalMaintenanceShop VALUES (11, '충남모터스', '충남 아산시 배방읍 고속도로 963', '041-555-0011', '송와이퍼', 'song@chungnammotor.com');
INSERT INTO ExternalMaintenanceShop VALUES (12, '전북정비센터', '전북 전주시 덕진구 건산로 159', '063-555-0012', '문벨트', 'moon@jeonbukservice.com');
INSERT INTO ExternalMaintenanceShop VALUES (13, '경남해안정비', '경남 통영시 무전동 해안로 753', '055-555-0013', '조냉각', 'cho@gyeongnamcoast.com');
INSERT INTO ExternalMaintenanceShop VALUES (14, '충북산업정비', '충북 청주시 흥덕구 가경로 486', '043-555-0014', '노플러그', 'no@chungbukindust.com');
INSERT INTO ExternalMaintenanceShop VALUES (15, '전남남도정비', '전남 목포시 하당로 592', '061-555-0015', '윤호스', 'yun@jeonnamnam.com');

-- ExternalMaintenance (4~5월 사이 외부 정비 기록)
INSERT INTO ExternalMaintenance VALUES (1, 1, 1, 1, 'DL0001', '엔진 오일 교환 및 필터 점검', '2025-04-07', 85000, '2025-04-14', '정기점검 포함');
INSERT INTO ExternalMaintenance VALUES (2, 2, 2, 2, 'DL0002', '브레이크 패드 교체', '2025-04-10', 150000, '2025-04-17', '브레이크액 교환');
INSERT INTO ExternalMaintenance VALUES (3, 3, 3, 3, 'DL0003', '타이어 4개 교체 및 휠 밸런싱', '2025-04-14', 320000, '2025-04-21', '타이어 정렬 포함');
INSERT INTO ExternalMaintenance VALUES (4, 4, 4, 4, 'DL0004', '배터리 교체 및 충전 시스템 점검', '2025-04-17', 120000, '2025-04-24', '전기계통 점검');
INSERT INTO ExternalMaintenance VALUES (5, 5, 5, 5, 'DL0005', '에어컨 필터 교체 및 가스 충전', '2025-04-21', 95000, '2025-04-28', '냉각시스템 청소');
INSERT INTO ExternalMaintenance VALUES (6, 6, 6, 6, 'DL0006', '냉각수 교환 및 워터펌프 점검', '2025-04-24', 75000, '2025-05-01', '라디에이터 청소');
INSERT INTO ExternalMaintenance VALUES (7, 7, 7, 7, 'DL0007', '점화플러그 교체 및 점화시스템 점검', '2025-04-28', 65000, '2025-05-05', '연비 개선 작업');
INSERT INTO ExternalMaintenance VALUES (8, 8, 8, 8, 'DL0008', '연료필터 교체 및 인젝터 청소', '2025-05-02', 110000, '2025-05-09', '연료시스템 점검');
INSERT INTO ExternalMaintenance VALUES (9, 9, 9, 9, 'DL0009', '타이밍벨트 교체', '2025-05-05', 280000, '2025-05-12', '워터펌프 동시교체');
INSERT INTO ExternalMaintenance VALUES (10, 10, 10, 10, 'DL0010', '워셔액 보충 및 와이퍼 점검', '2025-05-08', 25000, '2025-05-15', '유리세정 시스템 점검');
INSERT INTO ExternalMaintenance VALUES (11, 11, 11, 11, 'DL0011', '미션오일 교환', '2025-05-12', 85000, '2025-05-19', '변속기 점검');
INSERT INTO ExternalMaintenance VALUES (12, 12, 12, 12, 'DL0012', '쇼크업소버 교체', '2025-05-15', 200000, '2025-05-22', '서스펜션 점검');
INSERT INTO ExternalMaintenance VALUES (13, 13, 13, 13, 'DL0013', '헤드라이트 교체 및 조정', '2025-05-18', 130000, '2025-05-25', '전조등 점검');
INSERT INTO ExternalMaintenance VALUES (14, 14, 14, 14, 'DL0014', '와이퍼블레이드 교체', '2025-05-21', 35000, '2025-05-28', '우천시 안전점검');
INSERT INTO ExternalMaintenance VALUES (15, 15, 15, 15, 'DL0015', '연료호스 교체 및 연료계통 점검', '2025-05-24', 75000, '2025-05-31', '연료누출 방지');

-- 캠핑카 대여 예약에 필요한 테이블에 대해서만 권한 부여
-- 1) 캠핑카 조회를 위한 테이블들
GRANT SELECT ON DBTEST.Camper TO 'user1'@'localhost';
GRANT SELECT ON DBTEST.RentalCompany TO 'user1'@'localhost';

-- 2) 고객 관리를 위한 테이블
GRANT SELECT, INSERT, UPDATE, DELETE ON DBTEST.Customer TO 'user1'@'localhost';

-- 3) 대여 관리를 위한 테이블
GRANT SELECT, INSERT, UPDATE, DELETE ON DBTEST.Rental TO 'user1'@'localhost';

-- 4) 외부 정비 관련 테이블 (회원이 외부 정비 요청할 수 있음)
GRANT SELECT ON DBTEST.ExternalMaintenanceShop TO 'user1'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON DBTEST.ExternalMaintenance TO 'user1'@'localhost';

-- 주의: 다음 테이블들은 관리자 전용이므로 user1에게 권한 부여하지 않음
-- - Part (부품 재고 관리)
-- - Employee (직원 관리)  
-- - InternalMaintenance (자체 정비 관리)
