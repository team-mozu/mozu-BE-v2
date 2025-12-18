ALTER TABLE tbl_lesson
    MODIFY COLUMN lesson_num CHAR(4);

ALTER TABLE tbl_organ
    MODIFY COLUMN password CHAR(60) NOT NULL;

ALTER TABLE tbl_article
    MODIFY COLUMN article_image VARCHAR(255);

ALTER TABLE tbl_item
    MODIFY COLUMN item_info VARCHAR(10000) NOT NULL;
