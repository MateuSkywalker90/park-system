insert into USERS (id, username, password, role) values (100, 'ana@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_ADMIN');
insert into USERS (id, username, password, role) values (101, 'bia@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (102, 'bob@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (103, 'toby@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_CLIENT');

insert into CLIENTS (id, name, cpf, id_user) values (10, 'Bianca Silver', '88599201085', 101);
insert into CLIENTS (id, name, cpf, id_user) values (20, 'Robert Garcia', '88617905000', 102);