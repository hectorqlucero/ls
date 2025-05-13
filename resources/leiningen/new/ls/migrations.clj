(ns {{name}}.migrations
  (:require
   [clojure.java.io :as io]
   [ragtime.jdbc :as jdbc]
   [ragtime.repl :as repl]))

(defn get-config
  []
  (try
    (binding [*read-eval* false]
      (read-string (str (slurp (io/resource "private/config.clj")))))
    (catch Exception e (.getMessage e))))

(def config (get-config))

(defn load-config []
  {:datastore (jdbc/sql-database (:database-url config))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))

(comment
  (:database-url config)
  (load-config))
