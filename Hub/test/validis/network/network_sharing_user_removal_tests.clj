;; test/validis/network/network_sharing_user_removal_tests.clj
(ns validis.network.network-sharing-user-removal-tests 
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.network :as n-query]
            [validis.queries.user :as u-query]
            [validis.queries.cis :as c-query]
            [ring.mock.request :as mock]
            [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;
;; Teardown function ;;
;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  [f]
  (do
      (helper/add-users)
      (helper/add-networks)
      (helper/add-user-to-network)
      (f)
      (n-query/empty-network-database)
      (u-query/empty-user-database)))

(use-fixtures :each setup-teardown)

(deftest can-remove-user-from-network-if-present-and-owner
  (testing "Can remove user from network if user present and if owner"
    (let [test-user         (u-query/get-user-by-field {:username "user2"})
          test-user-id      (str (:_id test-user))
          test-network      (n-query/get-network-by-name {:name "test-network-1"})
          test-network-id   (str (:_id test-network))
          response          (app (-> (mock/request :delete (str "/api/network/" test-network-id "/user/" test-user-id))
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          updated-user-list (:shared-user-list updated-network)
          expected-response "User was successfully removed from the shared users' list."]
        (is (= 200               (:status response)))
        (is (= 0                 (count updated-user-list)))
        (is (= expected-response (:message body))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Add test for removing owner! ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest can-not-remove-user-from-network-if-user-exists-and-is-not-shared-in-network
  (testing "Cannot remove user from network if user exists but is not shared in network"
     (let [new-user         {:username "user3" :password "password" :email "user3@test.com"}
          _                 (app (-> (mock/request :post "/api/user" (ch/generate-string new-user))
                                     (mock/content-type "application/json")))
          new-user-db       (u-query/get-user-by-field {:username "user3"})
          test-user-id      (str (:_id new-user-db))
          test-network      (n-query/get-network-by-name {:name "test-network-1"})
          test-network-id   (str (:_id test-network))
          response          (app (-> (mock/request :delete (str "/api/network/" test-network-id "/user/" test-user-id))
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          updated-cis-list  (:shared-user-list updated-network)
          expected-error-response "The user you are trying to remove is not in the shared users list!"]
        (is (= 400                     (:status response)))
        (is (= 1                       (count updated-cis-list)))
        (is (= expected-error-response (:error body))))))

(deftest can-not-remove-user-from-network-if-user-does-not-exist
  (testing "Cannot remove user from network if user does not exist"
     (let [;; HACK: Assume this does not happen.
          test-user-id      "57911e3c3b21bc4aa0237d05"
          test-network      (n-query/get-network-by-name {:name "test-network-1"})
          test-network-id   (str (:_id test-network))
          response          (app (-> (mock/request :delete (str "/api/network/" test-network-id "/user/" test-user-id))
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          updated-user-list  (:shared-user-list updated-network)
          expected-error-response "The user you are trying to remove does not exist!"]
        (is (= 400                     (:status response)))
        (is (= 1                       (count updated-user-list)))
        (is (= expected-error-response (:error body))))))
