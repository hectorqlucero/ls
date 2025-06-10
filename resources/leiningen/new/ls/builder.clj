(ns {{name}}.builder
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [{{name}}.models.crud :refer [get-table-columns]]
            [{{name}}.models.routes :as routes]))

;; --- GRID TEMPLATES (existing) ---

(def controller-template
  "(ns {{name}}.handlers.admin.{{table}}.controller
  (:require
   [{{name}}.handlers.admin.{{table}}.model :refer [get-{{table}} get-{{table}}-id]]
   [{{name}}.handlers.admin.{{table}}.view :refer [{{table}}-view {{table}}-form-view]]
   [{{name}}.layout :refer [application error-404]]
   [{{name}}.models.crud :refer [build-form-delete build-form-save]]
   [{{name}}.models.util :refer [get-session-id user-level]]
   [hiccup.core :refer [html]]))

(defn {{table}}
  [request]
  (let [title \"{{TableTitle}}\"
        ok (get-session-id request)
        js nil
        rows (get-{{table}})
        content ({{table}}-view title rows)]
    (if (= (user-level request) \"S\")
      (application request title ok js content)
      (application request title ok nil \"Not authorized to access this item! (level 'S')\"))))

(defn {{table}}-add-form
  [_]
  (let [title \"New {{TableTitle}}\"
        row nil
        content ({{table}}-form-view title row)]
    (html content)))

(defn {{table}}-edit-form
  [_ id]
  (let [title \"Edit {{TableTitle}}\"
        row (get-{{table}}-id id)
        content ({{table}}-form-view title row)]
    (html content)))

(defn {{table}}-save
  [{params :params}]
  (let [table \"{{table}}\"
        result (build-form-save params table)]
    (if result
      {:status 200 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":true}\"}
      {:status 500 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":false}\"})))

(defn {{table}}-delete
  [_ id]
  (let [table \"{{table}}\"
        result (build-form-delete table id)]
    (if result
      {:status 302 :headers {\"Location\" \"/admin/{{table}}\"}}
      (error-404 \"Unable to process record!\" \"/admin/{{table}}\"))))
")

(def view-template
  "(ns {{name}}.handlers.admin.{{table}}.view
  (:require [{{name}}.models.form :refer [form build-field build-modal-buttons]]
            [{{name}}.models.grid :refer [build-grid]]))

(defn {{table}}-view
  [title rows]
  (let [labels [{{labels}}]
        db-fields [{{dbfields}}]
        fields (apply array-map (interleave db-fields labels))
        table-id \"{{table}}_table\"
        href \"/admin/{{table}}\"
        args {:new true :edit true :delete true}]
    (build-grid title rows table-id fields href args)))

(defn build-{{table}}-fields
  [row]
  (list
    (build-field {:id \"id\" :type \"hidden\" :name \"id\" :value (:id row)})
    {{fields}}
  ))

(defn {{table}}-form-view
  [title row]
  (form \"/admin/{{table}}/save\" (build-{{table}}-fields row) (build-modal-buttons) title {:bare true}))
")

(def model-template
  "(ns {{name}}.handlers.admin.{{table}}.model
  (:require [{{name}}.models.crud :refer [Query db]]))

(def get-{{table}}-sql
  (str \"SELECT * FROM {{table}}\"))

(defn get-{{table}}
  []
  (Query db get-{{table}}-sql))

(defn get-{{table}}-id
  [id]
  (first (Query db (str get-{{table}}-sql \" WHERE id=\" id))))
")

;; --- DASHBOARD TEMPLATES (new) ---

(def controller-dashboard-template
  "(ns {{name}}.handlers.{{table}}.controller
  (:require
   [{{name}}.handlers.{{table}}.model :refer [get-{{table}}]]
   [{{name}}.handlers.{{table}}.view :refer [{{table}}-view]]
   [{{name}}.layout :refer [application]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn {{table}}
  [request]
  (let [title \"{{TableTitle}}\"
        ok (get-session-id request)
        js nil
        rows (get-{{table}})
        content ({{table}}-view title rows)]
    (application request title ok js content)))
")

(def view-dashboard-template
  "(ns {{name}}.handlers.{{table}}.view
  (:require [{{name}}.models.grid :refer [build-dashboard]]))

(defn {{table}}-view
  [title rows]
  (let [labels [{{labels}}]
        db-fields [{{dbfields}}]
        fields (apply array-map (interleave db-fields labels))
        table-id \"{{table}}_table\"]
    (build-dashboard title rows table-id fields)))
")

(def model-dashboard-template
  "(ns {{name}}.handlers.{{table}}.model
  (:require [{{name}}.models.crud :refer [Query db]]))

(def get-{{table}}-sql
  (str \"SELECT * FROM {{table}}\"))

(defn get-{{table}}
  []
  (Query db get-{{table}}-sql))
")

;; --- REPORT TEMPLATES (new) ---

(def controller-report-template
  "(ns {{name}}.handlers.reports.{{table}}.controller
  (:require
   [{{name}}.handlers.reports.{{table}}.model :refer [get-{{table}}]]
   [{{name}}.handlers.reports.{{table}}.view :refer [{{table}}-view]]
   [{{name}}.layout :refer [application]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn {{table}}
  [params]
  (let [title \"{{TableTitle}} Report\"
        ok (get-session-id params)
        js nil
        rows (get-{{table}})
        content ({{table}}-view title rows)]
    (application params title ok js content)))
")

(def view-report-template
  "(ns {{name}}.handlers.reports.{{table}}.view
  (:require [{{name}}.models.grid :refer [build-dashboard]]))

(defn {{table}}-view
  [title rows]
  (let [table-id \"{{table}}_table\"
        labels []
        db-fields []
        fields (apply array-map (interleave db-fields labels))]
    (build-dashboard title rows table-id fields)))
")

(def model-report-template
  "(ns {{name}}.handlers.reports.{{table}}.model
  (:require [{{name}}.models.crud :refer [db Query]]))

(def get-{{table}}-sql
  (str \"SELECT * FROM {{table}}\"))

(defn get-{{table}}
  []
  (Query db get-{{table}}-sql))
")

;; --- TEMPLATE RENDERING ---

(defn render-template [template m]
  (reduce (fn [s [k v]]
            (str/replace s (re-pattern (str "\\{\\{" (name k) "\\}\\}")) v))
          template
          m))

(defn field-block [fields]
  (apply str
         (for [[label field] fields]
           (str "(build-field {:label \"" label "\" :type \"text\" :id \"" field "\" :name \"" field "\" :placeholder \"" label " here...\" :required false :value (get row :" field ")})\n    "))))

(defn auto-label [field]
  (-> field
      (clojure.string/replace #"_" " ")
      (clojure.string/capitalize)))

;; --- FILE GENERATION ---

(defn generate-files [table fields]
  (let [TableTitle (str/capitalize table)
        labels (str/join " " (map (fn [[label _]] (str "\"" label "\"")) fields))
        dbfields (str/join " " (map (fn [[_ field]] (str ":" field)) fields))
        fields-block (field-block fields)
        m {:table table
           :TableTitle TableTitle
           :labels labels
           :dbfields dbfields
           :fields fields-block}
        base-path (str "src/{{name}}/handlers/admin/" table "/")]
    (io/make-parents (str base-path "controller.clj"))
    (spit (str base-path "controller.clj") (render-template controller-template m))
    (spit (str base-path "view.clj") (render-template view-template m))
    (spit (str base-path "model.clj") (render-template model-template m))
    (routes/process-grid table)
    (println (str "Code generated in: src/{{name}}/handlers/admin/" table))))

(defn generate-dashboard-files [table fields]
  (let [TableTitle (str/capitalize table)
        labels (str/join " " (map (fn [[label _]] (str "\"" label "\"")) fields))
        dbfields (str/join " " (map (fn [[_ field]] (str ":" field)) fields))
        m {:table table
           :TableTitle TableTitle
           :labels labels
           :dbfields dbfields}
        base-path (str "src/{{name}}/handlers/" table "/")]
    (io/make-parents (str base-path "controller.clj"))
    (spit (str base-path "controller.clj") (render-template controller-dashboard-template m))
    (spit (str base-path "view.clj") (render-template view-dashboard-template m))
    (spit (str base-path "model.clj") (render-template model-dashboard-template m))
    (routes/process-dashboard table)
    (println (str "Dashboard code generated in: src/{{name}}/handlers/" table))))

(defn generate-report-files [table]
  (let [TableTitle (str/capitalize table)
        m {:table table
           :TableTitle TableTitle}
        base-path (str "src/{{name}}/handlers/reports/" table "/")]
    (io/make-parents (str base-path "controller.clj"))
    (spit (str base-path "controller.clj") (render-template controller-report-template m))
    (spit (str base-path "view.clj") (render-template view-report-template m))
    (spit (str base-path "model.clj") (render-template model-report-template m))
    (routes/process-report table)
    (println (str "Report code generated in: src/{{name}}/handlers/reports/" table))))

;; --- USAGE ---

(defn usage []
  (println "Usage: lein run -m builder grid <table> <Label1>:<field1> <Label2>:<field2> ...")
  (println "       lein run -m builder dashboard <table> <Label1>:<field1> <Label2>:<field2> ...")
  (println "       lein run -m builder report <table>")
  (println "Example: lein run -m builder grid users Name:name Email:email")
  (println "         lein run -m builder dashboard users Name:name Email:email")
  (println "         lein run -m builder report users"))

;; --- MAIN ENTRYPOINTS ---

(defn build-grid
  [& args]
  (let [[table & field-pairs] args]
    (cond
      (nil? table) (usage)
      (empty? field-pairs)
      ;; If no fields given, auto-generate from DB
      (let [fields (for [field (map name (get-table-columns table))]
                     [(auto-label field) field])]
        (generate-files table (rest fields)))
      :else
      (let [fields (map #(let [[label field] (str/split % #":")]
                           [label field])
                        field-pairs)]
        (generate-files table fields)))))

(defn build-dashboard
  [& args]
  (let [[table & field-pairs] args]
    (cond
      (nil? table) (usage)
      (empty? field-pairs)
      ;; If no fields given, auto-generate from DB
      (let [fields (for [field (map name (get-table-columns table))]
                     [(auto-label field) field])]
        (generate-dashboard-files table (rest fields)))
      :else
      (let [fields (map #(let [[label field] (str/split % #":")]
                           [label field])
                        field-pairs)]
        (generate-dashboard-files table fields)))))

(defn build-report
  [& args]
  (let [[table] args]
    (if (nil? table)
      (usage)
      (generate-report-files table))))

(comment
  (map name (get-table-columns "users"))
  (auto-label "name")
  (let [fields (for [field (map name (get-table-columns "users"))]
                 [(auto-label field) field])]
    (rest fields)))
