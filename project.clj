(defproject users-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [metosin/compojure-api "1.1.11"]
                 [ring "1.7.1"]]
  :ring {:handler users-api.web/app}
  :profiles {:dev {:plugins [[lein-ring "0.10.0"]]}})
