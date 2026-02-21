CREATE TABLE IF NOT EXISTS task_category_links (
   id SERIAL PRIMARY KEY,
   task_id INT NOT NULL REFERENCES tasks(id),
   category_id INT NOT NULL REFERENCES categories(id),
   UNIQUE (task_id, category_id)
);