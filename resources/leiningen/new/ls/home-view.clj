(ns {{name}}.handlers.home.view
  (:require
   [{{name}}.models.form :refer [login-form password-form]]))

(defn home-view
  []
  (list
   [:div.container.mt-5
    [:div.text-center
     [:h1.text-info "My Site Title"]
     [:p.text-muted "Serving the community since 2003"]
     [:p "123 New Lane, Industrial City, CA 90210"]
     [:p "Phone: (686) 123-4567 | Email: contactlhc@lhc.com"]]]))

(defn main-view
  "This creates the login form and we are passing the title from the controller"
  [title]
  (let [href "/home/login"]
    (login-form title href)))

(defn change-password-view
  [title]
  (password-form title))
