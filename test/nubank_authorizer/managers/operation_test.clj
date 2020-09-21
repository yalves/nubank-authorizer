(ns nubank-authorizer.managers.operation-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.managers.operation :as operation-manager]
            [nubank-authorizer.processors.account :as account-processor]))

(facts "Operation manager"
  (fact "Given a account operation, the manager should trigger the account flow"
    (def test-operation {:account {:availableLimit 100 :activeCard true }})
    (def account {})
    (operation-manager/execute-operation test-operation account)
    => {:success true :violations [] :account {}}
    ; (provided
    ;   (account-validator/validate account) => {:success true :violations [] :account {}})
      ))
    
  ; (fact "Given a successful input, the user available limit should be updated"
  ;   (def transaction { :merchant "Burger King", :amount 80, :time "2019-02-13T10:00:00.000Z" })
  ;   (def account {:availableLimit 100 :activeCard true })
  ;   (def input {:success true :violations [] :transaction transaction :account account})
  ;   (transaction-processor/transact input)
  ;   => {:success true :violations [] :transaction transaction :account {:availableLimit 20 :activeCard true }}))