insert into USERS (id, username, password, role)
    values (100, 'ana@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_ADMIN');
insert into USERS (id, username, password, role)
    values (101, 'bia@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_CLIENT');
insert into USERS (id, username, password, role)
    values (102, 'bob@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_CLIENT');

insert into CLIENTS (id, name, cpf, id_user) values (10, 'Bianca Silver', '88599201085', 101);
insert into CLIENTS (id, name, cpf, id_user) values (20, 'Robert Garcia', '88617905000', 102);

insert into SPACES (id, code, status) values (100, 'A-01', 'OCCUPIED');
insert into SPACES (id, code, status) values (200, 'A-02', 'OCCUPIED');
insert into SPACES (id, code, status) values (300, 'A-03', 'OCCUPIED');
insert into SPACES (id, code, status) values (400, 'A-04', 'OCCUPIED');
insert into SPACES (id, code, status) values (500, 'A-05', 'OCCUPIED');

insert into CUSTOMER_VACANCY (receipt_number, license_plate, brand, model, color, entry_date, id_client, id_space)
    values ('20230313-101200', 'CAD-4679', 'FIAT', 'PALIO', 'GREEN', '2023-03-13 10:13:56', 20, 100);
insert into CUSTOMER_VACANCY (receipt_number, license_plate, brand, model, color, entry_date, id_client, id_space)
    values ('20240207-113500', 'KLP-7723', 'FORD', 'KA 2022', 'SILVER', '2024-02-07 11:36:56', 10, 200);
insert into CUSTOMER_VACANCY (receipt_number, license_plate, brand, model, color, entry_date, id_client, id_space)
    values ('20240313-101300', 'OKA-3287', 'HONDA', 'CITY', 'BLACK', '2024-03-13 10:13:56', 20, 300);
insert into CUSTOMER_VACANCY (receipt_number, license_plate, brand, model, color, entry_date, id_client, id_space)
    values ('20240313-102300', 'OKA-3289', 'HONDA', 'CITY', 'BLACK', '2024-03-13 10:23:56', 20, 400);
insert into CUSTOMER_VACANCY (receipt_number, license_plate, brand, model, color, entry_date, id_client, id_space)
     values ('20240313-103300', 'OKA-4520', 'HONDA', 'CITY', 'BLACK', '2024-03-13 10:33:56', 10, 400);