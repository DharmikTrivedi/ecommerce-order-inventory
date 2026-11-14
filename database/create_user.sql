-- run once as root (e.g. in MySQL Workbench) before starting the app
CREATE DATABASE IF NOT EXISTS ecom_oims;
CREATE USER IF NOT EXISTS 'oims_app'@'localhost' IDENTIFIED BY 'Oims@2026dev';
ALTER USER 'oims_app'@'localhost' IDENTIFIED BY 'Oims@2026dev';
GRANT ALL PRIVILEGES ON ecom_oims.* TO 'oims_app'@'localhost';
FLUSH PRIVILEGES;
