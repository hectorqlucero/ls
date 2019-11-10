(ns {{name}}.models.util
  (:require [{{name}}.models.crud :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-jwt.core :refer :all]
            [clojure.string :refer [join]]
            [date-clj :as d]
            [noir.session :as session])
  (:import java.text.SimpleDateFormat
           [java.util Calendar UUID]))

;;Example here: (t/from-time-zone (t/now) tz) -> gives me a joda datetime with correct timezone
;;(def halloween-2016 (t/date-time 2016 10 31 18 0 0))
;;(def hal-central (t/from-time-zone halloween-2016 (t/time-zone-for-id "US/Centarl"))
;;(def tz-london (t/time-zone-for-id "Europe/London"))
;;Convert to other timezone: (t/to-time-zone hal-central tz-london)
;;Today local date no timezone: (t/today)
;;Month - day - year: (t/month (t/today)) - (t/day (t/today)) - (t/year (t/today))
;;(def hector-birthdate (t/local-date 1957 2 7))
;;Hector birthdate: (t/local-date (t/year (t/today)) (t/month hectors-birthdate) (t/day hectors-birthdate))
;;3 days from now: (t/plus (t/now) (t/days 3))
;;3 days ago: (-> 3 t/days t/ago)
;;6 hours from now: (-> 6 t/hours t/from-now)
;;(t/beforce? (t/now) (-> 3 t/years t/from-now)) ... can user after? as opposite of this
;;(def d1 (t/date-time 2015 4 5))
;;(def d2 (t/plus (t/date-time 2014 4 5) (t/years 1)))
;;so:  (= d1 d2)  -> answer is true
;;time in minutes:  (t/in-minutes (t/hours 72))
;;time in days: (t/in-days (t/hours 72))
;;time in weeks: (t/in-weeks (t/hours 72)) ... Note: can't use months because they are relative
;;(def this-week (t/interval (t/now) (t/plus (t/now) (t/days 7))))
;;time in years,months,weeks,days,hour,minutes,seconds: (t/in-days this-week) -> 7 is the answer
;;last week: (def last-week (t/interval (t/minus (t/now) (t/days 7)) (t/now)))
;;abuts?: (t/abuts? last-week this-week)
;;within: (t/within? this-week (t/plus (t/now) (t/days 3))) -> you get true or false
;;overlaps?: (t/overlaps? this-week last-week)
;;extend: (t/extend this-week (t/months 6))

(def tz (t/time-zone-for-id (:tz config)))

(def internal-date-parser (f/formatter tz "YYYY-MM-dd" "MM/dd/YYYY"))

(def external-date-parser (f/formatter tz "MM/dd/YYYY" "YYYY-MM-dd"))

(def external-ld-parser (f/formatter tz "E M d Y" "YYYY-MM-dd"))

(def external-dt-parser (f/formatter tz "E M d Y  h:m a" "YYYY-MM-dd"))

(def internal-time-parser (f/formatter tz "HH:mm:ss" "H:k:s"))

(def external-time-parser (f/formatter tz "hh:mm:ss a" "H:k:s"))

(defn get-base-url [request]
  (str (subs (str (:scheme request)) 1) "://" (:server-name request) ":" (:server-port request)))

(defn get-reset-url [request token]
  (str (get-base-url request) "/reset_password/" token))

;; Start jwt token
(defn create-token [k]
  "Creates jwt token with 10 minutes expiration time"
  (let [data {:iss k
              :exp (t/plus (t/now) (t/minutes 10))
              :iat (t/now)}]
    (-> data jwt to-str)))

(defn decode-token [t]
  "Decodes jwt token"
  (-> t str->jwt :claims))

(defn verify-token [t]
  "Verifies that token is good"
  (-> t str->jwt verify))

(defn check-token [t]
  "Checks if token verifes and it's not expired, returns id or nil"
  (let [token (decode-token t)
        exp (:exp token)
        cexp (c/to-epoch (t/now))
        token-id (:iss token)]
    (if (and (verify-token t) (> exp cexp))
      token-id
      nil)))
;; End jwt token

(defn get-session-id []
  (try
    (if (session/get :user_id) 1 0)
    (catch Exception e 0)))

(defn current_date []
  "Get current date formatted MM/dd/YYYY"
  (f/unparse external-date-parser (t/now)))

(defn current_date_long []
  "Get current date formatted Sat 11 12 2016"
  (f/unparse external-ld-parser (t/now)))

(defn current_date_time []
  "Get current date formatted Sat 11 12 2016 at 18:20 PM"
  (f/unparse external-dt-parser (t/now)))

(defn current_time []
  "Get simple date formatted time h:m:s a"
  (f/unparse external-time-parser (t/now)))

(defn current_time_internal []
  "Get current time formatted H:k:s"
  (f/unparse internal-time-parser (t/now)))

(defn previous_year []
  (t/year (t/minus (t/now) (t/years 1))))

(defn current_year []
  "GEt simple date formatted year"
  (t/year (t/from-time-zone (t/now) tz)))

(defn current_month []
  "Get simple date formatted month :MM"
  (t/month (t/from-time-zone (t/now) tz)))

(defn current_day []
  "Get Simple Date formatted :dd"
  (t/day (t/from-time-zone (t/now) tz)))

(defn get_date_external [d]
  "Convert date instance to external MM/dd/YYYY"
  (f/unparse external-date-parser d))

(defn to-utc [dt]
  (t/to-time-zone dt t/utc))

(defn get-monday []
  (d/format-date (d/monday) "MM/dd/yyyy"))

(defn get-previous-monday []
  (d/format-date (-> (d/monday) (d/subtract 1 :week)) "MM/dd/yyyy"))

(defn get-tuesday []
  (d/format-date (d/tuesday) "MM/dd/yyyy"))

(defn get-wednesday []
  (d/format-date (d/wednesday) "MM/dd/yyyy"))

(defn get-thursday []
  (d/format-date (d/thursday) "MM/dd/yyyy"))

(defn get-friday []
  (d/format-date (d/friday) "MM/dd/yyyy"))

(defn get-saturday []
  (d/format-date (d/saturday) "MM/dd/yyyy"))

(defn get-sunday []
  (d/format-date (d/following :sunday) "MM/dd/yyyy"))

(defn get-next-monday []
  (d/format-date (-> (d/monday) (d/add 1 :week)) "MM/dd/yyyy"))

(defn get-next-tuesday []
  (d/format-date (-> (d/tuesday) (d/add 1 :week)) "MM/dd/yyyy"))

(defn get-next-wednesday []
  (d/format-date (-> (d/wednesday) (d/add 1 :week)) "MM/dd/yyyy"))

(defn get-next-thursday []
  (d/format-date (-> (d/thursday) (d/add 1 :week)) "MM/dd/yyyy"))

(defn get-next-friday []
  (d/format-date (-> (d/friday) (d/add 1 :week)) "MM/dd/yyyy"))

(defn get-next-saturday []
  (d/format-date (-> (d/saturday) (d/add 1 :week)) "MM/dd/yyyy"))

(defn get-previous-saturday []
  (d/format-date (-> (d/saturday) (d/subtract 1 :week)) "MM/dd/yyyy"))

(defn get-next-sunday []
  (d/format-date (-> (d/sunday) (d/add 2 :week)) "MM/dd/yyyy"))

(defn week-of-year []
  (t/week-number-of-year (t/now)))

(defn next-week-of-year []
  (+ (t/week-number-of-year (t/now)) 1))

(defn week-of-year-date [date]
  (let [cal (Calendar/getInstance)]
    (.setTime cal (.toDate date))
    (- (.get cal Calendar/WEEK_OF_YEAR) 1)))

(defn get-weekday-long [n]
  "0=sunday....6=Saturday"
  (nth (d/names :week-days) n))

(defn validate-email [email]
  (let [pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (and (string? (clojure.string/lower-case email)) (re-matches pattern (clojure.string/lower-case email)))))

(defn merge-maps [k & m]
  "k = keyword ex. :id, m= list of maps.  ex. (merge-maps :id a b)"
  (map #(apply merge %) (vals (group-by k (apply concat m)))))

(defn parse-int [s]
  "Attempt to convert to integer or on error return nil or itself if it's already an integer"
  (try
    (Integer. s)
    (catch Exception _ (if (integer? s) s nil))))

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn contains-many? [m & ks]
  "Checks if multiple keys exist in map.  It can be a single key also"
  (every? true? (map #(contains? m %) ks)))

(defn commify [s]
  "takes a string as a number and adds commas a separators"
  (let [matcher (re-matcher #"(\d\d\d)(?=\d)(?!\d*\.)" (apply str (reverse s)))]
    (apply str (reverse (.replaceAll matcher "$1,")))))

(defn spl [n c p]
  "n=number,c=pad number,p=padding str"
  (loop [s (str n)]
    (if (< (.length s) c)
      (recur (str p s))
      s)))

(defn spr [n c p]
  "n=number,c=zeropad number"
  (loop [s (str n)]
    (if (< (.length s) c)
      (recur (str s p))
      s)))

(defn zpl [n c]
  "n=number,c=zeropad number"
  (loop [s (str n)]
    (if (< (.length s) c)
      (recur (str "0" s))
      s)))

(defn zpr [n c]
  "n=number,c=zeropad number"
  (loop [s (str n)]
    (if (< (.length s) c)
      (recur (str s "0"))
      s)))

(def allowed-mimetypes-image ["image/png" "image/jpeg" "image/gif"]) ; MIME types allowed for 'image'
(def allowed-mimetypes-document ["application/pdf" "text/csv"]) ; MIME types allowed for 'file'
(def max-filesize (* 1000 1000 7))                          ; 5MB max filesize allowed on uploads

(defn verify-file [file file-type]
  "Verify that a file matches MIME type, and doesn't exceed the max file size"
  (and (in? (cond
              (= file-type "file")  allowed-mimetypes-document
              (= file-type "image") allowed-mimetypes-image) (:content-type file))
       (<= (:size file) max-filesize)
       (not (= 0 (:size file)))))

(defn file-error [file file-type]
  "Returns an error to let the user know if their file was too large or the invalid type"
  (if (not (= 0 (:size file)))
    (cond
      (> (:size file) max-filesize) {:error (str "File not uploaded: Filesize maximum is: "
                                                 (/ max-filesize (* 1000 1000)) "MB")}
      (not (in? (cond
                  (= file-type "file")  allowed-mimetypes-document
                  (= file-type "image") allowed-mimetypes-image) (:content-type file)))
      {:error "File not uploaded: Invalid file type"})))

(defn date-to-internal [d]
  (if (instance? java.util.Date d)
    (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") d))
    nil))

(defn date-to-external [d]
  (if (instance? java.util.Date d)
    (str (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") d))
    nil))

(defn format-date-internal [s]
  "Convert a MM/dd/yyyy format date to yyyy-MM-dd format using a string as a date
   eg. 02/01/1997 -> 1997-02-01"
  (if (not-empty s)
    (try
      (do
        (.format
          (SimpleDateFormat. "yyyy-MM-dd")
          (.parse
            (SimpleDateFormat. "MM/dd/yyyy") s)))
      (catch Exception e nil))
    nil))

(defn format-date-external [s]
  "Convert a yyyy-MM-dd format date to MM/dd/yyyy format using a string as a date
   eg. 1997-02-01 -> 02/01/1997"
  (if (not-empty s)
    (try
      (do
        (.format
          (SimpleDateFormat. "MM/dd/yyyy")
          (.parse
            (SimpleDateFormat. "yyyy-MM-dd") s)))
      (catch Exception e nil))
    nil))

(defn format-date-external-mx [s]
  "Convert a yyyy-MM-dd format date to dd/MM/yyyy format using a string as a date
   eg. 1997-02-01 -> 01/02/1997"
  (if (not-empty s)
    (try
      (do
        (.format
          (SimpleDateFormat. "dd/MM/yyyy")
          (.parse
            (SimpleDateFormat. "yyyy-MM-dd") s)))
      (catch Exception e nil))
    nil))

(defn get_username [username]
  (let [row       (first (Query db ["select firstname,lastname from users where username = ?" username]))
        user_name (str (:firstname row) " " (:lastname row))]
    user_name))

(defn get-active-flag [row]
  "Extract the :active keyword from a row"
  (:active row))

(defn t1 []
  "Get a timezone aware date instance of now"
  (t/to-time-zone (t/now) tz))

(defn t2 [d]
  "Get a timezone aware date instance from a valid joda date ex. d = (t/date-time 2016 11 12 18 4"
  (t/to-time-zone d tz))

(defn today-date []
  "Get today simple date formatted MM/dd/yyyy"
  (f/unparse external-date-parser (t/now)))

(defn today-time []
  (str (t/hour (t1)) ":" (t/minute (t1))))

(defn today-internal []
  "internal today date formatted YYYY-MM-dd"
  (str (t/year (t1)) "-" (t/month (t1)) "-" (t/day (t1))))

(defn today-month []
  "get month with timezone"
  (str (format "%02d" (t/month (t1)))))

(defn today-day []
  "get month with timezone"
  (str (format "%02d" (t/day (t1)))))

(defn today-year []
  "get month with timezone"
  (str (t/year (t1))))

(defn get-month-desc [m]
  (nth (d/names :months :long) (- m 1)))

(defn date-internal [month day year]
  "Internal date formatted YYYY-MM-dd - input macc MM dd YYYY"
  (let [d (t/date-time year month day (t/hour (t/now)) (t/minute (t/now)))]
    (str (t/year (t2 d)) "-" (t/month (t2 d)) "-" (t/day (t2 d)))))

(defn date-external [month day year]
  "Internal date formatted YYYY-MM-dd - input macc MM dd YYYY"
  (let [d (t/date-time year month day (t/hour (t/now)) (t/minute (t/now)))]
    (str (t/month (t2 d)) "/" (t/day (t2 d)) "/" (t/year (t2 d)))))

(defn date-instance [d]
  "get a date i.e MM/dd/YYY and return a date tiemzone aware instance"
  (let [v (clojure.string/split d #"/")
        d (t/date-time (parse-int (nth v 2)) (parse-int (nth v 0)) (parse-int (nth v 1)) (t/hour (t/now)) (t/minute (t/now)))]
    d))

(defn add-days [d n]
  "Adds days to a date and returns date d = MM/dd/YYYY n = number of days"
  (let [date (date-instance d)]
    (t/plus date (t/days n))))

(defn get-month [d]
  (t/month d))

(defn get-day [d]
  (t/day d))

(defn get-year [d]
  (t/year d))

(defn today-dow []
  "Get today's day or week"
  (d/week-day (d/today)))

(defn get-dosing-dow [d]
  "Get day of week that matches dosing table. d=date instance"
  (try
    (let [month (get-month d)
          day   (get-day d)
          year  (get-year d)
          date  (d/date :month (- month 1) :day day :year year)
          dow   (d/week-day date)]
      (if (= dow 1) 7 (- dow 1)))
    (catch Exception e nil)))

(defn process-record [table postvars id]
  (let [processed (Save db (keyword table) postvars ["id = ?" id])]
    (if (nil? processed)
      0
      (if (> processed 0) 1 0))))

(defn get-description [tablename fieldname idname id]
  (let [id           (str "'" id "'")
        sql          (str "select " fieldname " from " tablename " where " idname " = " id)
        row          (first (Query db [sql]))
        return-value ((keyword fieldname) row)]
    return-value))

(defn FLAGS []
  (first (Query db ["select * from flags where id=?" "setup"])))

(defn IS_SYSTEM [user-id]
  "Check if a user-id is a system user"
  (let [row (Query db ["select role_id from user_role where user_id=? and role_id=1 limit 1" user-id])]
    (if (get-in (first row) [:role_id]) true false)))

(defn audit [username ip controller function args]
  (if (and
        (not-empty controller)
        (not-empty function))
    (let [postvars {:username   username
                    :date       (today-internal)
                    :time       (today-time)
                    :ip         ip
                    :controller controller
                    :function   function
                    :arguments  args}]
      (Insert db :user_audit postvars))))

(defn fix-id [v]
  (if (clojure.string/blank? v) nil v))

(defn fix-hour [v]
  (str v ":00"))

(defn user-level []
  (let [id   (get-session-id)
        type (if (nil? id)
               nil
               (:level (first (Query db ["select level from users where id = ?" id]))))]
    type))

(defn user-email []
  (let [id    (get-session-id)
        email (if (nil? id)
                nil
                (:username (first (Query db ["select username from users where id = ?" id]))))]
    email))

(defn get-photo-val [table-name field-name id-name id-value]
  (if (or
        (nil? table-name)
        (nil? field-name)
        (nil? id-name)
        (nil? id-value))
    nil
    ((keyword field-name) (first (Query db (str "SELECT " field-name " FROM " table-name " WHERE " id-name " = " id-value))))))

(defn build-photo-html [photo-val uuid]
  (if (or
        (nil? photo-val)
        (nil? uuid))
    nil
    (str "<img src='" (:path config)  photo-val "?t=" uuid "' onError=\"this.src='/images/placeholder_profile.png'\" width='95' height='71'></img>")))

(defn get-photo [table-name field-name id-name id-value]
  (let [photo-val   (get-photo-val table-name field-name id-name id-value)
        uuid        (str (UUID/randomUUID))
        placeholder "<img src='/images/placeholder_profile.png' width='95' height='71'></img>"
        photo       (build-photo-html photo-val uuid)]
    (if (empty? photo-val) placeholder photo)))

(defn get-thumbnail [photo-val]
  (let [uuid (str (UUID/randomUUID))
        placeholder "<img src='/images/placeholder_profile.png' width='42' height='42'></img>"
        photo (str "<img src='/uploads/patient_images/" photo-val "?t=" uuid "'  onError=\"this.src='/images/placeholder_profile.png'\" width='42' height='42'></img>")]
    (if (empty? photo-val) placeholder photo)))

(defn capitalize-words
  "Captitalizar todas las palabras en una hilera"
  [s]
  (->> (clojure.string/split (str s) #"\b")
       (map clojure.string/capitalize)
       (clojure.string/join)))

(defn get-month-name [month]
  (cond
    (= month 1) "Enero"
    (= month 2) "Febrero"
    (= month 3) "Marzo"
    (= month 4) "Abril"
    (= month 5) "Mayo"
    (= month 6) "Junio"
    (= month 7) "Julio"
    (= month 8) "Agosto 4"
    (= month 9) "Septiembre"
    (= month 10) "Octubre"
    (= month 11) "Noviembre"
    (= month 12) "Diciembre"))

(defn get-counter []
  (let [row (first (Query db "select numero_registro from contador where id = 'C'"))
        numero-registro (:numero_registro row)
        next-numero (parse-int (inc numero-registro))
        values {:id "C"
                :numero_registro (str next-numero)}
        result (Update db :contador values ["id = ?" "C"])]
    next-numero))

(defn deep-merge [& maps]
  "Merge maps recursively"
  (apply merge-with deep-merge maps))

(defn- deprecated? [method]
  (.isAnnotationPresent method java.lang.Deprecated))

(defn- method-description [method]
  (join " "
        [(.getName method)
         (java.util.Arrays/toString (.getParameterTypes method))
         "->"
         (.getReturnType method)]))

(defn jmethods
  "Returns a sequence of all public java methods available on a given class,
   including the methods inherited from parent(s)."
  ([clazz]
   (jmethods clazz false))
  ([clazz include-deprecated?]
   (->> (:methods (bean clazz))
        (filter #(or include-deprecated?
                     (not (deprecated? %))))
        (sort-by #(.getName %))
        (map method-description))))

(defn create-categorias [rows]
  (map (fn [cid]
         {:carreras_id cid
          :categorias_id cid
          :categoria (get-description "categorias" "descripcion" "id" cid)}) (into '() (into #{} (map #(str (:categorias_id %)) rows)))))

(defn calculate-speed [distance seconds]
  (let [hours (/ (parse-int seconds) 3600.0)
        speed (/ (/ (parse-int distance) 1000) hours)]
    (format "%.3f" speed)))

(defn create-carreras-categorias []
  "This is to create carreras_categorias example"
  (doseq [item (Query db "SELECT * FROM categorias")]
    (doseq [sitem (Query db "SELECT * FROM carreras")]
      (let [carreras_id   (str (:id sitem))
            categorias_id (str (:id item))
            status        "T"
            crow          (first (Query db ["SELECT * FROM carreras_categorias WHERE carreras_id = ? AND categorias_id = ?" carreras_id categorias_id]))
            id            (:id crow)
            postvars      (if (seq crow)
                            {:id (str id)
                             :carreras_id (str (:carreras_id crow))
                             :categorias_id (str (:categorias_id crow))
                             :status (str (:status crow))}
                            {:id            (str id)
                             :carreras_id   carreras_id
                             :categorias_id categorias_id
                             :status        status})]
        (Save db :carreras_categorias postvars ["id = ?" id])))))

;; Start update-inventory
(def recibido-sql
  "SELECT
   CAST(sum(num_recibido) as SIGNED) as total
   FROM compras
   WHERE
   producto_id = ?")

(def enviado-sql
  "SELECT
   CAST(sum(enviado_numero) as SIGNED) as total
   FROM orders
   WHERE
   producto_id = ?")

(def productos-row-sql
  "SELECT
   *
   FROM productos
   WHERE
   id = ?")

(defn get-productos-row [producto_id]
  (first (Query db [productos-row-sql producto_id])))

(defn get-inventory-inicio [producto_id]
  (let [row (get-productos-row producto_id)
        inv_inicio (or
                     (parse-int
                       (:inv_inicio row)) 0)]
    inv_inicio))

(defn get-inventory-recibido [producto_id]
  (or
    (parse-int
      (:total
        (first
          (Query db [recibido-sql producto_id])))) 0))

(defn get-inventory-enviado [producto_id]
  (or
    (parse-int
      (:total
        (first
          (Query db [enviado-sql producto_id])))) 0))

(defn update-inventory [producto_id]
  (if-not (nil? producto_id)
    (let [inv_inicio (get-inventory-inicio producto_id)
          recibido (get-inventory-recibido producto_id)
          enviado (get-inventory-enviado producto_id)
          inv_en_mano (- (+ inv_inicio recibido) enviado)
          postvars {:inv_recibido (str recibido)
                    :inv_enviado (str enviado)
                    :inv_en_mano (str inv_en_mano)}]
      (Update db :productos postvars ["id =?" (str producto_id)]))))

(defn update-all-inventory []
  (let [rows (Query db "SELECT id FROM productos")]
    (doseq [row rows]
      (future (update-inventory (:id row))))))
;; End update-inventory

;; Start hiccup stuff
(defn build-field [label options]
  "label options map, including id, name, class, data-options, validType etc..."
  [:div.form-group.col-10
   [:label [:span label]]
   [:input options]])

(defn build-button [label options]
  "label options map, including id,href,class etc..."
  [:div.form-group.col-10
   [:a options label]])

(defn build-table-field [label options & styler]
  "label, data-options and styler as optional"
  [:th {:data-options options :styler (first styler)} label])

(defn build-toolbar [& extra]
  [:div#toolbar
   [:a {:href "javascript:void(0)"
        :class "easyui-linkbutton"
        :data-options "iconCls: 'icon-add',plain: true"
        :onclick "defaultGridNewItem()"} "Crear"]
   [:a {:href "javascript:void(0)"
        :class "easyui-linkbutton"
        :data-options "iconCls: 'icon-edit',plain: true"
        :onclick "defaultGridEditItem({})"} "Editar"]
   [:a {:href "javascript:void(0)"
        :class "easyui-linkbutton"
        :data-options "iconCls: 'icon-remove',plain: true"
        :onclick "defaultGridDeleteItem()"} "Remover"]
   extra
   [:div {:style "float: right"}
    [:input {:id "datagrid-searchbox"
             :class "easyui-textbox"
             :data-options "prompt: 'Busqueda'"
             :title "Buscar"}]]])

(defn build-dialog-buttons []
  [:div#dlg-buttons
   [:a {:href "javascript:void(0)"
        :class "easyui-linkbutton c6"
        :style "margin-right:5px;"
        :data-options "iconCls: 'icon-ok'"
        :onclick "defaultGridSaveItem()"} "Postear"]
   [:a {:href "javascript:void(0)"
        :class "easyui-linkbutton"
        :data-options "iconCls: 'icon-cancel'"
        :onclick "defaultGridDialogClose()"} "Cancelar"]])

(defn build-table [title url fields]
  [:table#dg.easyui-datagrid.datagrid-searchable
   {:style "width: 100%"
    :title title
    :data-options 
    (str
    "url: '" url "',
     toolbar: '#toolbar',
     queryParams: {'__anti-forgery-token':token},
     pagination: true,
     rownumbers: true,
     nowrap: true,
     autoRowHeight: false,
     fitColumns: true,
     autoSizeColumns: true,
     singleSelect: true")}
   [:thead
    [:tr
     fields]]])

(defn build-dialog [title fields]
  [:div.dlg.easyui-dialog {:title title
                           :closed "true"
                           :buttons "#dlg-buttons"
                           :style "width:480px;height:auto;padding:10px 20px"}
   [:form.fm {:enctype "multipart/form-data"
              :method "post"
              :style "width:100%;background-color:white;margin-bottom:10px;align:center;"}
    fields
    (build-dialog-buttons)]])
;; End hiccup stuff
