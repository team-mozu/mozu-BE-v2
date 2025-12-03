SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------
-- Table `tbl_organ`
-- -----------------------------------------------------
CREATE TABLE `tbl_organ` (
  `id` binary(16) NOT NULL,
  `organ_code` varchar(30) NOT NULL,
  `organ_name` varchar(100) NOT NULL,
  `password` varchar(300) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsk60nqhne4kcp6iujuixnsawp` (`organ_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_article`
-- -----------------------------------------------------
CREATE TABLE `tbl_article` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `article_description` varchar(10000) NOT NULL,
  `article_image` text,
  `article_name` varchar(300) NOT NULL,
  `is_deleted` bit(1) NOT NULL,
  `organ_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_article_organ` (`organ_id`),
  CONSTRAINT `FK_article_organ`
    FOREIGN KEY (`organ_id`) REFERENCES `tbl_organ` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_lesson`
-- -----------------------------------------------------
CREATE TABLE `tbl_lesson` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `base_money` bigint NOT NULL,
  `cur_inv_round` int NOT NULL,
  `is_deleted` bit(1) NOT NULL,
  `is_in_progress` bit(1) NOT NULL,
  `is_starred` bit(1) NOT NULL,
  `lesson_name` varchar(100) NOT NULL,
  `lesson_num` varchar(10) DEFAULT NULL,
  `max_inv_round` int NOT NULL,
  `organ_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lesson_num` (`lesson_num`),
  KEY `FK_lesson_organ` (`organ_id`),
  CONSTRAINT `FK_lesson_organ`
    FOREIGN KEY (`organ_id`) REFERENCES `tbl_organ` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_item`
-- -----------------------------------------------------
CREATE TABLE `tbl_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `capital` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `debt` bigint NOT NULL,
  `is_deleted` bit(1) NOT NULL,
  `item_info` text NOT NULL,
  `item_logo` varchar(255) DEFAULT NULL,
  `item_name` varchar(100) NOT NULL,
  `money` bigint NOT NULL,
  `net_profit` bigint NOT NULL,
  `profit` bigint NOT NULL,
  `profit_benefit` bigint NOT NULL,
  `profit_og` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `organ_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_item_organ` (`organ_id`),
  CONSTRAINT `FK_item_organ`
    FOREIGN KEY (`organ_id`) REFERENCES `tbl_organ` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_lesson_item`
-- -----------------------------------------------------
CREATE TABLE `tbl_lesson_item` (
  `current_money` bigint NOT NULL,
  `round1money` bigint NOT NULL,
  `round2money` bigint NOT NULL,
  `round3money` bigint NOT NULL,
  `round4money` bigint DEFAULT NULL,
  `round5money` bigint DEFAULT NULL,
  `item_id` int NOT NULL,
  `lesson_id` binary(16) NOT NULL,
  PRIMARY KEY (`item_id`,`lesson_id`),
  KEY `FK_lessonitem_lesson` (`lesson_id`),
  CONSTRAINT `FK_lessonitem_lesson`
    FOREIGN KEY (`lesson_id`) REFERENCES `tbl_lesson` (`id`),
  CONSTRAINT `FK_lessonitem_item`
    FOREIGN KEY (`item_id`) REFERENCES `tbl_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_lesson_article`
-- -----------------------------------------------------
CREATE TABLE `tbl_lesson_article` (
  `investment_round` int NOT NULL,
  `article_id` binary(16) NOT NULL,
  `lesson_id` binary(16) NOT NULL,
  PRIMARY KEY (`article_id`,`lesson_id`),
  KEY `FK_lessonarticle_lesson` (`lesson_id`),
  CONSTRAINT `FK_lessonarticle_lesson`
    FOREIGN KEY (`lesson_id`) REFERENCES `tbl_lesson` (`id`),
  CONSTRAINT `FK_lessonarticle_article`
    FOREIGN KEY (`article_id`) REFERENCES `tbl_article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_lesson_team` (rename 이전)
-- -----------------------------------------------------
CREATE TABLE `tbl_lesson_team` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `cash_money` bigint NOT NULL,
  `is_investment_in_progress` bit(1) NOT NULL,
  `lesson_num` varchar(40) NOT NULL,
  `school_name` varchar(100) NOT NULL,
  `team_name` varchar(100) NOT NULL,
  `total_money` bigint NOT NULL,
  `valuation_money` bigint NOT NULL,
  `lesson_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lessonteam_lesson` (`lesson_id`),
  CONSTRAINT `FK_lessonteam_lesson`
    FOREIGN KEY (`lesson_id`) REFERENCES `tbl_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_team_order` (rename 이전)
-- -----------------------------------------------------
CREATE TABLE `tbl_team_order` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `inv_count` int NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `item_price` bigint NOT NULL,
  `order_count` int NOT NULL,
  `order_type` enum('BUY','SELL') NOT NULL,
  `total_money` bigint NOT NULL,
  `item_id` int NOT NULL,
  `team_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_teamorder_item` (`item_id`),
  KEY `FK_teamorder_team` (`team_id`),
  CONSTRAINT `FK_teamorder_item`
    FOREIGN KEY (`item_id`) REFERENCES `tbl_item` (`id`),
  CONSTRAINT `FK_teamorder_team`
    FOREIGN KEY (`team_id`) REFERENCES `tbl_lesson_team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_stock`
-- -----------------------------------------------------
CREATE TABLE `tbl_stock` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `avg_purchase_price` bigint NOT NULL,
  `buy_money` bigint NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `profit_num` double NOT NULL,
  `quantity` int NOT NULL,
  `val_profit` bigint NOT NULL,
  `item_id` int NOT NULL,
  `team_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_stock_item` (`item_id`),
  KEY `FK_stock_team` (`team_id`),
  CONSTRAINT `FK_stock_item`
    FOREIGN KEY (`item_id`) REFERENCES `tbl_item` (`id`),
  CONSTRAINT `FK_stock_team`
    FOREIGN KEY (`team_id`) REFERENCES `tbl_lesson_team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


SET FOREIGN_KEY_CHECKS = 1;
