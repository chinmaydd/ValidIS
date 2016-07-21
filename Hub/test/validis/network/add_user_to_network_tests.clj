;; test/validis/network/add_user_to_network_tests.clj
(ns validis.network.add-user-to-network-tests
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
      (helper/add-cis)
      (f)
      (c-query/empty-cis-database)
      (n-query/empty-network-database)
      (u-query/empty-user-database)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test user for deletion queries ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use-fixtures :each setup-teardown)

(deftest can-share-network-with-user-if-owner-and-user-exists
  (testing "Can share network with user if owner and user exists"
    (let [network-id        (str (:_id (n-query/get-network-by-name {:name "test-network-1"})))
          user-id           (str (:_id (u-query/get-user-by-field {:username "user2"})))
          response          (app (-> (mock/request :post (str "/api/network/" network-id "/user/" user-id))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          shared-user-list  (:shared-user-list updated-network)
          first-user-id     (nth shared-user-list 0)
          body              (helper/parse-body (:body response))
          expected-response "User was successfully added!"]
      (is (= 200               (:status response)))
      (is (= expected-response (:message body) ))
      (is (= 1                 (count shared-user-list)))
      (is (= user-id           first-user-id)))))

(deftest can-not-share-network-with-user-if-owner-and-user-does-not-exist
  (testing "Cannot share network with user if owner and network do not exist"
    (let [network-id        (str (:_id (n-query/get-network-by-name {:name "test-network-1"})))
          ;; HACK: Assume this doesnt happen.
          user-id           "57911e3c3b21bc4aa0237d05"
          response          (app (-> (mock/request :post (str "/api/network/" network-id "/user/" user-id))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          shared-user-list  (:shared-user-list updated-network)
          body              (helper/parse-body (:body response))
          expected-response "User not found!"]
      (is (= 400               (:status response)))
      (is (= expected-response (:error body)))
      (is (= 0                 (count shared-user-list))))))
