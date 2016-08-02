;; src/validis/api-routes/cis.clj
(ns validis.api-routes.cis
  "api-routes for CIS."
  (:require
   ;; Middleware inclusions
   [validis.middleware.cors :refer [cors-mw]]
   [validis.middleware.token-auth :refer [token-auth-mw]]
   [validis.middleware.authenticated :refer [authenticated-mw]]
   [validis.print-handler :refer [print-handler]]

   ;; Route function inclusions
   [validis.core-functions.cis.insert-cis :refer [insert-cis-response]]
   [validis.core-functions.cis.get-all-cis :refer [get-all-cis]]

   ;; Schema inclusions
   [validis.schemas.cis :refer [CISList]]

   ;; Utility libs
   [compojure.api.sweet :refer :all]))

(def cis-api-routes
  "Specify api-routes for cis functions"
  (context "/api/cis" []
    :tags ["CIS"]

    ;;;;;;;;;;;;;;;;;;;;;
    ;; CIS CRUD api-routes ;;
    ;;;;;;;;;;;;;;;;;;;;;

    (POST "/" {:as request}
      :return {:id String :name String}
      :middleware [cors-mw]
      :body-params [name :- String address :- String api_url :- String]
      :summary     "Adds a new CIS to the database"
      :description "The API URL should point to the TPS(TouchPoint Sensor) URL of the CIS. Adds the CIS into the database. This does not require the user to be a ValidIS user."
      (insert-cis-response request name address api_url))

    (GET "/all"    {:as request}
      :return      {:list CISList}
      :middleware  [cors-mw]
      :summary     "Returns an entire list of CIS with their IDs"
      :description "This function is useful for adding new CISs to networks. This does not require the user to be a ValidIS user. This facilitates lookup into the CIS table for non-users."
      (get-all-cis))))
