(defproject sk "0.1.0"
  :description "App-name" ; Change me
  :url "http://example.com/FIXME" ; Change me - optional (no requerido)
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/data.csv "1.1.0"]
                 [compojure "1.7.1"]
                 [hiccup "1.0.5"]
                 [lib-noir "0.9.9"]
                 [com.draines/postal "2.0.5"]
                 [cheshire "5.13.0"]
                 [clj-pdf "2.7.0"]
                 [ondrs/barcode "0.1.0"]
                 [pdfkit-clj "0.1.7"]
                 [cljfmt "0.9.2"]
                 [clj-jwt "0.1.1"]
                 [clj-time "0.15.2"]
                 [date-clj "1.0.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.clojure/data.codec "0.2.0"]
                 [mysql/mysql-connector-java "8.0.33"]
                 [ragtime "0.8.1"]
                 [ring/ring-core "1.13.0"]]
  :main ^:skip-aot {{name}}.core
  :aot [{{name}}.core]
  :plugins [[lein-ancient "0.7.0"]
            [lein-pprint "1.3.2"]]
  :uberjar-name "{{name}}.jar" 
  :target-path "target/%s"
  :ring {:handler {{name}}.core
         :auto-reload? true
         :auto-refresh? false}
  :resources-paths ["shared" "resources"]
  :aliases {"migrate" ["run" "-m" "{{name}}.migrations/migrate"]
            "rollback" ["run" "-m" "{{name}}.migrations/rollback"]
            "database" ["run" "-m" "{{name}}.models.cdb/database"]
            "grid" ["run" "-m" "{{name}}.models.builder/build-grid"]
            "dashboard" ["run" "-m" "{{name}}.models.builder/build-dashboard"]
            "report" ["run" "-m" "{{name}}.models.builder/build-report"]}
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:source-paths ["src" "dev"]
                   :main {{name}}.dev}})
