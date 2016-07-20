;; test/validis/cis/insert_cis_tests.clj
(ns validis.cis.insert-cis-tests
  "Contains tests to check CIS insertion into a database."
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as u-query]
            [validis.queries.network :as n-query]
            [validis.queries.cis :as c-query]
            [ring.mock.request :as mock]
            [buddy.hashers :as hashers]
            [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;
;; Teardown function ;;
;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  "Sets up the database for testing"
  [f]
  (do
      (helper/add-users)
      (helper/add-networks)
      (f)
      (u-query/empty-user-database)
      (c-query/empty-cis-database)
      (n-query/empty-network-database)))

(use-fixtures :each setup-teardown)

;;;;;;;;;;;;;;;;
;; Test cases ;;
;;;;;;;;;;;;;;;;

(deftest can-insert-cis-with-valid-details
  (testing "Can insert CIS with valid details"
    (let [new-cis           {:name "Clinic1" :address "Vic" :api_url "https://www.google.com"}
          response          (app (-> (mock/request :post "/api/cis" (ch/generate-string new-cis))
                                     (mock/content-type "application/json")))
          body              (helper/parse-body (:body response))
          new-cis           (c-query/get-cis-by-field {:name "Clinic1"})
          cis-id            (str (:_id new-cis))
          expected-response {:name "Clinic1" :id cis-id}]
      (is (= expected-response body))
      (is (= 200               (:status response)))
      (is (= 2                 (count body)))
      (is (= 1                 (count (c-query/get-all-cis)))))))

(deftest can-not-insert-cis-with-empty-address
  (testing "Cannot insert CIS with an empty address"
     (let [new-cis           {:name "Clinic1" :address "" :api_url "https://www.google.com"}
          response          (app (-> (mock/request :post "/api/cis" (ch/generate-string new-cis))
                                     (mock/content-type "application/json")))
          body              (helper/parse-body (:body response))
          expected-response "Address is empty!"]
      (is (= expected-response (:error body)))
      (is (= 400               (:status response)))
      (is (= 0                 (count (c-query/get-all-cis)))))))

(deftest can-not-insert-cis-with-empty-api-url
  (testing "Cannot insert CIS with an empty api url"
     (let [new-cis           {:name "Clinic1" :address "BC" :api_url ""}
          response          (app (-> (mock/request :post "/api/cis" (ch/generate-string new-cis))
                                     (mock/content-type "application/json")))
          body              (helper/parse-body (:body response))
          expected-response "API URL is empty!"]
      (is (= expected-response (:error body)))
      (is (= 400               (:status response)))
      (is (= 0                 (count (c-query/get-all-cis)))))))

(deftest can-not-insert-cis-with-empty-name
  (testing "Cannot insert CIS with an empty name"
     (let [new-cis           {:name "" :address "BC" :api_url "https://www.google.com"}
          response          (app (-> (mock/request :post "/api/cis" (ch/generate-string new-cis))
                                     (mock/content-type "application/json")))
          body              (helper/parse-body (:body response))
          expected-response "Name is empty!"]
      (is (= expected-response (:error body)))
      (is (= 400               (:status response)))
      (is (= 0                 (count (c-query/get-all-cis)))))))

(deftest can-not-insert-cis-with-same-api-url
  (testing "Cannot insert CIS with the same API URL"
     (let [cis-1            {:name "Clinic1" :address "BC" :api_url "https://facebook.com"}
           cis-2            {:name "Clinic2" :address "BC" :api_url "https://facebook.com"}
          _                 (app (-> (mock/request :post "/api/cis" (ch/generate-string cis-1))
                                     (mock/content-type "application/json")))
          response          (app (-> (mock/request :post "/api/cis" (ch/generate-string cis-2))
                                     (mock/content-type "application/json")))
          body              (helper/parse-body (:body response))
          expected-response "CIS with the same api-url already exists in the database. Use a different url for your own CIS"]
      (is (= expected-response (:error body)))
      (is (= 409               (:status response)))
      (is (= 1                 (count (c-query/get-all-cis)))))))
