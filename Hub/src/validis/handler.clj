(ns validis.handler
  (:require [compojure.api.sweet     :refer :all]
            [ring.util.http-response :refer :all]
            [validis.routes.user     :refer [user-routes]]
            [validis.routes.auth     :refer [auth-routes]]
            [validis.routes.network  :refer [network-routes]]))

(defapi app
  {:swagger
   {:ui "/"
    :spec "/swagger.json"
    :data {:info {:title "Validis"
                  :description "Cloud based runtime monitoring system for clinical information systems"
                  :version "0.0.1"}
           :tags [{:name "User" :description "Create, delete and update user details"}
                  {:name "Auth" :description "View authentication details for a user!"}
                  {:name "Network" :description "Create a network of CIS!"}]}}}

  user-routes
  network-routes
  auth-routes
)
