DROP TABLE IF EXISTS `test`.`user`;
CREATE TABLE `test`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `card_id` VARCHAR(45) NULL,
  `first_name` VARCHAR(45) NULL,
  `second_name` VARCHAR(45) NULL,
  `type` VARCHAR(45) NULL,
  `status` INT NULL,
  `level` INT NULL,
  `date_of_birth` VARCHAR(45) NULL,
  `age` INT NULL,
  PRIMARY KEY (`id`));
