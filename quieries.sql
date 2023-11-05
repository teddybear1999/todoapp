use
    taskdb;

CREATE TABLE `tasks`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `description`  VARCHAR(255) NOT NULL,
    `is_completed` TINYINT(1)   NOT NULL,
    `due_date`     DATE,
    `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
)