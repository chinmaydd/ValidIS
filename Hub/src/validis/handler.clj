(ns validis.handler
  "App request handler!"
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [validis.api-routes.user :refer [user-api-routes]]
            [validis.api-routes.auth :refer [auth-api-routes]]
            [validis.api-routes.network :refer [network-api-routes]]
            [validis.api-routes.cis :refer [cis-api-routes]]
            [validis.api-routes.verify :refer [verification-api-routes]]))

(defapi app
  {:swagger
   {:ui "/api-docs"
    :spec "/swagger.json"
    :data {:info {:title "Validis"
                  :description "Cloud based runtime monitoring system for clinical information systems"
                  :version "0.0.1"}
           :tags [{:name "User" :description "Create, delete and update user details"}
                  {:name "Auth" :description "View authentication details for a user!"}
                  {:name "Network" :description "Create a network of CIS!"}
                  {:name "CIS" :description "CIS Info"}
                  {:name "Verify" :description "Verification for the user!"}]}}}
  ;Defines the `Validis` application instance. We specify the swagger configurations and the various api-routes for the given app. They are:
  ;1. User api-routes
  ;2. Network api-routes
  ;3. CIS api-routes
  ;4. Authentication api-routes
  ;5. Verification api-routes
  user-api-routes
  network-api-routes
  cis-api-routes
  auth-api-routes
  verification-api-routes)
