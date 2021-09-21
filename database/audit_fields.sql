ALTER TABLE `pipe_lake`.`document`
    ADD COLUMN `created_by` INT NULL AFTER `text`,
    ADD COLUMN `modified_by` INT NULL AFTER `created_by`,
    ADD INDEX `fk_document_modified_by_idx` (`modified_by` ASC) VISIBLE,
    ADD INDEX `fk_document_created_by_idx` (`created_by` ASC) VISIBLE;
;
ALTER TABLE `pipe_lake`.`document`
    ADD CONSTRAINT `fk_document_modified_by`
        FOREIGN KEY (`modified_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT `fk_document_created_by`
        FOREIGN KEY (`created_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

update document set modified_by=4, created_by=4;

ALTER TABLE `pipe_lake`.`document`
    DROP FOREIGN KEY `fk_document_created_by`,
    DROP FOREIGN KEY `fk_document_modified_by`;
ALTER TABLE `pipe_lake`.`document`
    CHANGE COLUMN `created_by` `created_by` INT NOT NULL ,
    CHANGE COLUMN `modified_by` `modified_by` INT NOT NULL ;
ALTER TABLE `pipe_lake`.`document`
    ADD CONSTRAINT `fk_document_created_by`
        FOREIGN KEY (`created_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`),
    ADD CONSTRAINT `fk_document_modified_by`
        FOREIGN KEY (`modified_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`);

-- Event
ALTER TABLE `pipe_lake`.`event`
    ADD COLUMN `created` TIMESTAMP NULL AFTER `comment`,
    ADD COLUMN `last_updated` TIMESTAMP NULL AFTER `created`,
    ADD COLUMN `created_by` INT NULL AFTER `last_updated`,
    ADD COLUMN `modified_by` INT NULL AFTER `created_by`,
    ADD INDEX `fk_event_created_by_idx` (`created_by` ASC) VISIBLE,
    ADD INDEX `fk_event_modified_by_idx` (`modified_by` ASC) VISIBLE;
;
ALTER TABLE `pipe_lake`.`event`
    ADD CONSTRAINT `fk_event_created_by`
        FOREIGN KEY (`created_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT `fk_event_modified_by`
        FOREIGN KEY (`modified_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

update event set created_by=reporter_id, modified_by=reporter_id;
update event set created=now(), last_updated=now();

ALTER TABLE `pipe_lake`.`event`
    DROP FOREIGN KEY `fk_event_created_by`,
    DROP FOREIGN KEY `fk_event_modified_by`,
    DROP FOREIGN KEY `fk_event_reporter`;
ALTER TABLE `pipe_lake`.`event`
    DROP COLUMN `reporter_id`,
    CHANGE COLUMN `created` `created` TIMESTAMP NOT NULL ,
    CHANGE COLUMN `last_updated` `last_updated` TIMESTAMP NOT NULL ,
    CHANGE COLUMN `created_by` `created_by` INT NOT NULL ,
    CHANGE COLUMN `modified_by` `modified_by` INT NOT NULL ,
    DROP INDEX `fk_event_reporter_idx` ;
;
ALTER TABLE `pipe_lake`.`event`
    ADD CONSTRAINT `fk_event_created_by`
        FOREIGN KEY (`created_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`),
    ADD CONSTRAINT `fk_event_modified_by`
        FOREIGN KEY (`modified_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`);

-- Measurement
ALTER TABLE `pipe_lake`.`measurement`
    ADD COLUMN `created` TIMESTAMP NULL AFTER `funding_source_id`,
    ADD COLUMN `last_updated` TIMESTAMP NULL AFTER `created`,
    ADD COLUMN `created_by` INT NULL AFTER `last_updated`,
    ADD COLUMN `modified_by` INT NULL AFTER `created_by`,
    ADD INDEX `fk_measurement_created_by_idx` (`created_by` ASC) VISIBLE,
    ADD INDEX `fk_measurement_modified_by_idx` (`modified_by` ASC) VISIBLE;
;
ALTER TABLE `pipe_lake`.`measurement`
    ADD CONSTRAINT `fk_measurement_created_by`
        FOREIGN KEY (`created_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    ADD CONSTRAINT `fk_measurement_modified_by`
        FOREIGN KEY (`modified_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

update measurement set created_by=reporter_id, modified_by=reporter_id;
update measurement set created=now(), last_updated=now();

ALTER TABLE `pipe_lake`.`measurement`
    DROP FOREIGN KEY `fk_measurement_created_by`,
    DROP FOREIGN KEY `fk_measurement_modified_by`,
    DROP FOREIGN KEY `fk_measurement_reporter`;
ALTER TABLE `pipe_lake`.`measurement`
    DROP COLUMN `reporter_id`,
    CHANGE COLUMN `created` `created` TIMESTAMP NOT NULL ,
    CHANGE COLUMN `last_updated` `last_updated` TIMESTAMP NOT NULL ,
    CHANGE COLUMN `created_by` `created_by` INT NOT NULL ,
    CHANGE COLUMN `modified_by` `modified_by` INT NOT NULL ,
    DROP INDEX `fk_measurement_reporter_idx` ;
;
ALTER TABLE `pipe_lake`.`measurement`
    ADD CONSTRAINT `fk_measurement_created_by`
        FOREIGN KEY (`created_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`),
    ADD CONSTRAINT `fk_measurement_modified_by`
        FOREIGN KEY (`modified_by`)
            REFERENCES `pipe_lake`.`reporter` (`id`);
