SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS property_agent;
DROP TABLE IF EXISTS property_images;
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS agents;
DROP TABLE IF EXISTS offices;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_roles;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    image VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
    CURRENT_TIMESTAMP,
    last_password_change_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE offices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    email VARCHAR(100) NOT NULL,
    image VARCHAR(255) NULL,
    office_id BIGINT NOT NULL,
    CONSTRAINT fk_agent_office FOREIGN KEY (office_id) REFERENCES offices(id)
);

CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE properties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    location VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    type VARCHAR(10) NOT NULL,
    floors INT NOT NULL,
    bedrooms INT NOT NULL,
    bathrooms INT NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE property_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    property_id BIGINT NOT NULL,
    CONSTRAINT fk_property_images_property
        FOREIGN KEY (property_id) REFERENCES properties(id)
        ON DELETE CASCADE
);

CREATE TABLE property_agent (
    property_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    PRIMARY KEY (property_id, agent_id),
    CONSTRAINT fk_property_agent_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_property_agent_agent FOREIGN KEY (agent_id) REFERENCES agents(id)
);

CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_timestamp BIGINT NOT NULL,
    notes VARCHAR(255),
    agent_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    CONSTRAINT fk_appointment_agent FOREIGN KEY (agent_id) REFERENCES agents(id),
    CONSTRAINT fk_appointment_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_appointment_property FOREIGN KEY (property_id) REFERENCES properties(id)
);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_timestamp BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    property_id BIGINT UNIQUE,
    client_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    CONSTRAINT fk_transaction_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_transaction_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_transaction_agent FOREIGN KEY (agent_id) REFERENCES agents(id)
);