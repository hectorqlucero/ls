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
         :zeroDateTimeBehavior            "CONVERT_TO_NULL"})

(def SALT "897sdn9j98u98kj")
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
  (try
    (if (clojure.string/blank? v) 0 (Long/parseLong (str v)))
    (catch Exception _ 0)))

(defn crud-capitalize-words
  "Capitalize words"
  [s]
  (->> (clojure.string/split (str s) #"\b")
       (map clojure.string/capitalize)
       (clojure.string/join)))

(defn crud-format-date-internal
  [s]
  (try
    (if (not-empty s)
      (.format
       (SimpleDateFormat. "yyyy-MM-dd")
       (.parse
        (SimpleDateFormat. "MM/dd/yyyy") s))
      nil)
    (catch Exception _ nil)))

(defn get-table-describe
  [table]
  (Query db (str "DESCRIBE " table)))

(defn get-table-columns
  [table]
  (map #(keyword (:field %)) (get-table-describe table)))

(defn get-table-types
  [table]
  (map #(keyword (:type %)) (get-table-describe table)))

(defn process-field
  [params field field-type]
  (let [value (str ((keyword field) params))
        field-type (-> field-type st/lower-case (st/replace #"\(.*\)" ""))]
    (cond
      ;; String types
      (or (st/includes? field-type "varchar")
          (st/includes? field-type "text")
          (st/includes? field-type "enum")
          (st/includes? field-type "set")) value

      (st/includes? field-type "char") (st/upper-case value)

      ;; Integer types
      (or (st/includes? field-type "int")
          (st/includes? field-type "tinyint")
          (st/includes? field-type "smallint")
          (st/includes? field-type "mediumint")
          (st/includes? field-type "bigint"))
      (cond
        (clojure.string/blank? value) 0
        (re-matches #"(?i)true" value) 1
        (re-matches #"(?i)on" value) 1
        (re-matches #"(?i)false" value) 0
        (re-matches #"(?i)off" value) 0
        (re-matches #"^-?\d+$" value) (try (Long/parseLong value) (catch Exception _ 0))
        :else 0)

      ;; Floating point types
      (or (st/includes? field-type "float")
          (st/includes? field-type "double")
          (st/includes? field-type "decimal"))
      (cond
        (clojure.string/blank? value) 0.0
        (re-matches #"^-?\d+(\.\d+)?$" value) (try (Double/parseDouble value) (catch Exception _ 0.0))
        :else 0.0)

      ;; Date/time types
      (st/includes? field-type "year")
      (if (clojure.string/blank? value)
        nil
        (subs value 0 4))

      (or (st/includes? field-type "date")
          (st/includes? field-type "datetime")
          (st/includes? field-type "timestamp"))
      (if (clojure.string/blank? value) nil value)

      (st/includes? field-type "time")
      (if (clojure.string/blank? value) nil value)

      ;; Binary/blob/json types
      (or (st/includes? field-type "blob")
          (st/includes? field-type "binary")
          (st/includes? field-type "varbinary")) value

      (st/includes? field-type "json")
      (if (clojure.string/blank? value) nil value)

      ;; Boolean/bit types
      (or (st/includes? field-type "bit")
          (st/includes? field-type "bool")
          (st/includes? field-type "boolean"))
      (cond
        (clojure.string/blank? value) false
        (re-matches #"(?i)true" value) true
        (re-matches #"(?i)on" value) true
        (re-matches #"(?i)false" value) false
        (re-matches #"(?i)off" value) false
        (re-matches #"^-?\d+$" value) (not= value "0")
        :else false)

      ;; Default fallback
      :else
      ;; (println "Warning: Unhandled MySQL type" field-type "for field" field)
      value)))

(defn build-postvars
  "Build post vars for table and process by type"
  [table params]
  (let [td (get-table-describe table)]
    (into {}
          (keep (fn [x]
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
  (when (seq d)
    (when-let [pk (first (filter #(= (:key %) "PRI") d))]
      (:field pk))))

(defn build-form-row
  "Builds form row"
  [table id]
  (let [describe (get-table-describe table)
        tid (get-table-key describe)]
    (when tid
      (let [head "SELECT "
            body (apply str (interpose #"," (map #(build-form-field %) describe)))
            foot (str " FROM " table " WHERE " tid " = ?")
            sql (str head body foot)
            row (Query db [sql id])]
        (first row)))))

(defn blank->nil
  [m]
  (into {}
        (for [[k v] m]
          [k (if (and (string? v) (clojure.string/blank? v)) nil v)])))

(defn remove-emptys
  [postvars]
  (if (map? postvars)
    (apply dissoc postvars
           (for [[k v] postvars :when (nil? v)] k))
    {}))

(defn process-regular-form
  "Standard form save ex. (build-for-save params 'eventos')"
  [params table]
  (let [id (crud-fix-id (:id params))
        postvars (cond-> (-> (build-postvars table params)
                             blank->nil)
                   (= id 0) (dissoc :id))]
    (if (and (map? postvars) (seq postvars))
      (let [result (Save db (keyword table) postvars ["id = ?" id])]
        (if (seq result) true false))
      false)))

(defn crud-upload-image
  "Uploads image and renames it to the id passed"
  [table file id path]
  (let [valid-exts #{"jpg" "jpeg" "png" "gif" "bmp" "webp"}
        tempfile   (:tempfile file)
        size       (:size file)
        orig-name  (:filename file)
        ext-from-name (when orig-name
                        (-> orig-name
                            (clojure.string/split #"\.")
                            last
                            clojure.string/lower-case))
        ext (if (and ext-from-name (valid-exts ext-from-name))
              (if (= ext-from-name "jpeg") "jpg" ext-from-name)
              "jpg") ; default to jpg if unknown or missing
        image-name (str table "_" id "." ext)]
    (when (and tempfile (not (zero? size)))
      (io/copy tempfile (io/file (str path image-name))))
    image-name))

(defn get-id
  [id postvars table]
  (if (= id 0)
    (when (map? postvars)
      (-> (Save db (keyword table) postvars ["id = ?" id])
          first
          :generated_key))
    id))

(defn process-upload-form
  [params table folder]
  (let [id (crud-fix-id (:id params))
        file (:file params)
        postvars (dissoc (build-postvars table params) :file)
        postvars (if (= id 0) (dissoc postvars :id) postvars)
        postvars (-> postvars blank->nil)]
    (if (and (map? postvars) (seq postvars))
      (let [the-id (str (get-id id postvars table))
            path (str (:uploads config) folder "/")
            image-name (crud-upload-image table file the-id path)
            postvars (assoc postvars :imagen image-name :id the-id)
            result (Save db (keyword table) postvars ["id = ?" the-id])]
        (if (seq result) true false))
      false)))

(defn build-form-save
  "Builds form save"
  [params table & args]
  (if-not (nil? (:file params))
    (process-upload-form params table (first (:path args)))
    (process-regular-form params table)))

(defn build-form-delete
  [table id]
  (let [result (if (and (not (nil? id)) (not (clojure.string/blank? (str id))))
                 (Delete db (keyword table) ["id = ?" id])
                 nil)]
    (if (seq result) true false)))


(comment
  (Query db "select * from users"))
