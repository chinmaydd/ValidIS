;; test/validis/network/cis_removal_from_networks_tests.clj
(ns validis.network.cis-removal-from-networks-tests  
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
      (helper/add-cis-to-network)
      (f)
      (c-query/empty-cis-database)
      (n-query/empty-network-database)
      (u-query/empty-user-database)))

(use-fixtures :each setup-teardown)

(deftest can-remove-cis-from-network-if-present-and-owner
  (testing "Can remove CIS from network if CIS present and if owner"
    (let [test-cis          (c-query/get-cis-by-field {:name "Clinic1"})
          test-cis-id       (str (:_id test-cis))
          test-network      (n-query/get-network-by-name {:name "test-network-1"})
          test-network-id   (str (:_id test-network))
          response          (app (-> (mock/request :delete (str "/api/network/" test-network-id "/cis/" test-cis-id))
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          updated-cis-list  (:CIS-list updated-network)
          expected-response "CIS was successfully removed."]
        (is (= 200               (:status response)))
        (is (= 0                 (count updated-cis-list)))
        (is (= expected-response (:message body))))))

(deftest can-not-remove-cis-from-network-if-cis-exists-and-is-not-present-in-network
  (testing "Cannot remove CIS from network if CIS exists but is not present in network"
     (let [test-cis         (c-query/get-cis-by-field {:name "Clinic2"})
          test-cis-id       (str (:_id test-cis))
          test-network      (n-query/get-network-by-name {:name "test-network-1"})
          test-network-id   (str (:_id test-network))
          response          (app (-> (mock/request :delete (str "/api/network/" test-network-id "/cis/" test-cis-id))
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          updated-cis-list  (:CIS-list updated-network)
          expected-error-response "CIS you are trying to remove is not present in the network!"]
        (is (= 400                     (:status response)))
        (is (= 1                       (count updated-cis-list)))
        (is (= expected-error-response (:error body))))))

(deftest can-not-remove-cis-from-network-if-cis-does-not-exist
  (testing "Cannot remove CIS from network if CIS does not exist"
     (let [;; HACK: Assume this does not happen.
          test-cis-id      "57911e3c3b21bc4aa0237d05"
          test-network      (n-query/get-network-by-name {:name "test-network-1"})
          test-network-id   (str (:_id test-network))
          response          (app (-> (mock/request :delete (str "/api/network/" test-network-id "/cis/" test-cis-id))
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          updated-network   (n-query/get-network-by-name {:name "test-network-1"})
          updated-cis-list  (:CIS-list updated-network)
          expected-error-response "CIS you are trying to remove does not exist!"]
        (is (= 400                     (:status response)))
        (is (= 1                       (count updated-cis-list)))
        (is (= expected-error-response (:error body))))))
