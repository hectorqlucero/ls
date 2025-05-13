CREATE VIEW users_view AS
SELECT id,
    lastname,
    firstname,
    username,
    dob,
    cell,
    phone,
    fax,
    email,
    level,
    active,
    imagen,
    last_login,
    DATE_FORMAT(dob, '%d/%m/%Y') as dob_formatted,
    CASE
        WHEN level = 'U' THEN 'Usuario'
        WHEN level = 'A' THEN 'Administrador'
        ELSE 'Sistema'
    END level_formatted,
    CASE
        WHEN active = 'T' THEN 'Activo'
        ELSE 'Inactivo'
    END active_formatted
FROM users
ORDER BY lastname,
    firstname;