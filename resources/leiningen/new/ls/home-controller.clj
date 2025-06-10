(ns {{name}}.handlers.home.controller
  (:require
   [buddy.hashers :as hashers]
   [clojure.string :as st]
   [{{name}}.handlers.home.model :refer [get-user get-users update-password]]
   [{{name}}.handlers.home.view :refer [change-password-view home-view
                                         main-view]]
   [{{name}}.layout :refer [application error-404]]
   [{{name}}.models.util :refer [get-session-id]]
   [ring.util.response :refer [redirect]]))

(defn main
  [request]
  (let [title "Home"
        ok (get-session-id request)
        js nil
        content (if (> ok 0)
                  (home-view)
                  [:h2.text-info.text-center "Welcome to the Home Page"])]
    (application request title ok js content)))

(defn login
  [request]
  (let [title "Login"
        ok (get-session-id request)
        js nil
        content (main-view title)]
    (application request title ok js content)))

(defn login-user
  [{:keys [params session]}]
  (let [username (:username params)
        password (:password params)
        row (first (get-user username))
        active (:active row)]
    (if (= active "T")
      (if (hashers/check password (:password row))
        (-> (redirect "/")
            (assoc :session (assoc session :user_id (:id row))))
        (error-404 "Incorrect Username and or Password!" "/"))
      (error-404 "User is not active!" "/"))))

(defn change-password
  [request]
  (let [title "Change Password"
        ok (get-session-id request)
        js nil
        content (change-password-view title)]
    (application request title ok js content)))

(defn process-password
  [{:keys [params]}]
  (let [username (:email params)
        password (:password params)
        row (first (get-user username))]
    (if (and row (= (:active row) "T"))
      (if (and password (not (st/blank? password)))
        (if (> (update-password username (hashers/derive password)) 0)
          (error-404 "Your password was changed successfully!" "/home/login")
          (error-404 "Unable to change password!" "/home/login"))
        (error-404 "Password cannot be blank!" "/home/login"))
      (error-404 "Unable to change password!" "/home/login"))))

(defn logoff-user
  [_]
  (-> (redirect "/")
      (assoc :session {})))

(comment
  (:username (first (get-users))))
