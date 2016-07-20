;; test/auth/credential_retrieval_tests.clj
(ns validis.auth.credential-retrieval-tests
  (:require [clojure.test :refer :all]
            [environ.core :refer [env]]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as query]
            [buddy.sign.jwt :as jwt]
            [ring.mock.request :as mock]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fixture function (before-hook) ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown [f]
  (do
    (helper/add-users)
    (f)
    (query/empty-user-database)))

(use-fixtures :each setup-teardown)

;;;;;;;;;;;;;;;;
;; Test cases ;;
;;;;;;;;;;;;;;;;

(deftest valid-username-and-password-return-correct-auth-credentials
  (testing "Valid username and password return correct auth credentials"
    (let [response       (app (-> (mock/request :get "/api/auth")
                                  (helper/basic-auth-header "user1:password")))
          body           (helper/parse-body (:body response))
          id             (:id body)
          token-contents (jwt/unsign (:token body) (env :auth-key) {:alg :hs512})]
      (is (= 4                 (count body)))
      (is (= 200               (:status response)))
      (is (= "user1"           (:username body)))
      (is (= 4                 (count token-contents)))
      (is (= id                (:id token-contents)))
      (is (= "user1@test.com"  (:email token-contents)))
      (is (= "user1"           (:username token-contents)))
      (is (number?             (:exp token-contents))))))

(deftest invlid-password-does-not-return-auth-credentials
  (testing "Invalid password does not return auth credentials"
    (let [response (app (-> (mock/request :get "/api/auth")
                            (helper/basic-auth-header "user1:badpass")))
          body     (helper/parse-body (:body response))]
      (is (= 401              (:status response)))
      (is (= "Not authorized." (:error body))))))

(deftest invlid-username-does-not-return-auth-credentials
  (testing "Invalid username does not return auth credentials"
    (let [response (app (-> (mock/request :get "/api/auth")
                            (helper/basic-auth-header "baduser:badpass")))
          body     (helper/parse-body (:body response))]
      (is (= 401              (:status response)))
      (is (= "Not authorized." (:error body))))))

(deftest no-auth-credentials-are-returned-when-no-username-and-password-provided
  (testing "No auth credentials are returned when no username and password provided"
    (let [response (app (mock/request :get "/api/auth"))
          body     (helper/parse-body (:body response))]
      (is (= 401              (:status response)))
      (is (= "Not authorized." (:error body))))))
