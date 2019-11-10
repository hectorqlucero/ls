(ns {{name}}.models.cdb
  (:require [{{name}}.models.crud :refer :all]
            [noir.util.crypt :as crypt]))


;; Start users table
(def users-sql
  "CREATE TABLE users (
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  lastname varchar(45) DEFAULT NULL,
  firstname varchar(45) DEFAULT NULL,
  username varchar(45) DEFAULT NULL,
  password TEXT DEFAULT NULL,
  dob varchar(45) DEFAULT NULL,
  cell varchar(45) DEFAULT NULL,
  phone varchar(45) DEFAULT NULL,fax varchar(45) DEFAULT NULL,
  email varchar(100) DEFAULT NULL,
  level char(1) DEFAULT NULL COMMENT 'A=Administrador,U=Usuario,S=Sistema',
  active char(1) DEFAULT NULL COMMENT 'T=Active,F=Not active'
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8")

(def user-rows
  [{:lastname  "Lucero"
    :firstname "Hector"
    :username  "hectorqlucero@gmail.com"
    :password  (crypt/encrypt "elmo1200")
    :dob       "1957-02-07"
    :email     "hectorqlucero@gmail.com"
    :level     "S"
    :active    "T"}
   {:lastname  "Pescador"
    :firstname "Marco"
    :username  "marcopescador@hotmail.com"
    :dob       "1968-10-04"
    :email     "marcopescador@hotmail.com"
    :password  (crypt/encrypt "10201117")
    :level     "S"
    :active    "T"}])
;; End users table

(defn create-database []
  "Create database tables and default admin users
   Note: First create the database on MySQL with any client"
  (Query! db users-sql))

(defn reset-database []
  "Removes existing tables and re-creates them"
  (Query! db "DROP table IF EXISTS users")
  (Query! db users-sql))

(defn migrate []
  "Migrate by the seat of my pants"
  (Query! db "DROP table IF EXISTS users")
  (Query! db users-sql)
  (Query! db "LOCK TABLES users WRITE;")
  (Insert-multi db :users user-rows)
  (Query! db "UNLOCK TABLES;"))
