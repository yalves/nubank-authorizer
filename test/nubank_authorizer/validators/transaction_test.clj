(ns nubank-authorizer.validators.transaction-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.validators.transaction :as transaction-validator]))

(facts "Transaction validator"
  (fact "Given a transaction with amount within user limit, the validation should be successful"
    (def transaction {:transaction { :merchant "Burger King", :amount 20, :time "2019-02-13T10:00:00.000Z" } })
    (def account {:availableLimit 100 :activeCard true })
    (transaction-validator/validate transaction account)
    => {:success true :violations [] :transaction transaction :account {:availableLimit 100 :activeCard true }})

  (fact "Given a transaction with amount greater than user limit, the validation should fail"
    (def transaction {:transaction { :merchant "Burger King", :amount 110, :time "2019-02-13T10:00:00.000Z" } })
    (def account {:availableLimit 100 :activeCard true })
    (transaction-validator/validate transaction account)
    => {:success false :violations ["insufficient-limit"] :transaction transaction :account {:availableLimit 100 :activeCard true }})
    
  (fact "Given a user with inactive card, the validation should fail"
    (def transaction {:transaction { :merchant "Burger King", :amount 20, :time "2019-02-13T10:00:00.000Z" } })
    (def account {:availableLimit 100 :activeCard false })
    (transaction-validator/validate transaction account)
    => {:success false :violations ["card-not-active"] :transaction transaction :account {:availableLimit 100 :activeCard false }})

  (fact "Given a user with inactive card and transaction with amount greater than user limit, the validation should fail"
    (def transaction {:transaction { :merchant "Burger King", :amount 110, :time "2019-02-13T10:00:00.000Z" } })
    (def account {:availableLimit 100 :activeCard false })
    (transaction-validator/validate transaction account)
    => {:success false :violations ["insufficient-limit" "card-not-active"] :transaction transaction :account {:availableLimit 100 :activeCard false }})
    )