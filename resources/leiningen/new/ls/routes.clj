(ns {{name}}.routes
  (:require [compojure.core :refer [defroutes GET POST]]
            [cheshire.core :refer [generate-string]]
            [{{name}}.table_ref :as table_ref]
            [{{name}}.routes.home :as home]
            [{{name}}.routes.registrar :as registrar]))

(defroutes open-routes
  ;; Start table_ref
  (GET "/table_ref/get_users" [] (generate-string (table_ref/get-users)))
  (GET "/table_ref/validate_email/:email" [email] (generate-string (table_ref/get-users-email email)))
  (GET "/table_ref/months" [] (generate-string (table_ref/months)))
  (GET "/table_ref/years/:pyears/:nyears" [pyears nyears] (generate-string (table_ref/years pyears nyears)))
  ;; End table_ref
  ;; Start home
  (GET "/" request [] (home/main request))
  (GET "/login" request [] (home/login request))
  (POST "/login" [username password] (home/login! username password))
  (GET "/logoff" [] (home/logoff))
  ;; End home
  ;; Start registrar
  (GET "/registrar" request [] (registrar/registrar request))
  (POST "/registrar" request [] (registrar/registrar! request))
  (GET "/rpaswd" request [] (registrar/reset-password request))
  (POST "/rpaswd" request [] (registrar/reset-password! request))
  (GET "/reset_password/:token" [token] (registrar/reset-jwt token))
  (POST "/reset_password" request [] (registrar/reset-jwt! request))
  ;; End registrar
  )
