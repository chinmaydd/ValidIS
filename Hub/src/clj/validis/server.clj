;; src/validis/server.clj
(ns validis.server
  "Server."
  (:require [org.httpkit.server :as httpkit]
            [compojure.core :as route]
            [compojure.handler :as handler]
            [validis.handler :refer [app]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [validis.views.routes :refer [home-routes]]))

(def site-and-api
  "Wrap the app and home routes with ring defaults"
  (wrap-defaults (route/routes app home-routes) site-defaults))

;; Main server function.
(defn -main [port]
  "Main server function."
  (httpkit/run-server site-and-api {:port (Integer/parseInt port) :join false})
  (println "Server started on port:" port))
