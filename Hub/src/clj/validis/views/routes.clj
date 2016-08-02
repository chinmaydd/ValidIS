(ns validis.views.routes
  (:use validis.views.home
        compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]))

;; Current status ;)
(defroutes home-routes
  (GET "/" [] index-page)
  (route/resources "/")
  (route/not-found "Page not found!"))
