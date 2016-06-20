(ns validis.server
  (:require [org.httpkit.server :as httpkit]
            [validis.handler :refer [app]]))

;; Main server function.
(defn -main [port]
  (httpkit/run-server app {:port (Integer/parseInt port) :join false})
  (println "Server started on port:" port))
