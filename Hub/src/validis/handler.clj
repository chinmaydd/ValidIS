(ns validis.handler
  (:require [compojure.api.sweet     :refer :all]
            [ring.util.http-response :refer :all]
            [validis.routes.user     :refer [user-routes]]
            [validis.routes.auth     :refer [auth-routes]]
            [validis.routes.network  :refer [network-routes]]
            [validis.routes.cis      :refer [cis-routes]]
            [validis.routes.verify   :refer [verification-routes]]))

(defapi app
  {:swagger
   {:ui "/"
    :spec "/swagger.json"
    :data {:info {:title "Validis"
                  :description "Cloud based runtime monitoring system for clinical information systems"
                  :version "0.0.1"}
           :tags [{:name "User" :description "Create, delete and update user details"}
                  {:name "Auth" :description "View authentication details for a user!"}
                  {:name "Network" :description "Create a network of CIS!"}
                  {:name "CIS" :description "CIS Info"}
                  {:name "Verify" :description "Verification for the user!"}]}}}
  ;Defines the `Validis` application instance. We specify the swagger configurations and the various routes for the given app. They are:
  ;1. User routes
  ;2. Network routes
  ;3. CIS routes
  ;4. Authentication routes
  ;5. Verification routes"
  user-routes
  network-routes
  cis-routes
  auth-routes
  verification-routes)
