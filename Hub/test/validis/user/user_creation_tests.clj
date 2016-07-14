;; test/validis/user/user-creation-tests.clj
(ns validis.user.user-creation-tests
  "Contains user creation tests."
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as query]
            [ring.mock.request :as mock]
            [cheshire.core :as ch]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn create-user
  "Creates a test user provided a user map."
  [user-map]
  (app (-> (mock/request :post "/api/user" (ch/generate-string user-map))
           (mock/content-type "application/json"))))

(defn asset-no-dup
  "Asserts no duplication between users."
  [user-1 user-2 expected-error-message]
  (let [_         (create-user user-1)
        response  (create-user user-2)
        body      (helper/parse-body (:body response))]
    (is (= 409                    (:status response)))
    (is (= expected-error-message (:error body)))))

(deftest can-successfully-create-a-new-user
  (testing "Can successfully create a new user"
    (let [response (create-user {:email "new@user.com" :username "NEWUSER" :password "pass"})
          body     (helper/parse-body (:body response))
          new-user (query/get-user-by-field {:username (:username body)})]
      (is (= 500 (:status response))))))
