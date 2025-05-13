(ns {{name}}.core
  (:require
   [compojure.core :refer [defroutes routes]]
   [compojure.route :as route]
   [noir.response :refer [redirect]]
   [noir.session :as session]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [{{name}}.migrations :refer [config]]
   [{{name}}.models.crud :refer [KEY]]
   [{{name}}.routes.proutes :refer [proutes]]
   [{{name}}.routes.routes :refer [open-routes]])
  (:gen-class))

;; Middleware for handling login
(defn wrap-login [handler]
  (fn [request]
    (if (nil? (session/get :user_id))
      (redirect "home/login")
      (try
        (handler request)
        (catch Exception _
          {:status 400 :body "Unable to process your request!"})))))

;; Middleware for handling exceptions
(defn wrap-exception-handling [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception _
        {:status 400 :body "Invalid data"}))))

;; Middleware to wrap public and private routes
(defn wrap-routes [route-fn]
  (fn [routes]
    (route-fn routes)))

;; Define the application routes
(defroutes app-routes
  (route/resources "/")
  (route/files (:path config) {:root (:uploads config)})
  (wrap-routes open-routes)
  (wrap-login (wrap-routes proutes))
  (route/not-found "Not Found"))

;; Application configuration
(def app
  (-> (routes #'app-routes)
      (wrap-exception-handling)
      (wrap-multipart-params)
      (wrap-session)
      (session/wrap-noir-session*)
      (wrap-defaults (-> site-defaults
                         (assoc-in [:security :anti-forgery] true)
                         (assoc-in [:session :store] (cookie-store {:key KEY}))
                         (assoc-in [:session :cookie-attrs] {:max-age 28800})
                         (assoc-in [:session :cookie-name] "LS")))))

;; Main entry point
(defn -main []
  (jetty/run-jetty app {:port (:port config)}))

(comment
  (:port config))
