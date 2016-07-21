;; test/validis/network/cis_addition_to_networks_tests.clj
(ns validis.network.cis-addition-to-network-tests
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

(deftest can-add-cis-to-network-if-owner-and-cis-exists
  (testing "Can add CIS to network if owner and cis exists"
    (let [network-id        (str (:_id (n-query/get-network-by-name {:name "test-network-1"})))
          cis-id            (str (:_id (c-query/get-cis-by-field {:api-url "https://google.com"})))
          response          (app (-> (mock/request :post (str "/api/network/" network-id "/cis/" cis-id))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          cis-list          (:CIS-list updated-network)
          first-cis-id      (nth cis-list 0)
          body              (helper/parse-body (:body response))
          expected-response "CIS was successfully added."]
      (is (= 200               (:status response)))
      (is (= expected-response (:message body) ))
      (is (= 1                 (count cis-list)))
      (is (= cis-id            first-cis-id)))))

(deftest can-not-add-cis-to-network-if-owner-and-cis-does-not-exist
  (testing "Cannot add CIS to network if owner and cis does not exist"
    (let [network-id        (str (:_id (n-query/get-network-by-name {:name "test-network-1"})))
          ;; HACK: Assume this doesnt happen.
          cis-id            "57911e3c3b21bc4aa0237d05"
          response          (app (-> (mock/request :post (str "/api/network/" network-id "/cis/" cis-id))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          cis-list          (:CIS-list updated-network)
          body              (helper/parse-body (:body response))
          expected-response "CIS does not exist! Please add it to the db first"]
      (is (= 400               (:status response)))
      (is (= expected-response (:error body)))
      (is (= 0                 (count cis-list))))))
