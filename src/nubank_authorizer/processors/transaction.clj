(ns nubank-authorizer.processors.transaction
  (:require [nubank-authorizer.validators.transaction :as transaction-validator])
  (:gen-class))

(defn commit-transaction
  [transaction account]
  (def transaction-amount (transaction :amount))
  (def current-amount ((account :account) :availableLimit))
  (def final-amount (- current-amount transaction-amount))
  (def user-account (atom account))
  (swap! user-account update-in [:account :availableLimit] (constantly final-amount)))

(defn process-transaction-operation
  [transaction account]
  (def validation-result (transaction-validator/validate transaction account))
  (if 
    (validation-result :success)

    (commit-transaction transaction account)

    (validation-result :violations)))