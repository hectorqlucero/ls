(ns {{name}}.models.routes
  (:require
   [clojure.string :as str]))

(defn build-grid-defroutes
  "Genera rutas especificas para el grid para una tabla"
  [table]
  (let [data (str
              "  (GET \"/admin/" table "\" params [] (" table "-controller/" table " params))\n"
              "  (GET \"/admin/" table "/add-form\" params [] (" table "-controller/" table "-add-form params))\n"
              "  (GET \"/admin/" table "/edit-form/:id\" [id :as request] (" table "-controller/" table "-edit-form request id))\n"
              "  (POST \"/admin/" table "/save\" params [] (" table "-controller/" table "-save params))\n"
              "  (GET \"/admin/" table "/delete/:id\" [id :as request] (" table "-controller/" table "-delete request id))")]
    data))

(defn build-defroutes
  "Genera rutas especificas para un dashboard para una tabla"
  [table]
  (let [data (str
              "  (GET \"/" table "\" params [] (" table "-dashboard/" table " params))")]
    data))

(defn build-defroutes-reporte
  "Genera rutas especificas para un dashboard para una tabla"
  [table]
  (let [data (str
              "  (GET \"/reports/" table "\" params [] (" table "-report/" table " params))")]
    data))

(defn build-grid-require
  "Genera archivos requeridos para un grid para una tabla"
  [table]
  (let [data (str "   [{{name}}.handlers.admin." table ".controller :as " table "-controller]")]
    data))

(defn build-reporte-require
  "Genera archivos requeridos para un reporte para un controller"
  [table]
  (let [data (str "   [{{name}}.handlers.reports." table ".controller :as " table "-report]")]
    data))

(defn build-require
  "Genera archivos requeridos para un dashboard para una tabla"
  [table]
  (let [data (str "   [{{name}}.handlers." table ".controller :as " table "-dashboard]")]
    data))

(defn insert-lines-after-search [file-path lines-to-insert search-str]
  (let [file-contents (slurp file-path)
        lines (str/split-lines file-contents)
        found-index (first (keep-indexed (fn [idx line]
                                           (when (str/includes? line search-str) idx))
                                         lines))
        insert-at (if found-index (inc found-index) (count lines))
        lines-before (take insert-at lines)
        lines-after (drop insert-at lines)
        new-lines (concat lines-before lines-to-insert lines-after)
        new-file-contents (str/join "\n" new-lines)]
    (spit file-path new-file-contents)))

(defn process-grid
  "Actualiza proutes.clj"
  [table]
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-grid-require table)]
                             "[compojure.core :refer [defroutes GET POST")
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-grid-defroutes table)]
                             "(defroutes proutes"))
(defn process-dashboard
  "Actualiza proutes.clj"
  [table]
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-require table)]
                             "[compojure.core :refer [defroutes GET POST")
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-defroutes table)]
                             "(defroutes proutes"))

(defn process-report
  "Actualiza proutes.clj"
  [table]
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-reporte-require table)]
                             "[compojure.core :refer [defroutes GET POST")
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-defroutes-reporte table)]
                             "(defroutes proutes"))

(defn build-subgrid-defroutes
  "Genera rutas especificas para subgrids para una tabla"
  [table parent-table]
  (let [data (str
              "  ;; Subgrid routes for " table " (parent: " parent-table ")\n"
              "  (GET \"/admin/" table "\" params [] (" table "-controller/" table "-grid params))\n"
              "  (GET \"/admin/" table "/add-form/:parent_id\" [parent_id :as request] (" table "-controller/" table "-add-form request parent_id))\n"
              "  (GET \"/admin/" table "/edit-form/:id\" [id :as request] (" table "-controller/" table "-edit-form request id))\n"
              "  (POST \"/admin/" table "/save\" params [] (" table "-controller/" table "-save params))\n"
              "  (GET \"/admin/" table "/delete/:id\" [id :as request] (" table "-controller/" table "-delete request id))")]
    data))

(defn process-subgrid
  "Actualiza proutes.clj para subgrids"
  [table parent-table]
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-grid-require table)]
                             "[compojure.core :refer [defroutes GET POST")
  (insert-lines-after-search "src/{{name}}/routes/proutes.clj"
                             [(build-subgrid-defroutes table parent-table)]
                             "(defroutes proutes"))

(comment
  (process-grid "{{name}}")
  (build-defroutes-reporte "users")
  (build-reporte-require "users")
  (process-report "users")
  (process-dashboard "amigos")
  (process-grid "amigos")

  ;; New subgrid functionality
  (process-subgrid "user_contacts" "users")
  (process-subgrid "user_roles" "users")
  (build-subgrid-defroutes "user_contacts" "users"))
