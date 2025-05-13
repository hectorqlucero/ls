(ns {{name}}.handlers.reports.users.view
  (:require [{{name}}.models.grid :refer [build-dashboard]]))

(defn users-view
  [title rows]
  (let [table-id "users_table"
        labels ["LAST NAME" "FIRST NAME" "USERNAME" "DOB" "CELL PHONE" "LEVEL" "STATUS"]
        db-fields [:lastname :firstname :username :dob :cell :level_formatted :active_formatted]
        fields (apply array-map (interleave db-fields labels))]
    (build-dashboard title rows table-id fields)))
