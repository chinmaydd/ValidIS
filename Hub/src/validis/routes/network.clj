;; src/routes/network.clj
(ns validis.routes.network
  (:require
    ;; Middleware inclusions
    [validis.middleware.cors                                  :refer [cors-mw]]
    [validis.middleware.token-auth                            :refer [token-auth-mw]]
    [validis.middleware.authenticated                         :refer [authenticated-mw]]
    [validis.middleware.owner-auth                            :refer [owner-auth-mw]]

    ;; Route function inclusions
    [validis.route-functions.network.create-network           :refer [create-network-response]]
    [validis.route-functions.network.modify-network           :refer [modify-network-response]]
    [validis.route-functions.network.delete-network           :refer [delete-network-response]]
    [validis.route-functions.network.add-cis                  :refer [add-cis-response]]
    [validis.route-functions.network.remove-cis               :refer [remove-cis-response]]
    [validis.route-functions.network.add-user-to-network      :refer [add-user-to-network-response]]
    [validis.route-functions.network.remove-user-from-network :refer [remove-user-from-network-response]]

    ;; Utility libs
    [compojure.api.sweet                                      :refer :all]))


(def network-routes
  "Specify routes for network functions"
  (context "/api/network" []
           :tags ["Network"]
           
           ;;;;;;;;;;;;;;;;;;;;;;;;;
           ;; Network CRUD routes ;;
           ;;;;;;;;;;;;;;;;;;;;;;;;;

           (POST "/"            {:as request}
                 :return        {:network-id String}
                 :header-params [authorization :- String]
                 :middleware    [token-auth-mw cors-mw authenticated-mw]
                 :body-params   [name :- String location :- String]
                 :summary       "Create a new network. Requires the token to have ID of the user along with authentication."
                 :description   "Authorization header expects the following format 'Token {token}'"
                 (create-network-response request name location))

           (PATCH "/:network-id"      {:as request}
                  :path-params        [network-id :- String]
                  :body-params        [{name :- String ""} {location :- String ""}]
                  :header-params      [authorization :- String]
                  :return             {:network-id String}
                  :middleware         [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
                  :summary            "Modifies the existing network information. Requires user token and the network must belong to the user"
                  :description        "Authorization header expects the following format 'Token {token}'"
                  (modify-network-response name location network-id))

           (DELETE "/:network-id"     {:as request}
                   :path-params       [network-id :- String]
                   :header-params     [authorization :- String]
                   :return            {:message String}
                   :middleware        [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
                   :summary           "Used to delete the existing network. Requires user token and the network must belong to the user"
                   :description       "Authorization header expects the following format 'Token {token}'"
                   (delete-network-response network-id))

           ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
           ;; Network CIS interaction routes ;;
           ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

           (POST "/:network-id/cis/:cis-id" {:as request}
                 :path-params   [network-id :- String cis-id :- String]
                 :header-params [authorization :- String]
                 :return        {:message String}
                 :middleware    [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
                 :summary       "Used to add a CIS to an existing network"
                 :description   "Authorization header expects the following format 'Token {token}'"
                 (add-cis-response network-id cis-id))

           (DELETE "/:network-id/cis/:cis-id" {:as request}
                   :path-params   [network-id :- String cis-id :- String]
                   :header-params [authorization :- String]
                   :return        {:message String}
                   :middleware    [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
                   :summary       "Used to remove a CIS from a network"
                   :description   "Authorization header expects the following format 'Token {token}'"
                   (remove-cis-response network-id cis-id))

           ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
           ;; Network User interaction routes ;;
           ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

           (POST "/:network-id/user/:user-id" {:as request}
                 :path-params   [network-id :- String user-id :- String]
                 :header-params [authorization :- String]
                 :return        {:message String}
                 :middleware    [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
                 :summary       "Used to share the network with another user"
                 :description   "Authorization header expects the following format 'Token {token}'"
                 (add-user-to-network-response network-id user-id))

           (DELETE "/:network-id/user/:user-id" {:as request}
                   :path-params   [network-id :- String user-id :- String]
                   :header-params [authorization :- String]
                   :return        {:message String}
                   :middleware    [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
                   :summary       "Used to remove a user from shared list"
                   :description   "Authorization header expects the following format 'Token {token}'"
                   (remove-user-from-network-response network-id user-id))))
