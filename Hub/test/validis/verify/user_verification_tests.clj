;; test/verify/user_verification_tests.clj
(ns validis.verify.user-verification-tests
 "Contains email verification tests."
 (:require [clojure.test :refer :all]
           [validis.handler :refer :all]
           [validis.test-utils :as helper]
           [validis.queries.user :as query]
           [ring.mock.request :as mock]
           [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fixture function (before-hook) ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  "Empties the entire database for testing."
  [f]
  (do
      (query/empty-user-database)
      (f)
      (query/empty-user-database)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test user creation queries ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-user
  "Creates a test user provided a user map."
  [user-map]
  (app (-> (mock/request :post "/api/user" (ch/generate-string user-map))
           (mock/content-type "application/json"))))

(use-fixtures :each setup-teardown)

;;;;;;;;;;;;;;;;
;; Test cases ;;
;;;;;;;;;;;;;;;;

(deftest can-successfully-verify-a-user-with-a-valid-verification-string
  (testing "Can successfully verify a user based on email identification"
    (let [test-user           {:email "user1@test.com" :username "user1" :password "pass"}
          _                   (create-user test-user)
          new-user            (query/get-user-by-field {:username (:username test-user)})
          verification-string (:verification-string new-user)
          verification-map    {:email (:email test-user) :verification-string (:verification-string new-user)}
          response            (app (-> (mock/request :post "/api/verify" (ch/generate-string verification-map))
                                       (mock/content-type "application/json")))
          body                (helper/parse-body (:body response))
          is-verified?        (:verified? new-user)
          expected-response "Your email was verified. You can now use all the services!"]
      (is (= 200               (:status response)))
      (is (= expected-response (:message body)))
      (is (= is-verified?       true)))))


(deftest can-not-verify-a-user-with-an-invalid-verification-string
  (testing "Cannot verify a user based on an invalid verification string"
    (let [test-user           {:email "user1@test.com" :username "user1" :password "pass"}
          _                   (create-user test-user)
          new-user            (query/get-user-by-field {:username (:username test-user)})
          verification-string (:verification-string new-user)
          verification-map    {:email (:email test-user) :verification-string "abc"}
          response            (app (-> (mock/request :post "/api/verify" (ch/generate-string verification-map))
                                       (mock/content-type "application/json")))
          body                (helper/parse-body (:body response))
          is-verified?        (:verified? new-user)
          expected-response "Your verification string did not match. Please try again."]
      (is (= 400 (:status response)))
      (is (= expected-response (:error body)))
      ;; Verification is delayed.
      ;;(is (= is-verified? false))
      )))
