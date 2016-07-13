;; src/route-functions/auth/get-auth-credentials.clj
(ns validis.route-functions.auth.get-auth-credentials
  "Contains function for responding to a request for generating an auth-token."
  (:require [validis.general-functions.user.create-token :refer [create-token]]
            [validis.queries.user :as query]
            [ring.util.http-response :as respond]))

(defn auth-credentials-response
  "Generate response for get requests to /api/auth. This route requires basic
   authentication. A successful request to this route will generate a new
   refresh-token, and return `{:id :username :token :refreshToken}`"
  [request]
  (let [user          (:identity request)
        refresh-token (str (java.util.UUID/randomUUID))
        _             (query/update-user-refresh-token {:refresh_token refresh-token :id (:id user)})]
    (respond/ok {:id            (:id user)
                 :username      (:username user)
                 :token         (create-token user)
                 :refreshToken  refresh-token})))
