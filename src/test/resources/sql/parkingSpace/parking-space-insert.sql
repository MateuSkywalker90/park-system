insert into USERS (id, username, password, role) values (100, 'ana@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_ADMIN');
insert into USERS (id, username, password, role) values (101, 'bia@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (102, 'bob@email.com', '$2a$12$SrgjCv/sCgIZNXjjWuKSrej9mieEYjcULSmPS/xWSnSVSpwCaMqza', 'ROLE_CLIENT');

insert into SPACES (id, code, status) values (10, 'A-01', 'FREE');
insert into SPACES (id, code, status) values (20, 'A-02', 'FREE');
insert into SPACES (id, code, status) values (30, 'A-03', 'OCCUPIED');
insert into SPACES (id, code, status) values (40, 'A-04', 'FREE');