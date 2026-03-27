CREATE DATABASE delivereats;
GO
USE delivereats;
GO

CREATE TABLE order_read_model (
    order_id VARCHAR(36) PRIMARY KEY,
    customer_name NVARCHAR(255),
    restaurant_name NVARCHAR(255),
    items NVARCHAR(MAX),
    total_amount DECIMAL(10,2),
    status VARCHAR(50),
    rider_name NVARCHAR(255),
    tracking_number VARCHAR(36),
    failure_reason NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE saga_state (
    saga_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36),
    status VARCHAR(50),
    current_step VARCHAR(50),
    completed_steps NVARCHAR(MAX),
    compensated_steps NVARCHAR(MAX),
    failure_reason NVARCHAR(MAX),
    started_at DATETIME2 DEFAULT GETDATE(),
    completed_at DATETIME2
);
