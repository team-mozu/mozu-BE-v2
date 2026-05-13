-- 차수별 팀 종료 시점 자산 스냅샷. 모니터링 결과 데이터를 admin 브라우저 zustand에서만 보관하던
-- 문제를 해결하기 위해 BE에 영속화. SSE 손실/새로고침/admin 교체에도 데이터 보존.
CREATE TABLE `tbl_round_snapshot` (
  `id`              BINARY(16)   NOT NULL,
  `lesson_id`       BINARY(16)   NOT NULL,
  `team_id`         BINARY(16)   NOT NULL,
  `inv_round`       INT          NOT NULL,
  `total_money`     BIGINT       NOT NULL,
  `cash_money`      BIGINT       NOT NULL,
  `valuation_money` BIGINT       NOT NULL,
  `profit_num`      DOUBLE       NOT NULL,
  `ended_at`        DATETIME(6)  NOT NULL,
  `created_at`      DATETIME(6)  NOT NULL,
  `updated_at`      DATETIME(6)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_round_snapshot_lesson_team_round` (`lesson_id`, `team_id`, `inv_round`),
  KEY `ix_round_snapshot_lesson_round` (`lesson_id`, `inv_round`),
  KEY `ix_round_snapshot_team` (`team_id`),
  CONSTRAINT `fk_round_snapshot_lesson`
    FOREIGN KEY (`lesson_id`) REFERENCES `tbl_lesson` (`id`),
  CONSTRAINT `fk_round_snapshot_team`
    FOREIGN KEY (`team_id`) REFERENCES `tbl_team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
