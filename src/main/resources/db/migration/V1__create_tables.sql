-- Initial schema for MOZU (Auto-generated from staging DB)
-- Flyway Version 1 : Base Tables Creation

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------
-- Table `flyway_schema_history`
-- (필요 없으며 Flyway가 자동으로 생성하므로 제외 권장)
-- -----------------------------------------------------


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
  KEY `FKsbo1e0mmt0f3tldkemd4o6u1x` (`organ_id`),
  CONSTRAINT `FKsbo1e0mmt0f3tldkemd4o6u1x`
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
  UNIQUE KEY `UK1ojd81lqh48i11h0e2oedgpvr` (`lesson_num`),
  KEY `FKlgrh5u3cf4wvpnf91sgl8bj3g` (`organ_id`),
  CONSTRAINT `FKlgrh5u3cf4wvpnf91sgl8bj3g`
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
  KEY `FKosq83c049ouulml943f9wd3ba` (`organ_id`),
  CONSTRAINT `FKosq83c049ouulml943f9wd3ba`
    FOREIGN KEY (`organ_id`) REFERENCES `tbl_organ` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1074 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


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
  KEY `FKka6lx97amdkp3xk0hrx8olk9a` (`lesson_id`),
  CONSTRAINT `FKka6lx97amdkp3xk0hrx8olk9a`
    FOREIGN KEY (`lesson_id`) REFERENCES `tbl_lesson` (`id`),
  CONSTRAINT `FKqj8sgk0nrsg6e3rjd39ljb4e6`
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
  KEY `FKe3dw95swkky5p7ver17i9q8kt` (`lesson_id`),
  CONSTRAINT `FKe3dw95swkky5p7ver17i9q8kt`
    FOREIGN KEY (`lesson_id`) REFERENCES `tbl_lesson` (`id`),
  CONSTRAINT `FKoam6o917bvwpw3yw1lkb29sll`
    FOREIGN KEY (`article_id`) REFERENCES `tbl_article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_team`
-- -----------------------------------------------------
CREATE TABLE `tbl_team` (
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
  UNIQUE KEY `uq_team_name_lesson_id` (`team_name`,`lesson_id`),
  KEY `FKn5ywhkdehga1gvq7cpx2emdcs` (`lesson_id`),
  CONSTRAINT `FKn5ywhkdehga1gvq7cpx2emdcs`
    FOREIGN KEY (`lesson_id`) REFERENCES `tbl_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `tbl_order_item`
-- -----------------------------------------------------
CREATE TABLE `tbl_order_item` (
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
  KEY `FKcteiahcxwx60yr0lj8sx6p18` (`item_id`),
  KEY `FK59bli978wogs13m6rvwupm38s` (`team_id`),
  CONSTRAINT `FK59bli978wogs13m6rvwupm38s`
    FOREIGN KEY (`team_id`) REFERENCES `tbl_team` (`id`),
  CONSTRAINT `FKcteiahcxwx60yr0lj8sx6p18`
    FOREIGN KEY (`item_id`) REFERENCES `tbl_item` (`id`)
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
  KEY `FK3brsuekqyrly9yfjv0ye4ro9u` (`item_id`),
  KEY `FKtdg4wpvosckjgnlq04px27dhh` (`team_id`),
  CONSTRAINT `FK3brsuekqyrly9yfjv0ye4ro9u`
    FOREIGN KEY (`item_id`) REFERENCES `tbl_item` (`id`),
  CONSTRAINT `FKtdg4wpvosckjgnlq04px27dhh`
    FOREIGN KEY (`team_id`) REFERENCES `tbl_team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
