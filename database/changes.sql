ALTER TABLE location
    ADD COLUMN coordinates POINT NULL AFTER `comment`;

CREATE TABLE `pipe_lake`.`funding_source`
(
    `id`          INT          NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50)  NOT NULL,
    `description` VARCHAR(200) NOT NULL,
    `start_date`  DATE         NOT NULL,
    `end_date`    DATE         NULL,
    `type`        VARCHAR(15)  NOT NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE `pipe_lake`.`measurement`
    ADD COLUMN `funding_source_id` INT NULL AFTER `comment`;
ALTER TABLE `pipe_lake`.`measurement`
    ADD INDEX `fk_measurement_funding_source_idx` (`funding_source_id` ASC) VISIBLE;

ALTER TABLE `pipe_lake`.`measurement`
    ADD CONSTRAINT `fk_measurement_funding_source`
        FOREIGN KEY (`funding_source_id`)
            REFERENCES `pipe_lake`.`funding_source` (`id`)
            ON DELETE SET NULL
            ON UPDATE NO ACTION;

