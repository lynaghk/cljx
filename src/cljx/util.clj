(ns cljx.util
  "String manipulation util functions."
  (:import java.net.URI))

(defn slice
  "Extracts a section of a string and returns a new string."
  ([^String s ^long begin] (slice s begin (count s)))
  ([^String s ^long begin ^long end]
   (if (nil? s)
     s
     (let [end   (if (< end 0) (+ (count s) end) end)
           begin (if (< begin 0) (+ (count s) begin) begin)
           end   (if (> end (count s)) (count s) end)]
       (if (> begin end)
         ""
         (let [begin (if (< begin 0) 0 begin)
               end (if (< end 0) 0 end)]
           (.substring s begin end)))))))

(defn starts-with?
  "Check if the string starts with prefix."
  [^String s ^String prefix]
  (cond
    (nil? s) false
    (nil? prefix) false
    :else (let [region (slice s 0 (count prefix))]
            (= region prefix))))

(defn strip-prefix
  "Strip prefix in more efficient way."
  [^String s ^String prefix]
  (if (starts-with? s prefix)
    (slice s (count prefix) (count s))
    s))

(defn path
  "Get safe filesystem path representation
  from URI instance. Removing if is necessary
  the `file://` and `file:` prefix."
  [^URI uri]
  (let [^String path (.getPath uri)]
    (strip-prefix path "file://")))

