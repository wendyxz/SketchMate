
create table if not exists users
(
    id       INTEGER primary key AUTOINCREMENT,
    username TEXT unique not null,
    password TEXT not null
);

INSERT INTO users (username, password) VALUES
    ('Alice', 'password1'),
    ('Bob', 'password2'),
    ('Charlie', 'password3'),
    ('David', 'password4');
