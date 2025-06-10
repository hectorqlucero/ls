(ns leiningen.new.ls
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [clojure.java.io :as io]
            [leiningen.core.main :as main])
  (:import [java.util.jar JarFile JarEntry]
           [java.io File FileOutputStream InputStream]
           [java.net URL URLDecoder]))

(def render (renderer "ls"))

(defn get-resource-paths
  "Gets all resource paths under a given prefix."
  [prefix]
  (let [resource-url (io/resource prefix)
        url-str (.toString resource-url)]
    (cond
      ;; Handle resources in jar files
      (.startsWith url-str "jar:")
      (let [jar-path (-> url-str
                        (.substring 9) ; remove "jar:file:"
                        (.split "!") first)
            jar-path (URLDecoder/decode jar-path "UTF-8")
            jar (JarFile. jar-path)
            entries (enumeration-seq (.entries jar))
            prefix-path (str prefix "/")
            matching-entries (filter #(and 
                                      (.startsWith (.getName %) prefix-path)
                                      (not (.isDirectory %)))
                                    entries)]
        (doall (map #(.getName %) matching-entries)))
      
      ;; Handle resources in filesystem
      (.startsWith url-str "file:")
      (let [file (io/file resource-url)
            base-path-len (count (.getPath file))
            files (filter #(.isFile %) (file-seq file))]
        (map #(str prefix "/" (subs (.getPath %) (inc base-path-len))) files))
      
      :else 
      (throw (IllegalArgumentException. (str "Unsupported resource URL: " resource-url))))))

(defn copy-resource
  "Copy a resource to a file."
  [resource-path dest-file]
  (io/make-parents dest-file)
  (with-open [in (io/input-stream (io/resource resource-path))
              out (FileOutputStream. dest-file)]
    (io/copy in out)))

(defn copy-resources
  "Copy resources from the classpath to destination directory."
  [resource-prefix dest-dir]
  (let [paths (get-resource-paths resource-prefix)]
    (doseq [path paths]
      (let [rel-path (subs path (inc (count resource-prefix)))
            dest-file (io/file dest-dir rel-path)]
        (copy-resource path dest-file)))))

(defn ls
  "LS web app template"
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' ls project.")
    (->files data
      ;; Admin Users Handlers
      ["src/{{sanitized}}/handlers/admin/users/controller.clj" (render "admin-users-controller.clj" data)]
      ["src/{{sanitized}}/handlers/admin/users/model.clj" (render "admin-users-model.clj" data)]
      ["src/{{sanitized}}/handlers/admin/users/view.clj" (render "admin-users-view.clj" data)]

      ;; Home Handlers
      ["src/{{sanitized}}/handlers/home/controller.clj" (render "home-controller.clj" data)]
      ["src/{{sanitized}}/handlers/home/model.clj" (render "home-model.clj" data)]
      ["src/{{sanitized}}/handlers/home/view.clj" (render "home-view.clj" data)]

      ;; Users Handlers
      ["src/{{sanitized}}/handlers/users/controller.clj" (render "users-controller.clj" data)]
      ["src/{{sanitized}}/handlers/users/model.clj" (render "users-model.clj" data)]
      ["src/{{sanitized}}/handlers/users/view.clj" (render "users-view.clj" data)]

      ;; Reports Users Handlers
      ["src/{{sanitized}}/handlers/reports/users/controller.clj" (render "reports-users-controller.clj" data)]
      ["src/{{sanitized}}/handlers/reports/users/model.clj" (render "reports-users-model.clj" data)]
      ["src/{{sanitized}}/handlers/reports/users/view.clj" (render "reports-users-view.clj" data)]

      ;; Models
      ["src/{{sanitized}}/models/cdb.clj" (render "cdb.clj" data)]
      ["src/{{sanitized}}/models/crud.clj" (render "crud.clj" data)]
      ["src/{{sanitized}}/models/form.clj" (render "form.clj" data)]
      ["src/{{sanitized}}/models/grid.clj" (render "grid.clj" data)]
      ["src/{{sanitized}}/models/routes.clj" (render "models-routes.clj" data)]
      ["src/{{sanitized}}/models/email.clj" (render "email.clj" data)]
      ["src/{{sanitized}}/models/util.clj" (render "util.clj" data)]

      ;; Routes
      ["src/{{sanitized}}/routes/proutes.clj" (render "proutes.clj" data)]
      ["src/{{sanitized}}/routes/routes.clj" (render "routes.clj" data)]

      ;; Core App
      ["src/{{sanitized}}/core.clj" (render "core.clj" data)]
      ["src/{{sanitized}}/menu.clj" (render "menu.clj" data)]
      ["src/{{sanitized}}/layout.clj" (render "layout.clj" data)]
      ["src/{{sanitized}}/migrations.clj" (render "migrations.clj" data)]
      ["src/{{sanitized}}/builder.clj" (render "builder.clj" data)]

      ;; Test
      ["test/{{sanitized}}/core_test.clj" (render "core_test.clj" data)]

      ;; Dev
      ["dev/{{sanitized}}/dev.clj" (render "dev.clj" data)]

      ;; Project files
      ["project.clj" (render "project.clj" data)]
      ["README.md" (render "README.md" data)]
      [".gitignore" (render "gitignore" data)])
    
    ;; Copy static resources
    (copy-resources "leiningen/new/ls/resources/public" (str name "/resources/public"))
    (copy-resources "leiningen/new/ls/resources/private" (str name "/resources/private"))
    (copy-resources "leiningen/new/ls/resources/migrations" (str name "/resources/migrations"))))

(comment
  (ls "contactos"))
