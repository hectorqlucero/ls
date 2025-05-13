(ns {{name}}.handlers.admin.users.controller
  (:require
   [{{name}}.handlers.admin.users.model :refer [get-user get-users]]
   [{{name}}.handlers.admin.users.view :refer [users-add-view users-edit-view
                                         users-modal-script users-view]]
   [{{name}}.layout :refer [application error-404]]
   [{{name}}.models.crud :refer [build-form-delete build-form-save]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn users
  [_]
  (let [title "Users"
        ok (get-session-id)
        js nil
        rows (get-users)
        content (users-view title rows)]
    (application title ok js content)))

(defn users-edit
  [id]
  (let [title "Edit User"
        ok (get-session-id)
        js (users-modal-script)
        row (get-user id)
        rows (get-users)
        content (users-edit-view title row rows)]
    (application title ok js content)))

(defn users-save
  [{params :params}]
  (let [table "users"
        result (build-form-save params table)]
    (if (= result true)
      (error-404  "Processed successfuly!" "/admin/users")
      (error-404 "Unable to process record!" "/admin/users"))))

(defn users-add
  [_]
  (let [title "New User"
        ok (get-session-id)
        js (users-modal-script)
        row nil
        rows (get-users)
        content (users-add-view title row rows)]
    (application title ok js content)))

(defn users-delete
  [id]
  (let [table "users"
        result (build-form-delete table id)]
    (if (= result true)
      (error-404 "Processed successfuly!" "/admin/users")
      (error-404 "Unable to process record!" "/admin/users"))))
