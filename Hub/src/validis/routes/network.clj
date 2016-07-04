(ns validis.routes.network
  (:require [validis.middleware.cors                        :refer [cors-mw]]
            [validis.middleware.token-auth                  :refer [token-auth-mw]]
            [validis.middleware.authenticated               :refer [authenticated-mw]]
            [validis.route-functions.network.create-network :refer [create-network-response]]
            [validis.route-functions.network.modify-network :refer [modify-network-response]]
            [validis.route-functions.network.delete-network :refer [delete-network-response]]
            [compojure.api.sweet                            :refer :all])
  (:import [org.bson.types.ObjectId]))


(def network-routes
  "Specify routes for network functions"
  (context "/api/network" []
           :tags ["Network"]

    ;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Network CRUD routes ;;
    ;;;;;;;;;;;;;;;;;;;;;;;;;
           
    (POST "/"            {:as request}
          :return        {:name String :network-id String}
          :header-params [authorization :- String]
          :middleware    [token-auth-mw cors-mw authenticated-mw]
          :body-params   [name :- String location :- String]
          :summary       "Create a new network. Requires the token to have ID of the user along with authentication."
          :description   "Authorization header expects the following format 'Token {token}'"
          (create-network-response request name location))
    
    (PATCH "/:network-id"     {:as request}
          :path-params        [network-id :- String]
          :body-params        [{name :- String ""} {location :- String ""}]
          :header-params      [authorization :- String]
          :return             {:nwid String :name String}
          :middleware         [token-auth-mw cors-mw authenticated-mw]
          :summary            "Modifies the existing network information. Requires user token and the network must belong to the user"
          :description        "Authorization header expects the following format 'Token {token}'"
          (modify-network-response request name location network-id))

    (DELETE "/:network-id"     {:as request}
            :path-params       [network-id :- String]
            :header-params     [authorization :- String]
            :return            {:message String}
            :middleware        [token-auth-mw cors-mw authenticated-mw]
            :summary           "Used to delete the existing network. Requires user token and the network must belong to the user"
            :description       "Authorization header expects the following format 'Token {token}'"
            (delete-network-response request network-id))))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Network CIS interaction routes ;;
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
