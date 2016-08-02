;; src/validis/views/home.clj
(ns validis.views.home
  (:require [hiccup [page :refer [html5]]
                    [page :refer [include-js]]]
            [compojure.core :refer :all]
            [compojure.response :refer [render]]
            [clojure.java.io :as io]
            [compojure.handler :as handler]))

(defn index-page 
  "Function to render the index page."
  [req]
  (render (io/resource "index.html") req))
