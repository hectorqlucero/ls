(ns {{name}}.handlers.admin.users.model
  (:require
   [{{name}}.models.crud :refer [db Query]]))

(defn get-users
  []
  (Query db "select * from users_view"))

;; Start get-user
(def get-user-sql
  (str
   "
    SELECT *,
    DATE_FORMAT(dob,'%m/%d/%Y') as dob_formatted
    FROM users
    WHERE id = ?
    ORDER BY lastname,firstname
    "))

(defn get-user
  [id]
  (first (Query db [get-user-sql id])))
;; End get-user

(comment
  (get-user 2)
  (get-users))
