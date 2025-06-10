(ns {{name}}.routes.proutes
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [{{name}}.handlers.admin.users.controller :as users-controller]
   [{{name}}.handlers.reports.users.controller :as users-report]
   [{{name}}.handlers.users.controller :as users-dashboard]))

(defroutes proutes
  (GET "/reports/users" params [] (users-report/users params))
  (GET "/admin/users" params [] (users-controller/users params))
  (GET "/admin/users/add-form" params [] (users-controller/users-add-form params))
  (GET "/admin/users/edit-form/:id" [id :as request] (users-controller/users-edit-form request id))
  (POST "/admin/users/save" params [] (users-controller/users-save params))
  (GET "/admin/users/delete/:id" [id :as request] (users-controller/users-delete request id))
  (GET "/users" params [] (users-dashboard/users params)))
