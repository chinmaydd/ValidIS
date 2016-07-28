(ns validis.server
  "Server."
  (:require [org.httpkit.server :as httpkit]
            [compojure.core :as route]
            [compojure.handler :as handler]
            [validis.handler :refer [app]]
            [validis.api-routes.home :refer [home-api-routes]]))

(def site-and-api
  (route/api-routes app home-api-routes))

;; Main server function.
(defn -main [port]
  "Main server function."
  (httpkit/run-server site-and-api {:port (Integer/parseInt port) :join false})
  (println "Server started on port:" port))
