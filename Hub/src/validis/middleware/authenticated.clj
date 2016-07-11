;; src/middleware/authenticated.clj
(ns validis.middleware.authenticated
  (:require [buddy.auth              :refer [authenticated?]]
            [ring.util.http-response :refer [unauthorized]]
            [validis.queries.user    :as query]))

(defn verified?
  "Checks if the user account is verified."
  [request]
  (let [user-id      (get-in request [:identity :id])
        user         (query/get-user-by-id {:id user-id})
        is-verified? (:verified? user)]
    is-verified?))

(defn authenticated-mw
  "Middleware used in routes that require authentication. If request is not
   authenticated a 401 not authorized response will be returned"
  [handler]
  (fn [request]
   (if (authenticated? request)
    (if (verified? request)
     (handler request)
     (unauthorized {:error "Please verify your account!"}))
    (unauthorized {:error "Not authorized."})))) 
