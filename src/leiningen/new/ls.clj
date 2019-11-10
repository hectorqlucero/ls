(ns leiningen.new.ls
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "ls"))

(defn ls
  "LS web app template"
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' ls project.")
    (->files data
             ["src/{{sanitized}}/models/crud.clj" (render "crud.clj" data)]
             ["src/{{sanitized}}/models/cdb.clj" (render "cdb.clj" data)]
             ["src/{{sanitized}}/models/email.clj" (render "email.clj" data)]
             ["src/{{sanitized}}/models/grid.clj" (render "grid.clj" data)]
             ["src/{{sanitized}}/models/util.clj" (render "util.clj" data)]
             ["src/{{sanitized}}/routes/home.clj" (render "home.clj" data)]
             ["src/{{sanitized}}/routes/registrar.clj" (render "registrar.clj" data)]
             ["src/{{sanitized}}/proutes/ph.clj" (render "ph.clj" data)]
             ["src/{{sanitized}}/views/layout.clj" (render "layout_view.clj" data)]
             ["src/{{sanitized}}/views/home.clj" (render "home_view.clj" data)]
             ["src/{{sanitized}}/views/registrar.clj" (render "registrar_view.clj" data)]
             ["src/{{sanitized}}/core.clj" (render "core.clj" data)]
             ["src/{{sanitized}}/proutes.clj" (render "proutes.clj" data)]
             ["src/{{sanitized}}/routes.clj" (render "routes.clj" data)]
             ["src/{{sanitized}}/table_ref.clj" (render "table_ref.clj" data)]
             ["test/{{sanitized}}/core.clj" (render "core.clj" data)]
             ["project.clj" (render "project.clj" data)]
             ["README.md" (render "README.md" data)]
             [".gitignore" (render "gitignore" data)]
             "resources"
             "resources/private")))
