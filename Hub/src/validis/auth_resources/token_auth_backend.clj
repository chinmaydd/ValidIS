;; src/auth_resources/token_auth_backend.clj
(ns validis.auth-resources.token-auth-backend
  "Token auth backend. Contains functions for auth-token validation"
  (:require [environ.core :refer [env]]
            [buddy.auth.backends.token :refer [jws-backend]]))

(def token-backend
  "Use the `jws` from the buddy library as our token backend. Tokens
  are valid for fifteen minutes after creation. If token is valid the decoded
  contents of the token will be added to the request with the keyword of
  `:identity`"
  (jws-backend {:secret (env :auth-key) :options {:alg :hs512}}))
