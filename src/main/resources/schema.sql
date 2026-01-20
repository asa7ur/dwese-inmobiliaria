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
SET FOREIGN_KEY_CHECKS = 1;

-- ... (Tablas existentes: offices, properties, agents, etc.)

-- Tabla users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    role VARCHAR(50)
);

-- Tabla offices
CREATE TABLE offices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Tabla agents
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

-- Tabla clients
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Tabla properties
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

-- Tabla intermedia property_agent (ManyToMany)
CREATE TABLE property_agent (
    property_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    PRIMARY KEY (property_id, agent_id),
    CONSTRAINT fk_property_agent_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_property_agent_agent FOREIGN KEY (agent_id) REFERENCES agents(id)
);

-- Tabla appointments
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

-- Tabla transactions
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