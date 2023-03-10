CREATE TABLE IF NOT EXISTS user (
    id TEXT PRIMARY KEY,
    name TEXT,
    password TEXT
);

INSERT INTO user (id, name, password) VALUES
    ('1', 'Alice', 'password1'),
    ('2', 'Bob', 'password2'),
    ('3', 'Charlie', 'password3'),
    ('4', 'David', 'password4');
