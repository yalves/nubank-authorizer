(ns nubank-authorizer.validators.transaction
  (:require [clj-time [format :as time-formatter]]
                      ;[core :as time]]
            [nubank-authorizer.utils.date :as date-util])
  (:gen-class))

  (defn validate
    "Validates a transaction against an account an its past transactions"
    [transaction account past-transactions]

    (def transaction-amount (transaction :amount))
    (def current-amount (account :availableLimit))
    (def violations (atom []))
    (def success (atom true))

    (def current-transaction-time (time-formatter/parse (transaction :time)))

    (if (> (count past-transactions) 0)
        (do 
          (def last-transaction (last past-transactions))
          (def last-transaction-time (time-formatter/parse (last-transaction :time)))
          (def minutes-since-last-transaction (date-util/get-interval-in-minutes last-transaction-time current-transaction-time))
          (def is-same-amount-and-merchant 
            (= { :merchant (last-transaction :merchant) :amount (last-transaction :amount)} 
               { :merchant (transaction :merchant) :amount (transaction :amount) }))

          (if (and (<= minutes-since-last-transaction 2) is-same-amount-and-merchant)
              (do (swap! violations conj "doubled-transaction")
                  (reset! success false)))))

    (if (>= (count past-transactions) 2)
        (do 
          (def last-two-transactions (take-last 2 past-transactions))
          (def transaction-time-threshold (time-formatter/parse ((first last-two-transactions) :time)))
          (def minutes-since-threshold (date-util/get-interval-in-minutes transaction-time-threshold current-transaction-time))
          (if (<= minutes-since-threshold 2)
              (do (swap! violations conj "high-frequency-small-interval")
                  (reset! success false)))))

    (if (> transaction-amount current-amount)
      (do (swap! violations conj "insufficient-limit")
          (reset! success false)))

    (if (= (account :activeCard) false)
      (do (swap! violations conj "card-not-active")
          (reset! success false)))

    { :success @success :violations @violations :transaction transaction :account account })