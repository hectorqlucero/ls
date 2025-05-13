(ns {{name}}.models.cdb
  (:require
   [noir.util.crypt :as crypt]
   [{{name}}.models.crud :refer [db Insert-multi Query!]]))

(def users-rows
  [{:lastname  "User"
    :firstname "Regular"
    :username  "user@example.com"
    :password  (crypt/encrypt "user")
    :dob       "1957-02-07"
    :email     "user@example.com"
    :level     "U"
    :active    "T"}
   {:lastname "User"
    :firstname "Admin"
    :username "admin@example.com"
    :password (crypt/encrypt "admin")
    :dob "1957-02-07"
    :email "admin@example.com"
    :level "A"
    :active "T"}
   {:lastname "User"
    :firstname "System"
    :username "system@example.com"
    :password (crypt/encrypt "system")
    :dob "1957-02-07"
    :email "system@example.com"
    :level "S"
    :active "T"}])


(defn populate-tables
  "Populates table with default data"
  [table rows]
  (Query! db (str "LOCK TABLES " table " WRITE;"))
  (Insert-multi db (keyword table) rows)
  (Query! db "UNLOCK TABLES;"))

(defn database []
  (populate-tables "users" users-rows))
