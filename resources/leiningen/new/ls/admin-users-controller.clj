(ns {{name}}.handlers.admin.users.controller
  (:require
   [{{name}}.handlers.admin.users.model :refer [get-user get-users]]
   [{{name}}.handlers.admin.users.view :refer [users-add-view users-edit-view
                                                users-modal-script users-view]]
   [{{name}}.layout :refer [application error-404]]
   [{{name}}.models.crud :refer [build-form-delete build-form-save]]
   [{{name}}.models.util :refer [get-session-id user-level]]))

(defn users
  [request]
  (let [title "Users"
        ok (get-session-id request)
        js nil
        rows (get-users)
        content (users-view title rows)]
    (if (= (user-level request) "S")
      (application request title ok js content)
      (application request title ok nil "Not authorized to access this item! (level 'S')"))))

(defn users-edit
  [request id]
  (let [title "Edit User"
        ok (get-session-id request)
        js (users-modal-script)
        row (get-user id)
        rows (get-users)
        content (users-edit-view title row rows)]
    (application request title ok js content)))

(defn users-add
  [request]
  (let [title "New User"
        ok (get-session-id request)
        js (users-modal-script)
        row nil
        rows (get-users)
        content (users-add-view title row rows)]
    (application request title ok js content)))

(defn users-save
  [{:keys [params]}]
  (let [table "users"
        result (build-form-save params table)]
    (if result
      (error-404 "Processed successfully!" "/admin/users")
      (error-404 "Unable to process record!" "/admin/users"))))

(defn users-delete
  [_ id]
  (let [table "users"
        result (build-form-delete table id)]
    (if (= result true)
      (error-404 "Processed successfuly!" "/admin/users")
      (error-404 "Unable to process record!" "/admin/users"))))
