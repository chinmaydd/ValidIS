;; src/validis/api-routes/network.clj
(ns validis.api-routes.network
  "api-routes for network."
  (:require
    ;; Middleware inclusions
   [validis.middleware.cors :refer [cors-mw]]
   [validis.middleware.token-auth :refer [token-auth-mw]]
   [validis.middleware.authenticated :refer [authenticated-mw]]
   [validis.middleware.owner-auth :refer [owner-auth-mw]]

    ;; Route function inclusions
   [validis.core-functions.network.create-network :refer [create-network-response]]
   [validis.core-functions.network.modify-network :refer [modify-network-response]]
   [validis.core-functions.network.delete-network :refer [delete-network-response]]
   [validis.core-functions.network.add-cis :refer [add-cis-response]]
   [validis.core-functions.network.remove-cis :refer [remove-cis-response]]
   [validis.core-functions.network.add-user-to-network :refer [add-user-to-network-response]]
   [validis.core-functions.network.remove-user-from-network :refer [remove-user-from-network-response]]
   [validis.core-functions.network.get-network-information :refer [get-network-information]]

    ;; Utility libs
   [compojure.api.sweet :refer :all]))

(def network-api-routes
  "Specify api-routes for network functions"
  (context "/api/network" []
    :tags ["Network"]

           ;;;;;;;;;;;;;;;;;;;;
           ;; Network data ! ;;
           ;;;;;;;;;;;;;;;;;;;;

    (GET "/:network-id" {:as request}
      :return         {:message String};; A LOT OF THINGS
      :path-params    [network-id :- String]
      :header-params  [authorization :- String]
      :middleware     [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
      :summary        "Get the information regarding a particular network!" ;; This is real deal, bro
      :description    "Authorization header expects the following format 'Token {token}'"
      (get-network-information network-id))

           ;;;;;;;;;;;;;;;;;;;;;;;;;
           ;; Network CRUD api-routes ;;
           ;;;;;;;;;;;;;;;;;;;;;;;;;

    (POST "/"        {:as request}
      :return        {:network-id String}
      :header-params [authorization :- String]
      :middleware    [token-auth-mw cors-mw authenticated-mw]
      :body-params   [name :- String location :- String]
      :summary       "Create a new network. Requires the token to have ID of the user along with authentication."
      :description   "Authorization header expects the following format 'Token {token}'"
      (create-network-response request name location))

    (PATCH "/:network-id" {:as request}
      :path-params        [network-id :- String]
      :body-params        [{name :- String ""} {location :- String ""}]
      :header-params      [authorization :- String]
      :return             {:network-id String}
      :middleware         [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
      :summary            "Modifies the existing network information. Requires user token and the network must belong to the user"
      :description        "Authorization header expects the following format 'Token {token}'"
      (modify-network-response request network-id name location))

    (DELETE "/:network-id"     {:as request}
      :path-params       [network-id :- String]
      :header-params     [authorization :- String]
      :return            {:message String}
      :middleware        [token-auth-mw cors-mw authenticated-mw owner-auth-mw]
      :summary           "Used to delete the existing network. Requires user token and the network must belong to the user"
      :description       "Authorization header expects the following format 'Token {token}'"
      (delete-network-response network-id))

           ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
           ;; Network CIS interaction api-routes ;;
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
           ;; Network User interaction api-routes ;;
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
