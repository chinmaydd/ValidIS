(ns validis.general-functions.user.create-token
   (:require [environ.core :refer [env]]
             [clj-time.core :as time]
             [buddy.sign.jwt :as jwt]))

(defn create-token
  "Create a signed json web token. The token contents are; username, email, id,
   permissions and token expiration time. Tokens are valid for 15 minutes.
  ** Temporarily removed permissions **"
  [user]
  (let [stringify-user (-> user
                           (update-in [:username] str)
                           (update-in [:email] str)
                           (assoc     :exp (time/plus (time/now) (time/seconds 900))))
        token-contents (select-keys stringify-user [:username :email :id :exp])]
    (jwt/sign token-contents (env :auth-key))))