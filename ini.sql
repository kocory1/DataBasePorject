

-- 1. DB 삭제 및 생성
DROP DATABASE IF EXISTS camping_car_db;
CREATE DATABASE camping_car_db;
USE camping_car_db;

-- 2. 사용자 생성 및 권한 부여
DROP USER IF EXISTS 'user1'@'localhost';
CREATE USER 'user1'@'localhost' IDENTIFIED BY 'user1';
GRANT SELECT, INSERT, UPDATE, DELETE ON camping_car_db.* TO 'user1'@'localhost';

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

USE camping_car_db;

-- 샘플 데이터 삽입

-- RentalCompany
INSERT INTO RentalCompany VALUES (1, '렌탈회사1', '서울시 강남구 1로', '010-1111-1001', '매니저1', 'manager1@rent.com');
INSERT INTO RentalCompany VALUES (2, '렌탈회사2', '서울시 강남구 2로', '010-1111-1002', '매니저2', 'manager2@rent.com');
INSERT INTO RentalCompany VALUES (3, '렌탈회사3', '서울시 강남구 3로', '010-1111-1003', '매니저3', 'manager3@rent.com');
INSERT INTO RentalCompany VALUES (4, '렌탈회사4', '서울시 강남구 4로', '010-1111-1004', '매니저4', 'manager4@rent.com');
INSERT INTO RentalCompany VALUES (5, '렌탈회사5', '서울시 강남구 5로', '010-1111-1005', '매니저5', 'manager5@rent.com');
INSERT INTO RentalCompany VALUES (6, '렌탈회사6', '서울시 강남구 6로', '010-1111-1006', '매니저6', 'manager6@rent.com');
INSERT INTO RentalCompany VALUES (7, '렌탈회사7', '서울시 강남구 7로', '010-1111-1007', '매니저7', 'manager7@rent.com');
INSERT INTO RentalCompany VALUES (8, '렌탈회사8', '서울시 강남구 8로', '010-1111-1008', '매니저8', 'manager8@rent.com');
INSERT INTO RentalCompany VALUES (9, '렌탈회사9', '서울시 강남구 9로', '010-1111-1009', '매니저9', 'manager9@rent.com');
INSERT INTO RentalCompany VALUES (10, '렌탈회사10', '서울시 강남구 10로', '010-1111-1010', '매니저10', 'manager10@rent.com');
INSERT INTO RentalCompany VALUES (11, '렌탈회사11', '서울시 강남구 11로', '010-1111-1011', '매니저11', 'manager11@rent.com');
INSERT INTO RentalCompany VALUES (12, '렌탈회사12', '서울시 강남구 12로', '010-1111-1012', '매니저12', 'manager12@rent.com');

-- Camper
INSERT INTO Camper VALUES (1, '캠핑카1', 'VN0001', 3, 'image1.jpg', '캠핑카 상세 정보1', 51000, 1, '2025-05-19');
INSERT INTO Camper VALUES (2, '캠핑카2', 'VN0002', 4, 'image2.jpg', '캠핑카 상세 정보2', 52000, 2, '2025-05-16');
INSERT INTO Camper VALUES (3, '캠핑카3', 'VN0003', 5, 'image3.jpg', '캠핑카 상세 정보3', 53000, 3, '2025-05-13');
INSERT INTO Camper VALUES (4, '캠핑카4', 'VN0004', 6, 'image4.jpg', '캠핑카 상세 정보4', 54000, 4, '2025-05-10');
INSERT INTO Camper VALUES (5, '캠핑카5', 'VN0005', 2, 'image5.jpg', '캠핑카 상세 정보5', 55000, 5, '2025-05-07');
INSERT INTO Camper VALUES (6, '캠핑카6', 'VN0006', 3, 'image6.jpg', '캠핑카 상세 정보6', 56000, 6, '2025-05-04');
INSERT INTO Camper VALUES (7, '캠핑카7', 'VN0007', 4, 'image7.jpg', '캠핑카 상세 정보7', 57000, 7, '2025-05-01');
INSERT INTO Camper VALUES (8, '캠핑카8', 'VN0008', 5, 'image8.jpg', '캠핑카 상세 정보8', 58000, 8, '2025-04-28');
INSERT INTO Camper VALUES (9, '캠핑카9', 'VN0009', 6, 'image9.jpg', '캠핑카 상세 정보9', 59000, 9, '2025-04-25');
INSERT INTO Camper VALUES (10, '캠핑카10', 'VN0010', 2, 'image10.jpg', '캠핑카 상세 정보10', 60000, 10, '2025-04-22');
INSERT INTO Camper VALUES (11, '캠핑카11', 'VN0011', 3, 'image11.jpg', '캠핑카 상세 정보11', 61000, 11, '2025-04-19');
INSERT INTO Camper VALUES (12, '캠핑카12', 'VN0012', 4, 'image12.jpg', '캠핑카 상세 정보12', 62000, 12, '2025-04-16');

-- Part
INSERT INTO Part VALUES (1, '부품1', 1050, 21, '2025-05-21', '공급사1');
INSERT INTO Part VALUES (2, '부품2', 1100, 22, '2025-05-20', '공급사2');
INSERT INTO Part VALUES (3, '부품3', 1150, 23, '2025-05-19', '공급사3');
INSERT INTO Part VALUES (4, '부품4', 1200, 24, '2025-05-18', '공급사4');
INSERT INTO Part VALUES (5, '부품5', 1250, 25, '2025-05-17', '공급사5');
INSERT INTO Part VALUES (6, '부품6', 1300, 26, '2025-05-16', '공급사6');
INSERT INTO Part VALUES (7, '부품7', 1350, 27, '2025-05-15', '공급사7');
INSERT INTO Part VALUES (8, '부품8', 1400, 28, '2025-05-14', '공급사8');
INSERT INTO Part VALUES (9, '부품9', 1450, 29, '2025-05-13', '공급사9');
INSERT INTO Part VALUES (10, '부품10', 1500, 30, '2025-05-12', '공급사10');
INSERT INTO Part VALUES (11, '부품11', 1550, 31, '2025-05-11', '공급사11');
INSERT INTO Part VALUES (12, '부품12', 1600, 32, '2025-05-10', '공급사12');

-- Employee
INSERT INTO Employee VALUES (1, '직원1', '010-2222-2001', '서울시 중구 1길', 2230000, 1, '부서2', '사무');
INSERT INTO Employee VALUES (2, '직원2', '010-2222-2002', '서울시 중구 2길', 2260000, 2, '부서3', '정비');
INSERT INTO Employee VALUES (3, '직원3', '010-2222-2003', '서울시 중구 3길', 2290000, 0, '부서4', '관리');
INSERT INTO Employee VALUES (4, '직원4', '010-2222-2004', '서울시 중구 4길', 2320000, 1, '부서1', '사무');
INSERT INTO Employee VALUES (5, '직원5', '010-2222-2005', '서울시 중구 5길', 2350000, 2, '부서2', '정비');
INSERT INTO Employee VALUES (6, '직원6', '010-2222-2006', '서울시 중구 6길', 2380000, 0, '부서3', '관리');
INSERT INTO Employee VALUES (7, '직원7', '010-2222-2007', '서울시 중구 7길', 2410000, 1, '부서4', '사무');
INSERT INTO Employee VALUES (8, '직원8', '010-2222-2008', '서울시 중구 8길', 2440000, 2, '부서1', '정비');
INSERT INTO Employee VALUES (9, '직원9', '010-2222-2009', '서울시 중구 9길', 2470000, 0, '부서2', '관리');
INSERT INTO Employee VALUES (10, '직원10', '010-2222-2010', '서울시 중구 10길', 2500000, 1, '부서3', '사무');
INSERT INTO Employee VALUES (11, '직원11', '010-2222-2011', '서울시 중구 11길', 2530000, 2, '부서4', '정비');
INSERT INTO Employee VALUES (12, '직원12', '010-2222-2012', '서울시 중구 12길', 2560000, 0, '부서1', '관리');

-- Customer
INSERT INTO Customer VALUES (1, 'user1', 'pass1', 'DL0001', '고객1', '서울시 종로구 1로', '010-3333-3001', 'user1@mail.com', '2025-05-18', '캠핑카2형');
INSERT INTO Customer VALUES (2, 'user2', 'pass2', 'DL0002', '고객2', '서울시 종로구 2로', '010-3333-3002', 'user2@mail.com', '2025-05-14', '캠핑카3형');
INSERT INTO Customer VALUES (3, 'user3', 'pass3', 'DL0003', '고객3', '서울시 종로구 3로', '010-3333-3003', 'user3@mail.com', '2025-05-10', '캠핑카4형');
INSERT INTO Customer VALUES (4, 'user4', 'pass4', 'DL0004', '고객4', '서울시 종로구 4로', '010-3333-3004', 'user4@mail.com', '2025-05-06', '캠핑카1형');
INSERT INTO Customer VALUES (5, 'user5', 'pass5', 'DL0005', '고객5', '서울시 종로구 5로', '010-3333-3005', 'user5@mail.com', '2025-05-02', '캠핑카2형');
INSERT INTO Customer VALUES (6, 'user6', 'pass6', 'DL0006', '고객6', '서울시 종로구 6로', '010-3333-3006', 'user6@mail.com', '2025-04-28', '캠핑카3형');
INSERT INTO Customer VALUES (7, 'user7', 'pass7', 'DL0007', '고객7', '서울시 종로구 7로', '010-3333-3007', 'user7@mail.com', '2025-04-24', '캠핑카4형');
INSERT INTO Customer VALUES (8, 'user8', 'pass8', 'DL0008', '고객8', '서울시 종로구 8로', '010-3333-3008', 'user8@mail.com', '2025-04-20', '캠핑카1형');
INSERT INTO Customer VALUES (9, 'user9', 'pass9', 'DL0009', '고객9', '서울시 종로구 9로', '010-3333-3009', 'user9@mail.com', '2025-04-16', '캠핑카2형');
INSERT INTO Customer VALUES (10, 'user10', 'pass10', 'DL0010', '고객10', '서울시 종로구 10로', '010-3333-3010', 'user10@mail.com', '2025-04-12', '캠핑카3형');
INSERT INTO Customer VALUES (11, 'user11', 'pass11', 'DL0011', '고객11', '서울시 종로구 11로', '010-3333-3011', 'user11@mail.com', '2025-04-08', '캠핑카4형');
INSERT INTO Customer VALUES (12, 'user12', 'pass12', 'DL0012', '고객12', '서울시 종로구 12로', '010-3333-3012', 'user12@mail.com', '2025-04-04', '캠핑카1형');

-- Rental
INSERT INTO Rental VALUES (1, 1, 'DL0001', 1, '2025-05-17', 2, 103000, '2025-05-18', '기타요금1', 3100);
INSERT INTO Rental VALUES (2, 2, 'DL0002', 2, '2025-05-12', 3, 106000, '2025-05-13', '기타요금2', 3200);
INSERT INTO Rental VALUES (3, 3, 'DL0003', 3, '2025-05-07', 4, 109000, '2025-05-08', '기타요금3', 3300);
INSERT INTO Rental VALUES (4, 4, 'DL0004', 4, '2025-05-02', 5, 112000, '2025-05-03', '기타요금4', 3400);
INSERT INTO Rental VALUES (5, 5, 'DL0005', 5, '2025-04-27', 6, 115000, '2025-04-28', '기타요금5', 3500);
INSERT INTO Rental VALUES (6, 6, 'DL0006', 6, '2025-04-22', 7, 118000, '2025-04-23', '기타요금6', 3600);
INSERT INTO Rental VALUES (7, 7, 'DL0007', 7, '2025-04-17', 8, 121000, '2025-04-18', '기타요금7', 3700);
INSERT INTO Rental VALUES (8, 8, 'DL0008', 8, '2025-04-12', 9, 124000, '2025-04-13', '기타요금8', 3800);
INSERT INTO Rental VALUES (9, 9, 'DL0009', 9, '2025-04-07', 10, 127000, '2025-04-08', '기타요금9', 3900);
INSERT INTO Rental VALUES (10, 10, 'DL0010', 10, '2025-04-02', 1, 130000, '2025-04-03', '기타요금10', 4000);
INSERT INTO Rental VALUES (11, 11, 'DL0011', 11, '2025-03-28', 2, 133000, '2025-03-29', '기타요금11', 4100);
INSERT INTO Rental VALUES (12, 12, 'DL0012', 12, '2025-03-23', 3, 136000, '2025-03-24', '기타요금12', 4200);

-- InternalMaintenance
INSERT INTO InternalMaintenance VALUES (1, 1, 1, '2025-05-20', 31, 1);
INSERT INTO InternalMaintenance VALUES (2, 2, 2, '2025-05-18', 32, 2);
INSERT INTO InternalMaintenance VALUES (3, 3, 3, '2025-05-16', 33, 3);
INSERT INTO InternalMaintenance VALUES (4, 4, 4, '2025-05-14', 34, 4);
INSERT INTO InternalMaintenance VALUES (5, 5, 5, '2025-05-12', 35, 5);
INSERT INTO InternalMaintenance VALUES (6, 6, 6, '2025-05-10', 36, 6);
INSERT INTO InternalMaintenance VALUES (7, 7, 7, '2025-05-08', 37, 7);
INSERT INTO InternalMaintenance VALUES (8, 8, 8, '2025-05-06', 38, 8);
INSERT INTO InternalMaintenance VALUES (9, 9, 9, '2025-05-04', 39, 9);
INSERT INTO InternalMaintenance VALUES (10, 10, 10, '2025-05-02', 40, 10);
INSERT INTO InternalMaintenance VALUES (11, 11, 11, '2025-04-30', 41, 11);
INSERT INTO InternalMaintenance VALUES (12, 12, 12, '2025-04-28', 42, 12);

-- ExternalMaintenanceShop
INSERT INTO ExternalMaintenanceShop VALUES (1, '정비소1', '경기도 수원시 1길', '031-444-4001', '담당자1', 'repair1@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (2, '정비소2', '경기도 수원시 2길', '031-444-4002', '담당자2', 'repair2@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (3, '정비소3', '경기도 수원시 3길', '031-444-4003', '담당자3', 'repair3@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (4, '정비소4', '경기도 수원시 4길', '031-444-4004', '담당자4', 'repair4@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (5, '정비소5', '경기도 수원시 5길', '031-444-4005', '담당자5', 'repair5@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (6, '정비소6', '경기도 수원시 6길', '031-444-4006', '담당자6', 'repair6@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (7, '정비소7', '경기도 수원시 7길', '031-444-4007', '담당자7', 'repair7@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (8, '정비소8', '경기도 수원시 8길', '031-444-4008', '담당자8', 'repair8@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (9, '정비소9', '경기도 수원시 9길', '031-444-4009', '담당자9', 'repair9@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (10, '정비소10', '경기도 수원시 10길', '031-444-4010', '담당자10', 'repair10@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (11, '정비소11', '경기도 수원시 11길', '031-444-4011', '담당자11', 'repair11@shop.com');
INSERT INTO ExternalMaintenanceShop VALUES (12, '정비소12', '경기도 수원시 12길', '031-444-4012', '담당자12', 'repair12@shop.com');

-- ExternalMaintenance
INSERT INTO ExternalMaintenance VALUES (1, 1, 1, 1, 'DL0001', '외부수리 내용1', '2025-05-20', 40700, '2025-05-21', '기타 외부 정비1');
INSERT INTO ExternalMaintenance VALUES (2, 2, 2, 2, 'DL0002', '외부수리 내용2', '2025-05-18', 41400, '2025-05-19', '기타 외부 정비2');
INSERT INTO ExternalMaintenance VALUES (3, 3, 3, 3, 'DL0003', '외부수리 내용3', '2025-05-16', 42100, '2025-05-17', '기타 외부 정비3');
INSERT INTO ExternalMaintenance VALUES (4, 4, 4, 4, 'DL0004', '외부수리 내용4', '2025-05-14', 42800, '2025-05-15', '기타 외부 정비4');
INSERT INTO ExternalMaintenance VALUES (5, 5, 5, 5, 'DL0005', '외부수리 내용5', '2025-05-12', 43500, '2025-05-13', '기타 외부 정비5');
INSERT INTO ExternalMaintenance VALUES (6, 6, 6, 6, 'DL0006', '외부수리 내용6', '2025-05-10', 44200, '2025-05-11', '기타 외부 정비6');
INSERT INTO ExternalMaintenance VALUES (7, 7, 7, 7, 'DL0007', '외부수리 내용7', '2025-05-08', 44900, '2025-05-09', '기타 외부 정비7');
INSERT INTO ExternalMaintenance VALUES (8, 8, 8, 8, 'DL0008', '외부수리 내용8', '2025-05-06', 45600, '2025-05-07', '기타 외부 정비8');
INSERT INTO ExternalMaintenance VALUES (9, 9, 9, 9, 'DL0009', '외부수리 내용9', '2025-05-04', 46300, '2025-05-05', '기타 외부 정비9');
INSERT INTO ExternalMaintenance VALUES (10, 10, 10, 10, 'DL0010', '외부수리 내용10', '2025-05-02', 47000, '2025-05-03', '기타 외부 정비10');
INSERT INTO ExternalMaintenance VALUES (11, 11, 11, 11, 'DL0011', '외부수리 내용11', '2025-04-30', 47700, '2025-05-01', '기타 외부 정비11');
INSERT INTO ExternalMaintenance VALUES (12, 12, 12, 12, 'DL0012', '외부수리 내용12', '2025-04-28', 48400, '2025-04-29', '기타 외부 정비12');