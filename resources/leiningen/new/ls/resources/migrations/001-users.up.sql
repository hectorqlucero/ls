CREATE TABLE users (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `lastname` varchar(50) DEFAULT NULL,
  `firstname` varchar(50) DEFAULT NULL,
  `username` varchar(200) DEFAULT NULL,
  `password` text DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `cell` varchar(50) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `fax` varchar(50) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `level` char(1) DEFAULT NULL COMMENT 'A=Administrador,U=Usuario,S=Sistema',
  `active` char(1) DEFAULT NULL COMMENT 'T=Activo,F=Inactivo',
  `imagen` text DEFAULT NULL,
  `last_login` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
