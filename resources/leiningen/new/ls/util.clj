(ns {{name}}.models.util
  (:require
   [clojure.core :refer [random-uuid]]
   [clojure.data.json :as json]
   [clojure.string :as st]
   [clojure.walk :as walk]
   [{{name}}.models.crud :refer [config db Query]]))

(defn shorten
  "Shortens a string to a maximum length, appending '...' if truncated."
  [s max-length]
  (if (and s (> (count s) max-length))
    (str (subs s 0 max-length) "...")
    s))

(defn class-merge
  "Merges base-classes (vector) and attr map's :class (string or vector) into a single :class string, removing duplicates."
  [base-classes attr]
  (let [attr-class (:class attr)
        all-classes (concat base-classes
                            (cond
                              (vector? attr-class) attr-class
                              (string? attr-class) (st/split attr-class #"\s+")
                              (nil? attr-class) []
                              :else [attr-class]))
        class-str (->> all-classes (remove nil?) distinct (st/join " "))]
    (assoc attr :class class-str)))

(defn get-session-id [request]
  (try
    (if-let [uid (get-in request [:session :user_id])]
      uid
      0)
    (catch Exception e (.getMessage e))))

(defn user-level [request]
  (let [id   (get-session-id request)
        type (if (nil? id)
               nil
               (:level (first (Query db ["select level from users where id = ?" id]))))]
    type))

(defn user-email [request]
  (let [id    (get-session-id request)
        email (if (nil? id)
                nil
                (:username (first (Query db ["select username from users where id = ?" id]))))]
    email))

(defn user-name [request]
  (let [id (get-session-id request)
        username (if (nil? id)
                   nil
                   (:name (first (Query db ["select CONCAT(firstname,' ',lastname) as name from users where id = ?" id]))))]
    username))

(defn seconds->string [seconds]
  (let [n seconds
        day (int (/ n (* 24 3600)))
        day-desc (if (= day 1) " day " " days ")
        n (mod n (* 24 3600))
        hour (int (/ n 3600))
        hour-desc (if (= hour 1) " hour " " hours ")
        n (mod n 3600)
        minutes (int (/ n 60))
        minutes-desc (if (= minutes 1) " minute " " minutes ")
        n (mod n 60)
        seconds (int n)
        seconds-desc (if (= seconds 1) " second " " seconds ")
        minutes-desc (str day day-desc hour hour-desc minutes minutes-desc)
        seconds-desc (str day day-desc hour hour-desc minutes minutes-desc seconds seconds-desc)]
    seconds-desc))

(defn image-link
  [image-name]
  (let [path (str (:path config) image-name "?" (random-uuid))
        style "margin-right:wpx;cursor:pointer;"
        img-link (str "<img src='" path "' alt='" image-name "' width=42 height=32 style='" style "'>")]
    img-link))

(defn year-options
  [table date-field]
  (let [sql (str "SELECT DISTINCT YEAR(" date-field ") AS year FROM " table)]
    (Query db sql)))

(defn month-options
  [table date-field]
  (let [sql (str "SELECT DISTINCT MONTH(" date-field ") AS month, MONTHNAME(" date-field ") AS month_name FROM " table)]
    (Query db sql)))

(defn foreign-key
  "Fetches a field value from a foreign table by id.
   Example: (foreign-key 1 \"users\" \"name\")"
  [fkey-id fkey-table fkey-field]
  (let [sql (str "SELECT " fkey-field " FROM " fkey-table " WHERE id = ?")
        result (first (Query db [sql fkey-id]))]
    (get result (keyword fkey-field))))

(defn get-options
  "Returns a sequence of maps {:value <value-field> :label <label-fields-concatenated>} from the given table.
   Optionally sorts by the given field(s) and filters by field value.
   Example: (get-options \"users\" \"id\" [\"firstname\" \"lastname\"])
            (get-options \"users\" \"id\" \"firstname\")
            (get-options \"users\" \"id\" [\"firstname\" \"lastname\"] :sort-by \"lastname\")
            (get-options \"users\" \"id\" [\"firstname\" \"lastname\"] :sort-by [\"lastname\" \"firstname\"])
            (get-options \"users\" \"id\" [\"firstname\" \"lastname\"] :filter-field \"status\" :filter-value \"active\")"
  [table value-field label-fields & {:keys [sort-by filter-field filter-value]}]
  (let [label-fields (if (sequential? label-fields) label-fields [label-fields])
        label-sql (st/join ",' ', " label-fields)
        sort-fields (cond
                      (nil? sort-by) nil
                      (sequential? sort-by) (st/join ", " sort-by)
                      :else sort-by)
        where-clause (when (and filter-field filter-value)
                       (str " WHERE " filter-field " = ?"))
        sql (str "SELECT " value-field " AS value, CONCAT(" label-sql ") AS label FROM " table
                 where-clause
                 (when sort-fields (str " ORDER BY " sort-fields)))
        sql-params (if (and filter-field filter-value)
                     [sql filter-value]
                     sql)
        results (Query db sql-params)]
    (map #(select-keys % [:value :label]) results)))

(defn not-empty-str
  "Returns the string if it is not empty, otherwise nil."
  [s]
  (let [s (str s)]
    (when (not (st/blank? s)) s)))

(defn parse-int
  "Parses a string to integer, returns nil if not possible."
  [s]
  (try
    (Integer/parseInt (str s))
    (catch Exception _ nil)))

(defn parse-float
  "Parses a string to float, returns nil if not possible."
  [s]
  (try
    (Float/parseFloat (str s))
    (catch Exception _ nil)))

(defn parse-bool
  "Parses a string or boolean to a boolean value."
  [v]
  (cond
    (true? v) true
    (false? v) false
    (string? v) (#{"true" "1" "yes" "on"} (st/lower-case v))
    :else false))

(defn uuid
  "Generates a random UUID string."
  []
  (str (java.util.UUID/randomUUID)))

(defn slugify
  "Converts a string to a URL-friendly slug."
  [s]
  (-> s
      st/lower-case
      (st/replace #"[^a-z0-9]+" "-")
      (st/replace #"(^-|-$)" "")))

(defn update-in-if
  "Updates a value at a path only if it exists."
  [m ks f & args]
  (if (get-in m ks)
    (apply update-in m ks f args)
    m))

(defn dissoc-in
  "Dissociates a key at a nested path."
  [m [k & ks]]
  (if ks
    (let [sub (dissoc-in (get m k) ks)]
      (if (empty? sub)
        (dissoc m k)
        (assoc m k sub)))
    (dissoc m k)))

(defn deep-merge
  "Recursively merges maps."
  [& ms]
  (apply merge-with
         (fn [a b]
           (if (and (map? a) (map? b))
             (deep-merge a b)
             b))
         ms))

(defn ensure-vector
  "Ensures the value is a vector."
  [v]
  (cond
    (nil? v) []
    (vector? v) v
    (sequential? v) (vec v)
    :else [v]))

(defn safe-get
  "Safely gets a value from a nested map, returns nil if any key is missing."
  [m ks]
  (try
    (get-in m ks)
    (catch Exception _ nil)))

(defn error-response
  "Returns a Ring error response with JSON body."
  [msg & [status]]
  {:status (or status 400)
   :headers {"Content-Type" "application/json"}
   :body (json/write-str {:error msg})})

(defn now-sql
  "Returns the current timestamp as a SQL string."
  []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") (java.util.Date.)))

(defn keywordize-keys
  "Recursively transforms all map keys from strings to keywords."
  [m]
  (walk/keywordize-keys m))

(defn str->keyword
  "Converts a string or symbol to a keyword."
  [s]
  (when s (keyword (str s))))

(defn blank->nil
  "Returns nil if the string is blank, otherwise returns the string."
  [s]
  (when-not (st/blank? s) s))

(defn nil-or-empty?
  "Returns true if the value is nil or an empty collection/string."
  [v]
  (or (nil? v)
      (and (coll? v) (empty? v))
      (and (string? v) (st/blank? v))))

(defn map-values
  "Applies function f to all values in map m."
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn assoc-if
  "Associates key with value in map only if value is not nil."
  [m k v]
  (if (nil? v) m (assoc m k v)))

(defn json-response
  "Returns a Ring response with JSON body and appropriate headers."
  [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/write-str data)})

(defn redirect
  "Returns a Ring redirect response."
  [url]
  {:status 302
   :headers {"Location" url}
   :body ""})

(defn safe-parse-date
  "Parses a string to java.util.Date using the given format, returns nil if invalid."
  [s fmt]
  (try
    (.parse (java.text.SimpleDateFormat. fmt) s)
    (catch Exception _ nil)))

(defn format-date
  "Formats a java.util.Date as a string using the given format."
  [date fmt]
  (.format (java.text.SimpleDateFormat. fmt) date))

(defn html-response
  "Returns a Ring response with HTML body and appropriate headers."
  [html & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body html})

(defn text-response
  "Returns a Ring response with plain text body and appropriate headers."
  [text & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "text/plain; charset=utf-8"}
   :body text})

(defn pdf-response
  "Returns a Ring response with PDF body and appropriate headers.
   Expects pdf-bytes to be a byte array."
  [pdf-bytes & [status filename]]
  {:status (or status 200)
   :headers (merge
             {"Content-Type" "application/pdf"}
             (when filename
               {"Content-Disposition" (str "attachment; filename=\"" filename "\"")}))
   :body pdf-bytes})

(defn xml-response
  "Returns a Ring response with XML body and appropriate headers."
  [xml-str & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/xml; charset=utf-8"}
   :body xml-str})

;; ----------------------------------------------------------------------
;; Example usage and quick tests (remove or comment out in production)
(comment
  ;; Session/user helpers
  (get-session-id {:session {:user_id 1}})
  (user-level {:session {:user_id 1}})
  (user-email {:session {:user_id 1}})
  (user-name {:session {:user_id 1}})

  ;; Time formatting
  (seconds->string 90061)

  ;; Image link
  (image-link "avatar.png")

  ;; Year/month options
  (year-options "billing" "bill_date")
  (month-options "billing" "bill_date")

  ;; Foreign key lookup
  (foreign-key 1 "users" "username")

  ;; Options for select fields
  (get-options "users" "id" "firstname")
  (get-options "users" "id" ["firstname" "lastname"] :sort-by ["lastname" "firstname"])
  (get-options "users" "id" ["firstname" "lastname"] :filter-field "status" :filter-value "active")
  (get-options "employees" "id" "name" :filter-field "department" :filter-value "IT" :sort-by "name")

  ;; String and parsing helpers
  (not-empty-str "hello")
  (not-empty-str "")
  (parse-int "123")
  (parse-int "abc")
  (parse-float "3.14")
  (parse-float "abc")
  (parse-bool "true")
  (parse-bool "no")
  (uuid)
  (slugify "Hello, World! 2024")
  ;; Safe get
  (safe-get {:a {:b 1}} [:a :b])
  (safe-get {:a {:b 1}} [:a :c])
  (ensure-vector 1)
  (ensure-vector [1 2])
  (ensure-vector nil)

  ;; Safe get
  (safe-get {:a {:b 1}} [:a :b])
  (safe-get {:a {:b 1}} [:a :c])

  ;; Date/time helpers
  (now-sql)
  (safe-parse-date "2024-01-01" "yyyy-MM-dd")
  (format-date (java.util.Date.) "yyyy-MM-dd")

  ;; Map and string helpers
  (keywordize-keys {"a" 1 "b" {"c" 2}})
  (str->keyword "foo")
  (blank->nil "")
  (blank->nil "bar")
  (nil-or-empty? nil)
  (nil-or-empty? "")
  (nil-or-empty? [])
  (nil-or-empty? [1])
  (map-values inc {:a 1 :b 2})
  (assoc-if {:a 1} :b 2)
  (assoc-if {:a 1} :b nil)

  ;; Web helpers
  (json-response {:ok true})
  (json-response {:ok false} 400)
  (html-response "<h1>Hello</h1>")
  (text-response "Just some text")
  (pdf-response (byte-array 0) 200 "file.pdf")
  (xml-response "<root><ok>true</ok></root>")
  (redirect "/login"))
