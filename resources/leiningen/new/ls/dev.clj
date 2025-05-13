(ns {{name}}.dev
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [{{name}}.migrations :refer [config]]
            [{{name}}.core :as core]))

(defn -main []
  (jetty/run-jetty (wrap-reload #'core/app) {:port (:port config)}))
