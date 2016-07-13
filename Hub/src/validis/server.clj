(ns validis.server
  "Server."
  (:require [org.httpkit.server :as httpkit]
            [validis.handler :refer [app]]))

;; Main server function.
(defn -main [port]
  "Main server function."
  (httpkit/run-server app {:port (Integer/parseInt port) :join false})
  (println "Server started on port:" port))
