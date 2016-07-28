;; src/validis/validis/api-routes/home.clj
(ns validis.api-routes.home
  (:use [hiccup core form])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]))

(defn quick-form [& [name message error]]
  (html
   (form-to {:enctype "multipart/form-data"}
    [:post "/form-out"]
   (text-field "Hello")
   (submit-button {:class "btn" :name "submit"} "Save")
   (submit-button {:class "btn" :name "submit"} "Clone"))))

(defapi-routes home-api-routes
 (GET "/form-in" [] (quick-form))
 (POST "/form-out" [:as request] (str (request :multipart-params))))
