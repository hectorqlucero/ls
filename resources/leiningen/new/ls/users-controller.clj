(ns {{name}}.handlers.users.controller
  (:require
   [{{name}}.handlers.users.model :refer [get-users]]
   [{{name}}.handlers.users.view :refer [users-view]]
   [{{name}}.layout :refer [application]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn users
  [request]
  (let [title "Dashboard"
        ok (get-session-id request)
        js nil
        rows (get-users)
        content (users-view title rows)]
    (application request title ok js content)))
