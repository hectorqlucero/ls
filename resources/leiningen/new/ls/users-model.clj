(ns {{name}}.handlers.users.model
  (:require
   [{{name}}.models.crud :refer [db Query]]))

(defn get-users
  []
  (Query db "select * from users_view"))

(comment
  (get-users))
