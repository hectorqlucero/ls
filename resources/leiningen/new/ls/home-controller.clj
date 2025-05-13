(ns {{name}}.handlers.home.controller
  (:require
   [noir.response :refer [redirect]]
   [noir.session :as session]
   [noir.util.crypt :as crypt]
   [{{name}}.handlers.home.model :refer [get-user get-users update-password]]
   [{{name}}.handlers.home.view :refer [change-password-view main-view home-view]]
   [{{name}}.layout :refer [application error-404]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn main
  [_]
  (let [title "Home"
        ok (get-session-id)
        js nil
        content (if (> (get-session-id) 0)
                  (home-view)
                  [:h2.text-primary "Welcome to the Home Page"])]
    (application title ok js content)))

(defn login
  [_]
  (let [title "Login"
        ok (get-session-id)
        js nil
        content (main-view title)]
    (application title ok js content)))

(defn login-user
  [{params :params}]
  (let [username (:username params)
        password (:password params)
        row (first (get-user username))
        active (:active row)]
    (if (= active "T")
      (if (crypt/compare password (:password row))
        (do
          (session/put! :user_id (:id row))
          (redirect "/"))
        (error-404 "Incorrect Username and or Password!" "/"))
      (error-404 "User is not active!" "/"))))

(defn change-password
  [_]
  (let [title "Change Password"
        ok (get-session-id)
        js nil
        content (change-password-view title)]
    (application title ok js content)))

(defn process-password
  [{params :params}]
  (let [username (:email params)
        password (crypt/encrypt (:password params))
        row (first (get-user username))
        active (:active row)]
    (if (= active "T")
      (if (> (update-password username password) 0)
        (error-404 "Your password was changed successfuly!" "/home/login")
        (error-404 "Unable to change password!" "/home/login"))
      (error-404 "Unable to change password!" "/home/login"))))

(defn logoff-user
  [_]
  (session/clear!)
  (error-404 "Logoff successfully" "/"))

(comment
  (:username (first (get-users))))
