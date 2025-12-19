ALTER TABLE tbl_team MODIFY COLUMN lesson_num CHAR(4) NOT NULL;

ALTER TABLE tbl_team DROP INDEX uq_team_name_lesson_id;

ALTER TABLE tbl_team
    ADD CONSTRAINT uq_team_name_lesson_num_lesson_id
        UNIQUE (team_name, lesson_num, lesson_id);
