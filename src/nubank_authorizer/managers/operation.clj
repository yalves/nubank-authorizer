(ns nubank-authorizer.managers.operation
  (:require [nubank-authorizer.processors.transaction :as transaction-processor]
            [nubank-authorizer.validators.transaction :as transaction-validator]
            [nubank-authorizer.processors.account :as account-processor])
  (:gen-class))

  (defn execute-operation
    [operation account]

    (def operation-type (get-in (first (map (fn [[k v]] k) operation)) []))

    (case operation-type
      :account (do (account-processor/update-account (operation :account) account))

      :transaction (do (->>
      (transaction-validator/validate (operation :transaction) account)
      (transaction-processor/transact)))))