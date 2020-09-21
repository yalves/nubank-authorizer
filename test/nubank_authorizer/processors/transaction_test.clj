(ns nubank-authorizer.processors.transaction-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.processors.transaction :as transaction-processor]))

(facts "Transaction processor"
  (fact "Given a failed input, the transaction should fail"
    (def transaction { :merchant "Burger King", :amount 110, :time "2019-02-13T10:00:00.000Z" })
    (def account {:availableLimit 100 :activeCard true })
    (def input {:success false :violations ["insufficient-limit"] :transaction transaction :account account})
    (transaction-processor/transact input)
    => {:success false :violations ["insufficient-limit"] :transaction transaction :account {:availableLimit 100 :activeCard true }})
    
  (fact "Given a successful input, the user available limit should be updated"
    (def transaction { :merchant "Burger King", :amount 80, :time "2019-02-13T10:00:00.000Z" })
    (def account {:availableLimit 100 :activeCard true })
    (def input {:success true :violations [] :transaction transaction :account account})
    (transaction-processor/transact input)
    => {:success true :violations [] :transaction transaction :account {:availableLimit 20 :activeCard true }}))