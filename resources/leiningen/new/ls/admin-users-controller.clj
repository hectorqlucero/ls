(ns {{name}}.handlers.admin.users.controller
  (:require
   [{{name}}.handlers.admin.users.model :refer [get-user get-users]]
   [{{name}}.handlers.admin.users.view :refer [users-view]]
   [{{name}}.layout :refer [application error-404]]
   [{{name}}.models.crud :refer [build-form-delete build-form-save]]
   [{{name}}.models.util :refer [get-session-id user-level]]
   [hiccup.core :refer [html]]))

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

(defn users-add-form
  [_]
  (let [title "New User"
        row nil]
    (html ({{name}}.handlers.admin.users.view/users-add-form title row))))

(defn users-edit-form
  [_ id]
  (let [title "Edit User"
        row (get-user id)]
    (html ({{name}}.handlers.admin.users.view/users-edit-form title row))))

(defn users-save
  [{params :params}]
  (let [table "users"
        result (build-form-save params table)]
    (if result
      {:status 200 :headers {"Content-Type" "application/json"} :body "{\"ok\":true}"}
      {:status 500 :headers {"Content-Type" "application/json"} :body "{\"ok\":false}"})))

(defn users-delete
  [_ id]
  (let [table "users"
        result (build-form-delete table id)]
    (if result
      {:status 302 :headers {"Location" "/admin/users"}}
      (error-404 "Unable to process record!" "/admin/users"))))
