(ns advent-utils.ascii
  (:require [clojure.string :as str]
            [advent-utils.core :as u]))

(defn ascii->map
  "Convert an ASCII represention of a 2D grid into
   a Clojure map.
   
   charmap is a map where the keys are ASCII chars and
   the values are expected to be symbols to use in
   your application. Ex.: (def codes {\\. :space \\# :wall})
   
   Output will be a map with keys width, height, and grid.
   grid will be a map where the keys are [x y] positions
   and values will be the symbols defined in charmap"
  [charmap lines]
  (let [height  (count lines)
        width   (count (first lines))
        symbols (mapcat #(map charmap %) lines)]
    {:width  width
     :height height
     :grid (zipmap (for [y (range height)
                         x (range width)]
                     [x y])
                   symbols)}))

(defn map->ascii
  "Convert a Clojure map representing a 2D space into
   an ASCII-art string representation.
   
   charmap is a map where the keys are ASCII chars and
   the values are expected to be symbols to use in
   your application. Ex.: (def codes {\\. :space \\# :wall})"
  [charmap {:keys [width height grid]}]
  (let [chars (u/invert-map charmap)
        rep (partition width (for [y (range height)
                                   x (range width)]
                               (chars (get grid [x y]))))]
    (str/join "\n" (mapv #(apply str %) rep))))
