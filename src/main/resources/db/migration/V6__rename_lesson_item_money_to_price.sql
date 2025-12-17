ALTER TABLE tbl_lesson_item
    CHANGE COLUMN current_money end_price BIGINT NOT NULL;

ALTER TABLE tbl_lesson_item
    CHANGE COLUMN round1money round1price BIGINT NOT NULL;

ALTER TABLE tbl_lesson_item
    CHANGE COLUMN round2money round2price BIGINT NOT NULL;

ALTER TABLE tbl_lesson_item
    CHANGE COLUMN round3money round3price BIGINT NOT NULL;

ALTER TABLE tbl_lesson_item
    CHANGE COLUMN round4money round4price BIGINT NULL;

ALTER TABLE tbl_lesson_item
    CHANGE COLUMN round5money round5price BIGINT NULL;
