(ns {{name}}.routes.routes
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [{{name}}.handlers.home.controller :as home-controller]))

(defroutes open-routes
  (GET "/" params [] (home-controller/main params))
  (GET "/home/login" params [] (home-controller/login params))
  (POST "/home/login" params [] (home-controller/login-user params))
  (GET "/home/logoff" params [] (home-controller/logoff-user params))
  (GET "/change/password" params [] (home-controller/change-password params))
  (POST "/change/password" params [] (home-controller/process-password params)))
