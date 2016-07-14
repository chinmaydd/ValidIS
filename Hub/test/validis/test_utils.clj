(ns validis.test-utils
  "Utility functions required for testing."
  (:require [cheshire.core :as ch]
            [ring.mock.request :as mock]
            [validis.handler :refer [app]]
            [buddy.core.codecs :as codecs]
            [buddy.core.codecs.base64 :as b64]))

(def str->base64
  "Converts a string to base64"
  (comp codecs/bytes->str b64/encode))

(defn parse-body
  "Parse a body as string?"
  [body]
  (ch/parse-string (slurp body) true))

(defn basic-auth-header
  "Generates an auth-header for retrieving a user token"
  [request original]
  (mock/header request "Authorization" (str "Basic " (str->base64 original))))

(defn token-auth-header
  "Generates an auth-header for request authorization"
  [request token]
  (mock/header request "Authorization" (str "Token " token)))  


(defn get-user-token
  "Get a token for a user"
  [username-and-password]
  (let [initial-response (app (-> (mock/request :get "/api/auth")
                                  (basic-auth-header username-and-password)))
        initial-body     (parse-body (:body initial-response))]
    (:token initial-body)))

(defn get-token-auth-header-for-user
  "Get a auth-header for a token"
  [request username-and-password]
  (token-auth-header request (get-user-token username-and-password)))

(defn add-users
  "Add test users to the database."
  []
  (let [user-1 {:email "chinmay1dd@gmail.com" :username "chinmaydd" :password "password"}
        user-2 {:email "pranavv@gmail.com" :username "pranavv" :password "pass12345"}]
    (app (-> (mock/request :post "/api/user" (ch/generate-string user-1))
             (mock/content-type "application/json")))

    (app (-> (mock/request :post "/api/user" (ch/generate-string user-2))
             (mock/content-type "application/json")))))
