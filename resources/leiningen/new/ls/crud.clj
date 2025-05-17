(ns {{name}}.models.crud
  (:require
   [clojure.java.io :as io]
   [clojure.java.jdbc :as j]
   [clojure.string :as st])
  (:import
   java.text.SimpleDateFormat))

(defn get-config
  []
  (try
    (binding [*read-eval* false]
      (read-string (str (slurp (io/resource "private/config.clj")))))
    (catch Exception e (.getMessage e))))

(def config (get-config))

(def db {:classname                       (:db-class config)
         :subprotocol                     (:db-protocol config)
         :subname                         (:db-name config)
         :user                            (:db-user config)
         :password                        (:db-pwd config)
         :useSSL                          false
         :useTimezone                     true
         :useLegacyDatetimeCode           false
         :serverTimezone                  "UTC"
         :noTimezoneConversionForTimeType true
         :dumpQueriesOnException          true
         :autoDeserialize                 true
         :useDirectRowUnpack              false
         :cachePrepStmts                  true
         :cacheCallableStmts              true
         :cacheServerConfiguration        true
         :useLocalSessionState            true
         :elideSetAutoCommits             true
         :alwaysSendSetIsolation          false
         :enableQueryTimeouts             false
         :zeroDateTimeBehavior            "CONVERT_TO_NULL"}) ; Database connection

(def SALT "897sdn9j98u98kj")                                ; encryption salt for DB                            ; encryption salt for DB

(def KEY (byte-array 16))

(defn Query
  "queries database accepts query string"
  [db sql]
  (j/query db sql {:entities (j/quoted \`)}))

(defn Query!
  "queries database accepts query string no return value"
  [db sql]
  (j/execute! db sql {:entities (j/quoted \`)}))

(defn Insert
  "Inserts colums in the specified table"
  [db table row]
  (j/insert! db table row {:entities (j/quoted \`)}))

(defn Insert-multi
  "Inserts multiple rows in specified table"
  [db table rows]
  (j/with-db-transaction [t-con db]
    (j/insert-multi! t-con table rows)))

(defn Update
  "Updates columns in the specified table"
  [db table row where-clause]
  (j/update! db table row where-clause {:entities (j/quoted \`)}))

(defn Delete
  "Deletes columns in a specified table"
  [db table where-clause]
  (j/delete! db table where-clause {:entities (j/quoted \`)}))

(defn Save
  "Updates columns or inserts a new row in the specified table"
  [db table row where-clause]
  (j/with-db-transaction [t-con db]
    (let [result (j/update! t-con table row where-clause {:entities (j/quoted \`)})]
      (if (zero? (first result))
        (j/insert! t-con table row {:entities (j/quoted \`)})
        result))))

(defn crud-fix-id
  [v]
  (if (clojure.string/blank? v) (Integer. 0) v))

(defn crud-capitalize-words
  "Capitalize words"
  [s]
  (->> (clojure.string/split (str s) #"\b")
       (map clojure.string/capitalize)
       (clojure.string/join)))

(defn crud-format-date-internal
  "Convert a MM/dd/yyyy format date to yyyy-MM-dd format using a string as a date
   eg. 02/01/1997 -> 1997-02-01"
  [s]
  (if (not-empty s)
    (.format
     (SimpleDateFormat. "yyyy-MM-dd")
     (.parse
      (SimpleDateFormat. "MM/dd/yyyy") s))

    nil))

(defn get-table-describe
  [table]
  (Query db (str "DESCRIBE " table)))

(defn get-table-columns
  [table]
  (map #(keyword (:field %)) (get-table-describe table)))

(defn get-table-types [table]
  (map #(keyword (:type %)) (get-table-describe table)))

(defn process-field
  [params field field-type]
  (let [value (str ((keyword field) params))
        field-type (st/lower-case field-type)]
    (cond
      (st/includes? field-type "varchar") value
      (st/includes? field-type "char") (st/upper-case value)
      (st/includes? field-type "int") (if (clojure.string/blank? value) 0 value)
      ;;(st/includes? field-type "date") (crud-format-date-internal value)
      :else value)))

(defn build-postvars
  "Build post vars for table and process by type"
  [table params]
  (let [td (get-table-describe table)]
    (into {}
          (map (fn [x]
                 (when ((keyword (:field x)) params)
                   {(keyword (:field x))
                    (process-field params (:field x) (:type x))})) td))))

(defn build-form-field
  [d]
  (let [field (:field d)
        field-type (:type d)]
    (cond
      ;;(= field-type "date") (str "DATE_FORMAT(" field "," "'%m/%d/%Y') as " field)
      (= field-type "time") (str "TIME_FORMAT(" field "," "'%H:%i') as " field)
      :else field)))

(defn get-table-key
  [d]
  (:field (first (filter #(= (:key %) "PRI") d))))

(defn build-form-row
  "Builds form row"
  [table id]
  (let [tid (get-table-key (get-table-describe table))
        head "SELECT "
        body (apply str (interpose #"," (map #(build-form-field %) (get-table-describe table))))
        foot (str " FROM " table " WHERE " tid " = ?")
        sql (str head body foot)
        row (Query db [sql id])]
    (first row)))

(defn remove-emptys
  [postvars]
  (apply dissoc postvars (for [[k v] postvars :when (empty? v)] k)))

(defn process-regular-form
  "Standard form save ex. (build-for-save params 'eventos')"
  [params table]
  (let [id (crud-fix-id (:id params))
        postvars (build-postvars table params)
        postvars (if (= id 0) (dissoc postvars :id) postvars)
        postvars (remove-emptys postvars)
        result (Save db (keyword table) postvars ["id = ?" id])]
    (if (seq result) true false)))

;; Start upload form
(defn crud-upload-image
  "Uploads image and renames it to the id passed"
  [table file id path]
  (let [tempfile   (:tempfile file)
        size       (:size file)
        type       (:content-type file)
        extension  (peek (clojure.string/split type #"\/"))
        extension  (if (= extension "jpeg") "jpg" "jpg")
        image-name (str table "_" id "." extension)]
    (when-not (zero? size)
      (io/copy tempfile (io/file (str path image-name))))
    image-name))

(defn get-id [id postvars table]
  (if (= id 0)
    (-> (Save db (keyword table) postvars ["id = ?" id])
        first
        :generated_key)
    id))

(defn process-upload-form
  [params table folder]
  (let [id (crud-fix-id (:id params))
        file (:file params)
        postvars (dissoc (build-postvars table params) :file)
        postvars (if (= id 0) (dissoc postvars :id) postvars)
        postvars (remove-emptys postvars)
        the-id (str (get-id id postvars table))
        path (str (:uploads config) folder "/")
        image-name (crud-upload-image table file the-id path)
        postvars (assoc postvars :imagen image-name :id the-id)
        result (Save db (keyword table) postvars ["id = ?" the-id])]
    (if (seq result) true false)))
;; End upload form

(defn build-form-save
  "Builds form save"
  [params table & args]
  (if-not (nil? (:file params))
    (process-upload-form params table (first (:path args)))
    (process-regular-form params table)))

(defn build-form-delete
  [table id]
  (let [result (if-not (nil? id)
                 (Delete db (keyword table) ["id = ?" id])
                 nil)]
    (if (seq result) true false)))

(comment
  (Query db "select * from users"))
