(ns {{name}}.models.util
  (:require
   [{{name}}.models.crud :refer [config db Query]]
   [clojure.core :refer [random-uuid]]))

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
    minutes-desc))

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

(comment
  ;; Usage now requires passing the request map:
  ;; (user-level request)
  ;; (user-email request)
  ;; (user-name request)
  (month-options "billing" "bill_date")
  (year-options "billing" "bill_date")
  (seconds->string 90061))
