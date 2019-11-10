(ns {{name}}.routes.registrar
  (:require [cheshire.core :refer [generate-string]]
            [{{name}}.views.layout :refer :all]
            [{{name}}.views.registrar :refer [registrar-scripts
                                              registrar-view
                                              reset-password-scripts
                                              reset-password-view
                                              reset-jwt-scripts
                                              reset-jwt-view]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [noir.util.crypt :as crypt]
            [{{name}}.models.crud :refer [db 
                                          build-postvars
                                          Query 
                                          Save 
                                          Update]]
            [{{name}}.models.email :refer [host 
                                           send-email]]
            [{{name}}.models.util :refer [get-session-id
                                          get-reset-url
                                          check-token
                                          create-token]]))

;; Start registrar
(defn registrar [request]
  (let [title "Registro de usuarios"
        token (anti-forgery-field)
        ok (get-session-id)
        js (registrar-scripts)
        error-text "Existe una session, no se puede crear un nuevo usuario"
        return-url "/"
        content (registrar-view title token)]
    (if (= ok 1)
      (error-404 error-text return-url)
      (application title ok js content))))

(defn registrar! [{params :params}]
  "Postear los datos de registro de un nuevo cliente el la tabla usuarios"
  (let [email (clojure.string/lower-case (or (:email params) "0"))
        password (:password params)
        params (assoc params 
                      :level "u"
                      :active "t"
                      :password (crypt/encrypt password)
                      :email email
                      :username email)
        postvars (assoc (build-postvars "users" params)
                        :email email
                        :username email)
        result (Save db :users postvars ["username = ?" email])]
    (if (seq result)
      (generate-string {:url "/login"})
      (generate-string {:error "No se pudo registrar el usuario"}))))
;; End registrar

;; Start reset-password
(defn reset-password [request]
  (let [title "Resetear Contraseña"
        token (anti-forgery-field)
        ok (get-session-id)
        error-text "Existe una session, no se puede cambiar la contraseña"
        return-url "/"
        js (reset-password-scripts)
        content (reset-password-view title token)]
    (if (= ok 1)
      (error-404 error-text return-url)
      (application title ok js content))))

(defn get-username-row [username]
  (first (Query db ["SELECT * FROM users WHERE username = ?" username])))

(defn email-body [row url]
  "Crear el cuerpo del email"
  (let [nombre       (str (:firstname row) " " (:lastname row))
        email        (:email row)
        subject      "Resetear tu contraseña"
        content      (str "<strong>Hola</strong> " nombre ",</br></br>"
                          "Para resetear tu contraseña <strong>" "<a href='" url "'>Clic Aqui</a>" "</strong>.</br></br>"
                          "Alternativamente, puedes copiar y pegar el siguiente link en la barra de tu browser:</br></br>"
                          url "</br></br>"
                          "Este link solo sera bueno por 10 minutos.</br></br>"
                          "Si no solicito resetear su contraseña simplemente ignore este mensage.</br></br></br>"
                          "Sinceramente,</br></br>"
                          "La administracion")
        body         {:from    "lucero_systems@fastmail.com"
                      :to      email
                      :subject subject
                      :body    [{:type    "text/html;charset=utf-8"
                                 :content content}]}]
    body))

(defn reset-password! [request]
  (let [params     (:params request)
        username   (:email params)
        token      (create-token username)
        url        (get-reset-url request token)
        row        (get-username-row username)
        email-body (email-body row url)]
    (if (future (send-email host email-body))
      (generate-string {:url "/"})
      (generate-string {:error "No se pudo resetear su contraseña"}))))
;; End reset-password

(defn reset-jwt [token]
  (let [title "Resetear Contrasseña"
        csrf (anti-forgery-field)
        ok (get-session-id)
        username (check-token token)
        error-text "Su token es incorrecto o ya expiro!"
        return-url "/"
        js (reset-jwt-scripts)
        content (reset-jwt-view title csrf username)]
    (if-not (nil? username)
      (application title ok js content)
      (error-404 error-text return-url))))

(defn reset-jwt! [{params :params}]
  (let [username (or (:username params) "0")
        postvars  {:username username
                   :password  (crypt/encrypt (:password params))}
        result (Update db :users postvars ["username = ?" username])]
    (if (seq result)
      (generate-string {:url "/"})
      (generate-string {:error "No se pudo cambiar su contraseña!"}))))
;; End reset-password

