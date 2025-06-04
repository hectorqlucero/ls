(ns {{name}}.models.builder
  (:require
   [clojure.java.io :as io]
   [clojure.string :as st]
   [{{name}}.models.crud :refer [get-table-describe]]
   [{{name}}.models.routes :refer [process-grid process-dashboard process-report]]))

(defn create-path [path]
  (.mkdir (io/file path)))

(defn process-security [security]
  (cond
    (= security 1) (str "(if\n"
                        "(or\n"
                        "(= (user-level request) \"A\")\n"
                        "(= (user-level request) \"S\"))\n"
                        "(application request title ok js content)\n"
                        "(application request title ok nil \"" security-comments-1 "\"))")
    (= security 2) (str "(if (= (user-level request) \"S\")\n"
                        "(application request title ok js content)\n"
                        "(application request title ok nil \"" security-comments-2 "\"))")
    (= security 3) (str "(application request title ok js content)")))
;; Start build-grid
(defn build-grid-controller
  [options]
  (let [folder (:folder options)
        titulo (:title options)
        tabla (:table options)
        root (:root options)
        link (:link options)
        security (:secure options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)]
    (str
     "(ns " ns-root ".controller\n"
     "  (:require [{{name}}.layout :refer [application error-404]]\n"
     "            [{{name}}.models.util :refer [get-session-id user-level]]\n"
     "            [{{name}}.models.crud :refer [build-form-save build-form-delete]]\n"
     "            [" ns-root ".model :refer [get-" tabla " get-" tabla "-id]]\n"
     "            [" ns-root ".view :refer [" folder "-view " folder "-edit-view " folder "-add-view " folder "-modal-script]]))\n\n"
     "(defn " folder " [request]\n"
     "  (let [title \"" titulo "\"\n"
     "        ok (get-session-id request)\n"
     "        js nil\n"
     "        rows (get-" folder ")\n"
     "        content (" folder "-view title rows)]\n"
     (process-security security) "))\n\n"
     "(defn " folder "-edit\n"
     "  [request id]\n"
     "  (let [title \"Edit " folder "\"\n"
     "        ok (get-session-id request)\n"
     "        js (" folder "-modal-script)\n"
     "        row (get-" folder "-id id)\n"
     "        rows (get-" folder ")\n"
     "        content (" folder "-edit-view title row rows)]\n"
     "    (application request title ok js content)))\n\n"
     "(defn " folder "-save\n"
     "  [{:keys [params]}]\n"
     "  (let [table \"" tabla "\"\n"
     "        result (build-form-save params table)]\n"
     "    (if (= result true)\n"
     "      (error-404 \"Processed successfully!\" \"" link "\")\n"
     "      (error-404 \"Unable to process record!\" \"" link "\"))))\n\n"
     "(defn " folder "-add\n"
     "  [request]\n"
     "  (let [title \"New " tabla "\"\n"
     "        ok (get-session-id request)\n"
     "        js (" folder "-modal-script)\n"
     "        row nil\n"
     "        rows (get-" folder ")\n"
     "        content (" folder "-add-view title row rows)]\n"
     "    (application request title ok js content)))\n\n"
     "(defn " folder "-delete\n"
     "  [_ id]\n"
     "  (let [table \"" tabla "\"\n"
     "        result (build-form-delete table id)]\n"
     "    (if (= result true)\n"
     "      (error-404 \"Processed successfully!\" \"" link "\")\n"
     "      (error-404 \"Unable to process record!\" \"" link "\"))))\n\n")))

(defn build-grid-model
  [options]
  (let [folder (:folder options)
        tabla (:table options)
        root (:root options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)]
    (str
     "(ns " ns-root ".model\n"
     "  (:require [{{name}}.models.crud :refer [Query db]]\n"
     "            [clojure.string :as st]))\n\n"
     "(def get-" folder "-sql\n"
     "  (str\n"
     "    \"SELECT *\nFROM " tabla "\n\"))\n\n"
     "(defn get-" folder "\n"
     "  []\n"
     "  (Query db get-" folder "-sql))\n\n"
     "(def get-" folder "-id-sql\n"
     "  (str\n"
     "    \"SELECT *\nFROM " tabla "\nWHERE id = ?\n\"))\n\n"
     "(defn get-" folder "-id\n"
     "  [id]\n"
     "  (first (Query db [get-" folder "-id-sql id])))\n\n")))

(defn build-grid-form-col
  [col]
  (let [tipo (:type col)
        field (:field col)]
    (cond
      (= tipo "text")
      (str
       "(build-textarea {:label \"" (st/upper-case field) "\"\n"
       "  :id \"" field "\"\n"
       "  :name \"" field "\"\n"
       "  :rows \"3\"\n"
       "  :placeholder \"" field " here...\"\n"
       "  :required false\n"
       "  :value (:" field " row)})\n")
      (= tipo "date")
      (str
       "(build-field {:label \"" (st/upper-case field) "\"\n"
       "  :type \"date\"\n"
       "  :id \"" field "\"\n"
       "  :name \"" field "\"\n"
       "  :required false\n"
       "  :value (:" field " row)})\n")
      :else
      (str
       "(build-field {:label \"" (st/upper-case field) "\"\n"
       "  :type \"text\"\n"
       "  :id \"" field "\"\n"
       "  :name \"" field "\"\n"
       "  :placeholder \"" field " here...\"\n"
       "  :required false\n"
       "  :value (:" field " row)})\n"))))

(defn build-grid-view
  [options]
  (let [folder (:folder options)
        tabla (:table options)
        titulo (:title options)
        root (:root options)
        link (:link options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)
        data (get-table-describe tabla)
        cols (rest data)]
    (str
     "(ns " ns-root ".view\n"
     "  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]\n"
     "            [{{name}}.models.form :refer [form build-hidden-field build-field build-select build-radio build-modal-buttons build-textarea]]\n"
     "            [{{name}}.models.grid :refer [build-grid build-modal modal-script]]))\n\n"
     "(defn " folder "-view\n"
     "  [title rows]\n"
     "  (let [labels [" (apply str (map (fn [col] (str " \"" (st/upper-case (:field col)) "\"")) cols)) "]\n"
     "        db-fields [" (apply str (map (fn [col] (str " " (keyword (:field col)))) cols)) "]\n"
     "        fields (apply array-map (interleave db-fields labels))\n"
     "        table-id \"" folder "_table\"\n"
     "        args {:new true :edit true :delete true}\n"
     "        href \"" link "\"]\n"
     "    (build-grid title rows table-id fields href args)))\n\n"
     ";; Grid cache for performance\n"
     "(defonce " folder "-grid-cache (atom {}))\n\n"
     "(defn cached-" folder "-view\n"
     "  [title rows]\n"
     "  (let [cache-key [title (hash rows)]]\n"
     "    (if-let [cached (@" folder "-grid-cache cache-key)]\n"
     "      cached\n"
     "      (let [grid-html (" folder "-view title rows)]\n"
     "        (swap! " folder "-grid-cache assoc cache-key grid-html)\n"
     "        grid-html))))\n\n"
     "(defn build-" folder "-fields\n"
     "  [row]\n"
     "  (list\n"
     "    (build-hidden-field {:id \"id\"\n"
     "                        :name \"id\"\n"
     "                        :value (:id row)})\n"
     (apply str (map build-grid-form-col cols))
     "  ))\n\n"
     "(defn build-" folder "-form\n"
     "  [row]\n"
     "  (let [fields (build-" folder "-fields row)\n"
     "        href \"" link "/save\"\n"
     "        buttons (build-modal-buttons)]\n"
     "    (form href fields buttons)))\n\n"
     "(defn build-" folder "-modal\n"
     "  [title row]\n"
     "  (build-modal title row (build-" folder "-form row)))\n\n"
     "(defn " folder "-edit-view\n"
     "  [title row rows]\n"
     "  (list\n"
     "    (cached-" folder "-view \"" titulo "\" rows)\n"
     "    (build-" folder "-modal title row)))\n\n"
     "(defn " folder "-add-view\n"
     "  [title row rows]\n"
     "  (list\n"
     "    (cached-" folder "-view \"" titulo "\" rows)\n"
     "    (build-" folder "-modal title row)))\n\n"
     "(defn " folder "-modal-script\n"
     "  []\n"
     "  (modal-script))\n")))

(defn build-grid-skeleton
  "secure: 1=s/a, 2=s, 3=all"
  [options]
  (let [folder (:folder options)
        root (:root options)
        path (str root folder)]
    (create-path path)
    (spit (str path "/controller.clj") (build-grid-controller options))
    (spit (str path "/model.clj") (build-grid-model options))
    (spit (str path "/view.clj") (build-grid-view options))))

(defn build-grid
  [table]
  (build-grid-skeleton
   {:folder table
    :title (st/capitalize table)
    :table table
    :secure 1
    :link (str "/admin/" table)
    :root "src/{{name}}/handlers/admin/"})
  (process-grid table)
  (println (str "Code generated here: src/{{name}}/handlers/admin/" table)))
;; End build-grid

;; Start build-dashboard
(defn build-dashboard-controller
  [options]
  (let [folder (:folder options)
        titulo (:title options)
        tabla (:table options)
        root (:root options)
        security (:secure options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)]
    (str
     "(ns " ns-root ".controller\n"
     "  (:require [{{name}}.layout :refer [application]]\n"
     "            [{{name}}.models.util :refer [get-session-id]]\n"
     "            [" ns-root ".model :refer [get-" tabla "]]\n"
     "            [" ns-root ".view :refer [" tabla "-view]]))\n\n"
     "(defn " folder " [request]\n"
     "  (let [title \"" titulo "\"\n"
     "        ok (get-session-id request)\n"
     "        js nil\n"
     "        rows (get-" folder ")\n"
     "        content (" folder "-view title rows)]\n"
     (process-security security) "))\n\n")))

(defn build-dashboard-model
  [options]
  (let [folder (:folder options)
        tabla (:table options)
        root (:root options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)]
    (str
     "(ns " ns-root ".model\n"
     "  (:require [{{name}}.models.crud :refer [Query db]]\n"
     "            [clojure.string :as st]))\n\n"
     "(def get-" folder "-sql\n"
     "  (str\n"
     "    \"SELECT *\nFROM " tabla "\n\"))\n\n"
     "(defn get-" folder "\n"
     "  []\n"
     "  (Query db get-" folder "-sql))\n\n"
     "(def get-" folder "-id-sql\n"
     "  (str\n"
     "    \"SELECT *\nFROM " tabla "\nWHERE id = ?\n\"))\n\n"
     "(defn get-" folder "-id\n"
     "  [id]\n"
     "  (first (Query db [get-" folder "-id-sql id])))\n\n")))

(defn build-dashboard-view
  [options]
  (let [folder (:folder options)
        tabla (:table options)
        root (:root options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)
        data (get-table-describe tabla)
        cols (rest data)]
    (str
     "(ns " ns-root ".view\n"
     "  (:require [{{name}}.models.grid :refer [build-dashboard]]))\n\n"
     "(defn " folder "-view\n"
     "  [title rows]\n"
     "  (let [table-id \"" folder "_table\"\n"
     "        labels [" (apply str (map (fn [col] (str " \"" (st/upper-case (:field col)) "\"")) cols)) "]\n"
     "        db-fields [" (apply str (map (fn [col] (str " " (keyword (:field col)))) cols)) "]\n"
     "        fields (apply array-map (interleave db-fields labels))]\n"
     "    (build-dashboard title rows table-id fields)))\n")))

(defn build-dashboard-skeleton
  "secure: 1=s/a, 2=s, 3=all"
  [options]
  (let [folder (:folder options)
        root (:root options)
        path (str root folder)]
    (create-path path)
    (spit (str path "/controller.clj") (build-dashboard-controller options))
    (spit (str path "/model.clj") (build-dashboard-model options))
    (spit (str path "/view.clj") (build-dashboard-view options))))

(defn build-dashboard
  [table]
  (build-dashboard-skeleton
   {:folder table
    :title (st/capitalize table)
    :table table
    :secure 3
    :link (str "/" table)
    :root "src/{{name}}/handlers/"})
  (process-dashboard table)
  (println (str "Code generated here: src/{{name}}/handlers/" table)))
;; End build-dashboard

;; Start build-report
(defn build-report-controller
  [options]
  (let [folder (:folder options)
        titulo (:title options)
        tabla (:table options)
        root (:root options)
        security (:secure options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)]
    (str
     "(ns " ns-root ".controller\n"
     "  (:require [{{name}}.layout :refer [application]]\n"
     "            [{{name}}.models.util :refer [get-session-id]]\n"
     "            [" ns-root ".model :refer [get-" tabla "]]\n"
     "            [" ns-root ".view :refer [" tabla "-view]]))\n\n"
     "(defn " folder " [request]\n"
     "  (let [title \"" titulo "\"\n"
     "        ok (get-session-id request)\n"
     "        js nil\n"
     "        rows (get-" folder ")\n"
     "        content (" folder "-view title rows)]\n"
     (process-security security) "))\n\n")))

(defn build-report-model
  [options]
  (let [folder (:folder options)
        tabla (:table options)
        root (:root options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)]
    (str
     "(ns " ns-root ".model\n"
     "  (:require [{{name}}.models.crud :refer [Query db]]\n"
     "            [clojure.string :as st]))\n\n"
     "(def get-" folder "-sql\n"
     "  (str\n"
     "    \"SELECT *\nFROM " tabla "\n\"))\n\n"
     "(defn get-" folder "\n"
     "  []\n"
     "  (Query db get-" folder "-sql))\n\n")))

(defn build-report-view
  [options]
  (let [folder (:folder options)
        root (:root options)
        ns-root (subs (str (st/replace root #"/" ".") folder) 4)]
    (str
     "(ns " ns-root ".view\n"
     "  (:require [{{name}}.models.grid :refer [build-dashboard]]))\n\n"
     "(defn " folder "-view\n"
     "  [title rows]\n"
     "  (let [table-id \"" folder "_table\"\n"
     "        labels []\n"
     "        db-fields []\n"
     "        fields (apply array-map (interleave db-fields labels))]\n"
     "    (build-dashboard title rows table-id fields)))\n")))

(defn build-report-skeleton
  "secure: 1=s/a, 2=s, 3=all"
  [options]
  (let [folder (:folder options)
        root (:root options)
        path (str root folder)]
    (create-path path)
    (spit (str path "/controller.clj") (build-report-controller options))
    (spit (str path "/model.clj") (build-report-model options))
    (spit (str path "/view.clj") (build-report-view options))))

(defn build-report
  [table]
  (build-report-skeleton
   {:folder table
    :title (st/capitalize table)
    :table table
    :secure 3
    :link (str "/reports/" table)
    :root "src/{{name}}/handlers/reports/"})
  (process-report table)
  (println (str "Code generated here: src/{{name}}/handlers/reports/" table)))
;; End build-report

(comment
  ;; Test build-grid
  (build-grid "contactos")

  ;; Test build-grid-controller
  (build-grid-controller {:folder "contactos"
                          :title "Contactos"
                          :table "contactos"
                          :secure 1
                          :link "/admin/contactos"
                          :root "src/contactos/handlers/admin/"})

  ;; Test build-grid-model
  (build-grid-model {:folder "contactos"
                     :table "contactos"
                     :root "src/contactos/handlers/admin/"
                     :link "/admin/contactos"})

  ;; Test build-grid-form-col
  (build-grid-form-col {:type "text" :field "nombre"})
  (build-grid-form-col {:type "date" :field "fecha"})
  (build-grid-form-col {:type "other" :field "otro"})

  ;; Test build-grid-view
  (build-grid-view {:folder "contactos"
                    :table "contactos"
                    :title "Contactos"
                    :root "src/contactos/handlers/admin/"
                    :link "/admin/contactos"})

  ;; Test build-grid-skeleton
  (build-grid-skeleton {:folder "contactos"
                        :title "Contactos"
                        :table "contactos"
                        :secure 1
                        :link "/admin/contactos"
                        :root "src/contactos/handlers/admin/"})

  ;; Test build-dashboard
  (build-dashboard "contactos")

  ;; Test build-dashboard-controller
  (build-dashboard-controller {:folder "contactos"
                               :title "Contactos"
                               :table "contactos"
                               :secure 3
                               :link "/contactos"
                               :root "src/contactos/handlers/"})

  ;; Test build-dashboard-model
  (build-dashboard-model {:folder "contactos"
                          :table "contactos"
                          :root "src/contactos/handlers/"})

  ;; Test build-dashboard-view
  (build-dashboard-view {:folder "contactos"
                         :table "contactos"
                         :root "src/contactos/handlers/"
                         :link "/contactos"})

  ;; Test build-dashboard-skeleton
  (build-dashboard-skeleton {:folder "contactos"
                             :title "Contactos"
                             :table "contactos"
                             :secure 3
                             :link "/contactos"
                             :root "src/contactos/handlers/"})

  ;; Test build-report
  (build-report "contactos")

  ;; Test build-report-controller
  (build-report-controller {:folder "contactos"
                            :title "Contactos"
                            :table "contactos"
                            :secure 3
                            :link "/reports/contactos"
                            :root "src/contactos/handlers/reports/"})

  ;; Test build-report-model
  (build-report-model {:folder "contactos"
                       :table "contactos"
                       :root "src/contactos/handlers/reports/"
                       :link "/reports/contactos"})

  ;; Test build-report-view
  (build-report-view {:folder "contactos"
                      :table "contactos"
                      :root "src/contactos/handlers/reports/"
                      :link "/reports/contactos"})

  ;; Test build-report-skeleton
  (build-report-skeleton {:folder "contactos"
                          :title "Contactos"
                          :table "contactos"
                          :secure 3
                          :link "/reports/contactos"
                          :root "src/contactos/handlers/reports/"}))

