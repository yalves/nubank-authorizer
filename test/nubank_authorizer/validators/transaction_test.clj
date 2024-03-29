(ns nubank-authorizer.validators.transaction-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.validators.transaction :as transaction-validator]
            [clj-time [core :as t]
                      [format :as f]]))

(facts "Transaction validator"
  (fact "Given a transaction with amount within user limit, the validation should be successful"
    (def transaction {:merchant "Burger King", :amount 20, :time "2019-02-13T10:00:00.000Z" })
    (def account {:availableLimit 100 :activeCard true })
    (transaction-validator/validate transaction account [])
    => {:success true :violations [] :transaction transaction :account {:availableLimit 100 :activeCard true }})

  (fact "Given a transaction with amount greater than user limit, the validation should fail"
    (def transaction { :merchant "Burger King", :amount 110, :time "2019-02-13T10:00:00.000Z" })
    (def account {:availableLimit 100 :activeCard true })
    (transaction-validator/validate transaction account [])
    => {:success false :violations ["insufficient-limit"] :transaction transaction :account {:availableLimit 100 :activeCard true }})
    
  (fact "Given a user with inactive card, the validation should fail"
    (def transaction { :merchant "Burger King", :amount 20, :time "2019-02-13T10:00:00.000Z" })
    (def account {:availableLimit 100 :activeCard false })
    (transaction-validator/validate transaction account [])
    => {:success false :violations ["card-not-active"] :transaction transaction :account {:availableLimit 100 :activeCard false }})

  (fact "Given a user with inactive card and transaction with amount greater than user limit, the validation should fail"
    (def transaction { :merchant "Burger King", :amount 110, :time "2019-02-13T10:00:00.000Z" })
    (def account {:availableLimit 100 :activeCard false })
    (transaction-validator/validate transaction account [])
    => {:success false :violations ["insufficient-limit" "card-not-active"] :transaction transaction :account {:availableLimit 100 :activeCard false }})
  
  (fact "Given a past transaction with the same merchant and same amount in less than 2 minutes, the validation should fail"
    (def transaction { :merchant "Burger King", :amount 20, :time "2019-02-13T10:01:00.000Z" })
    (def account {:availableLimit 100 :activeCard true })
    (def transactions [{:time "2019-02-13T10:00:00.000Z" :merchant "Burger King" :amount 20}])
    (transaction-validator/validate transaction account transactions)
    => {:success false :violations ["doubled-transaction"] :transaction transaction :account {:availableLimit 100 :activeCard true }})
    
  (fact "Given two past transactions in less than 2 minutes, the validation should fail"
    (def transaction { :merchant "Burger King", :amount 20, :time "2019-02-13T10:05:00.000Z" })
    (def account {:availableLimit 100 :activeCard true })
    (def transactions [
      {:time "2019-02-13T10:03:00.000Z" :merchant "Mc Donalds" :amount 10}
      {:time "2019-02-13T10:04:00.000Z" :merchant "Burger King" :amount 10}])
    (transaction-validator/validate transaction account transactions)
    => {:success false :violations ["high-frequency-small-interval"] :transaction transaction :account {:availableLimit 100 :activeCard true }}))