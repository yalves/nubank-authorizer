(ns nubank-authorizer.processors.account-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.processors.account :as account-processor]))

(facts "Account validator"
  (fact "Given a non initialized account, the result must not contain violations"
    (account-processor/update-account { :availableLimit 100 :activeCard true } {})
    => {:success true :violations [] :account {:availableLimit 100 :activeCard true}})
  
  (fact "Given a initialized account, the result must contain the account-already-initialized violation") 
    (account-processor/update-account {:availableLimit 100 :activeCard true} { :availableLimit 100 :activeCard true })
    => {:success false :violations ["account-already-initialized"] :account { :availableLimit 100 :activeCard true }})