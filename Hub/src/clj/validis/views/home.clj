;; src/validis/views/home.clj
(ns validis.views.home
  (:require [hiccup [page :refer [html5]]
                    [page :refer [include-js]]]
            [compojure.core :refer :all]
            [compojure.response :refer [render]]
            [clojure.java.io :as io]
            [compojure.handler :as handler]))

;(defn quick-form [& [name message error]]
  ;(html
   ;(form-to {:enctype "multipart/form-data"}
    ;[:post "/form-out"]
   ;(text-field "Hello")
   ;(submit-button {:class "btn" :name "submit"} "Save")
   ;(submit-button {:class "btn" :name "submit"} "Clone"))))

(defn index-page 
  [req]
  (render (io/resource "index.html") req))

;(defroutes home-routes
 ;(GET "/form-in" [] (quick-form))
 ;(POST "/form-out" [:as request] (str (request :multipart-params))))
