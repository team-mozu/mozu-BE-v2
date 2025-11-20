ALTER TABLE tbl_lesson_team MODIFY COLUMN team_name VARCHAR(100) NOT NULL;
ALTER TABLE tbl_lesson_team ADD CONSTRAINT uq_team_name_lesson_id UNIQUE (team_name, lesson_id);
