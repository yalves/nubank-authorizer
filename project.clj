(defproject nubank-authorizer "0.1.0-SNAPSHOT"
  :description "A challenge for nubank recruiting process"
  :url "http://example.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "1.0.0"]]
  :main ^:skip-aot nubank-authorizer.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
