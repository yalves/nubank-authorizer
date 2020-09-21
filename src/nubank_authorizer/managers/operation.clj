(ns nubank-authorizer.managers.operation
  (:require [nubank-authorizer.processors.transaction :as transaction-processor]
            [nubank-authorizer.validators.transaction :as transaction-validator]
            [nubank-authorizer.processors.account :as account-processor]
            [nubank-authorizer.validators.account :as account-validator])
  (:gen-class))

  (defn execute-operation
    [operation account]
    
    (for [[k v] operation]
      (case k
        :account (        
        (->>
        (account-validator/validate account)
        (account-processor/register)
        (println)))

        :transaction ((->>
        (transaction-validator/validate v account)
        (transaction-processor/transact)))
        
        )))