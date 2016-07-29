;; src/validis/core-functions/verify/verify.clj
(ns validis.core-functions.verify.verify
  "Email based user verification."
  (:require [validis.queries.user :as query]
            [ring.util.http-response :as respond]))

(defn verify-user
  "Verifies a user based on email based verification. The format of the verification JSON is:
  `{:verification-string :email}`. This will make sure that all users are verified through email."
  [email verification-string]
  (let [user     (query/get-user-by-field {:email email})
        saved-vs (:verification-string user)
        success? (= verification-string saved-vs)]
    (if success?
      (do
        (query/verify-user {:email email})
        (respond/ok {:message "Your email was verified. You can now use all the services!"}))
      (respond/bad-request {:error "Your verification string did not match. Please try again."}))))
