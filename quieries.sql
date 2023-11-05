CREATE DATABASE taskdb;
 
 CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';
 GRANT ALL PRIVILEGES ON taskdb.* TO 'user'@'localhost';
 FLUSH PRIVILEGES;
 
 use
     taskdb;
 
 CREATE TABLE `task`
 (
     `id`           BIGINT       NOT NULL AUTO_INCREMENT,
     `description`  VARCHAR(255) NOT NULL,
     `is_completed` TINYINT(1)   NOT NULL,
     `due_date`     DATE,
     `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `updated_at`   TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
 )
