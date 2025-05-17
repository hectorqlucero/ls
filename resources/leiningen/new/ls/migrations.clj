(ns {{name}}.migrations
  (:require
   [{{name}}.models.crud :refer [db]]
   [ragtime.jdbc :as jdbc]
   [ragtime.repl :as repl]))

(defn load-config []
  {:datastore (jdbc/sql-database db)
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))

(comment
  (load-config))
