(ns nubank-authorizer.managers.operation-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.managers.operation :as operation-manager]
            [nubank-authorizer.processors.account :as account-processor]
            [nubank-authorizer.processors.transaction :as transaction-processor]
            [nubank-authorizer.validators.transaction :as transaction-validator]))

(facts "Operation manager"
  (fact "Given a account operation, the manager should trigger the account flow"
    (def test-operation {:account {:availableLimit 100 :activeCard true }})
    (def account {})
    (operation-manager/execute-operation test-operation account [])
      => {:success true :violations [] :account {:availableLimit 100 :activeCard true }}
    (provided
      (account-processor/update-account {:activeCard true, :availableLimit 100} {}) 
        => {:success true :violations [] :account {:availableLimit 100 :activeCard true }})
    (provided
      (transaction-processor/transact anything) => irrelevant :times 0)
      )
      
  (fact "Given a transaction operation, the manager should trigger only the transaction flow"
    (def test-operation {:transaction { :merchant "Burger King", :amount 90, :time "2019-02-13T10:00:00.000Z" }})
    (def account {:availableLimit 100 :activeCard true })
    (operation-manager/execute-operation test-operation account [])
      => {:success true :violations [] :account {:availableLimit 10 :activeCard true } :transaction (test-operation :transaction)}
    (provided
      (transaction-validator/validate { :merchant "Burger King", :amount 90, :time "2019-02-13T10:00:00.000Z" } {:availableLimit 100 :activeCard true } []) 
        => {:success true :violations [] :account {:availableLimit 100 :activeCard true } :transaction { :merchant "Burger King", :amount 90, :time "2019-02-13T10:00:00.000Z" }})
    (provided
      (transaction-processor/transact {:success true :violations [] :account {:availableLimit 100 :activeCard true } :transaction { :merchant "Burger King", :amount 90, :time "2019-02-13T10:00:00.000Z" }})
      => { :success true :violations [] :transaction { :merchant "Burger King", :amount 90, :time "2019-02-13T10:00:00.000Z" } :account {:availableLimit 10 :activeCard true }})
    (provided
      (account-processor/update-account anything) => irrelevant :times 0)))
    
  ; (fact "Given a successful input, the user available limit should be updated"
  ;   (def transaction { :merchant "Burger King", :amount 80, :time "2019-02-13T10:00:00.000Z" })
  ;   (def account {:availableLimit 100 :activeCard true })
  ;   (def input {:success true :violations [] :transaction transaction :account account})
  ;   (transaction-processor/transact input)
  ;   => {:success true :violations [] :transaction transaction :account {:availableLimit 20 :activeCard true }}))