(ns {{name}}.routes.home
  (:require [cheshire.core :refer [generate-string]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [{{name}}.models.crud :refer [db
                                            Query]]
            [{{name}}.models.util :refer [get-session-id]]
            [{{name}}.views.layout :refer :all]
            [{{name}}.views.home :refer [login-view
                                           login-scripts]]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [noir.response :refer [redirect]]))

;; Start Main
(def main-sql
  "SELECT
   username
   FROM users
   WHERE id = ?")

(defn get-main-title []
  (let [id (get-session-id)
        title (if (= id 1)
                (str "<strong>Usuario:</strong> " (:username (first (Query db [main-sql id]))))
                "Hacer clic en el menu \"Entrar\" para accessar el sitio")]
    title))

(defn main [request]
  (let [title (get-main-title)
        ok (get-session-id)
        content [:div [:span {:style "margin-left:20px;"} (get-main-title)]] ]
    (application "Mi Inventario" ok nil content)))
;; End Main

;; Start Login
(defn login [_]
  (let [title "Accesar al Sitio"
        ok (get-session-id)
        content (login-view (anti-forgery-field))
        scripts (login-scripts)]
    (if-not (= (get-session-id) 0)
      (redirect "/")
      (application title ok scripts content))))

(defn login! [username password]
  (let [row (first (Query db ["SELECT * FROM users WHERE username = ?" username]))
        active (:active row)]
    (if (= active "T")
      (do
        (if (crypt/compare password (:password row))
          (do
            (session/put! :user_id (:id row))
            (generate-string {:url "/"}))
          (generate-string {:error "Hay problemas para accesar el sitio!"})))
      (generate-string {:error "El usuario esta inactivo!"}))))
;; End login

(defn logoff []
  (session/clear!)
  (redirect "/"))
