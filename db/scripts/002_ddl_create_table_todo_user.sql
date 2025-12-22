CREATE TABLE IF NOT EXISTS todo_user (
   id SERIAL PRIMARY KEY,
   name TEXT NOT NULL,
   login TEXT NOT NULL,
   password TEXT NOT NULL,
   UNIQUE (login, password)
);