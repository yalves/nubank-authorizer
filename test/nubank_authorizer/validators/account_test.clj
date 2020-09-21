(ns nubank-authorizer.validators.account-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.validators.account :as account-validator]))

(facts "Account validator"
  (fact "Given a non initialized account, the result must not contain violations"
    (account-validator/validate {})
    => {:success true :violations [] :account {}})
  
  (fact "Given a initialized account, the result must contain the account-already-initialized violation") 
    (account-validator/validate { :availableLimit 100 :activeCard true })
    => {:success false :violations ["account-already-initialized"] :account { :availableLimit 100 :activeCard true }})