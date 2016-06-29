(ns validis.routes.network
  (:require [validis.middleware.cors :refer [cors-mw]]
            [validis.middleware.token-auth :refer [token-auth-mw]]
            [validis.middleware.authenticated :refer [authenticated-mw]]
            [validis.route-functions.network.create-network :refer [create-network-response]]
            [schema.core :as s]
            [compojure.api.sweet :refer :all])
  (:import [org.bson.types.ObjectId]))


(def network-routes
  "Specify routes for network functions"
  (context "/api/network/" []
           :tags ["Network"]

    (POST "/create/:id" {:as request}
          :return {:name String}
          :path-params [id :- String]
          :header-params [authorization :- String]
          :middleware [token-auth-mw cors-mw authenticated-mw]
          :body-params [name :- String location :- String]
          :summary "Create a new network. Requires the token to have ID of the user along with authentication."
          :description "Authorization header expects the following format 'Token {token}'"
          (create-network-response name location id)))) 
