;; src/routes/verify.clj
(ns validis.routes.verify
  "Verification route."
  (:require [validis.middleware.cors :refer [cors-mw]]
            [compojure.api.sweet :refer :all]
            [validis.route-functions.verify.verify :refer [verify-user]]))

(def verification-routes
  "Specify routes for user verification"
  (context "/api/verify" []
    :tags ["Verify"]

    (POST "/" {:as request}
      :return {:message String}
      :body-params [email :- String verification-string :- String]
      :middleware [cors-mw]
      :summary "Verifies the user based on the given user email id."
      (verify-user email verification-string))))
