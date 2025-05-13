(ns {{name}}.handlers.home.model
  (:require
   [{{name}}.models.crud :refer [db Query Update]]))

(defn get-user
  [username]
  (Query db ["SELECT * FROM users WHERE username=?" username]))

(defn get-users
  []
  (Query db ["SELECT * FROM users"]))

(defn update-password
  [username password]
  (let [where-clause ["username = ?" username]
        result (first (Update db :users {:password password} where-clause))]
    (Integer. result)))

(comment
  (get-user "sistema@gmail.com")
  (get-users))
