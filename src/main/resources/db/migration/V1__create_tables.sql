
				---====SEQUENCES=======----
	
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 50;    

CREATE SEQUENCE owner_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE vehicle_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE request_seq START WITH 1 INCREMENT BY 50;
	
CREATE SEQUENCE payment_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE license_plate_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE history_seq START WITH 1 INCREMENT BY 50;


					--=======Tabelas=====---
CREATE TABLE users (
    id          BIGINT          PRIMARY KEY DEFAULT nextval('user_seq'),
    public_id   UUID            NOT NULL UNIQUE,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    user_password    VARCHAR(255)    NOT NULL,
    province    VARCHAR(50)     NOT NULL,   
    user_role        VARCHAR(50)     NOT NULL    
);

CREATE TABLE owner (
    id          BIGINT          PRIMARY KEY DEFAULT nextval('owner_seq'),
    public_id   UUID            NOT NULL UNIQUE,
    first_name  VARCHAR(100)    NOT NULL,
    last_name   VARCHAR(100)    NOT NULL,
    nuit        VARCHAR(20)     NOT NULL UNIQUE,
    email       VARCHAR(255)    NOT NULL,
    birth_date  DATE            NOT NULL
);

CREATE TABLE vehicle (
    id                BIGINT          PRIMARY KEY DEFAULT nextval('vehicle_seq'),
    public_id         UUID            NOT NULL UNIQUE,
    brand             VARCHAR(100)    NOT NULL,
    model             VARCHAR(100)    NOT NULL,
    color             VARCHAR(50)     NOT NULL,
    chassis_number    VARCHAR(100)    NOT NULL UNIQUE,
    manufacture_year  INTEGER         NOT NULL
);

CREATE TABLE request (
    id          BIGINT          PRIMARY KEY DEFAULT nextval('request_seq'),
    public_id   UUID            NOT NULL UNIQUE,
    owner_id    BIGINT          NOT NULL REFERENCES owner(id),
    vehicle_id  BIGINT          NOT NULL REFERENCES vehicle(id),
    user_id     BIGINT          NOT NULL REFERENCES users(id),
    request_status      VARCHAR(50)     NOT NULL,   
    created_at  TIMESTAMP       NOT NULL
);

CREATE TABLE payment (
    id          BIGINT              PRIMARY KEY DEFAULT nextval('payment_seq'),
    public_id   UUID                NOT NULL UNIQUE,
    request_id  BIGINT              NOT NULL UNIQUE REFERENCES request(id), 
    amount      NUMERIC(10, 2)      NOT NULL,
    method      VARCHAR(50)         NOT NULL,   
    payment_status      VARCHAR(50)         NOT NULL    
);

CREATE TABLE license_plate (
    id          BIGINT          PRIMARY KEY DEFAULT nextval('license_plate_seq'),
    public_id   UUID            NOT NULL UNIQUE,
    request_id  BIGINT          NOT NULL UNIQUE REFERENCES request(id), 
    plate_number      VARCHAR(20)     NOT NULL UNIQUE,
    issue_date  DATE            NOT NULL,
    plate_status      VARCHAR(50)     NOT NULL    
);

CREATE TABLE history (
    id           BIGINT          PRIMARY KEY DEFAULT nextval('history_seq'),
    request_id   BIGINT          NOT NULL REFERENCES request(id),
    history_event        VARCHAR(50)     NOT NULL,  
    event_description  TEXT            NOT NULL,
    occurred_at  TIMESTAMP       NOT NULL
);