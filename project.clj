(defproject validis "0.1.0"
  :description "A prototype runtime monitoring system for clinical information systems. Configured to integrate with OSCAR EMR."
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.11.0"] ; required due to bug in `lein-ring uberwar`
                 [metosin/compojure-api "1.1.1"]
                 [com.novemberain/monger "3.0.2"]
                 [buddy "1.0.0"]
                 ]
  :ring {:handler validis.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [cheshire "5.5.0"]
                                  [ring/ring-mock "0.3.0"]]
                   :plugins [[lein-ring "0.9.7"]]}})
