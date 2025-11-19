ALTER TABLE tbl_lesson_team MODIFY COLUMN team_name VARCHAR(100) NOT NULL;
ALTER TABLE tbl_lesson_team ADD CONSTRAINT uq_team_name UNIQUE (team_name);
