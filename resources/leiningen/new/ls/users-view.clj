(ns {{name}}.handlers.users.view
  (:require
   [{{name}}.models.grid :refer [build-dashboard]]))

(defn users-view
  [title rows]
  (let [table-id "users_table"
        labels ["lastname"
                "name"
                "username"
                "DOB"
                "Cell Phone"
                "Level"
                "status"]
        db-fields [:lastname
                   :firstname
                   :username
                   :dob_formatted
                   :cell
                   :level_formatted
                   :active_formatted]
        fields (apply array-map (interleave db-fields labels))]
    (build-dashboard title rows table-id fields)))

(comment
  (users-view "Users" nil))
