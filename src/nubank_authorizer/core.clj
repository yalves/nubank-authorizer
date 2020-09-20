(ns nubank-authorizer.core
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [nubank-authorizer.processors.transaction :as transaction-processor])
  (:require [nubank-authorizer.validators.transaction :as transaction-validator]))

  (def account-data "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
  (def transaction-data "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 110, \"time\": \"2019-02-13T11:00:00.000Z\" } }")

  (def user-account (atom {}))
  (def user-transactions (atom []))
  (def user-violations (atom []))
  (def application-state (atom {:account {} :violations [] :transactions []}))

  (defn validate-account-creation
    [account user-account]

    (def violations (atom []))
    (def success (atom true))

    (if
      (not (= user-account {}))      
      (do (swap! violations conj "account-already-initialized")
          (reset! success false)))

    { :success @success :violations @violations })

  (defn process-acount-operation
    [account]
    (def validation-result (validate-account-creation account @user-account))
    (if 
      (validation-result :success)

      (swap! user-account assoc :account account)
    
      (do (reset! user-violations (validation-result :violations)))))    
    
  (defn update-state
    "Updates the application state."
    [state]
    (swap! user-account update-in [:account :availableLimit] (constantly ((:account state) :availableLimit)))
    (swap! user-transactions conj (state :transaction))
    (reset! user-violations (state :violations)))
        
  (defn main-workflow
    "Orchestrates the operation workflow."
    []
    (println "Insert your operations:")
    (def transaction-operation (json/read-str (read-line) :key-fn keyword))
    ;(def transaction-operation (json/read-str transaction-data :key-fn keyword))
    
    (->>
      (transaction-validator/validate (transaction-operation :transaction) (@user-account :account))
      (transaction-processor/transact)
      (update-state))

    (println (json/write-str { :account (@user-account :account) :violations @user-violations }))
    (println @user-transactions)
    (read-line))
      
  (defn -main
    "Triggers the authorization flow."
    [& args]
    (def account-operation (json/read-str account-data :key-fn keyword))
    (process-acount-operation (account-operation :account))
    (while true (main-workflow)))
        
        
;; (def transaction-result (transaction-processor/process-transaction-operation (transaction-operation :transaction) @user-account))
;; (if-let [value (:account transaction-result)] 
;;   (do 
;;     (swap! user-account update-in [:account :availableLimit] (constantly ((:account transaction-result) :availableLimit)))
;;     (swap! user-transactions conj (transaction-operation :transaction)))
;;   (do (reset! user-violations transaction-result)))
      

; { "account": { "activeCard": true, "availableLimit": 100 } }
; { "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }
; { "transaction": { "merchant": "Habbib's", "amount": 90, "time": "2019-02-13T11:00:00.000Z" } }



; (doseq [ln (line-seq (java.io.BufferedReader. *in*))]
;   (println "*************** \n\n\n")
;   (println (clojure.string/split-lines ln))))
;(def operations (json/read-str in))