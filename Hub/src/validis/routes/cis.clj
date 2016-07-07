;; src/routes/cis.clj
(ns validis.routes.cis
  (:require 
            ;; Middleware inclusions  
            [validis.middleware.cors                        :refer [cors-mw]]
            [validis.middleware.token-auth                  :refer [token-auth-mw]]
            [validis.middleware.authenticated               :refer [authenticated-mw]]
            
            ;; Route function inclusions
            [validis.route-functions.cis.insert-cis         :refer [insert-cis-response]]
            [validis.route-functions.cis.get-all-cis        :refer [get-all-cis]]
            
            ;; Schema inclusions
            [validis.schemas.cis                            :refer [CISList]]
            
            ;; Utility libs
            [compojure.api.sweet                            :refer :all]))

(def cis-routes
  "Specify routes for cis functions"
  (context "/api/cis" []
           :tags ["CIS"]

    ;;;;;;;;;;;;;;;;;;;;;
    ;; CIS CRUD routes ;; 
    ;;;;;;;;;;;;;;;;;;;;;

    (POST "/" {:as request}
          :return {:message String}
          :header-params [authorization :- String]
          :middleware [token-auth-mw cors-mw authenticated-mw]
          :body-params [name :- String address :- String api-url :- String]
          :summary "Add a new CIS to the database. Requires toke to have the ID of the user along with the authorization. The API url should point to the TPS Api of the CIS"
          :description "Authorization header expects the following format 'Token {token}'"
          (insert-cis-response request name address api-url))

    (GET "/all" {:as request}
         :return {:list CISList}
         :middleware [cors-mw]
         :summary "Returns an entire list of CIS with their IDs. Useful for users for addition into their own networks."
         (get-all-cis))))
