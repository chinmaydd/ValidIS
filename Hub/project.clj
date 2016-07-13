(defproject validis "0.0.1"
  :description "A prototype runtime monitoring system for clinical information systems. Configured to integrate with OSCAR EMR."
  :dependencies [[org.clojure/clojure        "1.8.0"]
                 ; required due to bug in `lein-ring uberwar`
                 [clj-time                   "0.12.0"]
                 [metosin/compojure-api      "1.1.4"]
                 [com.novemberain/monger     "3.0.2"]
                 [http-kit                   "2.2.0"]
                 [clj-http                   "2.2.0"]
                 [environ                    "1.0.3"]
                 [buddy                      "1.0.0"]
                 [com.draines/postal         "2.0.0"]
                 [crypto-random              "1.2.0"]
                 [ring/ring-json             "0.4.0"]]

  :plugins [[lein-cljfmt "0.5.3"]
            [lein-ancient "0.6.10"]
            [lein-bikeshed "0.3.0"]
            [jonase/eastwood "0.2.3"]
            [lein-kibit "0.1.2"]
            [lein-cloverage "1.0.6"]
            [lein-environ "1.0.2"]
            [funcool/codeina "0.3.0" :exclusions [org.clojure/clojure]]]

  :ring {:handler validis.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev [{:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [cheshire                  "5.6.3"]
                                  [ring/ring-mock            "0.3.0"]]
                    :plugins      [[lein-ring     "0.9.7"]]}
                    ;; Set in ./profiles.clj
                    :dev-env-vars]})
