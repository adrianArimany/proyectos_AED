(defun solucioncuadratica (a b c esX1)
(setq discriminante (- (* b b) (* 4 a c)))
(setq raiz-discriminante (sqrt discriminante))
(setq x1 (/ (+ (- b) raiz-discriminante) (* 2 a)))
(setq x2 (/ (- (- b) raiz-discriminante) (* 2 a)))
(cond
(esX1 x1)
(t x2)))