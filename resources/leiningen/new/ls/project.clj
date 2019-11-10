(defproject {{name}} "0.1.0"
  :description "Change me"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [compojure "1.6.1" :exclusions [commons-codec]]
                 [hiccup "1.0.5"]
                 [lib-noir "0.9.9"]
                 [com.draines/postal "2.0.3"]
                 [cheshire "5.9.0"]
                 [clj-pdf "2.4.0" :exclusions [commons-codec]]
                 [ondrs/barcode "0.1.0"]
                 [pdfkit-clj "0.1.7" :exclusions [commons-logging commons-codec]]
                 [clj-jwt "0.1.1"]
                 [clj-time "0.15.2"]
                 [date-clj "1.0.1"]
                 [org.clojure/java.jdbc "0.7.10"]
                 [org.clojure/data.codec "0.1.1"]
                 [mysql/mysql-connector-java "8.0.18"]
                 [ring/ring-devel "1.7.1" :exclusions [commons-codec ring/ring-codec]]
                 [ring/ring-core "1.7.1" :exclusions [ring/ring-codec commons-logging commons-codec]]
                 [ring/ring-anti-forgery "1.3.0"]
                 [ring/ring-defaults "0.3.2"]]
  :main ^:skip-aot {{name}}.core
  :aot [{{name}}.core]
  :plugins [[lein-ancient "0.6.10"]
            [lein-pprint "1.1.2"]]
  :uberjar-name "{{name}}.jar"
  :target-path "target/%s"
  :ring {:handler {{name}}.core/app
         :auto-reload? true
         :auto-refresh? false}
  :resources-paths ["shared" "resources"]
  :profiles {:uberjar {:aot :all}})
