(ns {{name}}.table_ref
  (:require [{{name}}.models.crud :refer [db Query]]
            [{{name}}.models.util :refer [parse-int
                                          current_year]]))

;; Start get-users
(def get-users-sql
  "SELECT
   id AS value,
   CONCAT(firstname,' ',lastname) AS text
   FROM users
   ORDER BY
   firstname,lastname")

(defn get-users []
  "Regresa todos los usuarios o vacio :ex: (get-users)"
  (Query db [get-users-sql]))
;; End get-users

;; Start get-users-email
(def get-users-email-sql
  "SELECT
   email
   FROM users
   WHERE email = ?")

(defn get-users-email [email]
  "Regresa el correo del usuario o nulo"
  (first (Query db [get-users-email-sql email])))
;; End get-users-email

(defn months []
  "Regresa un arreglo de meses en español ex: (months)"
  (list
   {:value 1 :text "Enero"}
   {:value 2 :text "Febrero"}
   {:value 3 :text "Marzo"}
   {:value 4 :text "Abril"}
   {:value 5 :text "Mayo"}
   {:value 6 :text "Junio"}
   {:value 7 :text "Julio"}
   {:value 8 :text "Agosto"}
   {:value 9 :text "Septiembre"}
   {:value 10 :text "Octubre"}
   {:value 11 :text "Noviembre"}
   {:value 12 :text "Diciembre"}))

(defn years [p n]
  "Genera listado para dropdown dependiendo de p=anterioriores de este año, n=despues de este año,
   ex: (years 5 4)"
  (let [year   (parse-int (current_year))
        pyears (for [n (range (parse-int p) 0 -1)] {:value (- year n) :text (- year n)})
        nyears (for [n (range 0 (+ (parse-int n) 1))] {:value (+ year n) :text (+ year n)})
        years  (concat pyears nyears)]
    years))
