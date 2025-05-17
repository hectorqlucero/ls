(ns {{name}}.handlers.reports.users.controller
  (:require
   [{{name}}.handlers.reports.users.model :refer [get-users]]
   [{{name}}.handlers.reports.users.view :refer [users-view]]
   [{{name}}.layout :refer [application]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn users [params]
  (let [title "Users Report"
        ok (get-session-id params)
        js nil
        rows (get-users)
        content (users-view title rows)]
    (application params title ok js content)))

