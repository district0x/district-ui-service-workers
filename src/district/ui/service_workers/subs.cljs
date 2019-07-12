(ns district.ui.service-workers.subs
  (:require
    [district.ui.service-workers.queries :as queries]
    [re-frame.core :refer [reg-sub]]))

(defn- sub-fn [query-fn]
  (fn [db [_ & args]]
    (apply query-fn db args)))

(reg-sub
  ::workers
  queries/workers)

(reg-sub
  ::worker-registration
  (sub-fn queries/worker-registration))
