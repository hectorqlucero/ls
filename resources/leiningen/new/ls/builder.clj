(ns {{name}}.builder
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [{{name}}.models.crud :refer [get-table-columns]]
            [{{name}}.models.routes :as routes]))

;; --- GRID TEMPLATES (existing) ---

(def controller-template
  "(ns {{name}}.handlers.admin._table_.controller
  (:require
   [{{name}}.handlers.admin._table_.model :refer [get-_table_ get-_table_-id]]
   [{{name}}.handlers.admin._table_.view :refer [_table_-view _table_-form-view]]
   [{{name}}.layout :refer [application error-404]]
   [{{name}}.models.crud :refer [build-form-delete build-form-save]]
   [{{name}}.models.util :refer [get-session-id user-level]]
   [hiccup.core :refer [html]]))

(defn _table_
  [request]
  (let [title \"_TableTitle_\"
        ok (get-session-id request)
        js nil
        rows (get-_table_)
        content (_table_-view title rows)]
    (if (= (user-level request) \"S\")
      (application request title ok js content)
      (application request title ok nil \"Not authorized to access this item! (level 'S')\"))))

(defn _table_-add-form
  [_]
  (let [title \"New _TableTitle_\"
        row nil
        content (_table_-form-view title row)]
    (html content)))

(defn _table_-edit-form
  [_ id]
  (let [title \"Edit _TableTitle_\"
        row (get-_table_-id id)
        content (_table_-form-view title row)]
    (html content)))

(defn _table_-save
  [{params :params}]
  (let [table \"_table_\"
        result (build-form-save params table)]
    (if result
      {:status 200 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":true}\"}
      {:status 500 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":false}\"})))

(defn _table_-delete
  [_ id]
  (let [table \"_table_\"
        result (build-form-delete table id)]
    (if result
      {:status 302 :headers {\"Location\" \"/admin/_table_\"}}
      (error-404 \"Unable to process record!\" \"/admin/_table_\"))))
")

(def view-template
  "(ns {{name}}.handlers.admin._table_.view
  (:require [{{name}}.models.form :refer [form build-field build-modal-buttons]]
            [{{name}}.models.grid :refer [build-grid build-grid-with-subgrids]]))

;; _table_-view: If you want to show subgrids, pass a :subgrids vector in args.
;; Example:
;;   (let [args {:new true
;;               :edit true
;;               :delete true
;;               :subgrids [{:title \"Phones\"
;;                           :table-name \"phones\"
;;                           :foreign-key \"user_id\"
;;                           :href \"/admin/phones\"
;;                           :icon \"bi bi-telephone\"
;;                           :label \"Phones\"}]}]
;;     (build-grid-with-subgrids title rows table-id fields href args))
;; Otherwise, just use build-grid as usual.

(defn _table_-view
  [title rows & [args]]
  (let [labels [_labels_]
        db-fields [_dbfields_]
        fields (apply array-map (interleave db-fields labels))
        table-id \"_table__table\"
        href \"/admin/_table_\"
        args (or args {:new true :edit true :delete true})]
    (if (and (map? args) (contains? args :subgrids))
      (build-grid-with-subgrids title rows table-id fields href args)
      (build-grid title rows table-id fields href args))))

(defn build-_table_-fields
  [row]
  (list
    (build-field {:id \"id\" :type \"hidden\" :name \"id\" :value (:id row)})
    _fields_
  ))

(defn _table_-form-view
  [title row]
  (form \"/admin/_table_/save\" (build-_table_-fields row) (build-modal-buttons) title {:bare true}))
")

(def model-template
  "(ns {{name}}.handlers.admin._table_.model
  (:require [{{name}}.models.crud :refer [Query db]]))

(def get-_table_-sql
  (str \"SELECT * FROM _table_\"))

(defn get-_table_
  []
  (Query db get-_table_-sql))

(defn get-_table_-id
  [id]
  (first (Query db (str \"SELECT * FROM _table_ WHERE id=\" id))))
")

;; --- DASHBOARD TEMPLATES (new) ---

(def controller-dashboard-template
  "(ns {{name}}.handlers._table_.controller
  (:require
   [{{name}}.handlers._table_.model :refer [get-_table_]]
   [{{name}}.handlers._table_.view :refer [_table_-view]]
   [{{name}}.layout :refer [application]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn _table_
  [request]
  (let [title \"_TableTitle_\"
        ok (get-session-id request)
        js nil
        rows (get-_table_)
        content (_table_-view title rows)]
    (application request title ok js content)))
")

(def view-dashboard-template
  "(ns {{name}}.handlers._table_.view
  (:require [{{name}}.models.grid :refer [build-dashboard]]))

(defn _table_-view
  [title rows]
  (let [labels [_labels_]
        db-fields [_dbfields_]
        fields (apply array-map (interleave db-fields labels))
        table-id \"_table__table\"]
    (build-dashboard title rows table-id fields)))
")

(def model-dashboard-template
  "(ns {{name}}.handlers._table_.model
  (:require [{{name}}.models.crud :refer [Query db]]))

(def get-_table_-sql
  (str \"SELECT * FROM _table_\"))

(defn get-_table_
  []
  (Query db get-_table_-sql))
")

;; --- REPORT TEMPLATES (new) ---

(def controller-report-template
  "(ns {{name}}.handlers.reports._table_.controller
  (:require
   [{{name}}.handlers.reports._table_.model :refer [get-_table_]]
   [{{name}}.handlers.reports._table_.view :refer [_table_-view]]
   [{{name}}.layout :refer [application]]
   [{{name}}.models.util :refer [get-session-id]]))

(defn _table_
  [params]
  (let [title \"_TableTitle_ Report\"
        ok (get-session-id params)
        js nil
        rows (get-_table_)
        content (_table_-view title rows)]
    (application params title ok js content)))
")

(def view-report-template
  "(ns {{name}}.handlers.reports._table_.view
  (:require [{{name}}.models.grid :refer [build-dashboard]]))

(defn _table_-view
  [title rows]
  (let [table-id \"_table__table\"
        labels []
        db-fields []
        fields (apply array-map (interleave db-fields labels))]
    (build-dashboard title rows table-id fields)))
")

(def model-report-template
  "(ns {{name}}.handlers.reports._table_.model
  (:require [{{name}}.models.crud :refer [db Query]]))

(def get-_table_-sql
  (str \"SELECT * FROM _table_\"))

(defn get-_table_
  []
  (Query db get-_table_-sql))
")

;; --- SUBGRID TEMPLATES (new) ---

(def controller-subgrid-template
  "(ns {{name}}.handlers.admin._table_.controller
  (:require
   [{{name}}.handlers.admin._table_.model :refer [get-_table_ get-_table_-id]]
   [{{name}}.handlers.admin._table_.view :refer [_table_-view]]
   [{{name}}.models.crud :refer [build-form-delete build-form-save]]
   [hiccup.core :refer [html]]))

;; Main subgrid endpoint - this is what the subgrid AJAX calls
(defn _table_-grid
  [request]
  (let [params (:params request)
        parent-id-str (get params :parent_id)
        parent-id (when parent-id-str (Integer/parseInt parent-id-str))
        title \" _TableTitle_ \"
        rows (get-_table_ parent-id)
        content (_table_-view title rows parent-id)]
      {:status 200
       :headers {\"Content-Type\" \"text/html\"}
       :body (html content)}))

(defn _table_-add-form
  [_ parent-id]
  (let [title \"New _TableTitle_\"
        row {:_parent_key_ parent-id}]
    (html ({{name}}.handlers.admin._table_.view/build-_table_-form title row))))

(defn _table_-edit-form
  [_ id]
  (let [title \"Edit _TableTitle_\"
        row (get-_table_-id id)]
    (html ({{name}}.handlers.admin._table_.view/build-_table_-form title row))))

(defn _table_-save
  [{params :params}]
  (let [table \"_table_\"
        result (build-form-save params table)]
    (if result
      {:status 200 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":true}\"}
      {:status 500 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":false}\"})))

(defn _table_-delete
  [_ id]
  (let [table \"_table_\"
        result (build-form-delete table id)]
    (if result
      {:status 200 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":true}\"}
      {:status 500 :headers {\"Content-Type\" \"application/json\"} :body \"{\\\"ok\\\":false}\"})))
")

(def view-subgrid-template
  "(ns {{name}}.handlers.admin._table_.view
  (:require [{{name}}.models.form :refer [form build-field build-modal-buttons]]
            [{{name}}.models.grid :refer [build-grid build-grid-with-custom-new build-grid]]))

(defn _table_-view
  ([title rows]
   ;; If no parent-id, fallback to normal grid
   (build-grid title rows \"_table__table\" (apply array-map (interleave [_dbfields_] [_labels_])) \"/admin/_table_\" {:new true :edit true :delete true}))
  ([title rows parent-id]
   (let [labels [_labels_]
         db-fields [_dbfields_]
         fields (apply array-map (interleave db-fields labels))
         table-id \"_table__table\"
         href \"/admin/_table_\"
         args {:new true :edit true :delete true}
         new-record-href (if parent-id
                           (str href \"/add-form/\" parent-id)
                           (str href \"/add-form\"))]
     (if parent-id
       (build-grid-with-custom-new title rows table-id fields href args new-record-href)
       (build-grid title rows table-id fields href args)))))

(defn build-_table_-fields
  [row]
  (list
    (build-field {:id \"id\" :type \"hidden\" :name \"id\" :value (:id row)})
    (build-field {:id \"_parent_key_\" :type \"hidden\" :name \"_parent_key_\" :value (:_parent_key_ row)})
    _fields_
  ))

(defn build-_table_-form
  [title row]
  (form \"/admin/_table_/save\" (build-_table_-fields row) (build-modal-buttons) title {:bare true}))
")

(def model-subgrid-template
  "(ns {{name}}.handlers.admin._table_.model
  (:require [{{name}}.models.crud :refer [Query db]]))

(def get-_table_-sql
  (str \"SELECT _sql_fields_
         FROM _table_ t
         WHERE t._parent_key_ = ?
         ORDER BY _order_field_\"))

(defn get-_table_
  [parent-id]
  (Query db [get-_table_-sql parent-id]))

(def get-_table_-id-sql
  (str \"SELECT _sql_fields_
         FROM _table_
         WHERE id = ?\"))

(defn get-_table_-id
  [id]
  (first (Query db [get-_table_-id-sql id])))
")

;; --- TEMPLATE RENDERING ---

(defn render-template [template m]
  (reduce (fn [s [k v]]
            (let [pattern (case k
                            :table "_table_"
                            :TableTitle "_TableTitle_"
                            :ParentTitle "_ParentTitle_"
                            :parent_table "_parent_table_"
                            :parent_key "_parent_key_"
                            :parent_id "_parent_id_"
                            :labels "_labels_"
                            :dbfields "_dbfields_"
                            :fields "_fields_"
                            :sql_fields "_sql_fields_"
                            :order_field "_order_field_"
                            (str "\\{\\{" (name k) "\\}\\}"))]
              (str/replace s (re-pattern pattern) v)))
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

(defn generate-subgrid-files
  [table parent-table parent-key view-fields sql-fields]
  (let [TableTitle (str/capitalize table)
        ParentTitle (str/capitalize parent-table)
        labels (str/join " " (map (fn [[label _]] (str "\"" label "\"")) view-fields))
        dbfields (str/join " " (map (fn [[_ field]] (str ":" field)) view-fields))
        fields-block (apply str
                            (for [[label field] view-fields]
                              (str "(build-field {:label \"" label "\" :type \"text\" :id \"" field "\" :name \"" field "\" :placeholder \"" label " here...\" :required false :value (get row :" field ")})\n    ")))
        sql-fields-list (str/join ", " (concat ["id" parent-key] sql-fields))
        order-field (if (seq sql-fields)
                      (str "t." (first sql-fields))
                      "t.id")
        m {:table table
           :TableTitle TableTitle
           :ParentTitle ParentTitle
           :parent_table parent-table
           :parent_key parent-key
           :parent_id (str parent-table "_id")
           :labels labels
           :dbfields dbfields
           :fields fields-block
           :sql_fields sql-fields-list
           :order_field order-field}
        base-path (str "src/{{name}}/handlers/admin/" table "/")]
    (io/make-parents (str base-path "controller.clj"))
    (spit (str base-path "controller.clj") (render-template controller-subgrid-template m))
    (spit (str base-path "view.clj") (render-template view-subgrid-template m))
    (spit (str base-path "model.clj") (render-template model-subgrid-template m))
    (routes/process-subgrid table parent-table)
    (println (str "Subgrid code generated in: src/{{name}}/handlers/admin/" table))))

;; --- USAGE ---

(defn usage []
  (println "Usage: lein run -m builder grid <table> <Label1>:<field1> <Label2>:<field2> ...")
  (println "       lein run -m builder dashboard <table> <Label1>:<field1> <Label2>:<field2> ...")
  (println "       lein run -m builder report <table>")
  (println "       lein run -m builder subgrid <table> <parent-table> <parent-key> <Label1>:<field1> <Label2>:<field2> ...")
  (println "")
  (println "Lein aliases available:")
  (println "       lein grid <table> <Label1>:<field1> <Label2>:<field2> ...")
  (println "       lein dashboard <table> <Label1>:<field1> <Label2>:<field2> ...")
  (println "       lein report <table>")
  (println "       lein subgrid <table> <parent-table> <parent-key> <Label1>:<field1> <Label2>:<field2> ...")
  (println "")
  (println "Example: lein grid users Name:name Email:email")
  (println "         lein dashboard users Name:name Email:email")
  (println "         lein report users")
  (println "         lein subgrid user_contacts users user_id \"Contact Name\":contact_name Email:email"))

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

(defn build-subgrid
  [& args]
  (let [[table parent-table parent-key & field-pairs] args]
    (cond
      (or (nil? table) (nil? parent-table) (nil? parent-key))
      (do
        (println "Usage: lein run -m builder subgrid <table> <parent-table> <parent-key> <Label1>:<field1> <Label2>:<field2> ...")
        (println "Example: lein run -m builder subgrid user_contacts users user_id \"Contact Name:contact_name\" Email:email"))
      (empty? field-pairs)
      ;; If no fields given, auto-generate from DB
      (let [all-fields (map name (get-table-columns table))
            filtered-fields (remove #(or (= % "id") (= % parent-key)) all-fields)
            view-fields (map #(vector (auto-label %) %) filtered-fields)
            sql-fields filtered-fields]
        (generate-subgrid-files table parent-table parent-key view-fields sql-fields))
      :else
      (let [view-fields (map #(let [[label field] (str/split % #":")]
                                [label field])
                             field-pairs)
            sql-fields (map second view-fields)]
        (generate-subgrid-files table parent-table parent-key view-fields sql-fields)))))
