;; src/validis/middleware/basic_auth.clj
(ns validis.middleware.basic-auth
  "Wraps the request handler with the `basic-auth-mw`"
  (:require [buddy.auth.middleware :refer [wrap-authentication]]
            [validis.auth-resources.basic-auth-backend :refer [basic-backend]]))

(defn basic-auth-mw
  "Middleware used on api-routes requiring basic authentication"
  [handler]
  (wrap-authentication handler basic-backend))
