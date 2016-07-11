;; src/route-functions/verify/verify.clj
(ns validis.route-functions.verify.verify
  (:require [validis.queries.user    :as query]
            [ring.util.http-response :as respond]))

(defn verify-user
  "Verifies a user based on email based verification."
  [email verification-string]
  (let [user     (query/get-user-by-field {:email email})
        saved-vs (:verification-string user)
        success? (= verification-string saved-vs)]
    (if success?
      (do
          (respond/ok "Your email was verified. You can now use all the services!")
          (query/verify-user {:email email}))
      (respond/bad-request {:error "You verification string did not match. Please try again."}))))
