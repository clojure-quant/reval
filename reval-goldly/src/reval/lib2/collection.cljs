;; COLLECTION UI

(def show-collection-debug-ui false)

(defn nb-item [open-link fmt ns]
  [:a
   [:p.w-full.truncate.bg-blue-200.hover:bg-blue-300.border.border-solid.border-blue-300.p-1.cursor-pointer
   ; trunctate does the text magic
   ; .overflow-x-hidden
   ;[:a ;.m-1
    {:on-click #(rf/dispatch [:bidi/goto open-link :query-params {:ns ns :fmt (name fmt)}])}
    (-> (string/split ns ".") last)
    ;]
    ]])

(defn nb-list [open-link [name [fmt list]]]
  (into
   [:div.w-full
    [:p.bg-red-300 name]
    (when show-collection-debug-ui
      [:p (meta list) (pr-str list)])]
   (map #(nb-item open-link fmt %) list)))

(defn notebook-collection [open-link d]
  [:div.w-full.h-full.w-min-64.max-h-full.overflow-y-auto
   (into
    [:div.flex.flex-col.items-stretch.bg-gray-50.h-full.w-full.max-h-full.overflow-y-auto]
    (map #(nb-list open-link %) d))])