(ns district.ui.service-workers.queries)

(def db-key :district.ui.service-workers)

(defn assoc-worker [db {:keys [:scope] :as worker}]
  (assoc-in db [db-key :workers scope] worker))

(defn assoc-workers [db workers]
  (reduce (fn [acc worker]
            (assoc-worker acc worker))
          db
          workers))

(defn assoc-worker-registration [db {:keys [:scope]} registration]
  (assoc-in db [db-key :workers scope :registration] registration))

(defn workers [db]
  (-> db db-key :workers))

(defn worker-registration [db scope]
  (get-in db [db-key :workers scope :registration]))

(defn dissoc-service-workers [db]
  (dissoc db db-key))
