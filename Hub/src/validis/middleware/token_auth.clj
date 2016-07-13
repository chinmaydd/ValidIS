;; src/middleware/token_auth.clj
(ns validis.middleware.token-auth
  "Wraps the `token-auth` middleware around the request handler."
  (:require [buddy.auth.middleware :refer [wrap-authentication]]
            [validis.auth-resources.token-auth-backend :refer [token-backend]]))

(defn token-auth-mw
  "Middleware used on routes requiring token authentication"
  [handler]
  (wrap-authentication handler token-backend))
