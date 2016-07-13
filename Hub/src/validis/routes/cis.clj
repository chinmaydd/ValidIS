;; src/routes/cis.clj
(ns validis.routes.cis
  "Routes for CIS."
  (:require
   ;; Middleware inclusions
   [validis.middleware.cors :refer [cors-mw]]
   [validis.middleware.token-auth :refer [token-auth-mw]]
   [validis.middleware.authenticated :refer [authenticated-mw]]
   [validis.print-handler :refer [print-handler]]

   ;; Route function inclusions
   [validis.route-functions.cis.insert-cis :refer [insert-cis-response]]
   [validis.route-functions.cis.get-all-cis :refer [get-all-cis]]

   ;; Schema inclusions
   [validis.schemas.cis :refer [CISList]]

   ;; Utility libs
   [compojure.api.sweet :refer :all]))

(def cis-routes
  "Specify routes for cis functions"
  (context "/api/cis" []
    :tags ["CIS"]

    ;;;;;;;;;;;;;;;;;;;;;
    ;; CIS CRUD routes ;;
    ;;;;;;;;;;;;;;;;;;;;;

    (POST "/" {:as request}
      :return {:message String}
      :middleware [cors-mw]
      :body-params [name :- String address :- String api_url :- String]
      :summary "Add a new CIS to the database. The API url should point to the TPS Api of the CIS."
      (insert-cis-response request name address api_url))

    (GET "/all" {:as request}
      :return {:list CISList}
      :middleware [cors-mw]
      :summary "Returns an entire list of CIS with their IDs. Useful for users for addition into their own networks."
      (get-all-cis))))
