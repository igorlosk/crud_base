INSERT INTO users(age, email, surname, username)
SELECT
    (random() * 80 + 18)::int AS age,  -- возраст от 18 до 98 лет
    'user' || gs || '@' ||
    CASE (random() * 3)::int
        WHEN 0 THEN 'gmail.com'
        WHEN 1 THEN 'yandex.ru'
        ELSE 'mail.ru'
END AS email,
    'Surname' || (random() * 100000)::int AS surname,
    'user_' || gs AS username
FROM generate_series(1, 10000000) gs;

ANALYZE;

SELECT count(*) FROM users;