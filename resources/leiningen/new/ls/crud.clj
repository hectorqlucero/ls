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
  "Get single primary key field (backward compatibility)"
  [d]
  (when (seq d)
    (when-let [pk (first (filter #(= (:key %) "PRI") d))]
      (:field pk))))

(defn get-table-primary-keys
  "Get all primary key fields for a table"
  [table]
  (let [describe (get-table-describe table)]
    (->> describe
         (filter #(= (:key %) "PRI"))
         (map :field)
         vec)))

(defn get-primary-key-map
  "Extract primary key values from params based on table's primary keys"
  [table params]
  (let [pk-fields (get-table-primary-keys table)]
    (into {}
          (keep (fn [field]
                  (when-let [value ((keyword field) params)]
                    [(keyword field) value]))
                pk-fields))))

(defn build-pk-where-clause
  "Build WHERE clause for primary key(s)"
  [pk-map]
  (when (seq pk-map)
    (let [conditions (map (fn [[k _]] (str (name k) " = ?")) pk-map)
          values (vals pk-map)]
      [(str (clojure.string/join " AND " conditions)) values])))

(defn build-form-row
  "Builds form row - supports both single and composite primary keys"
  [table id-or-pk-map]
  (let [describe (get-table-describe table)
        pk-fields (get-table-primary-keys table)]
    (when (seq pk-fields)
      (let [head "SELECT "
            body (apply str (interpose #"," (map #(build-form-field %) describe)))
            ;; Handle both single ID and composite primary key cases
            pk-map (cond
                     ;; If id-or-pk-map is a map, use it directly
                     (map? id-or-pk-map) id-or-pk-map
                     ;; If single primary key and single value provided
                     (= 1 (count pk-fields)) {(keyword (first pk-fields)) id-or-pk-map}
                     ;; Otherwise, cannot determine primary key mapping
                     :else nil)]
        (when pk-map
          (let [[where-clause values] (build-pk-where-clause pk-map)
                foot (str " FROM " table " WHERE " where-clause)
                sql (str head body foot)
                row (Query db (into [sql] values))]
            (first row)))))))

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
  "Standard form save - supports composite primary keys"
  [params table]
  (let [pk-fields (get-table-primary-keys table)]
    (if (= 1 (count pk-fields))
      ;; Single primary key - use original logic for backward compatibility
      (let [id (crud-fix-id (:id params))
            postvars (cond-> (-> (build-postvars table params)
                                 blank->nil)
                       (= id 0) (dissoc :id))]
        (if (and (map? postvars) (seq postvars))
          (let [result (Save db (keyword table) postvars ["id = ?" id])]
            (if (seq result) true false))
          false))
      ;; Composite primary key - use new logic
      (let [pk-map (get-primary-key-map table params)
            is-new-record? (or (empty? pk-map)
                               (every? (fn [[_ v]]
                                         (or (nil? v)
                                             (and (string? v) (clojure.string/blank? v))
                                             (and (number? v) (= v 0))
                                             (= (str v) "0"))) pk-map))
            postvars (cond-> (-> (build-postvars table params)
                                 blank->nil)
                       is-new-record? (apply dissoc (map keyword pk-fields)))]
        (if (and (map? postvars) (seq postvars))
          (let [where-clause (if is-new-record?
                               ["1 = 0"]
                               (let [[clause values] (build-pk-where-clause pk-map)]
                                 (into [clause] values)))
                result (Save db (keyword table) postvars where-clause)]
            (if (seq result) true false))
          false)))))

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
  "Get ID for insert operations - supports composite primary keys"
  [pk-values-or-id postvars table]
  (let [pk-fields (get-table-primary-keys table)]
    (cond
      ;; Single primary key case (backward compatibility)
      (and (= 1 (count pk-fields)) (number? pk-values-or-id))
      (if (= pk-values-or-id 0)
        (when (map? postvars)
          (-> (Save db (keyword table) postvars ["1 = 0"]) ; Force insert
              first
              :generated_key))
        pk-values-or-id)

      ;; Composite primary key case
      (map? pk-values-or-id)
      (let [is-new? (every? (fn [[_ v]] (or (nil? v) (= v 0)
                                            (and (string? v) (clojure.string/blank? v))))
                            pk-values-or-id)]
        (if is-new?
          (when (map? postvars)
            ;; For composite keys, we might need to return the full key map
            ;; This depends on your specific use case
            (let [result (Save db (keyword table) postvars ["1 = 0"])]
              (when (seq result)
                ;; Return the generated key or the pk-values-or-id map
                (or (:generated_key (first result)) pk-values-or-id))))
          pk-values-or-id))

      ;; Fallback for backward compatibility
      :else pk-values-or-id)))

(defn process-upload-form
  "Process upload form - supports composite primary keys"
  [params table folder]
  (let [pk-fields (get-table-primary-keys table)
        pk-map (get-primary-key-map table params)
        file (:file params)
        postvars (dissoc (build-postvars table params) :file)
        is-new-record? (or (empty? pk-map)
                           (every? (fn [[_ v]]
                                     (or (nil? v)
                                         (and (string? v) (clojure.string/blank? v))
                                         (and (number? v) (= v 0))
                                         (= (str v) "0"))) pk-map))
        postvars (cond-> postvars
                   is-new-record? (apply dissoc (map keyword pk-fields)))
        postvars (-> postvars blank->nil)]
    (if (and (map? postvars) (seq postvars))
      (let [;; For backward compatibility with single ID systems
            the-id (if (= 1 (count pk-fields))
                     (str (get-id (or ((keyword (first pk-fields)) pk-map) 0) postvars table))
                     ;; For composite keys, we'll use a combination or the first key
                     (str (or (some identity (vals pk-map))
                              (get-id pk-map postvars table))))
            path (str (:uploads config) folder "/")
            image-name (crud-upload-image table file the-id path)
            ;; Add the image name and update primary key if needed
            postvars (cond-> (assoc postvars :imagen image-name)
                       (and (= 1 (count pk-fields)) the-id)
                       (assoc (keyword (first pk-fields)) the-id))
            where-clause (if is-new-record?
                           ["1 = 0"] ; Force insert
                           (let [[clause values] (build-pk-where-clause pk-map)]
                             (into [clause] values)))
            result (Save db (keyword table) postvars where-clause)]
        (if (seq result) true false))
      false)))

(defn build-form-save
  "Builds form save"
  [params table & args]
  (if-not (nil? (:file params))
    (process-upload-form params table (first (:path args)))
    (process-regular-form params table)))

(defn build-form-delete
  "Delete record - supports composite primary keys"
  [table id-or-pk-map]
  (let [pk-fields (get-table-primary-keys table)]
    (when (seq pk-fields)
      (let [pk-map (cond
                     ;; If id-or-pk-map is a map, use it directly
                     (map? id-or-pk-map) id-or-pk-map
                     ;; If single primary key and single value provided
                     (= 1 (count pk-fields)) {(keyword (first pk-fields)) id-or-pk-map}
                     ;; Otherwise, cannot determine primary key mapping
                     :else nil)]
        (when (and pk-map (every? (fn [[_ v]] (not (or (nil? v)
                                                       (and (string? v) (clojure.string/blank? v)))))
                                  pk-map))
          (let [[where-clause values] (build-pk-where-clause pk-map)
                result (Delete db (keyword table) (into [where-clause] values))]
            (if (seq result) true false)))))))

;; Utility functions for composite primary key support

(defn has-composite-primary-key?
  "Check if table has composite (multi-column) primary key"
  [table]
  (> (count (get-table-primary-keys table)) 1))

(defn validate-primary-key-params
  "Validate that all primary key fields are provided in params"
  [table params]
  (let [pk-fields (get-table-primary-keys table)
        pk-map (get-primary-key-map table params)]
    (= (count pk-fields) (count pk-map))))

(defn build-pk-string
  "Build a string representation of primary key values (useful for file naming, etc.)"
  [pk-map]
  (when (seq pk-map)
    (clojure.string/join "_" (map (fn [[k v]] (str (name k) "-" v)) pk-map))))

(comment
  ;; Examples of using the enhanced CRUD functions

  ;; Traditional single primary key usage (backward compatible)
  (Query db "select * from users")
  (build-form-row "users" 123)
  (build-form-delete "users" 123)

  ;; Composite primary key usage
  ;; For a table with composite primary key (user_id, role_id)
  (get-table-primary-keys "user_roles")
  ; => ["user_id" "role_id"]

  (build-form-row "user_roles" {:user_id 123 :role_id 456})
  (build-form-delete "user_roles" {:user_id 123 :role_id 456})

  ;; Check if table has composite primary key
  (has-composite-primary-key? "user_roles")
  ; => true (if it has multiple primary key columns)

  ;; Validate primary key parameters
  (validate-primary-key-params "user_roles" {:user_id 123 :role_id 456 :other_field "value"})
  ; => true (all primary key fields provided)

  ;; Build primary key string representation
  (build-pk-string {:user_id 123 :role_id 456})
  ; => "user_id-123_role_id-456")
  )
