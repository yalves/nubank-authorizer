(ns nubank-authorizer.processors.transaction
  (:gen-class))

(defn process-transaction-operation
  [transaction]
  (def validation-result (validate-transaction transaction @user-account))
  (if 
    (validation-result :success)

    (commit-transaction transaction)

    (do (reset! user-violations (validation-result :violations)))))